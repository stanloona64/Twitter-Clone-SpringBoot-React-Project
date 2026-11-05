package com.example.twitterclone.repository;

import com.example.twitterclone.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Integer> {
    List<Like> findByTweetId(Integer tweetId);

    Optional<Like> findByTweetIdAndUserId(Integer tweetId, Integer userId);
}