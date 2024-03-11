package fr.pentagon.revevue.test.config;

import fr.pentagon.revevue.test.dto.TestBundle;
import fr.pentagon.revevue.test.exception.CompilationException;
import fr.pentagon.revevue.test.service.TestsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

@Component
public class SetupDataTest implements CommandLineRunner {
    private final TestsService testsService;
    private final Logger logger = Logger.getLogger(SetupDataTest.class.getName());
    public SetupDataTest(TestsService testsService) {
        this.testsService = testsService;
    }
    @Override
    public void run(String... args) throws Exception {
        try {
            logger.info("On Application Event...");
            var dependency = Files.readAllBytes(Paths.get("HelloWorld.java"));
            var testDependency = Files.readAllBytes(Paths.get("HelloWorldTest.java"));
            System.err.println("dependency => " + new String(dependency, StandardCharsets.UTF_8));
            logger.info("test => " + new String(testDependency, StandardCharsets.UTF_8));
            testsService.runTest(
                    new TestBundle(100000000, "HelloWorldTest.java", testDependency, "HelloWorld.java", dependency)
            );
        } catch (IOException e) {
            logger.severe("IOException");
        } catch (CompilationException e) {
            logger.severe("Compilation error => " + e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.severe("class not found error");
        }
    }
}
