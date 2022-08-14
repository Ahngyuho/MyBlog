package com.blog.request;

import com.blog.exception.InvalidRequest;
import lombok.*;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
@ToString
public class PostCreate {
    //  implementation 'org.springframework.boot:spring-boot-starter-validation'
    //spring boot 버전이 올라가면서 web 에 원래 이게 들어가있었는데 빠져버림
    //검증 추가
    @NotBlank(message = "타이블을 입력하세요.")
    public String title;

    @NotBlank(message = "내용을 입력하세요.")
    public String content;

    //이걸 사용하는 이유는
    //이것도 꼭 공부를 해야한다
    //생성자 위에 달아주는 방법을 추천
    //클래스 위에 달아도 되나 문제가 생길 경우 다분
    //이건 꼭 생성자를 필요로 함 기본 생성자 안 됨
    //빌더의 장점 파라미터가 굉장히 많아 진다면 밖에서 값을 넘겨주기 힘들어짐...
    //근데 빌더로 인해 값 생성이 유연해진다
    //필요한 값만 받을 수 있음 만약 값 하나만 받고 싶다면 그냥 생성자라면 오버로딩을 통해 해야됨
    //오버로딩 가능한 조건 찾아보기
    //객체의 불변성  가장 중요한 점
    @Builder
    public PostCreate(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void validate() {

        if(title.contains("바보")) {
            throw new InvalidRequest("title","제목에 바보를 포함할 수 없습니다.");
        }
    }
}


