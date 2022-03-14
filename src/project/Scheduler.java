/**
 * 
 */
package project;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * @author Ryan, Colton
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
		String[] data1 = elevators[0].split("|");
		String[] data2 = elevators[1].split("|");
		String[] data3 = elevators[2].split("|");
		String[] data4 = elevators[3].split("|");
		
		for(String d : data1)
		{
			System.out.print(d);
		}
		
		
		
		//Find which is closest to the requestFloor
		int elev1 = Integer.parseInt(data1[1]);
		int elev2 = Integer.parseInt(data2[1]);
		int elev3 = Integer.parseInt(data3[1]);
		int elev4 = Integer.parseInt(data4[1]);
		
		
		int[] elevs = new int[4];
		elevs[0] = elev1;
		elevs[1] = elev2;
		elevs[2] = elev3;
		elevs[3] = elev4;
		
		//need to implement a method for finding which floor is closest to the requestFloor
		int distance = Math.abs(elevs[0] - requestFloor);
		int id = 0;
		for(int i = 1; i < 4; i++)
		{
			int iDistance = Math.abs(elevs[i] - requestFloor);
			if(iDistance < distance)
			{
				id = i;
				distance = iDistance;
			}
		}
		
		System.out.println("The id of the elevator to move is: "+id);
		
		
	}
	
	/*
	 * Transfers the FloorSubsystem information over to the ElevatorSubsystem.
	 */
	private void sendToElevatorSubsystem()
	{
		//prepare the packet to send to server
		try {
			sendPacket = new DatagramPacket(receivePacket.getData(), receivePacket.getLength(), InetAddress.getLocalHost(), 5002);
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
		//prepare the packet to send to client
		try {
			sendPacket = new DatagramPacket(serverPacket.getData(), serverPacket.getLength(), InetAddress.getLocalHost(), receivePacket.getPort());
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
			scheduler.receivePacketFloorSubsystem();
			scheduler.requestElevatorLocations();
			scheduler.sendToElevatorSubsystem();
			scheduler.receiveFromElevatorSubsystem();
			scheduler.sendToFloorSubsystem();
		}
		
	}
    
}