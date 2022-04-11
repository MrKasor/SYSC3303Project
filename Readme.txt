Iteration 5:

Intstructions
Run ElevatorSubsystem, GUI, Scheduler, then FloorSubsystem in that order.

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
- Updated Scheduler to account for elevators that are stuck.
- Created Motor, ElevatorLamp, ElevatorButton, and Door classes.
- Updated Elevator class to include the functionality of the new classes.

Ryan Kasor
- Created basis for the Elevator Class.
- Reworked the ElevatorSubsystem Class to use Sockets.
- Created the GUI, reworked the Elevator/Scheduler Classes to work with the GUI.
- Reworked the Elevator class to work with GUI timings
- Created helper class for Socket use called PacketHelper.
- Created helper class for Config Files called Config to allow config file use and created the config file.
- Split the FloorSubsystem from the Basic Floor Class.
- Many Bugfixes in the Elevator/ElevatorSubSystem/FloorSubsystem/Floor/Scheduler/GUI classes.
- Created the FloorLamp/FloorButton.
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
