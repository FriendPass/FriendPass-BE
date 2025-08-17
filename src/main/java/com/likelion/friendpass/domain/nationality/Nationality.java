package com.likelion.friendpass.domain.nationality;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nationality")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Nationality {

    @Id
    @Column(name = "code", length = 2, nullable = false, updatable = false)
    private String code;

    @Column(name = "name_ko", nullable = false, length = 100)
    private String nameKo;

    @Column(name = "name_en", nullable = false, length = 100)
    private String nameEn;
}