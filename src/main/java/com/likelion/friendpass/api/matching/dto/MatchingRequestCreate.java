package com.likelion.friendpass.api.matching.dto;

import com.likelion.friendpass.domain.matching.MatchingRegion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchingRequestCreate {

    private Long userId;
    private Boolean isExchange;
    private MatchingRegion region;
    private List<String> interests;
}
