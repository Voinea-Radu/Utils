package com.voinearadu.event_manager.manager;

import com.voinearadu.event_manager.EventManagerTests;
import com.voinearadu.event_manager.annotation.EventHandler;
import com.voinearadu.event_manager.dto.TestComplexEvent;
import com.voinearadu.event_manager.dto.TestEvent;
import com.voinearadu.event_manager.dto.TestLocalEvent;
import com.voinearadu.event_manager.dto.TestLocalRequest;

public class TestEventListener {

    @EventHandler
    public void onTestEvent(TestEvent event) {
        EventManagerTests.executed1 = true;
        event.finished = true;
    }

    @EventHandler
    public void onTestEvent(TestComplexEvent event) {
        event.result = event.a + event.b;
    }

    @EventHandler
    public void onTestEvent(TestLocalEvent event) {
        EventManagerTests.executed2 = true;
        event.finished = true;
    }

    @EventHandler
    public void onTestEvent(TestLocalRequest event) {
        event.setResult(event.a + event.b);
    }

}
