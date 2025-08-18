package com.likelion.friendpass.api.matching.dto;

import com.likelion.friendpass.api.place.dto.InterestPlaceResponse;
import com.likelion.friendpass.api.user.dto.InterestTagResponse;
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
    private List<InterestTagResponse> representativeInterests;
    private List<InterestPlaceResponse> representativePlaces;
}
