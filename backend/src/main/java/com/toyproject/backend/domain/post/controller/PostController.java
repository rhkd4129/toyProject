package com.toyproject.backend.domain.post.controller;

import com.toyproject.backend.domain.post.dto.PostRequestDto;
import com.toyproject.backend.domain.post.dto.PostResponseDto;
import com.toyproject.backend.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 목록 조회
    @GetMapping("/list")
    public ResponseEntity<List<PostResponseDto>> getPosts() {
        return new ResponseEntity<>(postService.getPosts(), HttpStatus.OK);
    }

//    // 단건 조회
    @GetMapping("/list/{id}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable(name = "id") Long id) {
        return new ResponseEntity<>(postService.getPost(id), HttpStatus.OK);
    }

    // 작성
    @PostMapping("/create")
    public ResponseEntity<Void> createPost(@RequestBody PostRequestDto dto) {
        log.info("==== createPost 진입 ==== ");
        System.out.println(dto.getTitle());
        postService.createPost(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
//
//    // 수정
//    @PutMapping("/{id}")
//    public ResponseEntity<Void> updatePost(
//            @PathVariable(name = "id") Long id,
//            @RequestBody PostRequestDto dto
//    ) {
//        postService.updatePost(id, dto);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    // 삭제
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deletePost(@PathVariable(name = "id") Long id) {
//        postService.deletePost(id);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
}
