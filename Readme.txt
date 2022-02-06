ELEVATOR SUBSYSTEM ITERATION 1

INTRODUCTION
This project introduces the concept of concurrency between two or more threads (int his iteration, between subsystems). The floor event reads in event from using a specific format -
time, elevator number, and button. The line of input is then sent to the scheduler. The elevators then make a call to the scheduler to retrieve instructions on what to do, if any.
In this iteration, the scheduler is used as a communication channel for the Floor and the Elevator.

TEAM MEMBERS:
- Colton North
- Rami Haddad
- Mak Tyler
- Osas Iyamu

FILENAMES AND EXPLANATION
ELEVATOR:
This class is responsible for receiving information from the floor class through the network and will do something based on what it has received.

FLOOR:
This class sends information to the elevator class based on the event that occurs from the user. It sends this information through the network class.

NETWORK:
This class is the buffer class for the threads. It contains the information being passed around.

NETWORK TEST:
This class tests the methods of receive and transfer in the network class. It ensures that the class performs its assigned function.

SCHEDULER:
This class is the communication channel between the floor and the elevator.

TEST:
This is used to run the program.

SET UP INSTRUCTIONS
The program is set up to run from the test class. In the test class, click run.