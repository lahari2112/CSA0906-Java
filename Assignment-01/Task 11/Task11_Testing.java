import java.util.HashMap;
import java.util.Map;

public class Task11_Testing {

    static class Ticket {
        String vehicleNumber;
        String type;
        int slot;
        double fee;

        Ticket(String vehicleNumber, String type, int slot, double fee) {
            this.vehicleNumber = vehicleNumber;
            this.type = type;
            this.slot = slot;
            this.fee = fee;
        }
    }

    static class ParkingSystem {
        boolean[] slots = new boolean[2];
        Map<String, Ticket> activeTickets = new HashMap<>();
        double totalCollection = 0;

        double calculateFee(String type, int minutes) {
            if (minutes == 0) {
                return 0;
            }

            int hours = (minutes + 59) / 60;
            int rate = type.equals("TwoWheeler") ? 10 : 30;

            return hours * rate;
        }

        void enterVehicle(String number, String type, int minutes) {

            if (!type.equals("TwoWheeler")
                    && !type.equals("Car")
                    && !type.equals("ElectricCar")) {
                System.out.println("Invalid vehicle type");
                return;
            }

            if (activeTickets.containsKey(number)) {
                System.out.println("Duplicate registration number");
                return;
            }

            int slot = -1;

            for (int i = 0; i < slots.length; i++) {
                if (!slots[i]) {
                    slot = i;
                    break;
                }
            }

            if (slot == -1) {
                System.out.println("Parking full");
                return;
            }

            slots[slot] = true;

            double fee = calculateFee(type, minutes);

            Ticket ticket = new Ticket(number, type, slot, fee);
            activeTickets.put(number, ticket);

            totalCollection += fee;

            System.out.println("Vehicle " + number
                    + " allocated Slot " + (slot + 1));
            System.out.println("Parking Fee: Rs." + fee);
        }

        void releaseInvalidSlot(int slotNumber) {
            if (slotNumber < 1 || slotNumber > slots.length) {
                System.out.println("Invalid slot number");
            }
        }

        void exitVehicle(String number) {
            Ticket ticket = activeTickets.remove(number);

            if (ticket != null) {
                slots[ticket.slot] = false;
                System.out.println("Vehicle " + number
                        + " exited successfully");
            }
        }
    }

    public static void main(String[] args) {

        ParkingSystem parking = new ParkingSystem();

        System.out.println("NORMAL TESTING");

        parking.enterVehicle("TS01AB1234", "TwoWheeler", 60);
        parking.enterVehicle("TS02CD5678", "Car", 120);

        System.out.println("\nBOUNDARY TESTING");

        parking.enterVehicle("TS03EF9012", "ElectricCar", 30);

        System.out.println("Zero Minute Fee: Rs."
                + parking.calculateFee("Car", 0));

        parking.enterVehicle("TS04GH1111", "Car", 60);

        System.out.println("\nINVALID TESTING");

        parking.enterVehicle("TS05IJ2222", "Bus", 60);
        parking.enterVehicle("TS01AB1234", "Car", 60);
        parking.releaseInvalidSlot(5);

        System.out.println("\nEXIT TESTING");

        parking.exitVehicle("TS01AB1234");
        parking.enterVehicle("TS03EF9012", "ElectricCar", 60);

        System.out.println("\nTotal Collection: Rs."
                + parking.totalCollection);
    }
}