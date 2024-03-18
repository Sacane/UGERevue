package fr.pentagon.revevue.common.exception;

public record HttpError(int statusCode, String message) {
}
