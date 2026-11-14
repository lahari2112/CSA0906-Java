class ParkingSlot {
    private int slotNumber;
    private boolean occupied;

    ParkingSlot(int slotNumber) {
        setSlotNumber(slotNumber);
        occupied = false;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(int slotNumber) {
        if (slotNumber > 0) {
            this.slotNumber = slotNumber;
        } else {
            System.out.println("Invalid Slot Number");
        }
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }
}

class ParkingTicket {
    private String vehicleNumber;
    private Integer duration;

    ParkingTicket(String vehicleNumber, Integer duration) {
        this.vehicleNumber = vehicleNumber;
        setDuration(duration);
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        if (duration != null && duration >= 0) {
            this.duration = duration;
        } else {
            System.out.println("Invalid Parking Duration");
        }
    }
}

public class Task10_Encapsulation {
    public static void main(String[] args) {

        ParkingSlot slot = new ParkingSlot(5);
        ParkingTicket ticket =
                new ParkingTicket("TS09AB1234", 120);

        System.out.println("Slot Number: " +
                slot.getSlotNumber());

        System.out.println("Slot Occupied: " +
                slot.isOccupied());

        System.out.println("Vehicle Number: " +
                ticket.getVehicleNumber());

        System.out.println("Parking Duration: " +
                ticket.getDuration() + " minutes");

        slot.setOccupied(true);

        System.out.println("Updated Slot Status: " +
                slot.isOccupied());

        System.out.println("Integer Comparison: " +
                Integer.valueOf(120).equals(ticket.getDuration()));
    }
}