package com.github.heroslender.hero_api.exceptions;

import java.util.Objects;

public class RestApiExcetion extends RuntimeException {
    private final short statusCode;
    private final long timestamp;
    private final String error;

    public RestApiExcetion(short statusCode, long timestamp, String error, String message) {
        super(message);
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.error = error;
    }

    public RestApiExcetion(short statusCode, String error, String message) {
        super(message);
        this.statusCode = statusCode;
        this.timestamp = System.currentTimeMillis();
        this.error = error;
    }

    public short getStatusCode() {
        return statusCode;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getError() {
        return error;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RestApiExcetion that = (RestApiExcetion) o;
        return getStatusCode() == that.getStatusCode() && getTimestamp() == that.getTimestamp() && Objects.equals(getError(), that.getError());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStatusCode(), getTimestamp(), getError());
    }

    @Override
    public String toString() {
        return "RestApiExcetion{" +
                "statusCode=" + statusCode +
                ", timestamp=" + timestamp +
                ", error='" + error + '\'' +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}