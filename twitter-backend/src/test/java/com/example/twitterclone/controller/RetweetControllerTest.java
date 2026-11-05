package com.example.twitterclone.controller;

import com.example.twitterclone.entity.Retweet;
import com.example.twitterclone.repository.RetweetRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RetweetController.class)
@Import(SecurityConfig.class)
class RetweetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RetweetRepository retweetRepository;

    @Test
    void shouldCreateRetweetSuccessfully() throws Exception {
        when(retweetRepository.findByTweetIdAndUserId(1, 1)).thenReturn(Optional.empty());
        when(retweetRepository.save(any(Retweet.class))).thenReturn(new Retweet());
        mockMvc.perform(post("/retweet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tweetId\":1, \"userId\":1}"))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldDeleteRetweetSuccessfully() throws Exception {
        Retweet retweet = new Retweet();
        retweet.setId(1);
        when(retweetRepository.findById(1)).thenReturn(Optional.of(retweet));
        mockMvc.perform(delete("/retweet/1"))
                .andExpect(status().isOk());
    }
}