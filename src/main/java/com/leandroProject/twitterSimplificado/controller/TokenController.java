package com.leandroProject.twitterSimplificado.controller;

import com.leandroProject.twitterSimplificado.controller.dto.LoginRequest;
import com.leandroProject.twitterSimplificado.controller.dto.LoginResponse;
import com.leandroProject.twitterSimplificado.entity.Role;
import com.leandroProject.twitterSimplificado.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.stream.Collectors;

@RestController
public class TokenController {

    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public TokenController(JwtEncoder jwtEncoder, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login (@RequestBody LoginRequest loginRequest) {

       var user = this.userRepository.findByUsername(loginRequest.username());

       if(user.isEmpty() || !user.get().isLoginCorrect(loginRequest, bCryptPasswordEncoder)) {
           throw  new BadCredentialsException("Usuario ou senha incorretos");
       }

       var now = Instant.now();
       var expiresIn = 300L;

       var scopes = user.get().getRoles()
               .stream()
               .map(Role::getName)
               .collect(Collectors.joining(" "));

       var claims = JwtClaimsSet.builder()
               .issuer("mybackend")
               .subject(user.get().getUserId().toString())
               .issuedAt(now)
               .expiresAt(now.plusSeconds(expiresIn))
               .claim("scope", scopes)
               .build();

       var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

       return ResponseEntity.ok(new LoginResponse(jwtValue, expiresIn));


    }
}
