package com.voinearadu.event_manager.dto;

import com.voinearadu.event_manager.EventManagerTests;
import com.voinearadu.utils.event_manager.dto.LocalRequest;
import lombok.Getter;

@Getter
public class TestUnregisterEvent extends LocalRequest<Integer> {

    private final int number1;
    private final int number2;

    public TestUnregisterEvent(int number1, int number2) {
        super(EventManagerTests.getEventManager(), 0);
        this.number1 = number1;
        this.number2 = number2;
    }

}
