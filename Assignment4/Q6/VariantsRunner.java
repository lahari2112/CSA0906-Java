import java.nio.file.*;
import java.util.concurrent.*;
import java.io.*;
public class VariantsRunner {
    public static void main(String[]args)throws Exception {
        String java=Path.of(System.getProperty("java.home"),"bin",System.getProperty("os.name").startsWith("Windows")?"java.exe":"java").toString();
        for(String variant:new String[]{"unsafe","if_variant"}) {
            String name=variant.equals("unsafe")?"SeatBooking":"TokenCounter";
            String logName=variant.equals("unsafe")?"unsafe-scaffold":"if-scaffold";
            try(PrintWriter log=new PrintWriter("evidence/"+logName+".txt")) {
                for(int i=1;i<=10;i++) {
                    Path temp=Files.createTempFile("variant-",".txt");
                    try {
                        Process process=new ProcessBuilder(java,"-cp","build/"+variant,name).redirectErrorStream(true).redirectOutput(temp.toFile()).start();
                        boolean finished=process.waitFor(3,TimeUnit.SECONDS);
                        if(!finished){process.destroyForcibly();process.waitFor();}
                        log.println("Trial "+i);log.print(Files.readString(temp));
                        if(!finished)log.println("TIMEOUT after 3 seconds; demonstration process terminated");
                        log.flush();
                    }finally{Files.deleteIfExists(temp);}
                }
            }
        }
        System.out.println("Ten unsafe and ten if-variant executions saved with 3-second timeouts.");
    }
}
