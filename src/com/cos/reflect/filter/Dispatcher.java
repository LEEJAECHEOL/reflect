package com.cos.reflect.filter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cos.reflect.anno.RequestMapping;
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
//		if(endPoint.equals("/join")) {
//			userController.join();
//		} else if(endPoint.equals("/login")) {
//			userController.login();
//		} else if(endPoint.equals("/user")) {
//			userController.user();
//		}
		
		// 리플렉션 -> 메서드를 런타임 시점에서 찾아내서 실행
		Method[] methods = userController.getClass().getDeclaredMethods();
		// getMethods() : 모든 메서드들(상속된 것, 선언된 것)을 다 가져온다.
		// getDeclaredMethods() : 그파일에 선언된 메서드들을 다 가져온다.
		
		for (Method method : methods) {	// 4바퀴 돈다 (join, login, user, hello)
			Annotation annotation = method.getDeclaredAnnotation(RequestMapping.class);
			// 다운캐스팅을 하는 이유 : 사용하는 메서드가 다르다.
			RequestMapping requestMapping = (RequestMapping) annotation;
//			System.out.println(requestMapping.value());
			if(requestMapping.value().equals(endPoint)) {
				try {
					String path = (String)method.invoke(userController);
					
					// request를 들고가는 페이지도 있고 아닌 페이지도 있다. 그래서 RequestDispatcher를 사용한다.
					RequestDispatcher dis = request.getRequestDispatcher(path); //필터를 안타기 때문에 실행이 된다.
					dis.forward(request, response);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}

	}

}
