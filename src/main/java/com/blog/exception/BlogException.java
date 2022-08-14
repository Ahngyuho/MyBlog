package com.blog.exception;

//ExceptionController 에 모든 각각 다른 종류의 Exception 클래스마다
//Handler 를 만들어주기에는 그  수가 너무 많아질 수 있다.
//각 예외 클래스들이 이 추상 클래스를 상속하도록 만들어줌

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

//이렇게 비즈니스 예외 처리 최상위 예외 클래스를 만들어 놓고
//컨트롤러에서 처리하면 된다

@Getter
public abstract class BlogException extends RuntimeException{

    public final Map<String,String> validation = new HashMap<>();

    public BlogException(String message) {
        super(message);
    }

    public BlogException(String message, Throwable cause) {
        super(message, cause);
    }

    //반드시 구현해야 하는 코드임을 abstract 로 알림
    public abstract int getStatusCode();

    public void addValidation(String fieldName,String message){
        validation.put(fieldName,message);
    }
}
