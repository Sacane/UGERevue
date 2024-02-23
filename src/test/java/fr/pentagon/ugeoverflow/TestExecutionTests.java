package fr.pentagon.ugeoverflow;

import fr.pentagon.ugeoverflow.exception.CompilationException;
import fr.pentagon.ugeoverflow.utils.CustomTestClassLoader;
import fr.pentagon.ugeoverflow.utils.TestTracker;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

final class TestExecutionTests {

    private static final Path TEST_DIRECTORY = Paths.get("src", "test", "resources", "TempDoNotRemove");

    private static final String TEST_FILE_NAME = "HelloWorldTest.java";

    private static final String DEPENDENCY_FILE_NAME = "HelloWorld.java";

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

    @Nested
    @DisplayName("CustomTestClassLoader")
    final class CustomTestClassLoaderTests {

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
        void loadAssertions() throws IOException {
            var loader = CustomTestClassLoader.in(TEST_DIRECTORY);
            var f = Paths.get("src", "test", "resources", "FakeJavaFiles", "HelloWorldWithError.java");
            var d = Paths.get("src", "test", "resources", "TempDoNotRemove", "HelloWorldWithError.java");
            Files.copy(f, d);
            assertAll("'in' and 'load' assertions",
                    () -> assertThrows(NullPointerException.class, () -> CustomTestClassLoader.in(null)),
                    () -> assertThrows(NoSuchFileException.class, () -> CustomTestClassLoader.in(Paths.get("NOT_EXISTS"))),
                    () -> assertThrows(NullPointerException.class, () -> loader.load(null, "")),
                    () -> assertThrows(NullPointerException.class, () -> loader.load("", null)),
                    () -> assertThrows(IllegalArgumentException.class, () -> loader.load("", "")),
                    () -> assertThrows(IllegalArgumentException.class, () -> loader.load(".java", "")),
                    () -> assertThrows(NoSuchFileException.class, () -> loader.load(TEST_FILE_NAME, "NOT_EXISTS.java")),
                    () -> assertThrows(NoSuchFileException.class, () -> loader.load("NOT_EXISTS.java", DEPENDENCY_FILE_NAME)),
                    () -> assertThrows(CompilationException.class, () -> loader.load(TEST_FILE_NAME, "HelloWorldWithError.java"))
            );
            Files.delete(d);
        }
    }

    @Nested
    @DisplayName("TestTracker")
    final class TestTrackerTests {

        @Test
        @DisplayName("TestTracker is working")
        void testTrackerWorking() throws CompilationException, IOException, ClassNotFoundException {
            var loader = CustomTestClassLoader.in(TEST_DIRECTORY);
            var testClass = loader.load(TEST_FILE_NAME, DEPENDENCY_FILE_NAME);
            var tracker = TestTracker.runAndTrack(testClass);
            var dummyTestTracker = TestTracker.runAndTrack(UgeOverflowApplication.class);
            System.out.println(dummyTestTracker.failedTestsDetails());
            var expectedDetail = """
                    Failed tests :
                                        
                    	- wrongTest() : org.opentest4j.AssertionFailedError: expected: <Hello world ?> but was: <Hello world !>
                    """;
            assertAll("TestTracker is working",
                    () -> assertFalse(tracker.allTestsPassed()),
                    () -> assertEquals(1, tracker.failedTestsCount()),
                    () -> assertEquals(1, tracker.passedTestsCount()),
                    () -> assertEquals(expectedDetail, tracker.failedTestsDetails()),
                    () -> assertTrue(dummyTestTracker.allTestsPassed()),
                    () -> assertEquals(0, dummyTestTracker.passedTestsCount()),
                    () -> assertEquals(0, dummyTestTracker.failedTestsCount()),
                    () -> assertTrue(dummyTestTracker.failedTestsDetails().isEmpty())
            );
        }

        @Test
        @DisplayName("TestTracker assertions")
        void testTrackerAssertions() {
            assertThrows(NullPointerException.class, () -> TestTracker.runAndTrack(null));
        }

    }
}
