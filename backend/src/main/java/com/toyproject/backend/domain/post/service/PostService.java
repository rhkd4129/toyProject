package com.toyproject.backend.domain.post.service;

import com.toyproject.backend.domain.post.dto.PostRequestDto;
import com.toyproject.backend.domain.post.dto.PostResponseDto;
import com.toyproject.backend.domain.post.entity.Post;
import com.toyproject.backend.domain.post.repository.PostRepository;
import com.toyproject.backend.error.CommonException;
import com.toyproject.backend.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PostService {

    private final PostRepository postRepository;



    // 목록 조회
    public List<PostResponseDto> getPosts() {
        return postRepository.listPost(0, 10)
                .stream()
                .map(PostResponseDto::new)
                .collect(Collectors.toList());
    }

    // 단건 조회
    public PostResponseDto getPost(Long id) {
        Post post = postRepository.selectPost(id).orElseThrow(() -> new CommonException(ErrorCode.BOARD_N0_SUCH));
        return new PostResponseDto(post);
    }

    // 작성
    @Transactional
    public void createPost(PostRequestDto dto) {
        log.info(dto.getPostType());

        Post post = Post.createPost(
                dto.getTitle(),
                dto.getContent(),
                postRepository.selectPostType(dto.getPostType()).orElseThrow(() -> new CommonException(ErrorCode.POST_TYPE_NOT_FOUND))
        );
        log.info(post.getPostType().toString());
        postRepository.createPost(post);
    }

    // 수정
    @Transactional
    public void updatePost(Long id, PostRequestDto dto) {
        Post post = postRepository.selectPost(id).orElseThrow(() -> new CommonException(ErrorCode.BOARD_N0_SUCH));
        post.updatePost(dto.getTitle(), dto.getContent());
    }
}
