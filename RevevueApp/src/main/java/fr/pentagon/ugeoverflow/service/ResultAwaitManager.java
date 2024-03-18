package fr.pentagon.ugeoverflow.service;

import fr.pentagon.revevue.common.dto.TestBundle;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.logging.Logger;


/**
 * This service exists to handle the case when the micro service for tests
 */
@Service
@EnableAsync
public class ResultAwaitManager {
    private final Object lock = new Object();
    private final TestServiceRunner testServiceRunner;
    private final Logger logger = Logger.getLogger(ResultAwaitManager.class.getName());

    public ResultAwaitManager(TestServiceRunner testServiceRunner){
        this.testServiceRunner = testServiceRunner;
    }

    private final ArrayDeque<TestBundle> waitingTests = new ArrayDeque<>();

    public void addTest(TestBundle testBundle) {
        synchronized (lock){
            waitingTests.addLast(testBundle);
        }
    }

    @Scheduled(fixedRate = 60 * 1_000 * 5)
    private void retry() {
        logger.info("Retry test service...");
        synchronized (lock){
            while(!waitingTests.isEmpty()){
                var test = waitingTests.poll();
                testServiceRunner.sendTestAndGetFeedback(test.dependencyFileName(), test.testFileName(), test.dependencyFile(), test.testFile(), test.id());
            }
        }
    }
}
