import java.util.ArrayList;
import java.util.zip.DataFormatException;

public class BankAccount {
  private String accountID;
  private int balance;
  private ArrayList<String> transactions;


  public BankAccount(String accountID, int initialBalance) {// handle non-int
    transactions = new ArrayList<String>();
    this.accountID = accountID;
    if (initialBalance < 10) //pass if condition met
      throw new IllegalArgumentException("InitBalance is less than 10");
    else {
      deposit(initialBalance);
    }
  }


  public String getID() {
    return accountID;
  }


  public int getBalance() {
    return balance;
  }


  public boolean equals(BankAccount other) {
    return (this.accountID.equals(other.getID()));//return argument
  }


  public void deposit(int depositAmount) {
    if (depositAmount < 0)//pass if condition met
      throw new IllegalArgumentException("DepositAmount is negative");
    else {
      balance += depositAmount;
      transactions.add("1 " + depositAmount);
    }
  }


  public void withdraw(int withdrawAmount) throws java.util.zip.DataFormatException {//sdsdvfdv
    if (withdrawAmount < 0 || withdrawAmount % 10 != 0)//pass if condition met
      throw new DataFormatException("withdrawalAmou" + "nt is negative or is not a multiple of 10");
    if (withdrawAmount > balance)//pass if condition met
      throw new IllegalStateException(
          "withdrawalAmount is larger than " + "this bank account's balance");
    balance -= withdrawAmount;
    transactions.add("0 " + withdrawAmount);
  }


  public String[] getMostRecentTransactions() {
    String[] re = new String[5];
    if (getTransactionsCount() < 5) {//pass if condition met
      for (int i = getTransactionsCount()-1; i >=0 ; i--) {
        re[getTransactionsCount()-i-1] = transactions.get(i);
      }
    } else {
      for (int i = getTransactionsCount(); i > getTransactionsCount() - 5; i--) {
        re[getTransactionsCount()-i] = transactions.get(i-1);
      }
    }
    return re;
  }


  public int getTransactionsCount() {
    return transactions.size();
  }


}

