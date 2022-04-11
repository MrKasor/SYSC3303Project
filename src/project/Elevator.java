package project;

import java.util.ArrayList;
import java.io.IOException;


/**
 * @author Ryan, modified by Colton
 * 
 * Class Elevator represents a single elevator inside a building.
 *
 */
public class Elevator implements Runnable{
	private int id;
	private ElevatorSubsystem sysRef;
	private int pickupFloor = 0;
	private int destFloor = 0;
	private int curFloor = 0;
	private boolean hasPeople = false;
	private boolean boarding = true;
	private boolean errorDoor = false;
	private boolean errorFull = false;
	private Door door;
	private Motor motor;
	private ElevatorLamp lamp;
	private ElevatorButton button;
	Config config;
	
	/**
	 * Constructor for Elevator. Starts an Elevator thread.
	 * 
	 * @param id
	 * @param floor
	 * @param sysRef
	 */
	public Elevator(int id, int floor, ElevatorSubsystem sysRef) throws IOException {
		this.id=id;
		this.curFloor=floor;
		this.sysRef=sysRef;
		door = new Door(id, false, false);
		motor = new Motor(id, false, false);
		lamp = new ElevatorLamp(id, false);
		button = new ElevatorButton(id, false);
		config = new Config();
	}
	
	
	public enum ElevatorState
	{
		Waiting,
		Stopped,
		MovingToPassengers,
		MovingToDestination;
	}
	ElevatorState state = ElevatorState.Waiting;
	
	
	public void run()
	{
		while(true) {
			
			String temp = "";
			switch (state) {
			
				case Waiting:
					
					//Set ele as idle
					System.out.println("Elevator " +id+": is Idle");
					//System.out.println("Elevator "+id+" is currently waiting for further instruction...");
					temp = id+"|"+0+"|0|0|0|"+curFloor+"|0";//ID|FLOOR|PEOPLE|MOVING|DIRECTION|CURFLOOR|STATE
					sysRef.updateData(id, temp);
					
					//Find next destination
					pickupFloor = sysRef.send(id);
					destFloor = sysRef.getDestinationFloor();
					
					//Set ele to moving
					if(pickupFloor > curFloor) {//Going up
						temp = id+"|"+pickupFloor+"|0|0|0|"+curFloor+"|1";//ID|FLOOR|PEOPLE|MOVING|DIRECTION|CURFLOOR|STATE
					}
					else {//Going Down
						temp = id+"|"+pickupFloor+"|0|0|1|"+curFloor+"|1";//ID|FLOOR|PEOPLE|MOVING|DIRECTION
					}
					sysRef.updateData(id, temp); //update that elevator is moving
					
					System.out.println("Elevator "+id+": moving to floor "+pickupFloor);
					state = ElevatorState.MovingToPassengers;
					
					break;
				
				case MovingToPassengers:
					
					//Move elevator floor by floor up or down
					System.out.println("Please wait until elevator "+id+" has arrived...");
					//int timing = 0;
					
					//going up
					while(pickupFloor > curFloor){ 
						
						motor.elevatorGoingUp();
						try {
							Thread.sleep((long) config.getFloatProperty("timePerFloor"));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						//timing = destFloor - curFloor;
						curFloor++;
						temp = id+"|"+pickupFloor+"|0|0|0|"+curFloor+"|1";//ID|FLOOR|PEOPLE|MOVING|DIRECTION|CURFLOOR|STATE
						sysRef.updateData(id, temp);
					}
					
					//going down
					while(pickupFloor < curFloor) {
						
						motor.elevatorGoingDown();
						try {
							Thread.sleep((long) config.getFloatProperty("timePerFloor"));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						//timing = floor - destFloor;
						curFloor--;
						temp = id+"|"+pickupFloor+"|0|0|0|"+curFloor+"|1";//ID|FLOOR|PEOPLE|MOVING|DIRECTION|CURFLOOR|STATE
						sysRef.updateData(id, temp);
					}
					
					System.out.println("Elevator " +id+": has arrived at floor "+pickupFloor+".");
					motor.elevatorArrived();
					state = ElevatorState.Stopped;
					
					break;
					
				case Stopped:
					
					int people = 0;
					
					//Opening Doors
					temp = id+"|"+curFloor+"|"+people+"|0|0|"+curFloor+"|2";//ID|FLOOR|PEOPLE|MOVING|DIRECTION
					sysRef.updateData(id, temp); //Update that elevator has stopped
					try {
						Thread.sleep((long) config.getFloatProperty("timeDoor"));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					door.open();
					
					//Sleep while customers are getting on/off, then switch to waiting.
					if(boarding){
						hasPeople = true;
						people = 1;
						System.out.println("\nElevator "+id+": stopped, people are boarding.\n");
						boarding = false;
						button.elevatorButtonPressed();
						lamp.turnElevatorLampOn();
					}
					else {
						people = 0;
						hasPeople = false;
						System.out.println("\nElevator "+id+": stopped, people are disembarking.\n");
						boarding = true;
					}
					
					// set ele as people getting on or off then sleep
					if(hasPeople) {//Getting on
						temp = id+"|"+curFloor+"|"+people+"|0|0|"+curFloor+"|3";//ID|FLOOR|PEOPLE|MOVING|DIRECTION|STATE
						sysRef.updateData(id, temp); //Update that elevator has stopped
					}
					else {//Getting off
						temp = id+"|"+curFloor+"|"+people+"|0|0|"+curFloor+"|6";//ID|FLOOR|PEOPLE|MOVING|DIRECTION|STATE
						sysRef.updateData(id, temp); //Update that elevator has stopped
					}
					try {
						Thread.sleep((long) config.getFloatProperty("timeBoard"));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					//Closing Doors
					temp = id+"|"+curFloor+"|"+people+"|0|0|"+curFloor+"|4";//ID|FLOOR|PEOPLE|MOVING|DIRECTION|STATE
					sysRef.updateData(id, temp); //Update that elevator has stopped
					try {
						Thread.sleep((long) config.getFloatProperty("timeDoor"));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					door.close();
					
					if(!boarding) {
						state = ElevatorState.MovingToDestination;
					}
					else {
						state = ElevatorState.Waiting;
					}
					
					break;
					
				case MovingToDestination:
					
					//move ele to destination one floor at a time up or down
					System.out.println("Elevator "+id+": Bringing passengers to floor "+destFloor+".");
					door.close();
					//timing = 0;
					while(destFloor > curFloor){//going up
						//timing = destination - destFloor;
						motor.elevatorGoingUp();
						try {
							Thread.sleep((long) config.getFloatProperty("timePerFloor"));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						curFloor++;
						temp = id+"|"+destFloor+"|1|0|0|"+curFloor+"|5";//ID|FLOOR|PEOPLE|MOVING|DIRECTION|CURFLOOR|STATE
						sysRef.updateData(id, temp);
						
					}
					while(destFloor < curFloor){//going down
						//timing = destFloor - curFloor;
						motor.elevatorGoingDown();
						try {
							Thread.sleep((long) config.getFloatProperty("timePerFloor"));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						curFloor--;
						temp = id+"|"+destFloor+"|1|0|0|"+curFloor+"|5";//ID|FLOOR|PEOPLE|MOVING|DIRECTION|CURFLOOR|STATE
						sysRef.updateData(id, temp);
					}
					
					//Arrived so update ele
					System.out.println("Elevator " +id+": arrived at floor "+destFloor+".");
					curFloor = destFloor;
					temp = id+"|"+destFloor+"|"+"0|0|0|"+curFloor+"|5";
					sysRef.updateData(id, temp);
					motor.elevatorArrived();
					state = ElevatorState.Stopped;
					break;
			}
		}
	}
}
