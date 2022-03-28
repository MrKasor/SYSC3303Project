package project;

import java.util.ArrayList;


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
	private int floor;
	private boolean hasPeople = false;
	private boolean boarding = true;
	
	
	/**
	 * Constructor for Elevator. Starts an Elevator thread.
	 * 
	 * @param id
	 * @param netRef
	 */
	public Elevator(int id, int floor, ElevatorSubsystem sysRef) {
		this.id=id;
		this.floor=floor;
		this.sysRef=sysRef;
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
					//System.out.println("Elevator "+id+" is currently waiting for further instruction...");
					nextFloor = sysRef.send(id);
					destination = sysRef.getDestinationFloor();
					
					if(nextFloor > floor) {//Going up
						temp = id+"|"+nextFloor+"|1|0|0";//ID|FLOOR|PEOPLE|MOVING|DIRECTION
					}
					else {//Going Down
						temp = id+"|"+nextFloor+"|1|0|1";//ID|FLOOR|PEOPLE|MOVING|DIRECTION
					}
					System.out.println("Elevator "+id+": moving to floor "+nextFloor);
					sysRef.updateData(id, temp); //update that elevator is moving
					state = ElevatorState.MovingToPassengers;
					break;
					
				case Stopped:
					//Sleep while customers are getting on/off, then switch to waiting.
					if(boarding)
					{
						hasPeople = true;
						System.out.println("\nElevator "+id+": stopped, people are boarding.\n");
						boarding = false;
					}
					else
					{
						hasPeople = false;
						System.out.println("\nElevator "+id+": stopped, people are disembarking.\n");
						boarding = true;
					}
					
					if(hasPeople) {//Getting on
						temp = id+"|"+floor+"|1|0|0";//ID|FLOOR|PEOPLE|MOVING|DIRECTION
					}
					else {//Getting off
						temp = id+"|"+floor+"|0|0|0";//ID|FLOOR|PEOPLE|MOVING|DIRECTION
					}
					
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					sysRef.updateData(id, temp); //Update that elevator has stopped
					if(!boarding) 
					{
						state = ElevatorState.MovingToDestination;
					}
					else 
					{
						state = ElevatorState.Waiting;
					}
					break;
				
				case MovingToPassengers:
					System.out.println("Please wait until elevator "+id+" has arrived...");
					int timing = 0;
					if(nextFloor > floor)
					{
						timing = nextFloor - floor;
					}
					else
						timing = floor - nextFloor;
					try {
						Thread.sleep(timing * 2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					System.out.println("Elevator " +id+": has arrived at floor "+nextFloor+".");
					state = ElevatorState.Stopped;
					break;
					
				case MovingToDestination:
					System.out.println("Elevator "+id+": Bringing passengers to floor "+destination+".");
					timing = 0;
					if(destination > nextFloor)
					{
						timing = destination - nextFloor;
					}
					else
						timing = nextFloor - destination;
					try {
						Thread.sleep(timing * 2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					temp = id+"|"+destination+"|"+"0|0|0";
					floor = destination;
					sysRef.updateData(id, temp);
					System.out.println("Elevator " +id+": arrived at floor "+destination+".");
					state = ElevatorState.Stopped;
					break;
			}
		}
	}
	
}
