package com.blog.service;

import com.blog.domain.Post;
import com.blog.exception.PostNotFound;
import com.blog.repository.PostRepository;
import com.blog.request.PostCreate;
import com.blog.request.PostEdit;
import com.blog.request.PostSearch;
import com.blog.response.PostResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@SpringBootTest
class PostServiceTest {

    @Autowired PostService postService;
    @Autowired PostRepository postRepository;

    @BeforeEach
    void clean(){
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("글 작성")
    void test1(){
        //Given
        PostCreate request = PostCreate.builder()
                .content("내용입니다.")
                .title("제목입니다.")
                .build();

        //When
        postService.write(request);

        //Then
        assertEquals(1L,postRepository.count());
    }

    @Test
    @DisplayName("글 1개 조회")
    void test2(){
        //Given
        Post post = Post.builder()
                .content("bar")
                .title("foo")
                .build();

        postRepository.save(post);

        Long postId = post.getId();

        //When
        //java.lang.IllegalArgumentException: 존재하지 않는 글입니다.
        //지정해준 오류 잘 나옴
        PostResponse result = postService.get(postId);

        //Then
        assertNotNull(result);
        assertEquals(1L,postRepository.count());
    }

    @Test
    @DisplayName("글 여러개 조회")
    void test3(){
        //Given
        List<Post> requestPosts = IntStream.range(0,20)
                .mapToObj(i ->
                        Post.builder()
                                .title("제목 - " + i)
                                .content("내용 - " + i)
                                .build()
                ).collect(Collectors.toList());

        postRepository.saveAll(requestPosts);

        Pageable pageable = PageRequest.of(0,5, Sort.by(DESC,"id"));
        PostSearch postSearch = PostSearch.builder()
                .page(1)
                .size(10)
                .build();
        //When
        List<PostResponse> posts = postService.getList(postSearch);
        //expected
        //Assertions.assertNotNull(posts);
        assertEquals(10L,posts.size());
        assertEquals("제목 - 19",posts.get(0).getTitle());
    }

    @Test
    @DisplayName("글 1페이지 조회")
    void test4(){
        //Given
        List<Post> requestPosts = IntStream.range(1,31)
                .mapToObj(i ->
                     Post.builder()
                        .title("제목 - " + i)
                        .content("내용 - " + i)
                        .build()
                ).collect(Collectors.toList());

        postRepository.saveAll(requestPosts);

        PostSearch postSearch = PostSearch.builder()
                .page(1)
                .size(10)
                .build();
        //When
        List<PostResponse> posts = postService.getList(postSearch);

        //Then
        assertEquals(10L,posts.size());
        assertEquals("제목 - 30",posts.get(0).getTitle());
        assertEquals("제목 - 26",posts.get(4).getTitle());
    }

    @Test
    @DisplayName("글 제목 수정")
    void test5(){
        //Given
        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();

        postRepository.save(post);

        //When
        PostEdit postEdit = PostEdit.builder()
                .title("edit_title")
                //이렇게 수정되지 않더라도 값을 넣어줘야 함
                //클라이언트 단에서 수정되지 않는 값들도 모두 넘겨받도록 하게 함
                .content("content")
                .build();

        postService.edit(post.getId(),postEdit);

        //Then
        Post changedPost = postRepository.findById(post.getId()).orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다." + +post.getId()));

        assertEquals("edit_title",changedPost.getTitle());
        assertEquals("content",changedPost.getContent());
    }

    @Test
    @DisplayName("글 내용 수정")
    void test6(){
        //Given
        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();

        postRepository.save(post);

        //When
        PostEdit postEdit = PostEdit.builder()
                .title("title")
                //이렇게 수정되지 않더라도 값을 넣어줘야 함
                //클라이언트 단에서 수정되지 않는 값들도 모두 넘겨받도록 하게 함
                .content("edit_content")
                .build();

        postService.edit(post.getId(),postEdit);

        //Then
        Post changedPost = postRepository.findById(post.getId()).orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다." + +post.getId()));

        assertEquals("title",changedPost.getTitle());
        assertEquals("edit_content",changedPost.getContent());
    }

    @Test
    @DisplayName("게시글 삭제")
    void test7(){
        //Given
        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();

        postRepository.save(post);

        //when
        postService.delete(post.getId());

        //then
        assertEquals(0,postRepository.count());
    }

    @Test
    @DisplayName("글 1개 조회 - 존재하지 않는 글")
    void test8(){
        //Given
        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();

        postRepository.save(post);

        //when

        //then
        assertThrows(PostNotFound.class,()-> {
            postService.get(post.getId() + 1L);
        });
    }

    @Test
    @DisplayName("글 1개 삭제 - 존재하지 않는 글")
    void test9(){
        //Given
        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();

        postRepository.save(post);

        //when

        //then
        assertThrows(PostNotFound.class,()-> {
            postService.delete(post.getId() + 1L);
        });
    }

    @Test
    @DisplayName("글 1개 수정 - 존재하지 않는 글")
    void test10(){
        //Given
        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();

        PostEdit postEdit = PostEdit.builder().build();

        postRepository.save(post);

        //when

        //then
        assertThrows(PostNotFound.class,()-> {
            postService.edit(post.getId() + 1L,postEdit);
        });
    }


}