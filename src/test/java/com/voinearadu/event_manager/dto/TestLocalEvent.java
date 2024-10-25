package com.voinearadu.event_manager.dto;

import com.voinearadu.event_manager.EventManagerTests;

public class TestLocalEvent extends LocalEvent{

    public boolean finished;

    @Override
    public void fire() {
        EventManagerTests.getEventManager().fire(this);
    }
}
