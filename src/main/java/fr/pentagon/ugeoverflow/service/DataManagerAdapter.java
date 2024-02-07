package fr.pentagon.ugeoverflow.service;


import fr.pentagon.ugeoverflow.controllers.dtos.responses.NewReviewDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;

import java.util.List;

public interface DataManagerAdapter {

    List<ReviewDTO> openReviews();

    ReviewDTO registerReview(NewReviewDTO newReviewDTO) throws HttpException;

}
