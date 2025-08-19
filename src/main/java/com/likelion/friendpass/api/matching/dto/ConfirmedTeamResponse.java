package com.likelion.friendpass.api.matching.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ConfirmedTeamResponse {

    @JsonProperty("member_user_ids")
    private List<Long> memberUserIds;
    private double score;

    @JsonProperty("representative_interests")
    private List<String> representativeInterests;
}
