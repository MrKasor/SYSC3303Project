package project;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class NetworkTest {
    private Network network;
    private Floor floor;
    private Elevator elevator;
    private ArrayList<String> data;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
       this.network = new Network();
       this.floor = new Floor(1, this.network);
       this.elevator = new Elevator(1, this.network);

       this.data = new ArrayList<String>();
       data.add("Array of String");
    }

    @org.junit.jupiter.api.Test
    void transfer() {
        network.transfer(data, 1, 1);
        assertEquals(false, network.hasSomething);
    }

    @org.junit.jupiter.api.Test
    void recieve() {
        network.recieve(1);
        assertEquals(false, network.hasSomething);
    }
}