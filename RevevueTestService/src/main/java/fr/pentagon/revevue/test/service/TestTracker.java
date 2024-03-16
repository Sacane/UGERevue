package fr.pentagon.revevue.test.service;


import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.util.Objects;
import java.util.concurrent.*;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

/**
 * The TestTracker class is responsible for executing JUnit tests and tracking their results.
 * It provides methods to determine if all tests passed, get the count of failed tests,
 * and retrieve details about the failed tests.
 */
public final class TestTracker {

    private static final int TIMEOUT_IN_SECONDS = 15;

    private final TestExecutionSummary summary;

    /**
     * Constructs a TestTracker instance with the provided test execution summary.
     *
     * @param summary the test execution summary to track
     */
    private TestTracker(TestExecutionSummary summary) {
        this.summary = Objects.requireNonNull(summary);
    }

    /**
     * Executes a {@link Runnable} with a specified timeout.
     *
     * @param runnable the {@link Runnable} to execute
     * @throws TimeoutException if the execution exceeds the specified timeout
     * @throws NullPointerException if the {@code runnable} is {@code null}
     */
    private static void executeWithTimeout(Runnable runnable) throws TimeoutException {
        Objects.requireNonNull(runnable);
        try {
            var completableFuture = CompletableFuture.runAsync(runnable);
            completableFuture.get(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException e) { // If execution exceeds the timeout, throw a TimeoutException
            throw new TimeoutException();
        } catch (ExecutionException | InterruptedException e) { // Wrap unexpected exceptions in an AssertionError
            throw new AssertionError(e);
        }
    }

    /**
     * Executes the given test class and returns a TestTracker instance to track the results.
     *
     * @param testClass the test class to execute
     * @return a TestTracker instance tracking the test execution summary
     */
    public static TestTracker runAndTrack(Class<?> testClass) throws TimeoutException {
        Objects.requireNonNull(testClass);
        var request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(testClass))
                .build();
        var listener = new SummaryGeneratingListener();
        var launcher = LauncherFactory.create();
        launcher.registerTestExecutionListeners(listener);
        executeWithTimeout(() -> launcher.execute(request));
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

}