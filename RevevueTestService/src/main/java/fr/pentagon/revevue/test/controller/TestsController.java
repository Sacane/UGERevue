package fr.pentagon.revevue.test.controller;

import fr.pentagon.revevue.test.HttpException;
import fr.pentagon.revevue.test.dto.TestBundle;
import fr.pentagon.revevue.test.dto.TestResultDTO;
import fr.pentagon.revevue.test.exception.CompilationException;
import fr.pentagon.revevue.test.service.TestsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/tests/")
public final class TestsController {

    private final TestsService testsService;

    public TestsController(TestsService testsService) {
        this.testsService = testsService;
    }

    @GetMapping("hello")
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok("Hello world :D\n");
    }

    @PostMapping("run")
    public ResponseEntity<TestResultDTO> runTest(
            @RequestParam(value = "id") int id,
            @RequestParam(value = "dependencyFile") byte[] dependencyFile,
            @RequestParam(value = "testFile") byte[] testFile,
            @RequestParam(value = "dependencyFilename") String dependencyFilename,
            @RequestParam(value = "testFilename") String testFilename
    ){
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
