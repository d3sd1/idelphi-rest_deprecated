package com.uc3m.delphi.rest.exception;

public class LanguageException extends Exception {
    public LanguageException() {
    }

    public LanguageException(String message) {
        super(message);
    }

    public LanguageException(String message, Throwable cause) {
        super(message, cause);
    }

    public LanguageException(Throwable cause) {
        super(cause);
    }

    public LanguageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
