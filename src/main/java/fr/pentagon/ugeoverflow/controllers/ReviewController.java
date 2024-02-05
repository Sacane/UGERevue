package fr.pentagon.ugeoverflow.controllers;

import fr.pentagon.ugeoverflow.controllers.dtos.responses.NewReviewDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewDTO;
import fr.pentagon.ugeoverflow.service.DataManagerAdapter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@RestController
@RequestMapping("api/reviews")
public final class ReviewController {

    private static final Logger LOGGER = Logger.getLogger(ReviewController.class.getName());

    private final DataManagerAdapter dataManager;

    public ReviewController(DataManagerAdapter dataManagerAdapter){
        this.dataManager = Objects.requireNonNull(dataManagerAdapter);
    }

    @GetMapping
    public ResponseEntity<List<ReviewDTO>> openReviews(){
        LOGGER.info("GET performed on /api/reviews");
        return ResponseEntity.ok(dataManager.openReviews());
    }

    @PostMapping
    public ResponseEntity<String> createReview(@RequestBody NewReviewDTO newReviewDTO) {
        LOGGER.info("POST performed on /api/reviews");
        SecurityContextHolder.getContext().getAuthentication().getDetails();
        return ResponseEntity.ok("");
    }



}
