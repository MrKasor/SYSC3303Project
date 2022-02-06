/**
 * 
 */
package project;

import java.util.ArrayList;

/**
 * @author Ryan, Colton
 * 
 * Class Network is used to synchronize the Threads
 * of the Elevator, Floor, and Scheduler Classes.
 *
 */
public class Network {
	ArrayList<String> netData = new ArrayList<String>();
	boolean hasSomething = false;
	int n = 1;
	
	/**
	 * transfer is used to send data from one thread to another
	 * 
	 * @param data
	 * @param destination
	 * @param id
	 */
	public synchronized void transfer(ArrayList<String> data, int destination, int id) {
		while(hasSomething) {
			try{
				wait();
			} catch (InterruptedException e) {}
		}
		
		if(id == n) {
			n = destination;
			netData = data;
			hasSomething = true;
			System.out.println("Network recieved data from "+id+" to be sent to "+n);
		}
		
		notifyAll();
	}
	
	/**
	 * recieve is used to gather data from another thread.
	 * 
	 * @param destination
	 * @return
	 */
	public synchronized ArrayList<String> recieve(int destination) {	
		while(n != destination) {
			try{
				wait();
			} catch (InterruptedException e) {}
		}
		
		System.out.println("Recieving data at "+n);
		hasSomething = false;
		notifyAll();
		return netData;
	}

}
