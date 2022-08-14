package com.blog.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
public class PostEdit {

    @NotBlank(message = "타이틀을 입력하세요.")
    private String title;

    @NotBlank(message = "콘텐츠를 입력해주세요.")
    private String content;

    @Builder
    public PostEdit(@JsonProperty("title") String title,@JsonProperty("content") String content){
        this.title = title;
        this.content = content;
    }
}
