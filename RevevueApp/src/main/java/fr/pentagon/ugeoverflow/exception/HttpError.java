package fr.pentagon.ugeoverflow.exception;

public record HttpError(int statusCode, String message) {
}
