package com.qxs.generator.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 帮助信息控制器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2019-3-26
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/help")
public class HelpController {
	
	@GetMapping({"/","/index"})
	public String index(Model model){
		return "help/index";
	}
}
