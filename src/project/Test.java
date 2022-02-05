/**
 * 
 */
package project;

/**
 * @author Ryan
 *
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Network net = new Network();
		Floor floor1 = new Floor(1, net);
		Scheduler Sche1 = new Scheduler(2, net);
		Elevator ele1 = new Elevator(3, net);
	}

}
