package com.cos.reflect.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cos.reflect.controller.UserController;

public class Dispatcher implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
//		System.out.println("디스패쳐 진입");
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)resp;
		
//		System.out.println("컨텍스트패스 : " + request.getContextPath());	// /reflect
//		System.out.println("식별자주소 : " + request.getRequestURI());		// /reflect/user
//		System.out.println("전체주소 : " + request.getRequestURL());		// http://localhost:8000/reflect/user
		
		// user파싱하기
		String endPoint = request.getRequestURI().replaceAll(request.getContextPath(), "");
		System.out.println("endPoint : " + endPoint);
		
		// 함수를 호출해 줄 때 마다 계속 추가를 해주어야함. 이렇게 계속 추가하지 않기 위해서 리플렉션을 사용한다.
		UserController userController = new UserController();
		if(endPoint.equals("/join")) {
			userController.join();
		} else if(endPoint.equals("/login")) {
			userController.login();
		} else if(endPoint.equals("/user")) {
			userController.user();
		}
		
	}

}
