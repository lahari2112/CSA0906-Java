interface Chargeable {
    double chargingFee(int kWh);

    default void displayChargingInfo() {
        System.out.println("EV Charging Available");
    }

    static double standardRate() {
        return 12;
    }
}

abstract class Vehicle {
    String regNo;

    Vehicle(String regNo) {
        this.regNo = regNo;
    }

    abstract double ratePerHour();

    double calculateFee(int hours) {
        return hours * ratePerHour();
    }
}

class TwoWheeler extends Vehicle {
    TwoWheeler(String regNo) {
        super(regNo);
    }

    double ratePerHour() {
        return 10;
    }
}

class Car extends Vehicle {
    Car(String regNo) {
        super(regNo);
    }

    double ratePerHour() {
        return 30;
    }
}

class ElectricCar extends Car implements Chargeable {
    ElectricCar(String regNo) {
        super(regNo);
    }

    public double chargingFee(int kWh) {
        return kWh * Chargeable.standardRate();
    }
}

public class Task09_Abstraction {
    public static void main(String[] args) {

        Vehicle v1 = new TwoWheeler("TS01AB1234");
        Vehicle v2 = new Car("TS02CD5678");
        ElectricCar ev = new ElectricCar("TS03EF9012");

        System.out.println("Two Wheeler Fee: Rs." +
                v1.calculateFee(2));

        System.out.println("Car Fee: Rs." +
                v2.calculateFee(2));

        ev.displayChargingInfo();

        System.out.println("Electric Car Parking Fee: Rs." +
                ev.calculateFee(2));

        System.out.println("Charging Fee: Rs." +
                ev.chargingFee(10));
    }
}