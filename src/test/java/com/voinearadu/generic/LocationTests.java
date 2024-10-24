package com.voinearadu.generic;

import com.voinearadu.generic.dto.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocationTests {

    @Test
    public void testOffsets() {
        Location location = new Location(1, 2, 3);
        assertEquals(new Location(2, 4, 6), location.offsetNew(1, 2, 3));
        assertEquals(new Location(0, 0, 0), location.negativeOffsetNew(1, 2, 3));
        location.offset(1, 2, 3);
        assertEquals(new Location(2, 4, 6), location);
        location.negativeOffset(2, 4, 6);
        assertEquals(new Location(0, 0, 0), location);
        location.offset(1, 2, 3);
        location.multiply(2);
        assertEquals(new Location(2, 4, 6), location);
        assertEquals(new Location(1,1,1), Location.min(
                new Location(1,100, 100),
                new Location(100,1, 100),
                new Location(100,100, 1)
        ));
        assertEquals(new Location(100,100,100), Location.max(
                new Location(100,1, 1),
                new Location(1,100, 1),
                new Location(1,1, 100)
        ));
    }

}
