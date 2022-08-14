package com.blog.response;

import com.blog.domain.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *  서비스 정책에 맞는 응답 클래스 생성
 */
@Getter
public class PostResponse {
    private final Long id;
    private final String title;
    private final String content;


    @Builder
    public PostResponse(Long id, String title, String content) {
        this.id = id;
        //이렇게 서비스 계층에 부합되도록...
        this.title = title.substring(0,Math.min(title.length(),10));
        this.content = content;
    }

    //생성자 오버로딩
    public PostResponse(Post post){
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
    }

    //from 은 엔티티를 dto로 변환
    public static PostResponse from(Post post){
        return PostResponse.builder()
                .content(post.getContent())
                .id(post.getId())
                .title(post.getTitle())
                .build();
    }
}
