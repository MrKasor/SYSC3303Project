/**
 * 
 */
package project;

import java.awt.Taskbar.State;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import project.Elevator.ElevatorState;

/**
 * @author Colton
 * 
 * Class Scheduler represents the scheduler of the elevator system.
 *
 */
public class Scheduler{
	private ArrayList<String> data;
	String timestamp;
	int requestFloor;
	String direction;
	int floorDestination;
	
	String elevatorToMove;

	DatagramPacket sendPacket, receivePacket, serverPacket;
	DatagramSocket sendReceiveSocket, receiveSocket;
	
	public Scheduler()
	{
		try {
			
			// Construct a datagram socket and bind it to port 5000 
			// on the local host machine. This socket will be used to
			// receive UDP Datagram packets.
			receiveSocket = new DatagramSocket(5000);
			
			// Construct a datagram socket and bind it to any available 
			// port on the local host machine. This socket will be used to
			// send and receive UDP Datagram packets.
			sendReceiveSocket = new DatagramSocket();
		} catch (SocketException se) {   // Can't create the socket.
			se.printStackTrace();
			System.exit(1);
		}
	}
	
	public enum SchedulerState
	{
		WaitingForButtonPress,
		RequestingElevatorLocations,
		SendingElevator,
		UpdateFloor;
	}
	static SchedulerState state = SchedulerState.WaitingForButtonPress;
	
	/**
	 * receive a packet from the floor subsystem.
	 */
	private void receivePacketFloorSubsystem()
	{
		// Construct a DatagramPacket for receiving packets up 
		// to 100 bytes long (the length of the byte array).
		byte data[] = new byte[100];
		receivePacket = new DatagramPacket(data, data.length);
		
		// Block until a datagram packet is received from receiveSocket.
		try {        
			System.out.println("Waiting for response from FloorSubsystem..."); // so we know we're waiting
			receiveSocket.receive(receivePacket);
		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}
		
		// Process the received datagram.
		System.out.println("Scheduler: Packet received:");
		System.out.println("From FloorSubsystem: " + receivePacket.getAddress());
		System.out.println("FloorSubsystem port: " + receivePacket.getPort());
		int len = receivePacket.getLength();
		System.out.println("Length: " + len);
		System.out.print("Containing: "); 
		
		// Form a String from the byte array.
		String received = new String(data,0,len);   
		
		
		String[] temp = received.split(" ");
		timestamp = temp[0];
		System.out.println("timestamp: "+ timestamp);
		requestFloor = Integer.parseInt(temp[1]);
		System.out.println("request floor: "+ requestFloor);
		direction = temp[2];
		System.out.println("direction: "+ direction);
		floorDestination = Integer.parseInt(temp[3]);
		System.out.println("floorDestination: "+ floorDestination);
	}
	
