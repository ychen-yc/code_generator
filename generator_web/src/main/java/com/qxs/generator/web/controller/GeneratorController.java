package com.qxs.generator.web.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;
import com.qxs.generator.web.model.GenerateResult;
import com.qxs.generator.web.model.connection.Database;
import com.qxs.generator.web.model.connection.GenerateParameter;
import com.qxs.generator.web.model.connection.Ssh;
import com.qxs.generator.web.service.IGeneratorService;

/**
 * 代码生成控制器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-4-22
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/generator")
public class GeneratorController {
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_S");
	
	/**
	 * 生成的代码文件
	 * **/
	private String fileName = String.format("codes_%s_%s.zip", DATE_FORMAT.format(new Date()),System.nanoTime());
	
	@Autowired
	private IGeneratorService generatorService;
	
	
	@InitBinder("database")
    public void initBinderDatabase(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("database.");
    }
	@InitBinder("ssh")
    public void initBinderSSH(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("ssh.");
    }
	@InitBinder("generateParameter")
    public void initBinderGenerateParameter(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("generateParameter.");
    }
		
	/**
	 * 生成代码
	 * **/
	@PostMapping("/generate")
	public Object generate(Database database,Ssh ssh,GenerateParameter generateParameter, HttpServletResponse response)  
			throws IOException{
		GenerateResult generateResult = generatorService.generate(database, ssh, generateParameter);
		
		//生成成功
		if(generateResult.getStatus() == GenerateResult.Status.SUCCESS) {
			HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		    headers.setContentDispositionFormData("attachment",URLEncoder.encode(fileName, "UTF-8")); 
		    response.setHeader("Set-Cookie", "fileDownload=true; path=/");
		    
		    return new ResponseEntity<byte[]>(generateResult.getOutputStream().toByteArray(),headers, HttpStatus.OK);
		}else {
			//生成失败
		    response.setHeader("Set-Cookie", "fileDownload=true; path=/");
		    response.setHeader("Set-Cookie", "fileDownloadError=" + URLEncoder.encode(new Gson().toJson(generateResult), "UTF-8") + "; path=/");
		    response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
		    response.getWriter().write(new Gson().toJson(generateResult));
		    return null;
		}
        
	}
	
}
