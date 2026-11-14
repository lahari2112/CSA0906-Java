
class ParkingTicket {
    static int ticketCounter = 1000;

    int ticketNumber;
    String vehicleNumber;
    String entryTime;

    ParkingTicket(String vehicleNumber, String entryTime) {
        ticketCounter++;
        this.ticketNumber = ticketCounter;
        this.vehicleNumber = vehicleNumber;
        this.entryTime = entryTime;
    }

    static int getTicketCounter() {
        return ticketCounter;
    }

    void displayTicket() {
        System.out.println("Ticket Number: " + ticketNumber);
        System.out.println("Vehicle Number: " + vehicleNumber);
        System.out.println("Entry Time: " + entryTime);
    }
}

public class Task05_StaticMembers {
    public static void main(String[] args) {

        ParkingTicket t1 =
            new ParkingTicket("TS09AB1234", "10:00 AM");

        ParkingTicket t2 =
            new ParkingTicket("TS10CD5678", "10:30 AM");

        t1.displayTicket();

        System.out.println();

        t2.displayTicket();

        System.out.println();

        System.out.println("Total Tickets: " +
            ParkingTicket.getTicketCounter());

        t1 = null;

        System.out.println("First ticket reference released.");
    }
}
