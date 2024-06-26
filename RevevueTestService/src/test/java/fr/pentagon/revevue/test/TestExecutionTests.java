package fr.pentagon.revevue.test;

import fr.pentagon.revevue.test.exception.CompilationException;
import fr.pentagon.revevue.test.service.CustomTestClassLoader;
import fr.pentagon.revevue.test.service.TestTracker;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

final class TestExecutionTests {

    private static final Path TEST_DIRECTORY = Paths.get("src", "test", "resources", "TempDoNotRemove");

    private static final String TEST_FILE_NAME = "HelloWorldTest.java";

    private static final String DEPENDENCY_FILE_NAME = "HelloWorld.java";
    private static final String DEPENDENCY_INFINITE_FILE_NAME = "InfiniteLoop.java";
    private static final String TEST_INFINITE_FILE_NAME = "InfiniteLoopTest.java";


    @BeforeEach
    void initializeTempDirectory() throws IOException {
        var f1 = Paths.get("src", "test", "resources", "FakeJavaFiles", "HelloWorld.java");
        var f2 = Paths.get("src", "test", "resources", "FakeJavaFiles", "HelloWorldTest.java");
        var f3 = Paths.get("src", "test", "resources", "FakeJavaFiles", "InfiniteLoop.java");
        var f4 = Paths.get("src", "test", "resources", "FakeJavaFiles", "InfiniteLoopTest.java");
        var d1 = Paths.get("src", "test", "resources", "TempDoNotRemove", "HelloWorld.java");
        var d2 = Paths.get("src", "test", "resources", "TempDoNotRemove", "HelloWorldTest.java");
        var d3 = Paths.get("src", "test", "resources", "TempDoNotRemove", DEPENDENCY_INFINITE_FILE_NAME);
        var d4 = Paths.get("src", "test", "resources", "TempDoNotRemove", TEST_INFINITE_FILE_NAME);
        Files.copy(f1, d1);
        Files.copy(f2, d2);
        Files.copy(f3, d3);
        Files.copy(f4, d4);
    }

    @AfterEach
    void cleanTempDirectory() throws IOException {
        try (var files = Files.list(TEST_DIRECTORY)) {
            files.filter(p -> !p.endsWith(".gitkeep")).forEach(file -> {
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
                            assertEquals(5, files.toList().size());
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
        void testTrackerWorking() throws CompilationException, IOException, ClassNotFoundException, TimeoutException {
            var loader = CustomTestClassLoader.in(TEST_DIRECTORY);
            var testClass = loader.load(TEST_FILE_NAME, DEPENDENCY_FILE_NAME);
            var tracker = TestTracker.runAndTrack(testClass);
            var dummyTestTracker = TestTracker.runAndTrack(RevevueTestServiceApplication.class);
            var expectedDetail = """
                    Failed tests :
                                        
                    	- wrongTest() : org.opentest4j.AssertionFailedError: expected: <Hello world ?> but was: <Hello world !>
                    """;
            assertAll("TestTracker is working",
                    () -> assertFalse(tracker.allTestsPassed()),
                    () -> assertEquals(1, tracker.failuresCount()),
                    () -> assertEquals(1, tracker.passedCount()),
                    () -> assertEquals(expectedDetail, tracker.failureDetails()),
                    () -> assertTrue(dummyTestTracker.allTestsPassed()),
                    () -> assertEquals(0, dummyTestTracker.passedCount()),
                    () -> assertEquals(0, dummyTestTracker.failuresCount()),
                    () -> assertNotNull(dummyTestTracker.failureDetails()),
                    () -> assertTrue(dummyTestTracker.failureDetails().isEmpty())
            );
        }

        @Test
        @DisplayName("TestTracker assertions")
        void testTrackerAssertions() {
            assertThrows(NullPointerException.class, () -> TestTracker.runAndTrack(null));
        }

        @Test
        @DisplayName("TestTracker tracks also timeouts")
        void testTrackerTimeout() throws IOException, CompilationException, ClassNotFoundException {
            var loader = CustomTestClassLoader.in(TEST_DIRECTORY);
            var testClass = loader.load(TEST_INFINITE_FILE_NAME, DEPENDENCY_INFINITE_FILE_NAME);
            assertThrows(TimeoutException.class, () -> TestTracker.runAndTrack(testClass));
        }

    }
}
