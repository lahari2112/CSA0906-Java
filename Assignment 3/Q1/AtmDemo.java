import java.io.*;
import java.nio.file.*;
import java.math.BigDecimal;
class InsufficientFundsException extends Exception {
    InsufficientFundsException(String m){super(m);}
}
class InvalidAmountException extends IllegalArgumentException {
    InvalidAmountException(String m){super(m);}
}
public class AtmDemo {
    static String withdraw(String account,String input,Path card) {
        BigDecimal balance=new BigDecimal("5000.00");
        String branch="";
        try {
            // Fake card-reader file for local simulation; no real account data.
            String expected=Files.readString(card).trim();
            long id=Long.parseLong(account);
            BigDecimal amount=new BigDecimal(input);
            if(!account.equals(expected)||id<=0) throw new IllegalArgumentException("Unknown account");
            if(amount.signum()<=0||amount.scale()>2)throw new InvalidAmountException("Amount must be positive with at most 2 decimals");
            if(amount.compareTo(balance)>0)throw new InsufficientFundsException("Balance is only 5000.00");
            balance=balance.subtract(amount);branch="SUCCESS";
            System.out.println("Dispensed="+amount+" balance="+balance);
        } catch(NumberFormatException | NoSuchFileException e) {
            branch="MULTI_CATCH";System.out.println(branch+": "+e.getClass().getSimpleName()+" "+e.getMessage());
        } catch(InvalidAmountException e) {
            branch="INVALID_AMOUNT";System.out.println(branch+": "+e.getMessage());
        } catch(InsufficientFundsException e) {
            branch="INSUFFICIENT_FUNDS";System.out.println(branch+": "+e.getMessage());
        } catch(IllegalArgumentException e) {
            branch="BAD_ACCOUNT";System.out.println(branch+": "+e.getMessage());
        } catch(IOException e) {
            branch="IO_ERROR";System.out.println(branch+": "+e.getMessage());
        } finally {System.out.println("FINALLY: end session; return simulated card");}
        return branch;
    }
    public static void main(String[]args)throws Exception {
        if(args.length==3){withdraw(args[0],args[1],Path.of(args[2]));return;}
        if(args.length==1 && args[0].equals("interactive")) {
            java.util.Scanner in=new java.util.Scanner(System.in);
            System.out.print("Account: ");String account=in.nextLine();
            System.out.print("Amount: ");String amount=in.nextLine();
            withdraw(account,amount,Path.of("Q1/card.txt"));return;
        }
        String[][] cases={{"1001","500","Q1/card.txt","SUCCESS"},
            {"abc","500","Q1/card.txt","MULTI_CATCH"},
            {"1001","abc","Q1/card.txt","MULTI_CATCH"},
            {"1001","-10","Q1/card.txt","INVALID_AMOUNT"},
            {"1001","6000","Q1/card.txt","INSUFFICIENT_FUNDS"},
            {"1001","100","Q1/missing-card.txt","MULTI_CATCH"},
            {"9999","100","Q1/card.txt","BAD_ACCOUNT"}};
        for(int i=0;i<cases.length;i++){
            String[] c=cases[i];System.out.println("CASE "+(i+1)+" account="+c[0]+" amount="+c[1]+" file="+c[2]);
            if(!withdraw(c[0],c[1],Path.of(c[2])).equals(c[3]))throw new AssertionError("Wrong branch");
        }
        System.out.println("All 7 ATM paths verified");
    }
}
