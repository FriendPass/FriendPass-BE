package com.likelion.friendpass.api.rank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class StampUpdateReq {
    private int delta; // +1, +n, -n 등 변경량
}
