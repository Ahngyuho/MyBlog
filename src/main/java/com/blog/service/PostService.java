package com.blog.service;

import com.blog.domain.Post;
import com.blog.domain.PostEditor;
import com.blog.exception.PostNotFound;
import com.blog.repository.PostRepository;
import com.blog.request.PostCreate;
import com.blog.request.PostEdit;
import com.blog.request.PostSearch;
import com.blog.response.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class PostService {
    private final PostRepository postRepository;

    public void write(PostCreate postCreate){

        Post post = Post.builder()
                .title(postCreate.title)
                .content(postCreate.getContent())
                .build();

        postRepository.save(post);
    }


    public PostResponse get(Long id) {
        Post post = postRepository.findById(id)
                //사용자 정의 Exception 생성
                .orElseThrow(PostNotFound::new);

        //from 은 엔티티를 dto 로 변환
        return PostResponse.from(post);
    }


    public List<PostResponse> getList(PostSearch postSearch) {
        //페이징은  PageRequest 라는 구현체 많이 사용

        return postRepository.getList(postSearch).stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
    }

    //게시글 수정
    @Transactional
    public void edit(Long id, PostEdit postEdit){
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFound::new);
        PostEditor.PostEditorBuilder postEditorBuilder = post.toEditor();

//        if(postEdit.getTitle() != null){
//            postEditorBuilder.title(postEdit.getTitle());
//        }
//
//        if(postEdit.getContent() != null){
//            postEditorBuilder.content(postEdit.getContent());
//        }
//
//        PostEditor postEditor = postEditorBuilder.build();

        PostEditor postEditor = postEditorBuilder
                .content(postEdit.getContent())
                .title(postEdit.getTitle())
                .build();


        post.edit(postEditor);
    }

    public void delete(Long id) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFound::new);

        postRepository.delete(post);
    }
}














