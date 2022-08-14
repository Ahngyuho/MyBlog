package com.blog.controller;

import com.blog.domain.Post;
import com.blog.repository.PostRepository;
import com.blog.request.PostCreate;
import com.blog.request.PostEdit;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc // springboottest 할 때 MockMvc 에 빈 주입이 안 됨
@SpringBootTest
class PostControllerTest {

    @Autowired ObjectMapper objectMapper;

    @Autowired private MockMvc mvc;

    @Autowired private PostRepository postRepository;

    @BeforeEach
    void clean(){
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("/posts 요청시 Hello World를 출력한다.")
    void test1() throws Exception{
        //given
        PostCreate request = PostCreate.builder()
                //이렇게 해주면 이 두개가 순서가 바뀌어도 지정을 해서 넣어준거기 때문에
                //상관이 없다
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        //json 형태로 가공
        //꼭 공부해야 한다.
        //json = {"title":"제목입니다.","content":"내용입니다."}
        String json = objectMapper.writeValueAsString(request);

        System.out.println("json = " + json);

        mvc.perform(post("/posts")
                        //이런식으로 contentType 설정가능
                        //json 형식으로 받는 것이 좋음
                        //쿼리 파라미터 식으로 받는 것은 한계가 있다
                        //너무 길어짐 key=value&...&...
                        .contentType(APPLICATION_JSON)
                                .content(json)
//                        .param("title","글 제목입니다.")
//                        .param("content","글 내용입니다.")
                    ) //application/json 보통 content-type 이 이거임
                .andExpect(content().string(""))
                .andExpect(status().isOk())
                //웹 요청에 대한 summary 를 적어줌
                .andDo(print());
    }

    @Test
    @DisplayName("/posts 요청시 title 값은 필수")
    void test2() throws Exception{
        PostCreate request = PostCreate.builder()
                .content("content")
                .build();
        String json = objectMapper.writeValueAsString(request);
        mvc.perform(post("/posts")
                                .contentType(APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("타이블을 입력하세요."))
                .andExpect(status().isBadRequest())
                //웹 요청에 대한 summary 를 적어줌
                .andDo(print());
    }

    @Test
    @DisplayName("/posts 요청시 db에 값 저장")
    void test3() throws Exception{
        //given
        PostCreate request = PostCreate.builder()
                .content("내용")
                .title("제목")
                .build();
        String json = objectMapper.writeValueAsString(request);
        //when
        mvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                //웹 요청에 대한 summary 를 적어줌
                .andDo(print());
        //then
        assertEquals(1L,postRepository.count());

        Post post = postRepository.findAll().get(0);

        assertEquals("제목",post.getTitle());
        assertEquals("내용",post.getContent());
    }

    @Test
    @DisplayName("글 1개 조회")
    void test4() throws Exception {
        //Given
        Post request = Post.builder()
                .content("bar")
                .title("foo")
                .build();
        postRepository.save(request);

        //When & Then
        mvc.perform(get("/posts/{postId}", request.getId())
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                //응답으로 내려온 json 검증
                .andExpect(jsonPath("$.id").value(request.getId()))
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.content").value(request.getContent()))
                .andDo(print());
    }


    @Test
    @DisplayName("글 여러개 조회")
    void test5() throws Exception {
        //Given
        List<Post> requestPosts = IntStream.range(0,20)
                .mapToObj(i ->
                        Post.builder()
                                .title("제목 - " + i)
                                .content("내용 - " + i)
                                .build()
                ).collect(Collectors.toList());

        postRepository.saveAll(requestPosts);

        //expected
        mvc.perform(get("/posts?page=0&size=10")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()",Matchers.is(10)))
                .andExpect(jsonPath("$.[0].title").value("제목 - 19"))
                .andExpect(jsonPath("$.[0].content").value("내용 - 19"))
                .andDo(print());
    }

    @Test
    @DisplayName("글 제목 수정")
    void test7() throws Exception {
        //Given
        Post request = Post.builder()
                .content("bar")
                .title("foo")
                .build();

        postRepository.save(request);

        PostEdit postEdit = PostEdit.builder()
                .title("title")
                .content("content")
                .build();

        //expected
        mvc.perform(patch("/posts/{postId}",request.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 삭제")
    void test8() throws Exception{
        //Given
        Post request = Post.builder()
                .content("bar")
                .title("foo")
                .build();

        postRepository.save(request);

        mvc.perform(delete("/posts/{postId}",request.getId())
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회")
    void test9() throws Exception{
        mvc.perform(delete("/posts/{postId}",1L)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 수정")
    void test10() throws Exception{
        PostEdit postEdit = PostEdit.builder()
                .content("content")
                .title("title")
                .build();
        String json = objectMapper.writeValueAsString(postEdit);
        mvc.perform(patch("/posts/{postId}",1L)
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 작성시 제목에 '바보'는 포함될 수 없다.")
    void test11() throws Exception{
        PostCreate postCreate = PostCreate.builder()
                .title("바보")
                .content("content")
                .build();
        String json = objectMapper.writeValueAsString(postCreate);
        mvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andDo(print());

        //Response
        //Body = {"code":"400","message":"제목에 바보를 포함할 수 없습니다.","validation":{"title":"제목에 바보를 포함할 수 없습니다."}}
    }
}




