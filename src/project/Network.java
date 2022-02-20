/**
 * 
 */
package project;

import java.util.*;

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
	Hashtable<String, String> dictionary = new Hashtable<>();
	
	//Function for inserting values into Hashtable
	public void createHashTable() 
	{
		dictionary.put("1", "Floor");
		dictionary.put("2", "Scheduler");
		dictionary.put("3", "Elevator");
	}
	
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
			String s = "" + id;
			String des = "" + n;
			System.out.println("Network recieved data from "+dictionary.get(s)+" to be sent to "+dictionary.get(des));
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
		String des = "" + n;
		System.out.println("Recieving data at "+dictionary.get(des));
		hasSomething = false;
		notifyAll();
		return netData;
	}
}
