package com.leandroProject.twitterSimplificado.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "tb_tweets")
@Data
public class Tweet {

    private long tweetId;

    private User user;

    private String content;

    @CreationTimestamp
    private Instant creatAt;

}
