package project;
import java.net.*;
import java.util.*;

import project.Elevator.ElevatorState;

/**
 * 
 * @author Ryan, modified by Colton
 *
 */

public class ElevatorSubsystem {
	private DatagramSocket socket;
	private DatagramPacket receivePacket, sendPacket;
	private PacketHelper helper = new PacketHelper();
	private static final int ELE_PORT = 5002;
	private static final int SCH_PORT = 5000;
	private static final int NUM_ELE = 4;
	private int nextEle = 0;
	private int nextFloor = 0;
	public int destinationFloor = 0;
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
		System.out.println("Waiting for packet from Scheduler...");
        receivePacket = helper.receivePacket(socket);
        helper.print(receivePacket, "Elevator Subsystem", "received from Scheduler");
        
        System.out.println("\nContaining: " + new String(receivePacket.getData()) +"\n");
    }
	
	//Scheduler returning which elevator to send and to where
	synchronized void receivePacketTwo() {
        receivePacket = helper.receivePacket(socket);
        helper.print(receivePacket, "Elevator Subsystem", "received from Scheduler");
        
        System.out.println("\nContaining: " + new String(receivePacket.getData()) +"\n");
        
        String result = new String(receivePacket.getData());
        String[] data = result.split("\\|");
        nextEle = Integer.valueOf(data[0]);
        nextFloor = Integer.valueOf(data[1]);
        destinationFloor = Integer.valueOf(data[2]);
        notifyAll();
    }
	
	//Convert the message into a byte array then send it
	private void sendDataList() {
		byte[] data = formatDataList();
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
	            
			temp = temp + value + ";";
	    }
		
		byte[] data = new byte[temp.getBytes().length];
		
		//Add the message into the data byte array then set the byte after it to 0
		System.arraycopy(temp.getBytes(), 0, data, 0, temp.getBytes().length);
        
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
        nextEle = 0;
        notifyAll();
        return nextFloor;
	}
	
	public int getDestinationFloor()
	{
		return destinationFloor;
	}
	
	//Order that the methods run in
	public void run() {
		receivePacketOne();
		sendDataList();
		receivePacketTwo();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ElevatorSubsystem eleSystem = new ElevatorSubsystem();
		System.out.println("Elevator System started");
		
		int incr = 1;
		for (int i = 0; i < NUM_ELE; i++) {
			Thread tempThread = new Thread(new Elevator(i+1, eleSystem), "Elevator: "+(i+1));
			tempThread.start();
			eleSystem.updateData(i+1, i+1+"|"+incr+"|0|0|0");
			incr += 6;
        }

		while(true) {
			eleSystem.run();
		}
	}

}
