package com.likelion.friendpass.api.matching.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.likelion.friendpass.domain.matching.MatchingRegion;
import lombok.*;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MatchingRequestForModel {

    @JsonProperty("id")
    private Long userId;

    @JsonProperty("is_exchange")
    private int isExchange;
    private String region;
    private List<String> interests;

}