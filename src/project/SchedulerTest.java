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
        String data = "10000000 1 up 4";
        floor.send(data, 5000, 1);
        scheduler.receivePacketFloorSubsystem();
        int len = scheduler.getPacketData().getLength();
        String receivedData = new String(scheduler.getPacketData().getData(), 0, len);
        assertEquals(data, receivedData);
    }

    @Test
    void sendToElevatorSubsystem() {
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
    void receiveFromElevatorSubsystem() {
        eleSub.sendDataList();
        scheduler.receiveFromElevatorSubsystem();
        assertTrue(scheduler.getServerPacket() != null);
    }

    @Test
    void sendToFloorSubsystem() {
        String data = "10000000 1 up 4";
        floor.send(data, 5000, 1);
        scheduler.receivePacketFloorSubsystem();
        eleSub.sendDataArrived();
        scheduler.receiveFromElevatorSubsystem();
        scheduler.sendToFloorSubsystem();

        floor.receive();
        int len = floor.packetData().getLength();
        System.out.println(floor.packetData().getData());
        String receivedData = new String(floor.packetData().getData(), 0, len);
        System.out.println(receivedData);
        assertTrue(floor.packetData().getData() != null);
    }
}
