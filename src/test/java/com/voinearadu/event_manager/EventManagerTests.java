package com.voinearadu.event_manager;

import com.voinearadu.event_manager.dto.TestComplexEvent;
import com.voinearadu.event_manager.dto.TestEvent;
import com.voinearadu.event_manager.dto.TestLocalEvent;
import com.voinearadu.event_manager.dto.TestLocalRequest;
import com.voinearadu.event_manager.manager.TestEventListener;
import com.voinearadu.utils.event_manager.EventManager;
import lombok.Getter;
import org.apache.log4j.BasicConfigurator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventManagerTests {

    @Getter
    private static final EventManager eventManager = new EventManager();

    public static boolean executed1 = false;
    public static boolean executed2 = false;

    @BeforeAll
    public static void setup() {
        BasicConfigurator.configure();
        eventManager.register(new TestEventListener());
    }

    @Test
    public void testEvent() {
        TestEvent event = new TestEvent();
        eventManager.fire(event);

        assertTrue(executed1);
        assertTrue(event.finished);
    }

    @Test
    public void testComplexEvent() {
        TestComplexEvent event1 = new TestComplexEvent(1, 2);
        TestComplexEvent event2 = new TestComplexEvent(10, 20);

        eventManager.fire(event1);
        eventManager.fire(event2);

        assertEquals(3, event1.result);
        assertEquals(30, event2.result);
    }

    @Test
    public void testLocalEvent() {
        TestLocalEvent event = new TestLocalEvent();
        event.fire();

        assertTrue(executed2);
        assertTrue(event.finished);
    }

    @Test
    public void testLocalRequest() {
        TestLocalRequest event1 = new TestLocalRequest(1, 2);
        TestLocalRequest event2 = new TestLocalRequest(10, 20);

        assertEquals(0, event1.getResult());
        assertEquals(0, event2.getResult());

        eventManager.fire(event1);
        eventManager.fire(event2);

        assertEquals(3, event1.getResult());
        assertEquals(30, event2.getResult());
    }


}
