package com.uc3m.delphi.rest.exception;

public class DelphiProcessException extends Exception {
    public DelphiProcessException() {
    }

    public DelphiProcessException(String message) {
        super(message);
    }

    public DelphiProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public DelphiProcessException(Throwable cause) {
        super(cause);
    }

    public DelphiProcessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
