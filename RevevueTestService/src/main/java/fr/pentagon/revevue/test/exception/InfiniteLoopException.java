package fr.pentagon.revevue.test.exception;

public class InfiniteLoopException extends RuntimeException{
    public InfiniteLoopException(String message) {
        super(message);
    }
}
