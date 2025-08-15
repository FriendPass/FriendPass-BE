package com.likelion.friendpass.api.matching.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchingCompleteDto {
    private Long teamId;
    private MatchingStatusDto status;
    private List<MatchingMemberDto> members;
    private List<String> interests;
}
