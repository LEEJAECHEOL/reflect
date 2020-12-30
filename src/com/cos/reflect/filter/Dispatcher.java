package com.cos.reflect.filter;

import java.io.IOException;
import java.io.PrintWriter;
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

		boolean isMatching = false;
		for (Method method : methods) {	// 4바퀴 돈다 (join, login, user, hello) 리플렉션한 메서드 개수만큼 순회함
			Annotation annotation = method.getDeclaredAnnotation(RequestMapping.class);
			// 다운캐스팅을 하는 이유 : 사용하는 메서드가 다르다. (.value()함수를 사용하기 위해)
			RequestMapping requestMapping = (RequestMapping) annotation;
//			System.out.println(requestMapping.value());
			if(requestMapping.value().equals(endPoint)) {
				isMatching = true;
				try {
					// 1. 파라미터를 먼저 확인 (파라미터를 가지고있는 경우와 없는 경우가 있음.)
					Parameter[] params = method.getParameters();
					String path = null;
					if(params.length != 0) {
//						System.out.println("params[0] : " + params[0]);
//						System.out.println("params[0].getName() : " + params[0].getName());
//						System.out.println("params[0].getType() : " + params[0].getType());
						
						// /user/login -> LoginDto, /user/join -> JoinDto
						// 해당 dtoInstance를 리플렉션 해서 set함수 호출 (파라미터에 근거하여)
						Object dtoInstance = params[0].getType().newInstance(); 
						
//						String username = request.getParameter("username");
//						String password = request.getParameter("password");
//						System.out.println("username : " + username);
//						System.out.println("password : " + password);
						
						setData(dtoInstance, request); // 인스턴스에 파라메터 값 추가하기 (레퍼런스를 넘겨서 리턴 안받아도 됨)
						path = (String)method.invoke(userController, dtoInstance);

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
				break;// 더 이상 메서드를 리플렉션 할 필요 없어서 빠져나감.
			}
		}
		if(isMatching == false) {
			response.setContentType("text/html; charset=utf-8");
			PrintWriter out = response.getWriter();
			out.println("잘못된 주소 요청입니다. 404 에러");
			out.flush();
		}

	}
	private <T> void setData(T instance, HttpServletRequest request) {
		Enumeration<String> keys = request.getParameterNames();	// username,password
		// getParameterName()는 Enumeration<String>타입을 리턴한다.
		// key값을 변형 username -> setUsername, password -> setPassword
		while(keys.hasMoreElements()) {
//			hasMoreElements() // Enumeration의 요소가 있으면 true, 아니면 false 반환
//			nextElement();	// Enumeration 내의 다음 요소를 반환한다.
			String key = keys.nextElement();
			String methodKey = keyToMethodKey(key);
			
			Method[] methods = instance.getClass().getDeclaredMethods();
			
			for (Method method : methods) {
				if(method.getName().equals(methodKey)) {
					try {
						method.invoke(instance, request.getParameter(key));	//String
					} catch (Exception e) {
						try {
							int value = Integer.parseInt(request.getParameter(key));
							method.invoke(instance, value);	//int
						} catch (Exception e2) {
							e2.printStackTrace();
						}
						System.out.println("신경쓸 필요없는 별거 아닌 int 파싱 문제");
					}
				}
			}
		}
	}
	private String keyToMethodKey(String key) {
      String firstKey = "set";
      String upperKey = key.substring(0,1).toUpperCase();
      String remainKey = key.substring(1);
      
      return firstKey + upperKey + remainKey;
   }
}
