package com.voinearadu.event_manager.manager;

import com.voinearadu.event_manager.EventManagerTests;
import com.voinearadu.event_manager.annotation.EventHandler;
import com.voinearadu.event_manager.dto.TestComplexEvent;
import com.voinearadu.event_manager.dto.TestEvent;

public class TestEventListener {

    @EventHandler
    public void onTestEvent(TestEvent event) {
        EventManagerTests.executed = true;
        event.finished = true;
    }

    @EventHandler
    public void onTestEvent(TestComplexEvent event) {
        event.result = event.a + event.b;
    }

}
