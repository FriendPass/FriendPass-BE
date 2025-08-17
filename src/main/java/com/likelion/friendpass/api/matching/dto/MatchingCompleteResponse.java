package com.likelion.friendpass.api.matching.dto;

import com.likelion.friendpass.domain.interest.InterestTag;
import com.likelion.friendpass.domain.place.Place;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchingCompleteResponse {
    private Long teamId;
    private MatchingStatusResponse status;
    private List<MatchingMemberResponse> members;
    private List<String> representativeInterests;
    private List<Place> places;
}
