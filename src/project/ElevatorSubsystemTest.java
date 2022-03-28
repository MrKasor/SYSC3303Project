package project;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

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
        scheduler.receiveSocket.close();
        floor.sendReceiveSocket.close();
        elevSub.getSocket().close();
    }

    @Test
    void receivePacketOne() {
        floor.send("data 1 for 4", 5000, 1);
        scheduler.receivePacketFloorSubsystem();
        System.out.println(scheduler.getPacketData());
        scheduler.sendToElevatorSubsystem();
        elevSub.receivePacketOne();
        int len = elevSub.packetData().getLength();
        String receivedData = new String(elevSub.packetData().getData(), 0, len);
        assertEquals("data 1 for 4", receivedData);
    }

    @Test
    void receivePacketTwo() {
        floor.send("data 1 for 4", 5000, 1);
        scheduler.receivePacketFloorSubsystem();
        System.out.println(scheduler.getPacketData());
        scheduler.sendToElevatorSubsystem();
        elevSub.receivePacketTwo();
        int len = elevSub.packetData().getLength();
        String receivedData = new String(elevSub.packetData().getData(), 0, len);
        assertEquals("data 1 for 4", receivedData);
    }

    @Test
    void sendDataList() {
        elevSub.sendDataList();
        scheduler.receiveFromElevatorSubsystem();
        assertTrue(scheduler.getServerPacket() != null);
    }
}