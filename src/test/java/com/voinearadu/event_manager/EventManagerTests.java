package com.voinearadu.event_manager;

import com.voinearadu.event_manager.dto.TestComplexEvent;
import com.voinearadu.event_manager.dto.TestEvent;
import com.voinearadu.event_manager.manager.TestEventListener;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventManagerTests {

    private static final EventManager eventManager = new EventManager();

    public static boolean executed = false;

    @BeforeAll
    public static void setup() {
        eventManager.register(new TestEventListener());
    }

    @Test
    public void testEvent() {
        TestEvent event = new TestEvent();
        eventManager.fire(event);

        assertTrue(executed);
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


}
