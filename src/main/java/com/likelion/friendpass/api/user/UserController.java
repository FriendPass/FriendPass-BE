package com.likelion.friendpass.api.user;

import com.likelion.friendpass.api.auth.JwtTokenProvider;
import com.likelion.friendpass.api.user.dto.UserResponse;
import com.likelion.friendpass.config.TokenBlacklist;
import com.likelion.friendpass.api.user.UserService;
import com.likelion.friendpass.api.user.dto.UpdateProfileRequest;
import com.likelion.friendpass.api.user.dto.UpdateInterestsRequest;
import com.likelion.friendpass.api.user.dto.UpdateLanguageRequest;
import com.likelion.friendpass.api.user.dto.UploadProfileImageResponse;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PutMapping
    public ResponseEntity<Void> updateMyProfile(Authentication authentication,
                                                @Valid @RequestBody UpdateProfileRequest req) {
        Long userId = Long.valueOf(authentication.getName());
        userService.updateProfile(userId, req);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadProfileImageResponse> uploadProfileImage(
            Authentication authentication,
            @RequestPart("file") MultipartFile file) {
        Long userId = Long.valueOf(authentication.getName());
        String url = userService.updateProfileImage(userId, file);
        return ResponseEntity.ok(new UploadProfileImageResponse(url));
    }

    @PutMapping("/interests")
    public ResponseEntity<Void> updateInterests(Authentication authentication,
                                                @Valid @RequestBody UpdateInterestsRequest req) {
        Long userId = Long.valueOf(authentication.getName());
        userService.updateInterests(userId, req.getInterestIds());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/language")
    public ResponseEntity<Void> updateLanguage(Authentication authentication,
                                               @Valid @RequestBody UpdateLanguageRequest req) {
        Long userId = Long.valueOf(authentication.getName());
        userService.updateLanguage(userId, req.getLanguage());
        return ResponseEntity.noContent().build();
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