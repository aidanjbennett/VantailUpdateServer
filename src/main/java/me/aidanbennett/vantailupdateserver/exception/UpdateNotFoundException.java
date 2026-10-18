package me.aidanbennett.vantailupdateserver.exception;

public class UpdateNotFoundException extends RuntimeException {

    public UpdateNotFoundException(String message) {
        super(message);
    }
}
