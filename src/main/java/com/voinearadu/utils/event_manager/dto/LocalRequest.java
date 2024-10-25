package com.voinearadu.utils.event_manager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class LocalRequest<Result> extends LocalEvent {

    private Result result;

    public LocalRequest(Result defaultResult) {
        this.result = defaultResult;
    }

    public void respond(Result result) {
        this.result = result;
    }
}
