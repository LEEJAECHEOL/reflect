package com.cos.reflect.filter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;

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
import com.cos.reflect.controller.dto.LoginDto;

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
//		System.out.println("endPoint : " + endPoint);
		
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
					// 1. 파라미터를 먼저 확인 (파라미터를 가지고있는 경우와 없는 경우가 있음.)
					Parameter[] params = method.getParameters();
					String path = null;
					if(params.length != 0) {
//						System.out.println("params[0] : " + params[0]);
//						System.out.println("params[0].getName() : " + params[0].getName());
//						System.out.println("params[0].getType() : " + params[0].getType());
						
						// /user/login -> LoginDto, /user/join -> JoinDto
						Object dtoInstance = params[0].getType().newInstance(); 
//						String username = request.getParameter("username");
//						String password = request.getParameter("password");
//						System.out.println("username : " + username);
//						System.out.println("password : " + password);
						
						Enumeration<String> keys = request.getParameterNames();	// username,password

						// getParameterName()는 Enumeration<String>타입을 리턴한다.
						// key값을 변형 username -> setUsername, password -> setPassword
						while(keys.hasMoreElements()) {
//							hasMoreElements() // Enumeration의 요소가 있으면 true, 아니면 false 반환
//							nextElement();	// Enumeration 내의 다음 요소를 반환한다. 
							System.out.println(keys.nextElement());
						}
						path = "/";
					} else {
						path = (String)method.invoke(userController);
					}
					// request를 들고가는 페이지도 있고 아닌 페이지도 있다. 그래서 RequestDispatcher를 사용한다.
					// sendRequest 는 필터를 다시 탄다.
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
