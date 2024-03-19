package fr.pentagon.revevue.test.service;

import fr.pentagon.revevue.common.dto.TestBundle;
import fr.pentagon.revevue.common.dto.TestResultDTO;
import fr.pentagon.revevue.test.exception.CompilationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

@Service
public final class TestsService {
    private final Logger logger = Logger.getLogger(TestsService.class.getName());

    public TestResultDTO runTest(TestBundle testBundle) throws IOException, CompilationException, ClassNotFoundException {
        Objects.requireNonNull(testBundle);

        try {
            logger.info("Start running pipeline tests on the following files : "
                    + testBundle.dependencyFileName() + ", " + testBundle.testFileName());
            initializeFolder(testBundle);
            logger.info("Folder initialized");
            var loader = CustomTestClassLoader.in(Paths.get(testBundle.idAsString()));
            logger.info("Loading classes...");
            var clazz = loader.load(testBundle.testFileName(), testBundle.dependencyFileName());
            logger.info("Running tests...");
            var tracker = TestTracker.runAndTrack(clazz);
            logger.info("Tests has been running successfully");
            return new TestResultDTO(
                    tracker.allTestsPassed(),
                    tracker.passedCount(),
                    tracker.failuresCount(),
                    tracker.failureDetails()
            );
        }catch (TimeoutException timeoutException){
            return new TestResultDTO(false, 0, 0, timeoutException.getMessage());
        }finally {
            deleteFolder(testBundle.idAsString());
        }
    }

    private static void initializeFolder(TestBundle testBundle) throws IOException {
        Objects.requireNonNull(testBundle);
        var path = Paths.get(testBundle.idAsString());
        if(Files.exists(path)) throw new AssertionError("Folder already exists");
        Files.createDirectory(path);
        Files.write(path.resolve(testBundle.testFileName()), testBundle.testFile());
        Files.write(path.resolve(testBundle.dependencyFileName()), testBundle.dependencyFile());
    }

    private static void deleteFolder(String folderName) throws IOException {
        Objects.requireNonNull(folderName);
        var path = Paths.get(folderName);
        if(Files.exists(path) && Files.isDirectory(path)) {
            try (var stream = Files.list(path)) {
                stream.forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        // Do nothing
                    }
                });
            }
        }
        Files.delete(path);
    }

}
