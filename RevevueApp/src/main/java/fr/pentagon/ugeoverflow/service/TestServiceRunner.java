package fr.pentagon.ugeoverflow.service;

import fr.pentagon.revevue.common.dto.TestResultDTO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.util.Objects;

@Service
public class TestServiceRunner {
    private final WebClient webClient;
    public TestServiceRunner(WebClient webClient) {
        this.webClient = webClient;
    }

    public String sendTestAndGetFeedback(String dependencyFilename, String testFilename, byte[] javaFile, byte[] testFile, long authorId) {
        Objects.requireNonNull(javaFile);
        Objects.requireNonNull(testFile);
        try {
            var parts = getPartsTestEndpoints(dependencyFilename, testFilename, javaFile, testFile, authorId);
            var response = webClient.post()
                    .uri(builder -> builder.path("/tests/run").build())
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(parts))
                    .accept(MediaType.APPLICATION_JSON).exchangeToMono(r -> r.bodyToMono(TestResultDTO.class))
                    .block();
            if (response != null) {
                return response.result();
            }
            return "Une erreur est survenue lors du lancement du serveur";
        }catch (WebClientRequestException ignored) {
            return "Le serveur est indisponible...";
        }
    }

    public String runTestOrQueueIt(String dependencyFilename, String testFilename, byte[] javaFile, byte[] testFile, long authorId){
//        try {
//            var parts = getPartsTestEndpoints(dependencyFilename, testFilename, javaFile, testFile, authorId);
//            var response = webClient.post()
//                    .uri(builder -> builder.path("/tests/run").build())
//                    .contentType(MediaType.MULTIPART_FORM_DATA)
//                    .body(BodyInserters.fromMultipartData(parts))
//                    .accept(MediaType.APPLICATION_JSON).exchangeToMono(r -> r.bodyToMono(TestResultDTO.class))
//                    .block();
//            if (response != null) {
//                return response.result();
//            }
//            return "Une erreur est survenue lors du lancement du serveur";
//        }catch (WebClientRequestException ignored) {
//            return "Le serveur est indisponible...";
//        }
        return "";
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
