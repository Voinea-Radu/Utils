package com.voinearadu.event_manager.manager;

import com.voinearadu.event_manager.dto.*;
import com.voinearadu.utils.event_manager.annotation.EventHandler;

public class TestEventListener {

    @EventHandler
    public void onTestEvent(TestEvent event) {
        event.setResult(event.getNumber1() + event.getNumber2());
    }

    @EventHandler
    public void onTestLocalEvent(TestLocalEvent event) {
        event.setResult(event.getNumber1() + event.getNumber2());
    }

    @EventHandler
    public void onTestLocalRequest(TestLocalRequest event) {
        event.setResult(event.getNumber1() + event.getNumber2());
    }

    @EventHandler
    public void onExternalEvent(ExternalEvent.Wrapper event) {
        event.getEvent().setResult(event.getEvent().getNumber1() + event.getEvent().getNumber2());
    }



}
