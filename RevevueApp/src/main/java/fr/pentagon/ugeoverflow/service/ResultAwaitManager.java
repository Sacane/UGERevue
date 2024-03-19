package fr.pentagon.ugeoverflow.service;

import fr.pentagon.revevue.common.dto.TestBundle;
import fr.pentagon.revevue.common.dto.TestResultDTO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;


/**
 * This service exists to handle the case when the micro service for tests
 */
@Service
@EnableAsync
public class ResultAwaitManager {
    private final Object lock = new Object();
    private final QuestionService questionService;
    private final Logger logger = Logger.getLogger(ResultAwaitManager.class.getName());
    private final WebClient webClient;
    private final ArrayDeque<TestBundle> waitingTests = new ArrayDeque<>();

    public ResultAwaitManager(QuestionService questionService, WebClient webClient){
        this.questionService = questionService;
        this.webClient = webClient;
    }

    @Scheduled(fixedRate = 60 * 1_000 * 5)
    private void retry() {
        logger.info("Retry test service...");
        synchronized (lock){
            while(!waitingTests.isEmpty()){
                var test = waitingTests.poll();
                var bundle = new TestBundle(test.id(), test.testFileName(), test.testFile(), test.dependencyFileName(), test.dependencyFile());
                sendTestAndGetFeedback(test.dependencyFileName(), test.testFileName(), test.dependencyFile(), test.testFile(), test.id())
                        .ifPresentOrElse(result -> questionService.updateTest(test.id(), result), () -> waitingTests.push(bundle));
            }
        }
    }
    public Optional<String> sendTestAndGetFeedback(String dependencyFilename, String testFilename, byte[] javaFile, byte[] testFile, long authorId) {
        Objects.requireNonNull(javaFile);
        Objects.requireNonNull(testFile);
        synchronized (lock) {
            try {
                var parts = getPartsTestEndpoints(dependencyFilename, testFilename, javaFile, testFile, authorId);
                var response = webClient.post()
                        .uri(builder -> builder.path("/tests/run").build())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .body(BodyInserters.fromMultipartData(parts))
                        .accept(MediaType.APPLICATION_JSON).exchangeToMono(r -> r.bodyToMono(TestResultDTO.class))
                        .block();
                if (response != null) {
                    return Optional.of(response.result());
                }
                return Optional.of("Une erreur est survenue lors du lancement du serveur");
            } catch (WebClientRequestException ignored) {
                var bundle = new TestBundle(authorId, testFilename, testFile, dependencyFilename, javaFile);
                this.waitingTests.push(bundle);
                return Optional.of("Le serveur est indisponible, le test sera automatiquement redémarré lorsque le serveur sera de nouveau");
            }
        }
    }
    private static MultiValueMap<String, Object> getPartsTestEndpoints(String dependencyFilename, String testFilename, byte[] javaFile, byte[] testFile, long authorId) {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("dependencyFile", new ByteArrayResource(javaFile));
        parts.add("id", authorId);
        parts.add("testFile", new ByteArrayResource(testFile));
        parts.add("dependencyFilename", dependencyFilename);
        parts.add("testFilename", testFilename);
        return parts;
    }
}
