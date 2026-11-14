
class Vehicle {
    String vehicleNo;
    String vehicleType;

    // Default constructor
    Vehicle() {
        this("UNKNOWN", "Car");
    }

    // Parameterized constructor
    Vehicle(String vehicleNo, String vehicleType) {
        this.vehicleNo = vehicleNo;
        this.vehicleType = vehicleType;
    }

    void displayVehicle() {
        System.out.println("Vehicle Number: " + vehicleNo);
        System.out.println("Vehicle Type: " + vehicleType);
    }
}

class ParkingTicket {
    int ticketNumber;
    String vehicleNo;
    String entryTime;

    // Default constructor
    ParkingTicket() {
        this(1001, "TS09AB1234", "10:00 AM");
    }

    // Overloaded constructor
    ParkingTicket(int ticketNumber, String vehicleNo) {
        this(ticketNumber, vehicleNo, "Not Recorded");
    }

    // Parameterized constructor
    ParkingTicket(int ticketNumber, String vehicleNo, String entryTime) {
        this.ticketNumber = ticketNumber;
        this.vehicleNo = vehicleNo;
        this.entryTime = entryTime;
    }

    // Copy constructor
    ParkingTicket(ParkingTicket ticket) {
        this(ticket.ticketNumber + 1, ticket.vehicleNo, ticket.entryTime);
    }

    void displayTicket() {
        System.out.println("Ticket Number: " + ticketNumber);
        System.out.println("Vehicle Number: " + vehicleNo);
        System.out.println("Entry Time: " + entryTime);
    }
}

public class Task04_Constructors {
    public static void main(String[] args) {

        Vehicle v = new Vehicle("TS09AB1234", "Car");
        v.displayVehicle();

        System.out.println();

        ParkingTicket t1 = new ParkingTicket();
        t1.displayTicket();

        System.out.println();

        ParkingTicket t2 = new ParkingTicket(1002, "TS10CD5678");
        t2.displayTicket();

        System.out.println();

        ParkingTicket t3 = new ParkingTicket(t1);
        t3.displayTicket();
    }
}
