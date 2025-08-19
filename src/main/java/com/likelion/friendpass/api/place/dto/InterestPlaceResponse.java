package com.likelion.friendpass.api.place.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InterestPlaceResponse {
    private String interest;
    private List<PlaceResponse> places;
}
