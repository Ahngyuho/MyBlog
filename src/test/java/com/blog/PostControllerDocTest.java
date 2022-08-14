package com.blog;

import com.blog.domain.Post;
import com.blog.repository.PostRepository;
import com.blog.request.PostCreate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//API 문서 생성
// GET /post/{postId} -> 단건 조회
// POST /posts -> 게시글 등록

// 클라이언트 입장에서는 어떤 API 가 있는지 모름
//그래서 만들어둔 API 를 문서화 해서 넘겨줘야 함
//라우팅 규칙들을 잘 파싱 해서 자동 문서화 해주는 툴들이 많음
//그중  Spring RestDocs 로 만들것임
//장점 : 1. 운영 코드에 영향을 주지 않음
// 애너테이션 xml 로 문서를 만들어내게 되면 영향을 줄 수 있음
// 이건 test case 만을 통해서 문서를 생성할 수 있음
// 운영 코드에 비즈니스에 상관없는 코드가 들어가는 게 찝찝함
// 2. 변경된 기능에 대해 최신 문서 유지가 어느정도 가능함
//어떤 툴은 코드 수정 -> 문서를 수정 X 코드(기능) <-> 문서 => 문서의 신회성 하락
// Test 케이스를 실행 -> 통과 -> 문서 자동 생성

@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.MyBlog.com",uriPort = 443)
@SpringBootTest
@AutoConfigureMockMvc // MockMvc 주입받기 위함
@ExtendWith(RestDocumentationExtension.class)
public class PostControllerDocTest {


    @Autowired ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired PostRepository postRepository;


    @Test
    @DisplayName("글 단건 조회 테스트")
    void test1() throws Exception {
        //given
        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();

        postRepository.save(post);

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/posts/{postId}",1L).accept(APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andDo(document("post-inqeury",
                        pathParameters(RequestDocumentation.parameterWithName("postId").description("게시글 ID")),
                        PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("id").description("게시글 ID"),
                                PayloadDocumentation.fieldWithPath("title").description("title"),
                                PayloadDocumentation.fieldWithPath("content").description("content")
                        )
                        ));
    }

    @Test
    @DisplayName("글 등록")
    void test2() throws Exception {
        //given
        PostCreate postCreate = PostCreate.builder()
                .title("title")
                .content("content")
                .build();

        String json = objectMapper.writeValueAsString(postCreate);

        this.mockMvc.perform(RestDocumentationRequestBuilders.post("/posts")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(json))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andDo(document("post-create",
                        PayloadDocumentation.requestFields(
                                PayloadDocumentation.fieldWithPath("title").description("title").attributes(Attributes.key("constraint").value("input title")),
                                PayloadDocumentation.fieldWithPath("content").description("content").optional()
                        )
                ));
    }
}

