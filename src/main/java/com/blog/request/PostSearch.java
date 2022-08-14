package com.blog.request;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import static java.lang.Math.*;

@Setter
@Getter
//이렇게 클래스 단위에 붙여줘야 Default 가 작동
//내가 만든 생성자에 Builer 붙여주면 저 Default 가 작동안함
@Builder
public class PostSearch {
    private static final int MAX_SIZE = 2000;

    @Builder.Default
    private Integer page = 1;

    @Builder.Default
    private Integer size = 10;

//    @Builder
//    public PostSearch(Integer page, Integer size) {
//        this.page = page;
//        this.size = size;
//    }

    public long getOffice(){
        return (long) (max(1,page)- 1) * min(size,MAX_SIZE);
    }
}
