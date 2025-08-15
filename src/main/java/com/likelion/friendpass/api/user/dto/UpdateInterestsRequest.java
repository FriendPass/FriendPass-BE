package com.likelion.friendpass.api.user.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInterestsRequest {

        @Size(max = 3, message = "관심사는 최대 3개까지 선택 가능합니다.")
        private List<Long> interestIds;
}