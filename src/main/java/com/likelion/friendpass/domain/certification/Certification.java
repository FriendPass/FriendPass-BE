package com.likelion.friendpass.domain.certification;

import com.likelion.friendpass.domain.matching.MatchingTeam;
import com.likelion.friendpass.domain.place.Place;
import com.likelion.friendpass.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="certifications")
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="certification_id")
    private Long certificationId;

    @Column(name="certified_date", nullable=false)
    private LocalDate certifiedDate;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @ManyToOne
    @JoinColumn(name="team_id", nullable = false)
    private MatchingTeam team;

    @ManyToOne
    @JoinColumn(name="place_id", nullable=false)
    private Place place;

}
