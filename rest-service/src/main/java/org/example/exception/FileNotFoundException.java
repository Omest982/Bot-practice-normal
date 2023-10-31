package org.example.exception;

import java.io.Serial;

public class FileNotFoundException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;

    public FileNotFoundException(String msg) {
        super(msg);
    }
}
