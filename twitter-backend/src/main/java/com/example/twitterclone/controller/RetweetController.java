package com.example.twitterclone.controller;

import com.example.twitterclone.entity.Retweet;
import com.example.twitterclone.exception.ApiException;
import com.example.twitterclone.repository.RetweetRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3200")
public class RetweetController {
    private final RetweetRepository retweetRepository;

    public RetweetController(RetweetRepository retweetRepository) {
        this.retweetRepository = retweetRepository;
    }

    @PostMapping("/retweet")
    public ResponseEntity<Retweet> createRetweet(@Valid @RequestBody Retweet retweet) {
        if (retweetRepository.findByTweetIdAndUserId(retweet.getTweetId(), retweet.getUserId()).isPresent()) {
            throw new ApiException("Bu tweeti zaten retweetlediniz.", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(retweetRepository.save(retweet));
    }

    @DeleteMapping("/retweet/{id}")
    public ResponseEntity<Void> deleteRetweet(@PathVariable Integer id) {
        Retweet retweet = retweetRepository.findById(id)
                .orElseThrow(() -> new ApiException("Retweet bulunamadı.", HttpStatus.NOT_FOUND));
        retweetRepository.delete(retweet);
        return ResponseEntity.ok().build();
    }
}
