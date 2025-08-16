package com.likelion.friendpass.api.auth;

import com.likelion.friendpass.api.auth.dto.*;
import com.likelion.friendpass.domain.auth.EmailVerification;
import com.likelion.friendpass.domain.auth.EmailVerificationRepository;
import com.likelion.friendpass.domain.school.School;
import com.likelion.friendpass.domain.school.SchoolRepository;
import com.likelion.friendpass.domain.user.User;
import com.likelion.friendpass.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmailService emailService;
    private final EmailVerificationRepository evRepo;
    private final UserRepository userRepo;
    private final SchoolRepository schoolRepo;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwt;

    @Transactional
    public void sendEmail(SendEmailRequest req) {
        String email = req.email().trim().toLowerCase();
        String domain = email.substring(email.indexOf('@') + 1);

        schoolRepo.findByDomain(domain)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 학교 도메인입니다."));

        if (userRepo.findByEmailAndIsActiveTrue(email).isPresent()) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }

        String code = emailService.generate6Code();

        EmailVerification ev = evRepo.findTopByEmailOrderByExpiresAtDesc(email)
                .map(e -> {
                    e.setCode(code);
                    e.setExpiresAt(LocalDateTime.now().plusMinutes(5));
                    e.setVerified(false);
                    return e;
                })
                .orElse(EmailVerification.builder()
                        .email(email)
                        .code(code)
                        .expiresAt(LocalDateTime.now().plusMinutes(5))
                        .verified(false)
                        .build());

        evRepo.save(ev);
        emailService.sendCode(email, code);
    }

    @Transactional
    public void verifyEmail(VerifyEmailRequest req) {
        String email = req.email().trim().toLowerCase();

        EmailVerification ev = evRepo.findTopByEmailOrderByExpiresAtDesc(email)
                .orElseThrow(() -> new IllegalArgumentException("인증 요청 내역이 없습니다."));

        if (ev.isVerified()) return;
        if (LocalDateTime.now().isAfter(ev.getExpiresAt()))
            throw new IllegalStateException("인증코드가 만료되었습니다.");
        if (!ev.getCode().equals(req.code()))
            throw new IllegalArgumentException("인증코드가 일치하지 않습니다.");

        ev.setVerified(true);
    }

    @Transactional
    public void signup(SignupRequest req) {
        String email = req.email().trim().toLowerCase();

        if (!evRepo.existsByEmailAndVerifiedTrue(email))
            throw new IllegalStateException("이메일 인증이 완료되지 않았습니다.");

        School school = schoolRepo.findById(req.schoolId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 학교입니다."));

        if (userRepo.findByEmailAndIsActiveTrue(email).isPresent()) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }

        User inactive = userRepo.findByEmailAndIsActiveFalse(email).orElse(null);
        if (inactive != null) {
            inactive.setIsActive(true);
            inactive.setPassword(encoder.encode(req.password()));
            inactive.setNickname(req.nickname());
            inactive.setNationality(req.nationality());
            inactive.setIsExchange(req.isExchange());
            inactive.setLanguage(req.language() == null ? "ko" : req.language());
            if (inactive.getProfileImage() == null || inactive.getProfileImage().isBlank()) {
                inactive.setProfileImage("https://static.friendpass/default.png");
            }
            inactive.setSchool(school);

            return;
        }

        User user = User.builder()
                .email(email)
                .password(encoder.encode(req.password()))
                .nickname(req.nickname())
                .nationality(req.nationality())
                .isExchange(req.isExchange())
                .language(req.language() == null ? "ko" : req.language())
                .profileImage("https://static.friendpass/default.png")
                .isActive(true)
                .school(school)
                .build();
        userRepo.save(user);
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest req) {
        String email = req.email().trim().toLowerCase();

        User user = userRepo.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다."));

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new IllegalArgumentException("탈퇴한 계정입니다.");
        }

        if (!encoder.matches(req.password(), user.getPassword()))
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");

        return new TokenResponse(jwt.createToken(user.getUserId(), user.getEmail()));
    }
}