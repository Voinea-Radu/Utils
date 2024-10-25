package com.voinearadu.event_manager.dto;

import com.voinearadu.event_manager.EventManagerTests;

public class TestLocalRequest extends LocalRequest<Integer>{

    public int a;
    public int b;

    public TestLocalRequest(int a, int b) {
        super(0);
        this.a = a;
        this.b = b;
    }

    @Override
    public void fire() {
        EventManagerTests.getEventManager().fire(this);
    }
}
