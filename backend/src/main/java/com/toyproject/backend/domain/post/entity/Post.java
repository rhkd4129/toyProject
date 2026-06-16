package com.toyproject.backend.domain.post.entity;
import com.toyproject.backend.common.BaseEntity;
import com.toyproject.backend.domain.post.enums.PostTypeEnum;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name="POST")
public class Post extends BaseEntity {

    @Id
    @SequenceGenerator(
            name = "POST_SEQ",
            sequenceName = "POST_SEQ",
            initialValue = 1,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy= GenerationType.SEQUENCE,
            generator = "POST_SEQ")


    private Long id;
    private String title;
    @Column(columnDefinition = "CLOB")/// 길어지면 오류가발생하기 떄문에 지정
    private String content;

    private Integer likes;
    private Integer dislikes;

//    @ManyToOne(fetch =FetchType.LAZY)
//    @JoinColumn(name="posttype_id")

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostTypeEnum postType;  // Post 테이블에 바로 저장

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id")
//    private Member member;
//
//
//
//    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY ,cascade = CascadeType.ALL)
//    List<Reply> replyList = new ArrayList<>();


//    public void setMember(Member member){
//        this.member = member;
//        member.getPostList().add(this);
//
//    }


//    public void softDelete(){
//        changeUseStatus(UseStatus.N);
//        this.replyList.forEach(Reply::softDelete);
//    }


// Member member
    public static Post createPost( String title,String content,PostTypeEnum postTypeEnum ){
        Post post = new Post();
        post.title  = title;
        post.content = content;
        post.likes = 0;
        post.dislikes=0;
//        post.setMember(member);
        post.postType = postTypeEnum;
        return post;
    }



    public void updatePost(String title,String content){
        this.title = title;
        this.content = content;
    }
}


// 지연 로딩(Lazy Loading) 이슈: replyList를 FetchType.LAZY로 정의
// this.replyList.forEach(Reply::softDelete)를 호출하면 모든 답글의 지연 로딩이 트리거 됨.
// 게시물에 답글이 많은 경우, softDelete()를 호출할 때 세션이 닫혀 있으면 성능 문제나 LazyInitializationException이 발생할 수 있습니다.
//    캐스케이딩 vs 수동 삭제: replyList 관계에 이미 cascade = CascadeType.ALL을 설정했으므로, JPA는 자동으로 persist, merge, remove와 같은 연쇄 작업을 처리합니다.
//    하지만 소프트 삭제의 경우 수동으로 답글을 순회하고 있으며, 이는 캐스케이드 메커니즘에 의존하는 것과는 다른 접근 방식.
// 관련된 모든 답글을 일괄 업데이트하는 쿼리 사용:??
