package com.cos.reflect.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 타겟을 설정
@Target({ElementType.METHOD})
//실행 시점을 정해주어야 한다.
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
	String value();
}
