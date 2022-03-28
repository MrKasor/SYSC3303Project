package project;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class FloorTest {
    private Floor floor;
    private DatagramPacket testReceivePacket, testSendPacket;
    private DatagramSocket testSocket;
    byte classData[] = new byte[100];

    @BeforeEach
    void setUp() throws SocketException {
        this.floor = new Floor();
        testSocket = new DatagramSocket(6000);
        testReceivePacket = new DatagramPacket(classData, classData.length);
    }

    @AfterEach
    void tearDown() {
        floor.sendReceiveSocket.close();
        testSocket.close();
    }

    @Test
    void read() {
        //Test to ensure the right data is passed to the Scheduler
        assertEquals("10000000 1 up 4", floor.read().get(0));
    }

    @Test
    void send() throws UnknownHostException {
        String data = "10000000 1 up 4";
        floor.send(data, 6000, 1);
        try{
            testSocket.receive(testReceivePacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int len = testReceivePacket.getLength();
        String sentData = new String(classData, 0, len);
        assertEquals(sentData, data);
    }

    @Test
    void receive() throws UnknownHostException {
        String data = "10000000 1 up 4";
        floor.send(data, 6000, 1);
        try{
            testSocket.receive(testReceivePacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("print1");

        testSendPacket = new DatagramPacket(testReceivePacket.getData(), testReceivePacket.getLength(), InetAddress.getLocalHost(), testReceivePacket.getPort());

        try{
            testSocket.send(testSendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("print2");

        floor.receive();
        int len = floor.packetData().getLength();
        String receivedData = new String(floor.packetData().getData(), 0, len);
        assertEquals(receivedData, data);
    }
}