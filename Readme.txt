Iteration 4:

TEAM MEMBERS:
- Colton North
- Rami Haddad
- Ryan Kasor
- Osas Iyamu

Contributions:

Colton North
- Updated Scheduler to assign which elevator picks up passengers not only based on the elevators current floor, but the amount of passengers / if it is currently moving.
- Updated ElevatorSubsystem so that elevators are actually moving now. Fixed communication between scheduler and ElevatorSubsystem.
- Updated Elevator class with another state, and updated the timing it takes for elevators to move floors, as well as the information they transmit.
- Updated Floor Class so that it will send all values in input.txt.
- Updated input.txt to request multiple times (3).

Ryan Kasor
- Created basis for the Elevator Class.
- Reworked the ElevatorSubsystem Class to use Sockets.
- Created helper class for Socket use called PacketHelper.
- Setup the reading of input.txt in Floor Class.
- Setup state machine in Elevator Class.

Osas Iyamu
- Created ElevatorSubsystemTest.java
- Created ElevatorTest.java
- Created FloorTest.java
- Created SchedulerTest.java
- Sequence Diagram

Rami Haddad
- Class Diagram
- State Machine Diagram