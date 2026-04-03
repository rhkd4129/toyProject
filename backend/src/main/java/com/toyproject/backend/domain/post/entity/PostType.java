package com.toyproject.backend.domain.post.entity;
import com.toyproject.backend.domain.post.Enum.PostTypeEnum;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name="POST_TYPE")
public class PostType {

    @Id
    @SequenceGenerator(
            name = "POST_TYPE_SEQ",
            sequenceName = "POST_TYPE_SEQ",
            initialValue = 1,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy= GenerationType.SEQUENCE,
            generator = "POST_TYPE_SEQ")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private PostTypeEnum postType;

    @OneToMany(mappedBy = "postType",fetch = FetchType.LAZY)
    private List<Post> postList = new ArrayList<>();
}
