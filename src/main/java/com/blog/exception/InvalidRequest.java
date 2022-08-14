package com.blog.exception;


import lombok.Getter;

//잘못된 요청을 했을 경우 발생하는 예외

public class InvalidRequest extends BlogException{

    private static final String MESSAGE = "잘못된 요청입니다.";

//    private String fieldName;
//    private String message;

    public InvalidRequest(){
        super(MESSAGE);
    }

    public InvalidRequest(String fieldName,String message){
        super(MESSAGE);
        addValidation(fieldName,message);
    }

    @Override
    public int getStatusCode(){
        return 400;
    }
}


