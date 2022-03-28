package project;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class ElevatorSubsystemTest {
    private ElevatorSubsystem elevSub;
    private Scheduler scheduler;
    private Floor floor;

    @BeforeEach
    void setUp() {
        elevSub = new ElevatorSubsystem();
        scheduler = new Scheduler();
        floor = new Floor();
    }

    @AfterEach
    void tearDown() {
        //Close all sockets after each call
        scheduler.receiveSocket.close();
        floor.sendReceiveSocket.close();
        elevSub.getSocket().close();
    }

    @Test
    void receivePacketOne() {
        //To test this method, data is sent to the scheduler and then to the elevator subsystem.
        // This test ensures that the data is received
        floor.send("data 1 for 4", 5000, 1);
        scheduler.receivePacketFloorSubsystem();
        scheduler.sendToElevatorSubsystem();
        elevSub.receivePacketOne();
        int len = elevSub.packetData().getLength();
        String receivedData = new String(elevSub.packetData().getData(), 0, len);
        assertEquals("data 1 for 4", receivedData);
    }


    @Test
    void sendDataList() {
        //This method tests that the data sent to the scheduler is reflected in the packet of the scheduler.
        elevSub.sendDataList();
        scheduler.requestElevatorLocations();
        assertTrue(scheduler.getServerPacket() != null);
    }
}