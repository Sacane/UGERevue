package fr.pentagon.ugeoverflow.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pentagon.ugeoverflow.controllers.ReviewController;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewDTO;
import fr.pentagon.ugeoverflow.exception.HttpExceptionHandler;
import fr.pentagon.ugeoverflow.service.FakeDataManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReviewControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new ReviewController(new FakeDataManager()))
            .setControllerAdvice(new HttpExceptionHandler())
            .build();

    @Nested
    @Tag("GET")
    @DisplayName("Get all reviews")
    final class GetAllReviews {

        private static final String DEFAULT_ROUTE = "/api/reviews/";

        private final List<ReviewDTO> results;

        private final ResultActions request;

        GetAllReviews() throws Exception {
            request = mvc.perform(MockMvcRequestBuilders.get(DEFAULT_ROUTE).contentType(MediaType.APPLICATION_JSON));
            var responseBody = request.andReturn().getResponse().getContentAsString();
            var typeReference = new TypeReference<List<ReviewDTO>>() {};
            this.results = objectMapper.readValue(responseBody, typeReference);
        }

        @Test
        @DisplayName("Basic tests")
        void allOpenReviews() {
            assertAll(
                    () -> request.andExpect(MockMvcResultMatchers.status().isOk()),
                    () -> request.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3)),
                    () -> assertEquals(3, results.size()),
                    () -> assertEquals(3, results.stream().distinct().count())
            );
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 2})
        @DisplayName("IDs tests")
        void allOpenReviewsIDs(int index) {
            assertEquals(7L * index, results.get(index).id());
        }

        @Test
        @DisplayName("Complex test")
        void allValuesAllOpenReviews(){
            assertAll(
                    () -> assertEquals("How to concat string in for statement", results.get(0).title()),
                    () -> assertEquals("StringBuilder", results.get(0).javaFile()),
                    () -> assertTrue(results.get(2).testFile().isEmpty()),
                    () -> assertEquals("@Test", results.get(1).testFile()),
                    () -> assertEquals(118218L, results.get(0).authorID()),
                    () -> assertEquals(3630L, results.get(1).authorID()),
                    () -> assertEquals(17L, results.get(2).authorID())
            );
        }

    }




}
