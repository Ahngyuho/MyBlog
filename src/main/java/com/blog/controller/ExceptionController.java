package com.blog.controller;

import com.blog.exception.BlogException;
import com.blog.exception.InvalidRequest;
import com.blog.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;


//오류가 발생되면 json 형태로 내려줌
@ControllerAdvice
@Slf4j
public class ExceptionController {

    //스프링에서 제공하는 예외는 이렇게 따로 만들어 놓고
    //내가 만든 예외 클래스는 최상위 예외 클래스를 상속하도록 한 후
    //컨트롤러에서 처리
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ErrorResponse invalidRequestHandler(MethodArgumentNotValidException e){
        //이것도 builder 써주자
        //code 랑 message 를 json 형태로 응답을 넣어주기로 함
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("400")
                .message("잘못된 요청입니다.")
                //여기 코드 좀 뭔가 이상함
                .validation(new HashMap<>())
                .build();

        System.out.println("errorResponse = " + errorResponse.getCode());

        for(FieldError fieldError: e.getFieldErrors()){
            errorResponse.addValidation(fieldError.getField(),fieldError.getDefaultMessage());
        }

        return errorResponse;
    }

    @ResponseBody
    @ExceptionHandler(BlogException.class)
    public ResponseEntity<ErrorResponse> blogException(BlogException e){

        int statusCode = e.getStatusCode();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(String.valueOf(statusCode))
                .message(e.getMessage())
                .validation(e.getValidation())
                .build();

        //동적으로 상태 지정하기 위함
        //이렇게 ResponseEntity 로 감싸준 이유는 @ResponseStatus 를 붙이게 되면
        //동적으로 http 상태를 지정할 수 없었다
        //그래서 이걸 빼줬는데 이걸 뺴주게 되면 default 가 200이 된다
        //그래서 이 ResponseEntity 로 한번 감싸서 상태를 지정해준 후 리턴해주면
        //상태를 동적으로 넣어줄 수 있다.
        ResponseEntity<ErrorResponse> response = ResponseEntity.status(statusCode)
                .body(errorResponse);

        return response;
    }

//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ResponseBody
//    @ExceptionHandler(InvalidRequest.class)
//    public ErrorResponse invalidRequestHandler2(InvalidRequest e){
//        ErrorResponse errorResponse = ErrorResponse.builder()
//                .code("400")
//                .message(e.getMessage())
//                .build();
//
//        return errorResponse;
//    }

}
