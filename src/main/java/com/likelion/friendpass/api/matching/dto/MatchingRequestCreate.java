package com.likelion.friendpass.api.matching.dto;

import com.likelion.friendpass.domain.matching.MatchingRegion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchingRequestCreateDto {

    // 매칭하기 버튼을 눌렀을 때의 DTO
    // interest는 db에서 가져오기
    private Long userId;
    private MatchingRegion region;
}
