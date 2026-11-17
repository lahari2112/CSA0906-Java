import java.lang.management.*;
import java.util.concurrent.*;
public class DeadlockDemo {
    static class Wallet {final int id;long balance=1000;Wallet(int id){this.id=id;}}
    static void ordered(Wallet from,Wallet to,long amount){
        if(from==to)return;
        Wallet first=from.id<to.id?from:to,second=first==from?to:from;
        synchronized(first){synchronized(second){if(amount<=0||from.balance<amount)throw new IllegalArgumentException("transfer");from.balance-=amount;to.balance+=amount;}}
    }
    public static void main(String[]args)throws Exception {
        Wallet a=new Wallet(1),b=new Wallet(2);CountDownLatch held=new CountDownLatch(2);
        Thread one=new Thread(()->{synchronized(a){held.countDown();await(held);synchronized(b){a.balance--;b.balance++;}}},"unsafe-A-to-B");
        Thread two=new Thread(()->{synchronized(b){held.countDown();await(held);synchronized(a){b.balance--;a.balance++;}}},"unsafe-B-to-A");
        one.setDaemon(true);two.setDaemon(true);one.start();two.start();held.await();
        ThreadMXBean bean=ManagementFactory.getThreadMXBean();long[] ids=null;long deadline=System.nanoTime()+TimeUnit.SECONDS.toNanos(3);
        while(ids==null&&System.nanoTime()<deadline){Thread.sleep(10);ids=bean.findDeadlockedThreads();}
        if(ids==null)throw new AssertionError("Expected deadlock not detected");
        System.out.println("Detected deadlocked threads="+ids.length);
        for(ThreadInfo info:bean.getThreadInfo(ids,true,true))System.out.println(info);
        // New wallets isolate the corrected experiment from the intentionally stuck locks.
        Wallet x=new Wallet(3),y=new Wallet(4);x.balance=10000;y.balance=10000;Thread[] workers=new Thread[4];
        for(int i=0;i<4;i++){final boolean forward=i%2==0;workers[i]=new Thread(()->{for(int k=0;k<1000;k++)ordered(forward?x:y,forward?y:x,1);});workers[i].start();}
        for(Thread t:workers){t.join(5000);if(t.isAlive())throw new AssertionError("ordered deadlock");}
        if(x.balance+y.balance!=20000)throw new AssertionError("money lost");
        System.out.println("Ordered transfers complete: total="+(x.balance+y.balance)+" balances="+x.balance+","+y.balance);
        System.out.println("Unsafe daemon threads remain blocked until this demonstration JVM exits.");
    }
    static void await(CountDownLatch l){try{l.await();}catch(InterruptedException e){Thread.currentThread().interrupt();throw new RuntimeException(e);}}
}
