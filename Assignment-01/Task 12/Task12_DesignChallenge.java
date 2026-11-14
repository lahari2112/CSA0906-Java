public class Task12_DesignChallenge {

    // Abstract Vehicle class
    static abstract class Vehicle {
        String vehicleNumber;

        Vehicle(String vehicleNumber) {
            this.vehicleNumber = vehicleNumber;
        }

        abstract double calculateFee(int hours, int entryHour);
    }

    // Normal car
    static class Car extends Vehicle {
        Car(String number) {
            super(number);
        }

        double calculateFee(int hours, int entryHour) {
            return hours * 30;
        }
    }

    // E-bike with flat fee
    static class Ebike extends Vehicle {
        Ebike(String number) {
            super(number);
        }

        double calculateFee(int hours, int entryHour) {
            return 15;
        }
    }

    // Delivery van with peak-hour pricing
    static class DeliveryVan extends Vehicle {
        DeliveryVan(String number) {
            super(number);
        }

        double calculateFee(int hours, int entryHour) {
            if (entryHour >= 9 && entryHour < 17) {
                return hours * 60;
            }
            return hours * 30;
        }
    }

    // One-day visitor pass
    interface OneDayPass {
        boolean isValid();
    }

    static class Visitor implements OneDayPass {
        String visitorName;

        Visitor(String name) {
            visitorName = name;
        }

        public boolean isValid() {
            return true;
        }
    }

    // Parking facility with different slots on each floor
    static class ParkingFacility {
        String[][] slots;

        ParkingFacility(int[] slotsPerFloor) {
            slots = new String[slotsPerFloor.length][];

            for (int i = 0; i < slotsPerFloor.length; i++) {
                slots[i] = new String[slotsPerFloor[i]];
            }
        }

        void allocateSlot(String vehicleNumber) {
            for (int floor = 0; floor < slots.length; floor++) {
                for (int slot = 0; slot < slots[floor].length; slot++) {
                    if (slots[floor][slot] == null) {
                        slots[floor][slot] = vehicleNumber;

                        System.out.println(vehicleNumber
                                + " allocated Floor " + (floor + 1)
                                + ", Slot " + (slot + 1));
                        return;
                    }
                }
            }

            System.out.println("Parking Full");
        }
    }

    public static void main(String[] args) {

        // Five floors with different slot counts
        ParkingFacility parking =
                new ParkingFacility(new int[]{2, 3, 2, 4, 3});

        Vehicle car = new Car("TS01AB1234");
        Vehicle ebike = new Ebike("TS02CD5678");
        Vehicle van = new DeliveryVan("TS03EF9012");

        System.out.println("PARKING SLOT ALLOCATION");

        parking.allocateSlot(car.vehicleNumber);
        parking.allocateSlot(ebike.vehicleNumber);
        parking.allocateSlot(van.vehicleNumber);

        System.out.println("\nPARKING FEE DETAILS");

        System.out.println("Car Fee: Rs."
                + car.calculateFee(2, 10));

        System.out.println("E-bike Flat Fee: Rs."
                + ebike.calculateFee(2, 10));

        System.out.println("Delivery Van Peak Hour Fee: Rs."
                + van.calculateFee(2, 10));

        System.out.println("Delivery Van Normal Hour Fee: Rs."
                + van.calculateFee(2, 19));

        System.out.println("\nVISITOR PASS");

        Visitor visitor = new Visitor("Guest");

        if (visitor.isValid()) {
            System.out.println("Visitor One-Day Pass: Valid");
            System.out.println("Visitor Parking Fee: Rs.0");
        }
    }
}