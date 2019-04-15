package com.qxs.generator.web.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * 登录退出控制器
 * @author qixingshen
 * @date 2018-06-19
 * **/
@Controller
public class LoginController{
	
	/**
	 * 登录
	 * **/
	@GetMapping({"/login", "/logout"})
	public String login(){
		//如果是登陆状态则重定向到index
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		
		//已登录
		if(authentication != null && authentication instanceof UsernamePasswordAuthenticationToken) {
			return "redirect:/index";
		}
		
		return "login";
	}
}
