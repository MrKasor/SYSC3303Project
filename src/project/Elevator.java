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
	private int nextFloor = 0;
	private int destination = 0;
	private int currentFloor = 0;
	private int floor;
	private boolean hasPeople = false;
	private boolean boarding = true;
	private Door door;
	private Motor motor;
	private ElevatorLamp lamp;
	private ElevatorButton button;
	Config config;
	
	/**
	 * Constructor for Elevator. Starts an Elevator thread. Assigns default values for id, floor, and 
	 * also creates door, motor, lamp, and button objects for this elevator.
	 * 
	 * @param id
	 * @param floor
	 * @param sysRef
	 */
	public Elevator(int id, int floor, ElevatorSubsystem sysRef) throws IOException {
		this.id=id;
		this.floor=floor;
		this.sysRef=sysRef;
		door = new Door(id, false, false);
		motor = new Motor(id, false, false);
		lamp = new ElevatorLamp(id, false);
		button = new ElevatorButton(id, false);
		config = new Config();
	}
	
	/**
	 * ElevatorState consists of four states that an elevator will transition through. All elevators initially start
	 * in the Waiting state.
	 *
	 */
	public enum ElevatorState
	{
		Waiting,
		Stopped,
		MovingToPassengers,
		MovingToDestination;
	}
	ElevatorState state = ElevatorState.Waiting;
	
	/**
	 * This is where elevators are constantly transitioning between states, corresponding to the information
	 * they receive once they have been sent to a location from the ElevatorSubsystem.
	 * 
	 */
	public void run()
	{
		while(true) {
			
			String temp = "";
			switch (state) {
				
				/**
				 * This is the state upon which the elevator is waiting for further instructions and has either arrived at its
				 * destination, or is still waiting to be assigned a floor to go to.
				 */
				case Waiting:
					temp = id+"|"+floor+"|0|0|0|"+floor+"|0";//ID|FLOOR|PEOPLE|MOVING|DIRECTION|CURFLOOR|STATE
					sysRef.updateData(id, temp);
					nextFloor = sysRef.send(id);
					destination = sysRef.getDestinationFloor();
					door.close();
					if(nextFloor > floor) {//Going up
						temp = id+"|"+nextFloor+"|1|0|0|"+floor+"|1";//ID|FLOOR|PEOPLE|MOVING|DIRECTION|CURFLOOR|STATE
						motor.elevatorGoingUp();
					}
					else {//Going Down
						temp = id+"|"+nextFloor+"|1|0|1|"+floor+"|1";//ID|FLOOR|PEOPLE|MOVING|DIRECTION
						motor.elevatorGoingDown();
					}
					System.out.println("Elevator "+id+": moving to floor "+nextFloor);
					sysRef.updateData(id, temp); //update that elevator is moving
					state = ElevatorState.MovingToPassengers;
					break;
					
				/**
				 * This is the state in which the elevator has either arrived to pick passengers up, or to drop them off.
				 * When we are picking up passengers, we go to the MovingToDestination state. When we are dropping them off,
				 * we go back to the waiting state after they disembark.
				 */
				case Stopped:
					//Sleep while customers are getting on/off, then switch to waiting.
					if(boarding)
					{
						hasPeople = true;
						System.out.println("\nElevator "+id+": stopped, people are boarding.\n");
						boarding = false;
						button.elevatorButtonPressed();
						lamp.turnElevatorLampOn();
					}
					else
					{
						hasPeople = false;
						System.out.println("\nElevator "+id+": stopped, people are disembarking.\n");
						boarding = true;
					}
					if(hasPeople) {//Getting on
						temp = id+"|"+floor+"|1|0|0|"+floor+"|3";//ID|FLOOR|PEOPLE|MOVING|DIRECTION
						sysRef.updateData(id, temp); //Update that elevator has stopped
					}
					else {//Getting off
						temp = id+"|"+floor+"|0|0|0|"+floor+"|6";//ID|FLOOR|PEOPLE|MOVING|DIRECTION
						sysRef.updateData(id, temp); //Update that elevator has stopped
					}
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					if(!boarding) 
					{
						state = ElevatorState.MovingToDestination;
					}
					else 
					{
						state = ElevatorState.Waiting;
					}
					break;
				
				/**
				 * This is the state in which an elevator has just been given instructions, and is on its way to pick up passengers.
				 */
				case MovingToPassengers:
					System.out.println("Please wait until elevator "+id+" has arrived...");
					int timing = 0;
					
					//While we are going up, increment the current floor up by 1, and update the ElevatorSubsystem that we are at a new floor.
					while(nextFloor > floor)
					{
						timing = nextFloor - floor;
						floor++;
						temp = id+"|"+nextFloor+"|1|0|0|"+floor+"|1";//ID|FLOOR|PEOPLE|MOVING|DIRECTION|CURFLOOR|STATE
						sysRef.updateData(id, temp);
						try {
							Thread.sleep((long)(config.getFloatProperty("distanceBetweenFloors") * 1000));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					//While we are going down, increment the current floor down by 1, and update the ElevatorSubsystem that we are at a new floor.
					while(nextFloor < floor)
					{
						timing = floor - nextFloor;
						floor--;
						temp = id+"|"+nextFloor+"|1|0|0|"+floor+"|1";//ID|FLOOR|PEOPLE|MOVING|DIRECTION|CURFLOOR|STATE
						sysRef.updateData(id, temp);
						try {
							Thread.sleep((long)(config.getFloatProperty("distanceBetweenFloors") * 1000));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					System.out.println("Elevator " +id+": has arrived at floor "+nextFloor+".");
					motor.elevatorArrived();
					door.open();
					state = ElevatorState.Stopped;
					break;
				
				/**
				 * This is the state in which passengers have boarded the elevator, and are now being taken to their
				 * destination floor.
				 */
				case MovingToDestination:
					System.out.println("Elevator "+id+": Bringing passengers to floor "+destination+".");
					door.close();
					timing = 0;
					//While we are going up, increment the current floor up by 1, and update the ElevatorSubsystem that we are at a new floor.
					while(destination > nextFloor)
					{
						timing = destination - nextFloor;
						motor.elevatorGoingUp();
						nextFloor++;
						temp = id+"|"+nextFloor+"|1|0|0|"+nextFloor+"|5";//ID|FLOOR|PEOPLE|MOVING|DIRECTION|CURFLOOR|STATE
						sysRef.updateData(id, temp);
						try {
							Thread.sleep((long)(config.getFloatProperty("distanceBetweenFloors") * 1000));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
					}
					//While we are going down, increment the current floor down by 1, and update the ElevatorSubsystem that we are at a new floor.
					while(destination < nextFloor)
					{
						timing = nextFloor - destination;
						motor.elevatorGoingDown();
						nextFloor--;
						temp = id+"|"+nextFloor+"|1|0|0|"+nextFloor+"|5";//ID|FLOOR|PEOPLE|MOVING|DIRECTION|CURFLOOR|STATE
						sysRef.updateData(id, temp);
						try {
							Thread.sleep((long)(config.getFloatProperty("distanceBetweenFloors") * 1000));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					temp = id+"|"+destination+"|"+"1|0|0|"+nextFloor+"|5";
					floor = destination;
					sysRef.updateData(id, temp);
					System.out.println("Elevator " +id+": arrived at floor "+destination+".");
					motor.elevatorArrived();
					door.open();
					state = ElevatorState.Stopped;
					break;
			}
		}
	}
	
}
