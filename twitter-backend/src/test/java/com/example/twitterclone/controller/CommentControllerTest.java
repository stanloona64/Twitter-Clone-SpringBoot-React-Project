package com.example.twitterclone.controller;

import com.example.twitterclone.entity.Comment;
import com.example.twitterclone.repository.CommentRepository;
import com.example.twitterclone.repository.TweetRepository;
import com.example.twitterclone.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@Import(SecurityConfig.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private TweetRepository tweetRepository;

    @Test
    void shouldDeleteCommentSuccessfully() throws Exception {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setTweetId(10);
        comment.setUserId(100);

        when(commentRepository.findById(1)).thenReturn(Optional.of(comment));
        mockMvc.perform(delete("/comment/1")
                        .param("userId", "100"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotDeleteCommentUnauthorized() throws Exception {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setTweetId(10);
        comment.setUserId(100);

        when(commentRepository.findById(1)).thenReturn(Optional.of(comment));
        mockMvc.perform(delete("/comment/1")
                        .param("userId", "999"))
                .andExpect(status().isForbidden());
    }
}