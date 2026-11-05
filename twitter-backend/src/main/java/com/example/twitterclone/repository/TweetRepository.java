package com.example.twitterclone.repository;

import com.example.twitterclone.entity.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TweetRepository extends JpaRepository<Tweet, Integer> {
    List<Tweet> findByUserId(Integer userId);
}
