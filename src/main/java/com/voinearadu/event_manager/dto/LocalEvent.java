package com.voinearadu.event_manager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class LocalEvent implements IEvent {

    public abstract void fire();

}
