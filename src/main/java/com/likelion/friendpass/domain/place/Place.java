package com.likelion.friendpass.domain.place;


import com.likelion.friendpass.domain.matching.MatchingRegion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name="places")
public class Place {

    @Id
    @Column(name="place_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeId;

    @Column(name="name", nullable=false)
    private String name;

    @Column(name="latitude", nullable=false)
    private Double latitude;

    @Column(name="longitude", nullable=false)
    private Double longitude;

    @Column(name="address", nullable=false)
    private String address;

    @Column(name="description", nullable=false)
    private String description;

    @Column(name="region", nullable=false)
    @Enumerated(EnumType.STRING)
    private MatchingRegion region;

    // 나중에 FK로 변환
    @Column(name = "category_id")
    private Long categoryId;
    // private Category category;

}