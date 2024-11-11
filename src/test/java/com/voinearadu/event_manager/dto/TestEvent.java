package com.voinearadu.event_manager.dto;

import com.voinearadu.utils.event_manager.dto.IEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
public class TestEvent implements IEvent {

    private final int number1;
    private final int number2;
    private @Setter int result;

    public TestEvent(int number1, int number2) {
        this.number1 = number1;
        this.number2 = number2;
    }

}
