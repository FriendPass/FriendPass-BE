package com.likelion.friendpass.api.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private static final SecureRandom RND = new SecureRandom();

    public String generate6Code() {
        return String.format("%06d", RND.nextInt(1_000_000));
    }

    public void sendCode(String to, String code) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("[FriendPass] 이메일 인증코드");
        msg.setText("인증코드: " + code + " (5분 이내 입력)");
        mailSender.send(msg);
    }
}