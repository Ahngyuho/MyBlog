package com.blog.response;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
// 값이 채워져 있는 것만 json 형태로 보이도록
//클라이언트 측에서 null 이나 비어있는 값은 빼서 달라고 할 수 있음
//근데 개인적으로 선호 X
//@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ErrorResponse {
    private final String code;
    private final String message;
    private final Map<String,String> validation;

    @Builder
    public ErrorResponse(String code, String message,Map<String,String> validation) {
        this.code = code;
        this.message = message;
        this.validation = validation;
    }

    public void addValidation(String fieldName, String errorMessage){
        this.validation.put(fieldName,errorMessage);
    }
}
