package com.likelion.friendpass.api.user;

import com.likelion.friendpass.api.auth.JwtTokenProvider;
import com.likelion.friendpass.api.user.dto.UserResponse;
import com.likelion.friendpass.config.TokenBlacklist;
import com.likelion.friendpass.domain.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklist tokenBlacklist;

    @GetMapping
    public ResponseEntity<UserResponse> getMyInfo(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        UserResponse response = userService.getMyInfo(userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMe(HttpServletRequest request, Authentication auth) {
        Long userId = Long.valueOf(auth.getName());
        userService.deactivate(userId);

        String token = jwtTokenProvider.resolveToken(request);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            long exp = jwtTokenProvider.getExpiry(token);
            tokenBlacklist.add(token, exp);
        }
        return ResponseEntity.noContent().build();
    }
}