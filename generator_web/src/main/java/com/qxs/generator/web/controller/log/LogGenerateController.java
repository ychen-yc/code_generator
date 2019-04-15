package com.qxs.generator.web.controller.log;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.qxs.generator.web.model.GenerateResult;
import com.qxs.generator.web.model.log.Generate;
import com.qxs.generator.web.service.log.IGenerateService;

/**
 * 生成代码日志控制器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-1-29
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/log/generate")
public class LogGenerateController {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_S");
	
	/**
	 * 生成的代码文件
	 * **/
	private String fileName = String.format("codes_%s_%s.zip", DATE_FORMAT.format(new Date()),System.nanoTime());
	
	@Autowired
	private IGenerateService generateService;
	
	@GetMapping("/index")
	public String index() {
		return "log/generate/index";
	}
	/**
	 * 日志详情
	 * **/
	@GetMapping("/detail/{id}")
	public String detail(@PathVariable("id") String id,Model model) {
		
		model.addAttribute("generate", generateService.getById(id));
		
		return "log/generate/detail";
	}
	
	/**
	* 获取列表数据
	* @return PageList<Login>
	**/
	@GetMapping("/getList")
	@ResponseBody
	public Page<Generate> getList(String search,@RequestParam(required = false)Integer offset, @RequestParam(required = false)Integer limit,
			@RequestParam(required = false)String sort, @RequestParam(required = false)String order) {
		return generateService.findList(search, offset, limit, sort, order);
	}
	
	/**
	* 重新生成
	**/
	@PostMapping("/generate")
	public Object generate(@RequestParam String id, HttpServletResponse response)  
			throws IOException{
		GenerateResult generateResult = generateService.generate(id);
		
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
