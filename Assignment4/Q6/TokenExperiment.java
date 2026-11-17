import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.io.*;
public class TokenExperiment {
    static class Counter {
        final ArrayDeque<Integer> q=new ArrayDeque<>();final boolean useIf,oneNotify;final PrintWriter log;int event=0;
        Counter(boolean useIf,boolean oneNotify,PrintWriter log){this.useIf=useIf;this.oneNotify=oneNotify;this.log=log;}
        void wake(){if(oneNotify)notify();else notifyAll();}
        void record(String op,int value){if(log!=null)log.println((++event)+","+op+","+value+","+q.size());if(q.size()>5)throw new IllegalStateException("capacity exceeded");}
        synchronized void put(int value)throws InterruptedException {
            if(useIf){if(q.size()==5)wait();}else{while(q.size()==5)wait();}
            q.add(value);record("put",value);wake();
        }
        synchronized int take()throws InterruptedException {
            if(useIf){if(q.isEmpty())wait();}else{while(q.isEmpty())wait();}
            int value=q.remove();record("take",value);wake();return value;
        }
    }
    static String run(boolean useIf,boolean oneNotify,PrintWriter log)throws Exception {
        Counter c=new Counter(useIf,oneNotify,log);AtomicReference<Throwable> error=new AtomicReference<>();Set<Integer> seen=ConcurrentHashMap.newKeySet();AtomicInteger total=new AtomicInteger();
        Thread[] threads=new Thread[5];
        for(int i=0;i<2;i++){final int id=i;threads[i]=new Thread(()->{try{for(int k=1;k<=40;k++)c.put(id*40+k);}catch(Throwable e){error.compareAndSet(null,e);}});}
        for(int i=2;i<5;i++){final int n=i==4?26:27;threads[i]=new Thread(()->{try{for(int k=0;k<n;k++){int v=c.take();if(!seen.add(v))throw new AssertionError("duplicate token");total.addAndGet(v);}}catch(Throwable e){error.compareAndSet(null,e);}});}
        for(Thread t:threads)t.setDaemon(true);
        // Consumers start first, increasing contention on the empty condition.
        for(int i=2;i<5;i++)threads[i].start();for(int i=0;i<2;i++)threads[i].start();
        long deadline=System.nanoTime()+TimeUnit.MILLISECONDS.toNanos(600);
        for(Thread t:threads){long remain=deadline-System.nanoTime();if(remain>0)t.join(Math.max(1,TimeUnit.NANOSECONDS.toMillis(remain)));}
        boolean stuck=Arrays.stream(threads).anyMatch(Thread::isAlive);
        for(Thread t:threads)if(t.isAlive())t.interrupt();for(Thread t:threads)t.join(1000);
        if(stuck)return "TIMEOUT (possible lost progress; not proof of permanent deadlock)";
        if(error.get()!=null)return "ERROR "+error.get().getClass().getSimpleName()+": "+error.get().getMessage();
        if(seen.size()!=80||total.get()!=3240)return "INCORRECT count="+seen.size()+" sum="+total;
        return "OK count=80 sum=3240";
    }
    public static void main(String[]args)throws Exception {
        java.nio.file.Files.createDirectories(java.nio.file.Path.of("evidence"));
        try(PrintWriter log=new PrintWriter("evidence/q6-counter-sizes.csv")){
            log.println("event,operation,token,size");String result=run(false,false,log);System.out.println("while+notifyAll "+result);if(!result.startsWith("OK"))throw new AssertionError(result);
        }
        for(int i=1;i<=10;i++)System.out.println("if+notifyAll trial="+i+" "+run(true,false,null));
        for(int i=1;i<=10;i++)System.out.println("while+notify trial="+i+" "+run(false,true,null));
        ArrayBlockingQueue<Integer> queue=new ArrayBlockingQueue<>(5);AtomicInteger sum=new AtomicInteger();
        Thread producer=new Thread(()->{try{for(int i=1;i<=20;i++)queue.put(i);}catch(InterruptedException e){Thread.currentThread().interrupt();}});
        Thread consumer=new Thread(()->{try{for(int i=1;i<=20;i++)sum.addAndGet(queue.take());}catch(InterruptedException e){Thread.currentThread().interrupt();}});
        producer.start();consumer.start();producer.join();consumer.join();if(sum.get()!=210)throw new AssertionError();System.out.println("ArrayBlockingQueue sum="+sum);
    }
}
