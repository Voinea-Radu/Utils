package com.voinearadu.event_manager.dto;

import com.voinearadu.utils.event_manager.dto.IEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ExternalEvent {

    private int number1;
    private int number2;
    private int result;

    public ExternalEvent(int number1, int number2) {
        this.number1 = number1;
        this.number2 = number2;
        this.result = number1 + number2;
    }

    @AllArgsConstructor
    @Getter
    public static class Wrapper implements IEvent{
        private ExternalEvent event;
    }

}
