package me.aidanbennett.vantailupdateserver.exception;

public class PublishUnauthorizedException extends RuntimeException {

    public PublishUnauthorizedException() {
        super("A valid publish secret is required");
    }
}
