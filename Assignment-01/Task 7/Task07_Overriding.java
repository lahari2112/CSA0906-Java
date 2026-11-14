
class Vehicle {
    String regNo;

    Vehicle(String regNo) {
        this.regNo = regNo;
    }

    double calculateFee() {
        return 0;
    }

    @Override
    public String toString() {
        return "Vehicle Number: " + regNo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vehicle) {
            Vehicle v = (Vehicle) obj;
            return regNo.equals(v.regNo);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return regNo.hashCode();
    }
}

class TwoWheeler extends Vehicle {
    TwoWheeler(String regNo) {
        super(regNo);
    }

    @Override
    double calculateFee() {
        return 10;
    }
}

class Car extends Vehicle {
    Car(String regNo) {
        super(regNo);
    }

    @Override
    double calculateFee() {
        return 30;
    }
}

public class Task07_Overriding {
    public static void main(String[] args) {
        Vehicle v1 = new TwoWheeler("TS09AB1234");
        Vehicle v2 = new Car("TS10CD5678");

        System.out.println(v1);
        System.out.println("Parking Fee: Rs." + v1.calculateFee());

        System.out.println(v2);
        System.out.println("Parking Fee: Rs." + v2.calculateFee());

        Vehicle v3 = new Car("TS10CD5678");
        System.out.println("Same Vehicle: " + v2.equals(v3));
    }
}
