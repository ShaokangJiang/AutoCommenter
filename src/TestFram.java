import java.util.Scanner;

public class TestFram {
  static MyFrame myFrame;
  public static void main(String[] args) throws InterruptedException {
    Test.init();
    Scanner scnr = new Scanner(System.in);
    for(int i=0;i<3;i++) {
      System.out.println("Type new input:");
      Test.myFrame.editText("Type below for whatever you want to say after ");
      Test.myFrame.setDone(false); // Still need data
      while(!Test.myFrame.getFinished()) {
        Test.myFrame.setVisible(true);
        Thread.sleep(500);
      }
      System.out.print(Test.myFrame.getLastResult());
    }
    
  }
  
}

