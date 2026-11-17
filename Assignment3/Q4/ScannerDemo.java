import java.util.*;
import java.util.concurrent.*;
import java.io.*;
public class ScannerDemo {
    static class Station extends Thread {
        final List<Integer> order;final int id;int scanned;
        Station(int id,List<Integer> order){super("station-"+id);this.id=id;this.order=order;}
        public void run(){try{for(int i=0;i<10;i++){Thread.sleep(1);scanned++;}order.add(id);}catch(InterruptedException e){interrupt();}}
    }
    static class StationTask implements Runnable {
        final int id;final List<Integer> order;int scanned;
        StationTask(int id,List<Integer> order){this.id=id;this.order=order;}
        public void run(){try{for(int i=0;i<10;i++){Thread.sleep(1);scanned++;}order.add(id);}catch(InterruptedException e){Thread.currentThread().interrupt();}}
    }
    public static void main(String[]args)throws Exception {
        if(args.length>0 && args[0].equals("daemon")){
            CountDownLatch entered=new CountDownLatch(1);
            Thread worker=new Thread(()->{System.out.println("daemon began");entered.countDown();try{Thread.sleep(5000);System.out.println("daemon completed");}catch(InterruptedException e){Thread.currentThread().interrupt();}});
            worker.setDaemon(true);worker.start();entered.await();System.out.println("main ends without joining daemon");return;
        }
        FilesHelper.mkdir();
        try(PrintWriter out=new PrintWriter("evidence/q4-orders.csv")){
            out.println("mode,trial,priorities,finish_order,total");
            for(boolean priority:new boolean[]{false,true})for(int trial=1;trial<=10;trial++){
                List<Integer> order=Collections.synchronizedList(new ArrayList<>());Station[] stations=new Station[4];
                for(int i=0;i<4;i++){stations[i]=new Station(i+1,order);if(priority)stations[i].setPriority(i==0?Thread.MAX_PRIORITY:Thread.MIN_PRIORITY);}
                if(trial==1 && !priority)System.out.println("before start="+stations[0].getState()+" alive="+stations[0].isAlive());
                for(Station s:stations)s.start();
                if(trial==1 && !priority)System.out.println("after start="+stations[0].getState()+" alive="+stations[0].isAlive());
                for(Station s:stations)s.join();int total=Arrays.stream(stations).mapToInt(s->s.scanned).sum();
                if(total!=40)throw new AssertionError("total");
                String row="Thread,"+trial+","+priority+",\""+order+"\","+total;out.println(row);System.out.println(row);
                if(trial==1 && !priority)System.out.println("after join="+stations[0].getState()+" alive="+stations[0].isAlive());
            }
            for(int trial=1;trial<=10;trial++){
                List<Integer> order=Collections.synchronizedList(new ArrayList<>());StationTask[] tasks=new StationTask[4];Thread[] threads=new Thread[4];
                for(int i=0;i<4;i++){tasks[i]=new StationTask(i+1,order);threads[i]=new Thread(tasks[i]);threads[i].start();}
                for(Thread t:threads)t.join();int total=Arrays.stream(tasks).mapToInt(t->t.scanned).sum();
                if(total!=40)throw new AssertionError();String row="Runnable,"+trial+",false,\""+order+"\","+total;out.println(row);System.out.println(row);
            }
        }
        Thread direct=new Thread(()->System.out.println("run() executes on "+Thread.currentThread().getName()));direct.run();
        System.out.println("After direct run: state="+direct.getState()+" alive="+direct.isAlive());
        ExecutorService pool=Executors.newFixedThreadPool(4);
        try{List<Future<Integer>> results=new ArrayList<>();for(int i=0;i<4;i++)results.add(pool.submit(()->{for(int k=0;k<10;k++)Thread.sleep(1);return 10;}));int total=0;for(Future<Integer> f:results)total+=f.get();System.out.println("Executor total="+total);}
        finally{pool.shutdown();}
    }
}
class FilesHelper {static void mkdir()throws IOException{java.nio.file.Files.createDirectories(java.nio.file.Path.of("evidence"));}}
