package com.example.twitterclone.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "retweets")
public class Retweet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "tweet_id", nullable = false)
    private Integer tweetId;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getTweetId() { return tweetId; }
    public void setTweetId(Integer tweetId) { this.tweetId = tweetId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
}
