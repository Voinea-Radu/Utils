package com.voinearadu.utils.event_manager.exceptions;

public class RuntimeEventException extends RuntimeException{

    public RuntimeEventException(Exception exception) {
        super(exception);
    }

}
