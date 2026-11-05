package com.example.twitterclone.controller;

import com.example.twitterclone.entity.Like;
import com.example.twitterclone.repository.LikeRepository;
import com.example.twitterclone.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LikeController.class)
@Import(SecurityConfig.class)
class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LikeRepository likeRepository;

    @Test
    void shouldLikeTweetSuccessfully() throws Exception {
        when(likeRepository.findByTweetIdAndUserId(1, 1)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(new Like());
        mockMvc.perform(post("/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tweetId\":1, \"userId\":1}"))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldNotLikeAlreadyLikedTweet() throws Exception {
        when(likeRepository.findByTweetIdAndUserId(1, 1)).thenReturn(Optional.of(new Like()));
        mockMvc.perform(post("/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tweetId\":1, \"userId\":1}"))
                .andExpect(status().isBadRequest());
    }
}