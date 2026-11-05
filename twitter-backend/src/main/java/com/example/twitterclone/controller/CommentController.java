package com.example.twitterclone.controller;

import com.example.twitterclone.entity.Comment;
import com.example.twitterclone.exception.ApiException;
import com.example.twitterclone.repository.CommentRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comment")
@CrossOrigin(origins = "http://localhost:3200")
public class CommentController {
    private final CommentRepository commentRepository;

    public CommentController(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @PostMapping
    public ResponseEntity<Comment> createComment(@Valid @RequestBody Comment comment) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentRepository.save(comment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer id, @RequestParam Integer userId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ApiException("Yorum bulunamadı.", HttpStatus.NOT_FOUND));
        if (!comment.getUserId().equals(userId)) {
            throw new ApiException("Sadece yorum sahibi yorumu silebilir.", HttpStatus.FORBIDDEN);
        }
        commentRepository.delete(comment);
        return ResponseEntity.ok().build();
    }
}
