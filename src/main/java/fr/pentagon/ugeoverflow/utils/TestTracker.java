package fr.pentagon.ugeoverflow.utils;

import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.util.Objects;

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
    private TestTracker(TestExecutionSummary summary){
        this.summary = Objects.requireNonNull(summary);
    }

    /**
     * Executes the given test class and returns a TestTracker instance to track the results.
     *
     * @param testClass the test class to execute
     * @return a TestTracker instance tracking the test execution summary
     */
    public static TestTracker runAndTrack(Class<?> testClass){
        Objects.requireNonNull(testClass);
        var request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(testClass))
                .build();
        var listener = new SummaryGeneratingListener();
        var launcher = LauncherFactory.create();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);
        return new TestTracker(listener.getSummary());
    }

    /**
     * Checks if all tests passed.
     *
     * @return true if all tests passed, false otherwise
     */
    public boolean allTestsPassed(){
        return summary.getFailures().isEmpty();
    }

    /**
     * Gets the count of passed tests.
     *
     * @return the count of passed tests
     */
    public long passedTestsCount(){
        return summary.getTestsSucceededCount();
    }

    /**
     * Gets the count of failed tests.
     *
     * @return the count of failed tests
     */
    public long failedTestsCount(){
        return summary.getTestsFailedCount();
    }

    /**
     * Retrieves details about the failed tests.
     *
     * @return a string containing details about the failed tests
     */
    public String failedTestsDetails(){
        if(summary.getFailures().isEmpty()) return "";
        var details = new StringBuilder("Failed tests :\n\n");
        var failures = summary.getFailures();
        for (var failure : failures){
            details.append("\t- ").append(failure.getTestIdentifier().getDisplayName()).append(" : ");
            details.append(failure.getException()).append("\n");
        }
        return details.toString();
    }

}