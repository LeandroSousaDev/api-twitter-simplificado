package com.leandroProject.twitterSimplificado.repository;

import com.leandroProject.twitterSimplificado.entity.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {


}
