package project;
import java.net.*;
import java.util.*;

import project.Elevator.ElevatorState;

public class ElevatorSubsystem {
	private DatagramSocket socket;
	private DatagramPacket receivePacket, sendPacket;
	private PacketHelper helper = new PacketHelper();
	private static final int ELE_PORT = 5002;
	private static final int SCH_PORT = 5000;
	private static final int NUM_ELE = 4;
	private int nextEle = 0;
	private int nextFloor = 0;
	private int arrivedEle = 0;
	private int arrivedFloor = 0;
	private Map<Integer, String> eleList = new HashMap<>();
	
	//Server Socket on port 5000
	public ElevatorSubsystem() {
		try {
			socket = new DatagramSocket(ELE_PORT);
		} catch(SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	//When a floor needs a elevator
	private void receivePacketOne() {
        receivePacket = helper.receivePacket(socket);
        helper.print(receivePacket, "Elevator Subsystem", "received from Scheduler");
        
        System.out.println("\nContaining: " + new String(receivePacket.getData()) +"\n");
    }
	
	//When the elevator has arrived
	private void receivePacketTwo() {
        receivePacket = helper.receivePacket(socket);
        helper.print(receivePacket, "Elevator Subsystem", "received from Scheduler");
        
        System.out.println("\nContaining: " + new String(receivePacket.getData()) +"\n");
    }
	
	//Convert the message into a byte array then send it
	private void sendDataList() {
		byte[] data = formatDataList();
		sendPacket = helper.sendPacket(socket, data, SCH_PORT);
		helper.print(sendPacket, "Elevator Subsystem", "sent to Scheduler");
	}
	
	//Convert the message into a byte array then send it
	private void sendDataArrived() {
		byte[] data = formatDataArrived(arrivedEle, arrivedFloor);
		sendPacket = helper.sendPacket(socket, data, SCH_PORT);
		helper.print(sendPacket, "Elevator Subsystem", "sent to Scheduler");
	}
	
	//Format a list to send to the scheduler
	private byte[] formatDataList() {
		int size = 2;
		String temp = "";
			
		for (Map.Entry mapElement : eleList.entrySet()) {
			int key = (int)mapElement.getKey();
			String value = (String)mapElement.getValue();
	            
			temp = temp + key + "|" + value + " ";
	    }
		
		byte[] data = new byte[4 + temp.getBytes().length];
		data[0] = (byte)0;
		data[1] = (byte)1;
		
		String test = "1|6|0|0|0;2|2|0|0|0;3|7|0|0|0;4|3|0|0|0;";
		byte[] testing = new byte[test.getBytes().length];
		System.arraycopy(test.getBytes(), 0, testing, 0, test.getBytes().length);
		
		
		//Add the message into the data byte array then set the byte after it to 0
		System.arraycopy(temp.getBytes(), 0, data, size, temp.getBytes().length);
		size += temp.getBytes().length;
        
        return testing;
	}
	
	//Format a message of which elevator reached which floor to send to the scheduler
	private byte[] formatDataArrived(int id, int floor) {
		int size = 2;
		String temp = id + "|" + floor;
		
		byte[] data = new byte[4 + temp.getBytes().length];
		data[0] = (byte)0;
		data[1] = (byte)2;
		
		//Add the message into the data byte array then set the byte after it to 0
		System.arraycopy(temp.getBytes(), 0, data, size, temp.getBytes().length);
		size += temp.getBytes().length;
		data[size] = 0;
        
        return data;
	}
	
	//Elevators use this to update the hashmap of elevator locations
	synchronized void updateData(int id, String info) {
		eleList.put(id, info);
        
        notifyAll();
	}
	
	//Elevators use this to see if they have been sent to a floor
	synchronized int send(int id) {
		while(nextEle != id) {
			try{
				wait();
			} catch (InterruptedException e) {}
		}
        
        notifyAll();
        return nextFloor;
	}
	
	//Order that the methods run in
	public void run() {
		receivePacketOne();
		sendDataList();
		receivePacketTwo();
		sendDataArrived();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ElevatorSubsystem eleSystem = new ElevatorSubsystem();
		System.out.println("Elevator System started");
		
		for (int i = 0; i < NUM_ELE; i++) {
			Thread tempThread = new Thread(new Elevator(i+1, eleSystem), "Elevator: "+(i+1));
			tempThread.start();
        }

		while(true) {
			eleSystem.run();
		}
	}

}