	/*
	 * This function determines which elevator will be selected to pick up the passengers.
	 */
	private void requestElevatorLocations()
	{	
		byte data[] = new byte[100];
		data[0] = 1;
		//prepare a packet to send to server for a request to find the location of the elevators
		try {
			sendPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), 5002);
		} catch (UnknownHostException e) {
	         e.printStackTrace();
	         System.exit(1);
	      }
		//Send the packet
		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Scheduler: Packet sent to ElevatorSubsystem to get elevator locations.\n");
		
		//Now we need to store the locations we get from the response
		// Construct a DatagramPacket for receiving packets up 
		// to 100 bytes long (the length of the byte array).
		byte info[] = new byte[100];
		serverPacket = new DatagramPacket(info, info.length);
		
		// Block until a datagram packet is received from receiveSocket.
		try {        
			System.out.println("Waiting for response from ElevatorSubsystem..."); // so we know we're waiting
			receiveSocket.receive(serverPacket);
		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}
		
		//Process the received datagram and determine which elevator will go to the floor.
		System.out.println("Scheduler: Elevator locations received from ElevatorSubsystem:");
		System.out.println("From ElevatorSubsystem: " + serverPacket.getAddress());
		System.out.println("ElevatorSubsystem port: " + serverPacket.getPort());
		int len = serverPacket.getLength();
		System.out.println("Length: " + len);
		System.out.print("Containing: "); 
		System.out.println(new String(serverPacket.getData()));
		
		//parsing the result from elevator subsystem...
		String result = new String(serverPacket.getData());
		String[] elevators = result.split(";");
		System.out.print(elevators[0] + "\n" + elevators[1] + "\n" + elevators[2] + "\n" + elevators[3] + "\n");
		String[] data1 = elevators[0].split("\\|");
		String[] data2 = elevators[1].split("\\|");
		String[] data3 = elevators[2].split("\\|");
		String[] data4 = elevators[3].split("\\|");
		
		
		int[] elevs = new int[4];
		elevs[0] = Integer.parseInt(data1[1]);
		elevs[1] = Integer.parseInt(data2[1]);
		elevs[2] = Integer.parseInt(data3[1]);
		elevs[3] = Integer.parseInt(data4[1]);
		
		//need to implement a method for finding which floor is closest to the requestFloor
		int distance = Math.abs(elevs[0] - requestFloor);
		int id = 1;
		for(int i = 1; i < 4; i++)
		{
			int iDistance = Math.abs(elevs[i] - requestFloor);
			if(iDistance < distance)
			{
				id = i+1;
				distance = iDistance;
			}
		}
		
		System.out.println("The id of the elevator to move is: "+id);
		elevatorToMove = Integer.toString(id);
	}
	
	/*
	 * Transfers the FloorSubsystem information over to the ElevatorSubsystem.
	 */
	private void sendToElevatorSubsystem()
	{
		 //Create byte array and assign first byte to 0.
		 byte[] toSend = new byte[100];
		 
		 String format = elevatorToMove+"|"+requestFloor+"|"+floorDestination+"|";
		 //now we need to insert the message from data into our byte array.
		 System.arraycopy(format.getBytes(), 0, toSend, 0, format.getBytes().length);
		
		//prepare the packet to send to server
		try {
			sendPacket = new DatagramPacket(toSend, toSend.length, InetAddress.getLocalHost(), 5002);
		} catch (UnknownHostException e) {
	         e.printStackTrace();
	         System.exit(1);
	      }
		
		//Send the packet
		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Scheduler: Packet sent to ElevatorSubsystem.\n");
	}
	
	/*
	 * Waits to receive information from the server.
	 */
	private void receiveFromElevatorSubsystem()
	{
		// Construct a DatagramPacket for receiving packets up 
		// to 100 bytes long (the length of the byte array).
		byte data[] = new byte[100];
		serverPacket = new DatagramPacket(data, data.length);
		
		// Block until a datagram packet is received from receiveSocket.
		try {        
			System.out.println("Waiting for response from ElevatorSubsystem..."); // so we know we're waiting
			sendReceiveSocket.receive(serverPacket);
		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}
		
		// Process the received datagram.
		System.out.println("Scheduler: Packet received from ElevatorSubsystem:");
		System.out.println("From ElevatorSubsystem: " + serverPacket.getAddress());
		System.out.println("ElevatorSubsystem port: " + serverPacket.getPort());
		int len = serverPacket.getLength();
		System.out.println("Length: " + len);
		System.out.print("Containing: "); 
		System.out.println(String.valueOf(data[0])+String.valueOf(data[1])+String.valueOf(data[2])+String.valueOf(data[3]));
	}
	
	/*
	 * Sends the information from the server over to the client.
	 */
	private void sendToFloorSubsystem()
	{
		String floorInfo= "Elevator " + elevatorToMove + " is on its way...";
		
		//Create byte array and assign first byte to 0.
		 byte[] toSend = new byte[100];
		   
		 //now we need to insert the message from data into our byte array.
		 System.arraycopy(floorInfo.getBytes(), 0, toSend, 0, floorInfo.getBytes().length);
		
		//prepare the packet to send to client
		try {
			sendPacket = new DatagramPacket(toSend, toSend.length, InetAddress.getLocalHost(), receivePacket.getPort());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
		//Send to client
		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Scheduler: Packet sent to FloorSubsystem.\n");
	}
	
    public static void main(String[] args) 
	{
		Scheduler scheduler = new Scheduler();
		while(true)
		{
			switch (state)
			{
				case WaitingForButtonPress:
					scheduler.receivePacketFloorSubsystem();
					state = SchedulerState.RequestingElevatorLocations;
					break;
					
				case RequestingElevatorLocations:
					scheduler.requestElevatorLocations();
					state = SchedulerState.SendingElevator;
					break;
					
				case SendingElevator:
					scheduler.sendToElevatorSubsystem();
					state = SchedulerState.UpdateFloor;
					break;
					
				case UpdateFloor:
					scheduler.sendToFloorSubsystem();
					state = SchedulerState.WaitingForButtonPress;
					break;
			}

		}
		
	}
    
}