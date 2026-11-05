package com.example.twitterclone.dto;

import com.example.twitterclone.entity.Comment;
import com.example.twitterclone.entity.Like;
import com.example.twitterclone.entity.Retweet;

import java.time.LocalDateTime;
import java.util.List;

public record TweetResponse(
        Integer id,
        String content,
        Integer userId,
        LocalDateTime createdAt,
        UserSummary user,
        List<Comment> comments,
        List<Like> likes,
        List<Retweet> retweets
) {
    public record UserSummary(Integer id, String username) {
    }
}
