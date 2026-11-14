
import java.util.Scanner;

public class ParkingInput {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Vehicle ID: ");
        int vehicleId = sc.nextInt();

        System.out.print("Enter Vehicle Number: ");
        String vehicleNumber = sc.next();

        System.out.print("Enter Parking Fee: ");
        double parkingFee = sc.nextDouble();

        System.out.println("Vehicle ID: " + vehicleId);
        System.out.println("Vehicle Number: " + vehicleNumber);
        System.out.println("Parking Fee: " + parkingFee);

        sc.close();
    }
}
