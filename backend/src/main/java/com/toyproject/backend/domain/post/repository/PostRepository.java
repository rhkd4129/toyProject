package com.toyproject.backend.domain.post.repository;


import com.toyproject.backend.domain.post.Enum.PostTypeEnum;
import com.toyproject.backend.domain.post.entity.Post;
import com.toyproject.backend.domain.post.entity.PostType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepository {

    private final EntityManager em;



    public void createPost(Post post){
        em.persist(post);
    }

    public List<Post> listPost(int offset, int limit){
        return  em.createQuery("select p from Post p" +
//                                " join fetch  p.member"  +
                                " join fetch p.postType" +
                                " where p.useYn = 'Y'"   +
                                " order by p.id DESC "
                        ,Post.class)
//                .setFirstResult(offset)
//                .setMaxResults(limit)
                .getResultList();
    }
//    public List<PostType> listPostType(){
//        return em.createQuery("select p_t from PostType p_t"
//                        , PostType.class)
//                        .getResultList();
//    }
//
//    public Optional<PostType> selectPostType(String type){
//        try{
//          PostTypeEnum postTypeEnum = PostTypeEnum.valueOf(type);
//
//          PostType postType =   em.createQuery("select p_t from PostType p_t" +
//                    " where p_t.postType =:type",PostType.class)
//                    .setParameter("type",postTypeEnum)
//                    .getSingleResult();
//            return Optional.of(postType);
//        }catch (NoResultException e){
//            return Optional.empty();
//        }
//    }

    public Optional<Post> selectPost(Long id) {
        try {
            Post post = em.createQuery("select p from Post p " +
//                            " join fetch p.member " +
                             "join fetch p.postType" +
                            " where p.id = :id"
                            , Post.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.of(post);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
//
//    public void removePost(Long id) {
//        em.createQuery("update Post p " +
//                        "set p.useYn = 'N' " +  // 끝에 공백 추가
//                        "where p.id = :id")     // where 앞에 공백 추가
//                .setParameter("id", id)
//                .executeUpdate();       // executeUpdate() 추가
//    }
//
//    public void softRemoveReplays(Long id){
//        em.createQuery("update Reply  r " +
//                "set r.useYn = 'N' "+
//                "where r.post.id =: id")
//                .setParameter("id",id)
//                .executeUpdate();
//    }
//    public List<Post> getMemberPostList(Long id){
//        return em.createQuery("select * " +
//                "from Photo p " +
//                "where p.id =: id",Post.class)
//                .setParameter("id",id)
//                .getResultList();
//    }
//


// .getSingleResult() 이것은 반드시 하나반환해야함 아니면 다 오류남
}
