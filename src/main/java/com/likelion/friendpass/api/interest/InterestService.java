package com.likelion.friendpass.api.interest;

import com.likelion.friendpass.api.user.dto.InterestTagResponse;
import com.likelion.friendpass.domain.interest.InterestTag;
import com.likelion.friendpass.domain.interest.InterestTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterestService {

    private final InterestTagRepository interestTagRepository;

    @Transactional(readOnly = true)
    public List<InterestTagResponse> getAll() {
        List<InterestTag> tags = interestTagRepository.findAll(
                Sort.by(Sort.Direction.ASC, "name")
        );

        return tags.stream()
                .map(InterestTagResponse::from)
                .toList();
    }
}
