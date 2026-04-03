package com.toyproject.backend.domain.post.service;

import com.toyproject.backend.domain.post.dto.PostRequestDto;
import com.toyproject.backend.domain.post.dto.PostResponseDto;
import com.toyproject.backend.domain.post.entity.Post;
import com.toyproject.backend.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
        Post post = postRepository.selectPost(id)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다. id=" + id));
        return new PostResponseDto(post);
    }

    // 작성
    @Transactional
    public void createPost(PostRequestDto dto) {
        Post post = Post.createPost(
                dto.getTitle(),
                dto.getContent(),
                postRepository.selectPostType(dto.getPostType())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물 유형입니다."))
        );
        postRepository.createPost(post);
    }

    // 수정
    @Transactional
    public void updatePost(Long id, PostRequestDto dto) {
        Post post = postRepository.selectPost(id)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다. id=" + id));
        post.updatePost(dto.getTitle(), dto.getContent());
    }

    // 삭제 (soft delete)
    @Transactional
    public void deletePost(Long id) {
        postRepository.removePost(id);
    }
}
