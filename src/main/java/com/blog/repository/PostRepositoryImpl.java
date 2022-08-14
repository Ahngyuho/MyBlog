package com.blog.repository;

import com.blog.domain.Post;
import com.blog.domain.QPost;
import com.blog.request.PostSearch;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.blog.domain.QPost.post;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{

    //이건 수동 빈 등록을 해줘야 함
    //config/QueryDslConfig 에 등록해둠
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> getList(PostSearch postSearch) {

        return jpaQueryFactory.selectFrom(post)
                //10개만 가져옴
                .limit(postSearch.getSize())
                .offset(postSearch.getOffice())
                //정렬
                .orderBy(post.id.desc())
                .fetch();
    }
}
