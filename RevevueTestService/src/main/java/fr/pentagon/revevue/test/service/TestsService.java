package fr.pentagon.revevue.test.service;

import fr.pentagon.revevue.test.dto.TestBundle;
import fr.pentagon.revevue.test.dto.TestResultDTO;
import fr.pentagon.revevue.test.exception.CompilationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Logger;

@Service
public final class TestsService {
    private final Logger logger = Logger.getLogger(TestsService.class.getName());

    public TestResultDTO runTest(TestBundle testBundle) throws IOException, CompilationException, ClassNotFoundException {
        Objects.requireNonNull(testBundle);
        try {
            logger.info("initialisation");
            initializeFolder(testBundle);

            var loader = CustomTestClassLoader.in(Paths.get(testBundle.idAsString()));
            var clazz = loader.load(testBundle.testFileName(), testBundle.dependencyFileName());
            var tracker = TestTracker.runAndTrack(clazz);
            logger.info("Run test");
            TestResultDTO testResultDTO = new TestResultDTO(
                    tracker.allTestsPassed(),
                    tracker.passedCount(),
                    tracker.failuresCount(),
                    tracker.failureDetails()
            );
            logger.info(testResultDTO.toString());
            return testResultDTO;
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
