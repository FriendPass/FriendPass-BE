package com.likelion.friendpass.api.certification;

import com.likelion.friendpass.api.certification.dto.CertificationRequest;
import com.likelion.friendpass.api.certification.dto.CertificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CertificationController {
    private final CertificationService certificationService;

    @PostMapping("/certify")
    public ResponseEntity<CertificationResponse> certify(@RequestBody CertificationRequest request) {
        CertificationResponse response = certificationService.certify(request);
        return ResponseEntity.ok(response);
    }
}
