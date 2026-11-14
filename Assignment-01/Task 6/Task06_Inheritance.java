
class Vehicle {
    String regNo;

    Vehicle(String regNo) {
        this.regNo = regNo;
        System.out.println("Vehicle constructor called");
    }
}

class TwoWheeler extends Vehicle {
    TwoWheeler(String regNo) {
        super(regNo);
        System.out.println("TwoWheeler constructor called");
    }
}

class Car extends Vehicle {
    Car(String regNo) {
        super(regNo);
        System.out.println("Car constructor called");
    }
}

class ElectricCar extends Car {
    ElectricCar(String regNo) {
        super(regNo);
        System.out.println("ElectricCar constructor called");
    }
}

public class Task06_Inheritance {
    public static void main(String[] args) {
        ElectricCar e = new ElectricCar("TS09AB1234");

        System.out.println("Registration Number: " + e.regNo);
    }
}
