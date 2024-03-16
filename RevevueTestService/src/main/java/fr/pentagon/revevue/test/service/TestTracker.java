package fr.pentagon.revevue.test.service;


import fr.pentagon.revevue.test.exception.InfiniteLoopException;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

/**
 * The TestTracker class is responsible for executing JUnit tests and tracking their results.
 * It provides methods to determine if all tests passed, get the count of failed tests,
 * and retrieve details about the failed tests.
 */
public final class TestTracker {

    private final TestExecutionSummary summary;

    /**
     * Constructs a TestTracker instance with the provided test execution summary.
     *
     * @param summary the test execution summary to track
     */
    private TestTracker(TestExecutionSummary summary) {
        this.summary = Objects.requireNonNull(summary);
    }

//    private static void executeWithTimeout(Runnable runnable) throws TimeoutException {
//        Objects.requireNonNull(runnable);
//        try (var executor = Executors.newSingleThreadExecutor()) {
//            var future = executor.submit(runnable);
//            future.get(TIMEOUT, TimeUnit.SECONDS);
//        } catch (TimeoutException e) {
//            throw new TimeoutException();
//        } catch (ExecutionException | InterruptedException e) {
//            throw new AssertionError(e);
//        }
//    }

    /**
     * Executes the given test class and returns a TestTracker instance to track the results.
     *
     * @param testClass the test class to execute
     * @return a TestTracker instance tracking the test execution summary
     */
    public static TestTracker runAndTrack(Class<?> testClass) {
        Objects.requireNonNull(testClass);
        var request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(testClass))
                .build();
        var listener = new SummaryGeneratingListener();
        var launcher = LauncherFactory.create();
        launcher.registerTestExecutionListeners(listener);
        var timer = Timer.start();
        var future1 = CompletableFuture.runAsync(() -> {
            launcher.execute(request);
            timer.end();
        });
        var future2 = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(5_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        var anyFuture = CompletableFuture.anyOf(future1, future2);
        anyFuture.join();
        if(!timer.isFinished()){
            throw new InfiniteLoopException("The program contains a too long treatment");
        }
        return new TestTracker(listener.getSummary());
    }

    /**
     * Checks if all tests passed.
     *
     * @return true if all tests passed, false otherwise
     */
    public boolean allTestsPassed() {
        return summary.getFailures().isEmpty();
    }

    /**
     * Gets the count of passed tests.
     *
     * @return the count of passed tests
     */
    public long passedCount() {
        return summary.getTestsSucceededCount();
    }

    /**
     * Gets the count of failed tests.
     *
     * @return the count of failed tests
     */
    public long failuresCount() {
        return summary.getTestsFailedCount();
    }

    /**
     * Retrieves details about the failed tests.
     *
     * @return a string containing details about the failed tests
     */
    public String failureDetails() {
        if (summary.getFailures().isEmpty()) return "";
        var details = new StringBuilder("Failed tests :\n\n");
        var failures = summary.getFailures();
        for (var failure : failures) {
            details.append("\t- ").append(failure.getTestIdentifier().getDisplayName()).append(" : ");
            details.append(failure.getException()).append("\n");
        }
        return details.toString();
    }

    private static class Timer{
        private final Object lock = new Object();
        private boolean isTaskFinish = false;
        static Timer start() {
            return new Timer();
        }
        public void end() {
            synchronized (lock){
                System.out.println("test");
                isTaskFinish = true;
            }
        }
        public boolean isFinished() {
            synchronized (lock){
                return isTaskFinish;
            }
        }
    }
}