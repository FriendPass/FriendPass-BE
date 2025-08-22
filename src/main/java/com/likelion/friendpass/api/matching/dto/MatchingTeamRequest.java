package com.likelion.friendpass.api.matching.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchingTeamRequest {
    private List<MatchingRequestForModel> users;
}
