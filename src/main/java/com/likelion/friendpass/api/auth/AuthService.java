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
        // 학교 도메인 확인
        String domain = req.email().substring(req.email().indexOf('@') + 1);
        schoolRepo.findByDomain(domain)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 학교 도메인입니다."));

        if (userRepo.existsByEmail(req.email())) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }

        String code = emailService.generate6Code();
        EmailVerification ev = evRepo.findByEmail(req.email())
                .map(e -> { e.setCode(code); e.setExpiresAt(LocalDateTime.now().plusMinutes(5)); e.setVerified(false); return e; })
                .orElse(EmailVerification.builder()
                        .email(req.email())
                        .code(code)
                        .expiresAt(LocalDateTime.now().plusMinutes(5))
                        .verified(false)
                        .build());
        evRepo.save(ev);
        emailService.sendCode(req.email(), code);
    }

    @Transactional
    public void verifyEmail(VerifyEmailRequest req) {
        EmailVerification ev = evRepo.findByEmail(req.email())
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
        if (!evRepo.existsByEmailAndVerifiedTrue(req.email()))
            throw new IllegalStateException("이메일 인증이 완료되지 않았습니다.");

        School school = schoolRepo.findById(req.schoolId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 학교입니다."));

        User user = User.builder()
                .email(req.email())
                .password(encoder.encode(req.password()))
                .nickname(req.nickname())
                .nationality(req.nationality())
                .isExchange(req.isExchange())
                .language(req.language())
                .profileImage("https://static.friendpass/default.png")  // 기본 이미지
                .isActive(true)
                .school(school)
                .build();
        userRepo.save(user);
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest req) {
        User user = userRepo.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다."));
        if (!encoder.matches(req.password(), user.getPassword()))
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        return new TokenResponse(jwt.createToken(user.getUserId(), user.getEmail()));
    }
}