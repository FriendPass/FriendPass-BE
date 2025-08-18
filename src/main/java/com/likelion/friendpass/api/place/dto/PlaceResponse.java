package com.likelion.friendpass.api.place.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlaceResponse {
    private String name;
    private String address;
    private String description;
}
