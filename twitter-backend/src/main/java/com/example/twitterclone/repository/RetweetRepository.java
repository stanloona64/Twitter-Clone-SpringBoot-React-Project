package com.example.twitterclone.repository;

import com.example.twitterclone.entity.Like;
import com.example.twitterclone.entity.Retweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RetweetRepository extends JpaRepository<Retweet, Integer> {
    List<Retweet> findByTweetId(Integer tweetId);

    Optional<Retweet> findByTweetIdAndUserId(Integer tweetId, Integer userId);
}
