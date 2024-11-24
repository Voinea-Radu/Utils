package com.voinearadu.utils.event_manager.dto;

import com.voinearadu.utils.event_manager.EventManager;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class LocalEvent implements IEvent {

    private EventManager eventManager;

    public LocalEvent(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Override
    public void fire() {
        eventManager.fire(this);
    }
}
