package com.leandroProject.twitterSimplificado.controller;

import com.leandroProject.twitterSimplificado.controller.dto.CreateTweetDto;
import com.leandroProject.twitterSimplificado.controller.dto.FeedDto;
import com.leandroProject.twitterSimplificado.controller.dto.FeedItemDto;
import com.leandroProject.twitterSimplificado.entity.Role;
import com.leandroProject.twitterSimplificado.entity.Tweet;
import com.leandroProject.twitterSimplificado.repository.TweetRepository;
import com.leandroProject.twitterSimplificado.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class TweetController {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public TweetController(TweetRepository tweetRepository, UserRepository userRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/tweet")
    public ResponseEntity<Void> createTweet(@RequestBody CreateTweetDto createTweetDto,
            JwtAuthenticationToken jwtAuthenticationToken) {

        var user = userRepository.findById(UUID.fromString(jwtAuthenticationToken.getName()));

        var tweet = new Tweet();
        tweet.setContent(createTweetDto.content());
        tweet.setUser(user.get());

        tweetRepository.save(tweet);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/tweet/{id}")
    public ResponseEntity<Void> deleteTweet(@PathVariable("id") Long tweetId,
            JwtAuthenticationToken jwtAuthenticationToken) {

        var user = userRepository.findById(UUID.fromString(jwtAuthenticationToken.getName()));
        var tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var isAdmin = user.get().getRoles()
                .stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.values.ADMIN.name()));

        if (isAdmin || tweet.getUser().getUserId().equals(UUID.fromString(jwtAuthenticationToken.getName()))) {
            tweetRepository.deleteById(tweetId);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/feed")
    public ResponseEntity<FeedDto> feed() {
        var tweets = tweetRepository.findAll()
                .stream()
                .map(tweet -> new FeedItemDto(
                        tweet.getTweetId(),
                        tweet.getContent(),
                        tweet.getUser().getUsername()))
                .toList();

        return ResponseEntity.ok(new FeedDto(tweets));
    }

    @GetMapping("/myFeed")
    public ResponseEntity<FeedDto> myFeed(JwtAuthenticationToken jwtAuthenticationToken) {
        var tweets = tweetRepository.findAll();

        List<FeedItemDto> tweetList = new ArrayList<>();

        for( Tweet tweet : tweets) {
            if(tweet.getUser().getUserId().equals(UUID.fromString(jwtAuthenticationToken.getName()))) {
                tweetList.add(new FeedItemDto(
                        tweet.getTweetId(),
                        tweet.getContent(),
                        tweet.getUser().getUsername()
                ));
            }
        }

        return ResponseEntity.ok(new FeedDto(tweetList));
    }
}
