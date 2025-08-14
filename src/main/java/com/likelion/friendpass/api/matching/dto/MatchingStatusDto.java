package com.likelion.friendpass.api.matching.dto;

import com.likelion.friendpass.domain.matching.MatchingRegion;
import com.likelion.friendpass.domain.matching.MatchingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchingStatusDto {
    // 매칭 상태 출력 DTO
    private MatchingStatus status;
    private MatchingRegion region;
    private List<String> interests;

}
