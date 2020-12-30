package com.cos.reflect.controller;

import com.cos.reflect.anno.RequestMapping;

public class UserController {

	// 주석 힌트를 보는게 더 가독성이 좋다.
	// RequestMapping에는 value하나이기때문에 value를 생략하고 /join으로 적을 수 있다.
	@RequestMapping(value="/join")
	public String join() {
		System.out.println("join() 함수 호출됨.");
		return "/";
	}

	@RequestMapping("/login")
	public String login() {
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
