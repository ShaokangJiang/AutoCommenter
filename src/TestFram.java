import java.util.Scanner;

public class TestFram {
  static MyFrame myFrame;
  public static void main(String[] args) throws InterruptedException {
    myFrame = new MyFrame();
    Scanner scnr = new Scanner(System.in);
    for(int i=0;i<3;i++) {
      System.out.println("Type new input:");
      myFrame.editText("Type below for whatever you want to say after ");
      myFrame.setDone(false); // Still need data
      while(!myFrame.getFinished()) {
        myFrame.setVisible(true);
        Thread.sleep(500);
      }
      System.out.print(myFrame.getLastResult());
    }
    
  }
  
}

