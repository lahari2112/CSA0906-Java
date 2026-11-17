import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.io.*;
public class BookingExperiment {
    interface Booking {boolean book(int seat);}
    static class Unsafe implements Booking {
        final boolean[] sold;Unsafe(int n){sold=new boolean[n];}
        public boolean book(int s){if(sold[s])return false;Thread.yield();sold[s]=true;return true;}
    }
    static class MethodLock implements Booking {
        final boolean[] sold;MethodLock(int n){sold=new boolean[n];}
        public synchronized boolean book(int s){if(sold[s])return false;sold[s]=true;return true;}
    }
    static class BlockLock implements Booking {
        final boolean[] sold;private final Object lock=new Object();BlockLock(int n){sold=new boolean[n];}
        public boolean book(int s){synchronized(lock){if(sold[s])return false;sold[s]=true;return true;}}
    }
    static class Cas implements Booking {
        final AtomicIntegerArray sold;Cas(int n){sold=new AtomicIntegerArray(n);}
        public boolean book(int s){return sold.compareAndSet(s,0,1);}
    }
    static int forcedRace()throws Exception {
        boolean[] sold={false};CyclicBarrier bothChecked=new CyclicBarrier(2);AtomicInteger accepted=new AtomicInteger();
        Runnable user=()->{if(!sold[0]){try{bothChecked.await(2,TimeUnit.SECONDS);}catch(Exception e){throw new RuntimeException(e);}sold[0]=true;accepted.incrementAndGet();}};
        Thread a=new Thread(user),b=new Thread(user);a.start();b.start();a.join();b.join();return accepted.get();
    }
    static void measure(ExecutorService pool,PrintWriter out,String name,Booking booking,int trial)throws Exception {
        AtomicInteger accepted=new AtomicInteger();CountDownLatch ready=new CountDownLatch(8),start=new CountDownLatch(1);List<Future<?>> tasks=new ArrayList<>();
        for(int worker=0;worker<8;worker++){final int w=worker;tasks.add(pool.submit(()->{
            ready.countDown();try{start.await();for(int i=w;i<10000;i+=8)if(booking.book(i%100))accepted.incrementAndGet();}
            catch(InterruptedException e){Thread.currentThread().interrupt();throw new RuntimeException(e);}
        }));}
        ready.await();long t=System.nanoTime();start.countDown();for(Future<?> f:tasks)f.get();double ms=(System.nanoTime()-t)/1e6;
        if(!name.equals("unsafe")&&accepted.get()!=100)throw new AssertionError("atomic booking");
        String row=name+","+trial+",10000,100,"+accepted+","+Math.max(0,accepted.get()-100)+","+ms;out.println(row);System.out.println(row);
    }
    public static void main(String[]args)throws Exception {
        java.nio.file.Files.createDirectories(java.nio.file.Path.of("evidence"));
        try(PrintWriter out=new PrintWriter("evidence/q5-forced-race.csv")){
            out.println("trial,seats,accepted,extra_sales");for(int i=1;i<=10;i++){int n=forcedRace();if(n!=2)throw new AssertionError("race not forced");String row=i+",1,"+n+","+(n-1);out.println(row);System.out.println("Forced race "+row);}
        }
        ExecutorService pool=Executors.newFixedThreadPool(8);
        try(PrintWriter out=new PrintWriter("evidence/q5-bookings.csv")){
            out.println("mode,trial,attempts,seats,accepted,extra_sales,milliseconds");
            for(int trial=1;trial<=10;trial++){
                // Rotate methods so one method does not always get the coldest trial.
                String[] methods={"unsafe","method","block","CAS"};
                for(int k=0;k<4;k++){String name=methods[(k+trial)%4];Booking b=switch(name){case "unsafe"->new Unsafe(100);case "method"->new MethodLock(100);case "block"->new BlockLock(100);default->new Cas(100);};measure(pool,out,name,b,trial);}
            }
        }finally{pool.shutdown();}
    }
}
