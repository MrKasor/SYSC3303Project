/**
 * 
 */
package project;

import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;

/**
 * @author Ryan, Colton
 *
 */
public class Floor extends Thread{
	private Network netRef;
	private int id, count = 0;
	private boolean hasData = false, run = true;
	private ArrayList<String> data = new ArrayList<String>();
	
	public Floor(int id, Network netRef) {
		this.id=id;
		this.netRef=netRef;
		data = read();
		hasData = true;
		start();
	}
	
	public ArrayList<String> read(){
		ArrayList<String> data = new ArrayList<String>();
		
		try {
	        File file = new File("input.txt");
	
	        Scanner input = new Scanner(file);
	
	        while (input.hasNextLine()) {
	            String line = input.nextLine();
	            data.add(line);
	            System.out.println(line);
	        }
	        input.close();
	
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
		return data;
	}
	
	public void run() {	
		while(run) {
			if(hasData) {
				netRef.transfer(data, 2, id);
				hasData = false;
				
				count++;
				if(count == 3) {
					run = false;
				}
			}
			else {
				data = netRef.recieve(id);
				hasData = true;
			}
		} 
	}
}
