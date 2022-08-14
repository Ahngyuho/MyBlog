package com.blog.controller;

import com.blog.exception.InvalidRequest;
import com.blog.request.PostCreate;
import com.blog.request.PostEdit;
import com.blog.request.PostSearch;
import com.blog.response.PostResponse;
import com.blog.service.PostService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
public class PostController {
    private final PostService postService;
    @PostMapping("/posts")
    public void post(@RequestBody @Valid PostCreate request) {
        //Post 요청은 200 201 을 내림
        //응답값을 json 형태로 내려달라고 할 때도 있음
        //client 에서 id 를 요청하기도 함 수신한 id를 글 조회 API 를 통해서 테이터를 수신받음
        //id를 내려주면 이걸 통해 글 조회 API 로 다시 서버에 요청 수정 삭제 같은거
        //응답이 필요 없을 수도 있음
        //Bad Case : 서버에서 반드시 이렇게 한다고 fix 하는 건 안됨
        //개발시 항상 예외 상황이 발생할 수 있으므로 유연하게 대처해야 함 -> 코드를 잘 짜야 함...
        //일단 여기서는 void 형태로 내려주자

        //이렇게 데이터를 꺼내와서 검증하는 방식은 좋지 않음
//        if(request.getTitle().contains("바보")){
//            //NotBlank 같은 걸로 처리하기 까다로운
//            //그런 예외를 만들어 주기위함임
//            throw new InvalidRequest();
//        }

        request.validate();

        postService.write(request);
    }

    /**
     * /posts -> 글전체 조회
     * /posts/{postId} -> 글 한개만 조회
     */

    @GetMapping("/posts/{postId}")
    public PostResponse get(@PathVariable Long postId){
        //이렇게 엔티티를 반환하지 말고
        //서비스 정책에 맞는 클래스를 만들어서 분리해낸 후 그걸 반환하자
        //return post;
        return postService.get(postId);
    }

    /**
     * 조회 API
     * 단건 조회 API (1개의 글 post 가져오는 기능)
     * 여러 개의 글 조회 API
     * 게시글 목록 보기 ...
     * 단순히 List<PostResponse> 같은 형태로 반환하면 글이 만약 백만개라면 모든 글을 조회하게 되기 때문에
     * 비용이 너무 많이 들 수 있음
     * DB -> 애플리케이션 서버로 전달하는 시간,트래픽 비용 등이 많이 발생할 수 있음
     * 그래서 글 전체를 조회하는 경우는 없음
     * 페이징 처리를 해보자
     */

    @GetMapping("/posts")
    //Pageable 을 인자로 주는 것 보다는 나만의 페이징 클래스를 따로 만들어서 검증을 거친 후
    //postRepository 에 queryDsl 로 페이징 처리를 해주는 것을 추천
    public List<PostResponse> getList(@ModelAttribute PostSearch postSearch){
        return postService.getList(postSearch);
    }

    @PatchMapping("/posts/{postId}")
    public void edit(@PathVariable Long postId, @RequestBody @Valid PostEdit request){
        postService.edit(postId,request);
    }

    @DeleteMapping("/posts/{postId}")
    public void delete(@PathVariable Long postId){
        postService.delete(postId);
    }
}


//@RequestParam String title,@RequestParam String content
//@RequestParam Map<String, String> params
//좀 더 괜찮은 방식은 dto 를 만드는 것
//PostCreate request
//요청을 받는 dto
//@RequestBody 는 HTTP 메시지 컨버터가 적용됨
//컨트롤러 호출 전에 HttpMessageConverter 가 적용이 되어서 @RequestBody 라는 것이
//있다면 HttpBody 메시지를 꺼내서 변환 작용 후 객체든 문자열이든 만들어 넘겨줌
//데이터 검증도 해볼것인데 왜 데이터 검증을 할까?
//client 개발자가 실수로 값을 안 보낼수 있음
//client bug 어떤 버그로 인해 값이 누락될 수 있음
//외부에 의해 조작된 값이 들어올 수 있음
//DB에 값을 저장할 때 의도치 않은 오류 발생가능
//서버 개발자의 편안함을 위해 이 데이터 값이 검증된 상태라면...
//이 방식도 문제가 좀 있다. 검증 부분에서 버그가 발생할 여지가 높음
//개발자의 실수... 그리고 이런 작업들이 좀 지겨움
//응답값에 HashMap -> 응답 클래스를 만들어주는 게 좋음
//여러개의 에러처리가 힘들다 title 와 content 에 대한 에러처리 => for()
//세 번이상의 반복적인 작업은 피하자
//코드 && 개발에 관한 모든 것 => 뭔가 자동화 할만한게 없을까... 고민해봐야됨
// => ControllerAdvice
//        log.info("request = {} ",request.toString());
//
//        if(result.hasErrors()){
//            List<FieldError> fieldErrors = result.getFieldErrors();
//
//            Map<String,String> error = new HashMap<>();
//
//            for (FieldError fieldError : fieldErrors) {
//                error.put(fieldError.getField(),fieldError.getDefaultMessage());
//            }
//
////            return error;
//            return Map.of();
//        }
//        String title = request.getTitle();

//이런 검증 방법은 좋은 방법은 아님
//노가다 , 무언가 3번이상 반복작업을 할때 내가 뭔가 잘못하고 있는건 아닐지 의심한다.
//누락 가능성
//생각보다 검증해야랗 게 많음. 중요 {"title": ""} {"title" : "         "} 이렇게 의미 없는 것도 검증해야함
//뭔가 개발자 스럽지 않음
//        if(title.equals("") || title == null){
//            throw new Exception("타이틀 값이 없어요.");
//        }