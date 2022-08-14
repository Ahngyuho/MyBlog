package com.blog.repository;

import com.blog.domain.Post;
import com.blog.request.PostSearch;

import java.util.List;

//queryDsl 을 통한 페이징 처리를 위함
public interface PostRepositoryCustom {

    List<Post> getList(PostSearch postSearch);
}
