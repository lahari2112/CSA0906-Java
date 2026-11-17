import java.io.*;
import java.nio.file.*;
import java.util.*;
public class SalesDemo {
    static final Set<Closeable> live=Collections.newSetFromMap(new IdentityHashMap<>());
    static class Reader extends BufferedReader {
        Reader(Path p)throws IOException{super(Files.newBufferedReader(p));live.add(this);}
        public void close()throws IOException{try{super.close();}finally{live.remove(this);}}
    }
    static class Writer extends BufferedWriter {
        Writer(Path p)throws IOException{super(Files.newBufferedWriter(p));live.add(this);}
        public void close()throws IOException{try{super.close();}finally{live.remove(this);}}
    }
    static long sum(Reader r,Path p)throws IOException {
        long total=0;String line;int n=0;
        while((line=r.readLine())!=null){n++;try{long amount=Long.parseLong(line);if(amount<0)throw new NumberFormatException("negative");total=Math.addExact(total,amount);}
            catch(RuntimeException e){throw new IOException("Invalid sale in "+p+" line "+n,e);}}
        return total;
    }
    static void buggy(List<Path> inputs,Path output)throws IOException {
        Writer writer=null;
        try {
            writer=new Writer(output);
            for(Path p:inputs){Reader reader=new Reader(p);writer.write(p.getFileName()+","+sum(reader,p)+"\n");reader.close();}
        } finally {if(writer!=null)writer.close();} // Reader leaks if sum throws before close.
    }
    static void manual(List<Path> inputs,Path output)throws IOException {
        Writer w=null;Throwable failure=null;
        try {
            w=new Writer(output);
            for(Path p:inputs){Reader r=null;Throwable readFailure=null;
                try{r=new Reader(p);w.write(p.getFileName()+","+sum(r,p)+"\n");}
                catch(IOException|RuntimeException|Error e){readFailure=e;throw e;}
                finally{if(r!=null)try{r.close();}catch(IOException close){if(readFailure!=null)readFailure.addSuppressed(close);else throw close;}}
            }
        }catch(IOException|RuntimeException|Error e){failure=e;throw e;}
        finally{if(w!=null)try{w.close();}catch(IOException close){if(failure!=null)failure.addSuppressed(close);else throw close;}}
    }
    static void twr(List<Path> inputs,Path output)throws IOException {
        try(Writer w=new Writer(output)){
            for(Path p:inputs)try(Reader r=new Reader(p)){w.write(p.getFileName()+","+sum(r,p)+"\n");}
        }
    }
    static class BadClose implements AutoCloseable {
        final String name;BadClose(String n){name=n;}
        public void close()throws IOException{System.out.println("Closing "+name);throw new IOException("close "+name);}
    }
    public static void main(String[]args)throws Exception {
        Path out=Path.of("Q3/generated");Files.createDirectories(out);
        List<Path> valid=List.of(Path.of("Q3/input/store1.txt"),Path.of("Q3/input/store2.txt"));
        List<Path> invalid=List.of(valid.get(0),Path.of("Q3/input/corrupt.txt"));
        manual(valid,out.resolve("manual.csv"));twr(valid,out.resolve("twr.csv"));
        if(Files.mismatch(out.resolve("manual.csv"),out.resolve("twr.csv"))!=-1)throw new AssertionError("reports differ");
        System.out.println("Reports identical:\n"+Files.readString(out.resolve("twr.csv")));
        for(String mode:List.of("buggy","manual","twr")){
            try{if(mode.equals("buggy"))buggy(invalid,out.resolve(mode+"-partial.csv"));else if(mode.equals("manual"))manual(invalid,out.resolve(mode+"-partial.csv"));else twr(invalid,out.resolve(mode+"-partial.csv"));}
            catch(IOException e){System.out.println("LOG "+mode+": "+e);}
            System.out.println(mode+" open tracked file resources="+live.size());
            if(live.size()!=(mode.equals("buggy")?1:0))throw new AssertionError("leak count");
            for(Closeable c:new ArrayList<>(live))c.close(); // Clean intentional leak after observing it.
        }
        try(BadClose first=new BadClose("first");BadClose second=new BadClose("second")){throw new IOException("body failed");}
        catch(IOException e){e.printStackTrace(System.out);if(e.getSuppressed().length!=2)throw new AssertionError("suppressed count");}
    }
}
