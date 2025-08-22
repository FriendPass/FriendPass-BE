package com.likelion.friendpass.api.matching.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ConfirmedTeamResponse {

    @JsonProperty("team_ids")
    private List<Long> memberUserIds;
    private double score;

    @JsonProperty("representative_interests")
    private List<String> representativeInterests;

    @JsonProperty("matched_region")
    private String matchedRegion;

}
