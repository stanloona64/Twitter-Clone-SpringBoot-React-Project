package com.example.twitterclone.controller;

import com.example.twitterclone.dto.TweetResponse;
import com.example.twitterclone.entity.Tweet;
import com.example.twitterclone.exception.ApiException;
import com.example.twitterclone.repository.CommentRepository;
import com.example.twitterclone.repository.LikeRepository;
import com.example.twitterclone.repository.RetweetRepository;
import com.example.twitterclone.repository.TweetRepository;
import com.example.twitterclone.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/tweet")
@CrossOrigin(origins = "http://localhost:3200")
public class TweetController {
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final RetweetRepository retweetRepository;
    private final CommentRepository commentRepository;

    public TweetController(TweetRepository tweetRepository,
                           UserRepository userRepository,
                           LikeRepository likeRepository,
                           RetweetRepository retweetRepository,
                           CommentRepository commentRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.retweetRepository = retweetRepository;
        this.commentRepository = commentRepository;
    }

    private TweetResponse toResponse(Tweet t) {
        TweetResponse.UserSummary user = userRepository.findById(t.getUserId())
                .map(u -> new TweetResponse.UserSummary(u.getId(), u.getUsername()))
                .orElse(null);
        return new TweetResponse(
                t.getId(), t.getContent(), t.getUserId(), t.getCreatedAt(), user,
                commentRepository.findByTweetId(t.getId()),
                likeRepository.findByTweetId(t.getId()),
                retweetRepository.findByTweetId(t.getId()));
    }

    @PostMapping
    public ResponseEntity<Tweet> createTweet(@Valid @RequestBody Tweet tweet) {
        Tweet saved = tweetRepository.save(tweet);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/findByUserId")
    public List<TweetResponse> findByUserId(@RequestParam Integer userId) {
        return tweetRepository.findByUserId(userId).stream()
                .sorted(Comparator.comparing(Tweet::getId).reversed())
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/findById")
    public ResponseEntity<Tweet> findById(@RequestParam Integer id) {
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new ApiException("Tweet bulunamadı, ID: " + id, HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(tweet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tweet> updateTweet(@PathVariable Integer id, @Valid @RequestBody Tweet newTweet) {
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new ApiException("Güncellenecek tweet bulunamadı, ID: " + id, HttpStatus.NOT_FOUND));
        tweet.setContent(newTweet.getContent());
        return ResponseEntity.ok(tweetRepository.save(tweet));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTweet(@PathVariable Integer id, @RequestParam Integer userId) {
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new ApiException("Silinecek tweet bulunamadı, ID: " + id, HttpStatus.NOT_FOUND));

        if (!tweet.getUserId().equals(userId)) {
            throw new ApiException("Sadece tweet sahibi ilgili tweeti silebilir!", HttpStatus.FORBIDDEN);
        }

        tweetRepository.delete(tweet);
        return ResponseEntity.ok("Tweet başarıyla silindi.");
    }
}
