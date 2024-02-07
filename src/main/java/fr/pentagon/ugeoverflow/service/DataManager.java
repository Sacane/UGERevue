package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.controllers.dtos.responses.NewReviewDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public final class DataManager implements DataManagerAdapter {

    @Override
    public List<ReviewDTO> openReviews() {
        return Collections.emptyList();
    }

    @Override
    public ReviewDTO registerReview(NewReviewDTO newReviewDTO) throws HttpException {
        return null;
    }

}
