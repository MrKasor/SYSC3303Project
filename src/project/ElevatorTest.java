package project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ElevatorTest {
    private Elevator elevator;
    private ElevatorSubsystem elevatorSubsystem;
    private ArrayList<String> data = new ArrayList<>();

    @BeforeEach
    void setUp() {
        this.elevatorSubsystem = new ElevatorSubsystem();
        this.elevator = new Elevator(1, 1, elevatorSubsystem);

        this.data = new ArrayList<String>();
        data.add("Array of String");

    }

    @Test
    void run() {
        elevator.run();
        //assertEquals(data, elevator.getData());
    }
}