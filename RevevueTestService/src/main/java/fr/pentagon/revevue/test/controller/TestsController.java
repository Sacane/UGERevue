package fr.pentagon.revevue.test.controller;

import fr.pentagon.revevue.test.HttpException;
import fr.pentagon.revevue.test.dto.TestBundle;
import fr.pentagon.revevue.test.dto.TestResultDTO;
import fr.pentagon.revevue.test.exception.CompilationException;
import fr.pentagon.revevue.test.service.TestsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/tests/")
public final class TestsController {

    private final TestsService testsService;
    private final Logger logger = Logger.getLogger(TestsController.class.getName());
    public TestsController(TestsService testsService) {
        this.testsService = testsService;
    }

    @GetMapping("hello")
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok("Hello world :D\n");
    }

    @PostMapping("run")
    public ResponseEntity<TestResultDTO> runTest(
            @RequestPart(value = "id") int id,
            @RequestPart(value = "dependencyFile") byte[] dependencyFile,
            @RequestPart(value = "testFile") byte[] testFile,
            @RequestPart(value = "dependencyFilename") String dependencyFilename,
            @RequestPart(value = "testFilename") String testFilename
    ){
        logger.info(new String(testFile, StandardCharsets.UTF_8));
        logger.info(new String(dependencyFile, StandardCharsets.UTF_8));

        try {
            var bundle = new TestBundle(id, testFilename, testFile,dependencyFilename, dependencyFile);
            var results = testsService.runTest(bundle);
            return ResponseEntity.ok(results);
        } catch (CompilationException | ClassNotFoundException e) {
            throw HttpException.badRequest(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
