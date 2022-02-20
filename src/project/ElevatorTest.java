package project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ElevatorTest {
    private Elevator elevator;
    private Network network;
    private ArrayList<String> data = new ArrayList<>();

    @BeforeEach
    void setUp() {
        this.network = new Network();
        this.elevator = new Elevator(1, network);

        this.data = new ArrayList<String>();
        data.add("Array of String");

        network.transfer(data, 1, 1);
    }

    @Test
    void run() {
        elevator.run();
        assertEquals(data, elevator.getData());
    }
}