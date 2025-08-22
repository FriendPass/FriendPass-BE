package com.likelion.friendpass.api.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInterestsRequest {

        @NotNull
        @Size(min = 1, max = 3)
        private List<Long> interestIds;
}