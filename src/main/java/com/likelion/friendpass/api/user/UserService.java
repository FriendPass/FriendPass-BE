package com.likelion.friendpass.api.user;

import com.likelion.friendpass.api.user.dto.UpdateProfileRequest;
import com.likelion.friendpass.api.user.dto.UserResponse;
import com.likelion.friendpass.domain.interest.UserInterestRepository;
import com.likelion.friendpass.domain.interest.InterestTagRepository;
import com.likelion.friendpass.domain.interest.UserInterest;
import com.likelion.friendpass.domain.user.User;
import com.likelion.friendpass.domain.user.UserRepository;
import com.likelion.friendpass.infra.s3.S3Uploader;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Objects;                                           // [ADDED]
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserInterestRepository userInterestRepository;
    private final InterestTagRepository interestTagRepository;
    private final S3Uploader s3Uploader;
    @Value("${app.profile.default-image-url:https://your-bucket/public/default-profile.png}")
    private String defaultProfileImageUrl;

    @Transactional(readOnly = true)
    public UserResponse getMyInfo(Long userId) {
        User user = userRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        List<String> interests = userInterestRepository.findNamesByUserId(userId);
        return UserResponse.from(user, interests);
    }

    @Transactional
    public void updateProfile(Long userId, UpdateProfileRequest req) {
        User user = userRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setNickname(req.normalizedNickname());
    }

    @Transactional
    public String updateProfileImage(Long userId, MultipartFile file) {
        String url = s3Uploader.uploadProfileImage(userId, file);
        User user = userRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setProfileImage(url);
        return url;
    }

    @Transactional
    public String resetProfileImageToDefault(Long userId) {
        User user = userRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setProfileImage(defaultProfileImageUrl);
        return defaultProfileImageUrl;
    }

    @Transactional
    public void updateInterests(Long userId, List<Long> interestIds) {
        List<Long> ids = (interestIds == null) ? List.of() : interestIds;
        if (ids.size() > 3) {
            throw new IllegalArgumentException("관심사는 최대 3개까지 선택 가능합니다.");
        }
        long valid = ids.isEmpty() ? 0 : interestTagRepository.countByInterestIdIn(ids);
        if (valid != ids.size()) {
            throw new EntityNotFoundException("Invalid interest id included");
        }
        userInterestRepository.deleteByUserId(userId);
        if (!ids.isEmpty()) {
            List<UserInterest> links = ids.stream()
                    .map(i -> UserInterest.of(userId, i))
                    .collect(Collectors.toList());
            userInterestRepository.saveAll(links);
        }
    }

    @Transactional
    public void updateLanguage(Long userId, String language) {
        if (!Objects.equals(language, "ko") && !Objects.equals(language, "en")) {
            throw new IllegalArgumentException("language must be 'ko' or 'en'");
        }
        User user = userRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setLanguage(language);
    }

    @Transactional
    public void deactivate(Long userId) {
        User user = userRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.deactivate();
    }
}