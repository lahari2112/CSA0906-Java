import java.io.*;
import java.nio.file.*;
class WindowClosedException extends ExamException {WindowClosedException(){super("Exam window has closed");}}
class NotRegisteredException extends ExamException {NotRegisteredException(){super("Student is not registered");}}
class AnswerTooLargeException extends ExamException {AnswerTooLargeException(){super("Answer exceeds 500 KiB");}}
public class PortalDemo {
    static void storage(Path source,Path target)throws SubmissionStorageException {
        try{Files.copy(source,target,StandardCopyOption.REPLACE_EXISTING);}
        catch(IOException e){throw new SubmissionStorageException("Could not store submission",e);}
    }
    static void service(boolean registered,Path source,Path target)throws ExamException {
        if(!registered)throw new NotRegisteredException();
        try{if(Files.size(source)>500*1024)throw new AnswerTooLargeException();}
        catch(IOException e){throw new SubmissionStorageException("Could not inspect answer file",e);}
        storage(source,target);
    }
    static String controller(boolean open,boolean registered,Path source,Path target) {
        try{
            if(!open)throw new WindowClosedException();
            service(registered,source,target);System.out.println("UI: submission accepted");return "OK";
        }catch(ExamException e){
            System.out.println("UI: "+e.getMessage());
            System.out.println("TYPE="+e.getClass().getSimpleName()+" CAUSE="+e.getCause());
            if(e.getCause()!=null){e.printStackTrace(System.out);
                ExamException lost=new ExamException(e.getMessage());System.out.println("Without chaining getCause()="+lost.getCause());}
            return e.getClass().getSimpleName();
        }
    }
    public static void main(String[]args)throws Exception {
        Path dir=Files.createTempDirectory("exam-demo-");
        try{
            Path answer=dir.resolve("answer.txt"),large=dir.resolve("large.txt"),dest=dir.resolve("saved.txt");
            Files.writeString(answer,"Student R1: answers");Files.write(large,new byte[500*1024+1]);
            String[] got={controller(true,true,answer,dest),controller(false,true,answer,dest),
                controller(true,false,answer,dest),controller(true,true,large,dest),
                controller(true,true,answer,dir.resolve("absent-parent/saved.txt"))};
            String[] want={"OK","WindowClosedException","NotRegisteredException","AnswerTooLargeException","SubmissionStorageException"};
            if(!java.util.Arrays.equals(got,want))throw new AssertionError("portal paths");
            System.out.println("All 5 portal scenarios verified");
        }finally{try(var files=Files.walk(dir)){for(Path f:files.sorted(java.util.Comparator.reverseOrder()).toList())Files.delete(f);}}
    }
}
