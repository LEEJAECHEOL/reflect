package com.cos.reflect.controller;

import com.cos.reflect.anno.RequestMapping;
import com.cos.reflect.controller.dto.JoinDto;
import com.cos.reflect.controller.dto.LoginDto;

public class UserController {

	// 주석 힌트를 보는게 더 가독성이 좋다.
	// RequestMapping에는 value하나이기때문에 value를 생략하고 /join으로 적을 수 있다.
	@RequestMapping(value="/user/join")
	public String join(JoinDto joinDto) {	// username, password, email
		System.out.println("join() 함수 호출됨.");
		return "/";
	}

	@RequestMapping("/user/login")
	public String login(LoginDto loginDto) {	// username, password
		System.out.println("login() 함수 호출됨.");
		return "/";
	}

	@RequestMapping("/user")
	public String user() {
		System.out.println("user() 함수 호출됨.");
		return "/";
	}

	@RequestMapping("/hello")
	public String hello() {
		System.out.println("hello() 함수 호출됨.");
		return "/";
	}
}
