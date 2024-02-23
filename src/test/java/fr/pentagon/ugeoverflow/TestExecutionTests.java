package fr.pentagon.ugeoverflow;

import fr.pentagon.ugeoverflow.exception.CompilationException;
import fr.pentagon.ugeoverflow.utils.CustomTestClassLoader;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

final class TestExecutionTests {

    private static final Path TEST_DIRECTORY = Paths.get("src", "test", "resources", "TempDoNotRemove");

    private static final String TEST_FILE_NAME = "HelloWorldTest.java";

    private static final String DEPENDENCY_FILE_NAME = "HelloWorld.java";

    @Nested
    @DisplayName("CustomTestClassLoader")
    final class CustomTestClassLoaderTests {

        @BeforeEach
        void initializeTempDirectory() throws IOException {
            var f1 = Paths.get("src", "test", "resources", "FakeJavaFiles", "HelloWorld.java");
            var f2 = Paths.get("src", "test", "resources", "FakeJavaFiles", "HelloWorldTest.java");
            var d1 = Paths.get("src", "test", "resources", "TempDoNotRemove", "HelloWorld.java");
            var d2 = Paths.get("src", "test", "resources", "TempDoNotRemove", "HelloWorldTest.java");
            Files.copy(f1, d1);
            Files.copy(f2, d2);
        }

        @AfterEach
        void cleanTempDirectory() throws IOException {
            try (var files = Files.list(TEST_DIRECTORY)) {
                files.forEach(file -> {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }

        @Test
        @DisplayName("'load' and 'clean' are working")
        void loadIsWorking() throws IOException, CompilationException, ClassNotFoundException {
            var loader = CustomTestClassLoader.in(TEST_DIRECTORY);
            var clazz = loader.load(TEST_FILE_NAME, DEPENDENCY_FILE_NAME);
            assertAll("Load and clean are working",
                    () -> assertEquals("HelloWorldTest", clazz.getName()),
                    () -> assertTrue(Files.exists(TEST_DIRECTORY.resolve("HelloWorld.class"))),
                    () -> {
                        loader.clean();
                        assertDoesNotThrow(loader::clean);
                        try (var files = Files.list(TEST_DIRECTORY)) {
                            assertEquals(2, files.toList().size());
                        }
                    }
            );
        }

        @Test
        @DisplayName("'in' and 'load' assertions")
        void loadAssertions() throws MalformedURLException, NoSuchFileException {
            var loader = CustomTestClassLoader.in(TEST_DIRECTORY);
            assertAll("'in' and 'load' assertions",
                    () -> assertThrows(NullPointerException.class, () -> CustomTestClassLoader.in(null)),
                    () -> assertThrows(NoSuchFileException.class, () -> CustomTestClassLoader.in(Paths.get("NOT_EXISTS"))),
                    () -> assertThrows(NullPointerException.class, () -> loader.load(null, "")),
                    () -> assertThrows(NullPointerException.class, () -> loader.load("", null))
            );
        }
    }

}
