package com.likelion.friendpass.api.matching.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class MatchingMemberDto {
    private Long userId;
    private String name;
}
