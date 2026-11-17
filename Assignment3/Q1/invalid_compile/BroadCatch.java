import java.io.*;
public class BroadCatch {
    public static void main(String[]args) {
        try {throw new FileNotFoundException("reader missing");}
        catch(Exception e){System.out.println("broad");}
        catch(FileNotFoundException e){System.out.println("narrow");}
    }
}
