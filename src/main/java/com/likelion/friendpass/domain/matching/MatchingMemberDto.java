package com.likelion.friendpass.domain.matching;


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
