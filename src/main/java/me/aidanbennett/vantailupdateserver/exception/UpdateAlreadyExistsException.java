package me.aidanbennett.vantailupdateserver.exception;

public class UpdateAlreadyExistsException extends RuntimeException {

    public UpdateAlreadyExistsException(String message) {
        super(message);
    }
}
