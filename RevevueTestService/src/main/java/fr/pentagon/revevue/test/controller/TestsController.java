package fr.pentagon.revevue.test.controller;

import fr.pentagon.revevue.common.dto.TestBundle;
import fr.pentagon.revevue.common.dto.TestResultDTO;
import fr.pentagon.revevue.common.exception.HttpException;
import fr.pentagon.revevue.test.exception.CompilationException;
import fr.pentagon.revevue.test.service.TestsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tests/")
public final class TestsController {

    private final TestsService testsService;

    private static final int MAX_ERROR_DESCRIPTION = 50;

    private final Logger logger = Logger.getLogger(TestsController.class.getName());

    public TestsController(TestsService testsService) {
        this.testsService = testsService;
    }

    @GetMapping("hello")
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok("Hello world !\n");
    }


    @PostMapping("run")
    public ResponseEntity<TestResultDTO> runTest(
            @RequestPart(value = "id") int id,
            @RequestPart(value = "dependencyFile") byte[] dependencyFile,
            @RequestPart(value = "testFile") byte[] testFile,
            @RequestPart(value = "dependencyFilename") String dependencyFilename,
            @RequestPart(value = "testFilename") String testFilename
    ){
        try {
            logger.info("Called run on /api/tests");
            var bundle = new TestBundle(id, testFilename, testFile, dependencyFilename, dependencyFile);
            var results = testsService.runTest(bundle);
            return ResponseEntity.ok(results);
        } catch (CompilationException e) {
            logger.severe(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            throw HttpException.badRequest(e.getMessage().lines().limit(MAX_ERROR_DESCRIPTION)
                    .collect(Collectors.joining("\n", "", "...")));
        }catch (ClassNotFoundException | IOException e){
            logger.severe(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            throw new HttpException(500);
        }
    }

}
