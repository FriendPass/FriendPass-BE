package com.likelion.friendpass.api.nationality;

import com.likelion.friendpass.domain.nationality.Nationality;
import com.likelion.friendpass.domain.nationality.NationalityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/nationalities")
@RequiredArgsConstructor
public class NationalityController {

    private final NationalityRepository nationalityRepository;

    @GetMapping
    public List<NationalityDto> list() {
        return nationalityRepository.findAll().stream()
                .sorted(Comparator.comparing(Nationality::getNameEn, String.CASE_INSENSITIVE_ORDER))
                .map(n -> new NationalityDto(n.getCode(), n.getNameKo(), n.getNameEn()))
                .toList();
    }

    public record NationalityDto(String code, String nameKo, String nameEn) {}
}