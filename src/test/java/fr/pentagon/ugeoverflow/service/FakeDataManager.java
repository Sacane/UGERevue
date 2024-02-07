package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.controllers.dtos.responses.NewReviewDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Service
@Profile("test")
public final class FakeDataManager implements DataManagerAdapter {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final Set<Long> KNOWN_USERS = new HashSet<>(Set.of(118218L, 3630L, 17L));

    private static final List<ReviewDTO> REVIEWS = List.of(
            new ReviewDTO(
                    0L,
                    "How to concat string in for statement",
                    "StringBuilder",
                    "",
                    118218L,
                    Date.from(Instant.now())
            ),
            new ReviewDTO(
                    7L,
                    "Should I use C++",
                    "Maybe not",
                    "@Test",
                    3630L,
                    Date.from(Instant.parse("2007-12-03T10:15:30.00Z"))
            ),
            new ReviewDTO(
                    14L,
                    "Should I use C++",
                    "Maybe not",
                    "",
                    17L,
                    Date.from(Instant.now())
            )
    );

    @Override
    public List<ReviewDTO> openReviews() {
        return REVIEWS;
    }

    @Override
    public ReviewDTO registerReview(NewReviewDTO newReviewDTO) throws HttpException {
        Objects.requireNonNull(newReviewDTO);
        return null;
    }

}
