# Task 1 – Problem Analysis and Objects

## Aim
To analyse the Campus Smart-Parking System and identify the objects, classes, responsibilities, inputs, outputs and business rules required for implementation.

## Inputs
- Vehicle registration number
- Vehicle type
- Entry time and exit time
- Parking slot availability
- Monthly pass details

## Outputs
- Allocated parking slot
- Unique parking ticket
- Entry and exit details
- Calculated parking fee
- Updated slot availability

## Candidate Classes and Responsibilities

1. Vehicle – Stores vehicle registration number and vehicle type.
2. ParkingSlot – Stores slot number, floor number and occupancy status.
3. ParkingTicket – Stores ticket number, vehicle details and entry time.
4. ParkingFacility – Manages floors, slots and vehicle allocation.
5. ParkingFee – Calculates the parking fee based on vehicle type and duration.
6. MonthlyPass – Stores pass details for eligible students and staff.

## Business Rules
- A parking slot cannot hold two vehicles at the same time.
- Every ticket must have a unique ticket number.
- A vehicle must be assigned a suitable available slot.
- The slot must be released when the vehicle exits.
- Staff and students with valid monthly passes can park free.
- Visitors must pay the applicable parking fee.
- EV charging slots are charged separately.

## Assumptions
- Every vehicle has a unique registration number.
- Parking slots have unique numbers within each floor.
- Entry and exit times are recorded correctly.
- Parking fees depend on vehicle type and parking duration.

## Alternative Concept and Complexity Analysis

Instead of storing vehicles as objects, the system can use parallel arrays such as regNo[], type[] and entryTime[].

- Object-based design stores related vehicle information together.
- Parallel arrays store each attribute separately.
- Searching by registration number takes O(n) time in both approaches using sequential search.
- Adding a new attribute is easier in object-oriented design because a field can be added to the Vehicle class.
- Parallel arrays require an additional array and careful index management.

## Result
The candidate classes, responsibilities, inputs, outputs, business rules and assumptions for the Campus Smart-Parking System were identified successfully.
