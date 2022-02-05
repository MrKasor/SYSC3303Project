/**
 * 
 */
package project;

/**
 * @author Ryan, Colton
 * 
 * Class Test is used to verify that data is being
 * transferred and received by the various classes.
 *
 */
public class Test {

	/**
	 * Creates threads of all the Classes, which start() 
	 * when their constructor is called.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Network net = new Network();
		Floor floor1 = new Floor(1, net);
		Scheduler Sche1 = new Scheduler(2, net);
		Elevator ele1 = new Elevator(3, net);
	}

}
