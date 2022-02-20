package project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FloorTest {
    private Floor floor;
    private Network network;
    @BeforeEach
    void setUp() {
        this.network = new Network();
        this.floor = new Floor(1, network);
    }

    @Test
    void read() {
        floor.read();
        assertEquals(true, floor.hasData());
    }
}