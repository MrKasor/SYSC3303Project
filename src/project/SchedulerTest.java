package project;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SchedulerTest {
    Scheduler scheduler;
    Floor floor;
    ElevatorSubsystem eleSub;

    @BeforeEach
    void setUp() {
        scheduler = new Scheduler();
        floor = new Floor();
        eleSub = new ElevatorSubsystem();
    }

    @AfterEach
    void tearDown() {
        scheduler.receiveSocket.close();
        floor.sendReceiveSocket.close();
        eleSub.getSocket().close();
    }

    @Test
    void receivePacketFloorSubsystem() {
        //This method tests the scheduler's receive method. The floor sends packet to the scheduler and this method
        // confirms if the data is sent or not
        String data = "10000000 1 up 4";
        floor.send(data, 5000, 1);
        scheduler.receivePacketFloorSubsystem();
        int len = scheduler.getPacketData().getLength();
        String receivedData = new String(scheduler.getPacketData().getData(), 0, len);
        assertEquals(data, receivedData);
    }

    @Test
    void sendToElevatorSubsystem() {
        //The floor sends to the scheduler, the scheduler sends to the elevator subsystem and this method tests if the
        // packet is received by the elevator subsystem.
        String data = "10000000 1 up 4";
        floor.send(data, 5000, 1);
        scheduler.receivePacketFloorSubsystem();
        scheduler.sendToElevatorSubsystem();

        eleSub.receivePacketOne();
        int len = eleSub.packetData().getLength();
        String receivedData = new String(eleSub.packetData().getData(), 0, len);
        assertEquals(data, receivedData);
    }


    @Test
    void sendToFloorSubsystem() {
        //The floor sends to the scheduler, the scheduler sends back to the floor subsystem and this method tests if the
        // packet is received by the floor subsystem.
        String data = "10000000 1 up 4";
        floor.send(data, 5000, 1);
        scheduler.requestElevatorLocations();
        eleSub.sendDataList();
        scheduler.requestElevatorLocations();
        scheduler.sendToFloorSubsystem();

        floor.receive();
        int len = floor.packetData().getLength();
        System.out.println(floor.packetData().getData());
        String receivedData = new String(floor.packetData().getData(), 0, len);
        assertTrue(floor.packetData().getData() != null);
    }
}
