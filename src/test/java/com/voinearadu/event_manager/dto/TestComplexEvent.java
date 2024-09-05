package com.voinearadu.event_manager.dto;

public class TestComplexEvent implements IEvent {

    public int a;
    public int b;
    public int result;

    public TestComplexEvent(int a, int b) {
        this.a = a;
        this.b = b;
    }

}
