import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
public class ResultProcessor {
    record Outcome(String id,boolean success,double average,String grade,String error){}
    static class ScoreTask implements Callable<Outcome> {
        final Path path;ScoreTask(Path path){this.path=path;}
        public Outcome call(){
            String id=path.getFileName().toString();
            try {
                String line=Files.readString(path).trim();String[] values=line.split(",",-1);
                if(values.length!=3)throw new IllegalArgumentException("Expected three marks");
                int sum=0;boolean pass=true;
                for(String v:values){int mark=Integer.parseInt(v.trim());if(mark<0||mark>100)throw new IllegalArgumentException("Mark outside 0..100");sum+=mark;pass &= mark>=40;}
                return new Outcome(id,true,sum/3.0,pass?"PASS":"FAIL","");
            }catch(IOException|IllegalArgumentException e){return new Outcome(id,false,0,"",e.getClass().getSimpleName()+": "+e.getMessage());}
        }
    }
    static List<Outcome> process(List<Path> inputs,int size)throws Exception {
        Set<String> ids=new HashSet<>();for(Path p:inputs)if(!ids.add(p.getFileName().toString()))throw new IllegalArgumentException("Duplicate manifest ID");
        ExecutorService pool=Executors.newFixedThreadPool(size);ConcurrentHashMap<String,Outcome> results=new ConcurrentHashMap<>();
        List<Future<Outcome>> futures=new ArrayList<>();
        try {
            for(Path path:inputs)futures.add(pool.submit(()->{Outcome o=new ScoreTask(path).call();results.put(o.id(),o);return o;}));
            for(int i=0;i<futures.size();i++)try{futures.get(i).get();}
                catch(ExecutionException e){
                    if(e.getCause() instanceof Error fatal)throw fatal;
                    String id=inputs.get(i).getFileName().toString();results.put(id,new Outcome(id,false,0,"","Unexpected task failure: "+e.getCause()));
                }
            if(results.size()!=inputs.size())throw new AssertionError("Incomplete summary");
            return results.values().stream().sorted(Comparator.comparing(Outcome::id)).toList();
        }catch(InterruptedException e){for(Future<?> f:futures)f.cancel(true);Thread.currentThread().interrupt();throw e;}
        finally{pool.shutdownNow();if(!pool.awaitTermination(10,TimeUnit.SECONDS))System.err.println("WARNING: workers did not terminate within 10 seconds");}
    }
    static List<Path> generate(Path dir,int n)throws IOException {
        Files.createDirectories(dir);List<Path> paths=new ArrayList<>();
        for(int i=0;i<n;i++){Path p=dir.resolve(String.format("score%05d.txt",i));paths.add(p);
            if(i%100==0)continue;Files.writeString(p,i%100==1?"CORRUPT":"60,70,80");}
        return paths;
    }
    static void writeSummary(List<Outcome> results,Path path)throws IOException {
        try(PrintWriter out=new PrintWriter(Files.newBufferedWriter(path))){out.println("file,processed,average,grade,error");for(Outcome o:results)out.println(o.id()+","+o.success()+","+o.average()+","+o.grade()+",\""+o.error().replace("\"","\"\"")+"\"");}
    }
    public static void main(String[]args)throws Exception {
        Files.createDirectories(Path.of("evidence"));
        List<Path> small=List.of(Path.of("Q7/input/a.txt"),Path.of("Q7/input/b.txt"),Path.of("Q7/input/c.txt"),Path.of("Q7/input/d.txt"),Path.of("Q7/input/corrupt.txt"),Path.of("Q7/input/missing.txt"));
        List<Outcome> sample=process(small,2);long good=sample.stream().filter(Outcome::success).count();
        if(good!=4||sample.size()-good!=2)throw new AssertionError("small counts");
        Map<String,Outcome> byId=new HashMap<>();for(Outcome o:sample){System.out.println(o);byId.put(o.id(),o);}
        if(byId.get("a.txt").average()!=70.0||!byId.get("b.txt").grade().equals("FAIL")||!byId.get("c.txt").grade().equals("PASS")||byId.get("d.txt").average()!=100.0)throw new AssertionError("small results");
        writeSummary(sample,Path.of("evidence/q7-small-results.csv"));
        Path dir=Files.createTempDirectory("score-experiment-");
        try {
            List<Path> paths=generate(dir,10000);process(paths.subList(0,200),2);
            try(PrintWriter timing=new PrintWriter("evidence/q7-timings.csv")){
                timing.println("trial,pool,files,success,failure,milliseconds");
                int[][] orders={{1,2,4,8},{8,4,2,1},{2,8,1,4}};
                for(int trial=1;trial<=3;trial++)for(int pool:orders[trial-1]){
                    long start=System.nanoTime();List<Outcome> results=process(paths,pool);double ms=(System.nanoTime()-start)/1e6;
                    long ok=results.stream().filter(Outcome::success).count();
                    if(ok!=9800||results.size()-ok!=200)throw new AssertionError("large counts");
                    long missing=results.stream().filter(o->o.error().startsWith("NoSuchFileException")).count();
                    if(missing!=100)throw new AssertionError("missing count");
                    if(results.stream().filter(Outcome::success).anyMatch(o->o.average()!=70||!o.grade().equals("PASS")))throw new AssertionError("large grades");
                    String row=trial+","+pool+",10000,"+ok+","+(results.size()-ok)+","+ms;timing.println(row);timing.flush();System.out.println(row);
                    if(trial==3 && pool==4)writeSummary(results,Path.of("evidence/q7-all-results.csv"));
                }
            }
        }finally{try(var files=Files.walk(dir)){for(Path p:files.sorted(Comparator.reverseOrder()).toList())Files.delete(p);}}
        System.out.println("No unhandled per-file failures observed; all summary assertions passed.");
    }
}
