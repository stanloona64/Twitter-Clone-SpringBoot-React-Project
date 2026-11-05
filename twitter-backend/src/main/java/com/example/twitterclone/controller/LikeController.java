package com.example.twitterclone.controller;

import com.example.twitterclone.entity.Like;
import com.example.twitterclone.exception.ApiException;
import com.example.twitterclone.repository.LikeRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3200")
public class LikeController {
    private final LikeRepository likeRepository;

    public LikeController(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    @PostMapping("/like")
    public ResponseEntity<Like> createLike(@Valid @RequestBody Like like) {
        if (likeRepository.findByTweetIdAndUserId(like.getTweetId(), like.getUserId()).isPresent()) {
            throw new ApiException("Bu tweeti zaten beğendiniz.", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(likeRepository.save(like));
    }
}
