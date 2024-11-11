package com.voinearadu.event_manager.manager;

import com.voinearadu.event_manager.dto.TestUnregisterEvent;
import com.voinearadu.utils.event_manager.annotation.EventHandler;

public class TestUnregisterEventListener {

    @EventHandler
    public void onTestUnregisterEvent(TestUnregisterEvent event) {
        event.setResult(event.getNumber1() + event.getNumber2());
    }

}
