import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
  static MyFrame myFrame;
  static ArrayList<String[]> variables = new ArrayList<String[]>();
  static ArrayList<Pattern> pattern = new ArrayList<Pattern>();
  static HashMap<Pattern, String> conditions = new HashMap<Pattern, String>();
  static Pattern find1 = Pattern.compile("###[\\S]+###");
  static String file_dir = "new";
  static String path = "";
  private static HashMap<String, HashMap<String, String>> info =
      new HashMap<String, HashMap<String, String>>();
  // first element would be comment, second would be header
  private static HashMap<String, String> conf = new HashMap<String, String>();
  private static int lineLimit = 100;

  public static void main(String[] args) throws InterruptedException {
    init();
    try {
      if (args[0].contains("-d")) {
        for (int i = 1; i < args.length; i++) {
          path = args[i];
          analyzeDirectory(args[i]);
        }
      }
      else {
        for (int i = 0; i < args.length; i++) {
          readParameter(new Scanner(new File(args[i])));
          askParameter();
        }
        for (int i = 0; i < args.length; i++)
          analyzeFile(args[i]);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.print("Finished");
    System.exit(0);
  }


  /**
   * return comments to add for current line
   * 
   * @param scnr -- Scanner to the original file
   * @param wr -- File that is going to be written
   * @throws IOException
   */
  public static String meetCondition(String content) {
    String comments = "//";
    // FIX ME
    content = content.trim();
    for (Pattern find : pattern) {
      Matcher m = find.matcher(content);
      if (m.find()) {
        String com = conditions.get(find);
        Matcher m1 = find1.matcher(com);
        // Match format like ###dcd###
        int end = 0;
        while (m1.find()) {
          // whatever on the left handset should be put in the right handset.
          comments += com.substring(end, m1.start())
              + findCommon(find, content, com.substring(m1.start() + 3, m1.end() - 3));
          end = m1.end();
        }
        comments += com.substring(end, com.length());
      }
    }
    return comments.replaceAll("\\{", "").replaceAll("\\(", "").replaceAll("\\}", "")
        .replaceAll("\\)", "");
  }

  public static String findCommon(Pattern find, String a, String b) {
    String b1 = find.toString();
    String renew = a.replaceAll(b1.substring(0, b1.indexOf(b)), "")
        .replaceAll(b1.substring(b1.indexOf(b) + b.length(), b1.length()), "");
    return renew;

  }

  public static void read(Scanner scnr) {
    boolean var = false;// true if it is still in variable part
    boolean condition = false;// true if it is still in condition part
    boolean spe = false;
    while (scnr.hasNextLine()) {
      String buffer = scnr.nextLine();

      if ((!buffer.contains("-----vars:-----")) && (!buffer.contains("-----conditions:-----"))
          && (!buffer.contains("-----special:-----")) && buffer.trim().charAt(0) != '*') {
        if (var) {
          // read next line as variable format
          variables.add(buffer.trim().split("\\s+", 2));
        }
        if (condition) {
          // read next line as condition format
          String[] a = buffer.trim().split("\\s+", 2);
          // a[0] = analyzePattern(a[0]);
          Pattern h = Pattern.compile(a[0]);
          conditions.put(h, a[1]);
          pattern.add(h);
        }
        if (spe) {
          try {
          if (buffer.contains("BAIDU_APP_ID"))
            MyFrame.BAIDU_APP_ID = buffer.split(":")[1].trim();
          if (buffer.contains("BAIDU_SECURITY_KEY"))
            MyFrame.BAIDU_SECURITY_KEY = buffer.split(":")[1].trim();
          if (buffer.contains("IFLY_APP_ID"))
            MyFrame.IFLY_APP_ID = buffer.split(":")[1].trim();
          if (buffer.contains("lineLimit"))
            lineLimit = Integer.parseInt(buffer.split(":")[1].trim());
          }catch(Exception e) {
            System.err.println("Failed to read some personal data, Program will run without them.");
          }
          }

      }
      if (buffer.contains("-----vars:-----")) {
        var = true;
        condition = false;
        spe = false;
      } else if (buffer.contains("-----conditions:-----")) {
        condition = true;
        var = false;
        spe = false;
      } else if (buffer.contains("-----special:-----")) {
        condition = false;
        var = false;
        spe = true;
      }
    }
  }

  private static void init() {
    Scanner scnr = new Scanner(System.in);
    try {
      read(new Scanner(new File("configuration.txt")));
    } catch (FileNotFoundException e1) {
      System.out.print("File does not exist. Please reenter path of directory:");
      try {
        read(new Scanner(new File(scnr.nextLine())));
      } catch (FileNotFoundException e) {
        System.out.print("Faild to read.");
        System.exit(1);
      }
    }
    File create = new File(file_dir);
    if (create.exists()) {
      file_dir = "new_" + new Date().getTime();
      new File(file_dir).mkdir();
    } else {
      create.mkdir();
    }
    myFrame = new MyFrame();
  }

  private static void analyzeDirectory(String string)
      throws FileNotFoundException, IOException, InterruptedException {

    for (String a : readFile(string))
      readParameter(new Scanner(new File(string+"//"+a)));
    askParameter();
    for (String a : readFile(string)) {
      analyzeFile(a);
    }
  }


  private static void askParameter() throws InterruptedException {
    // TODO Auto-generated method stub
    Iterator<String[]> s = GenerateClassName.createIterator();
    while (s.hasNext()) {
      String[] v = s.next();
      System.out.println("AS v: " + v[0].split(" ")[1]);
      conf.put(v[0].split(" ")[1],
          v[0] + askInfo("There is a case described as \r\n  " + v[0] + "\r\n" + "exists in " + v[1]
              + "\r\nType below for whatever you want to say after \r\n  " + v[0]));
    }
  }

  private static String askInfo(String text) throws InterruptedException {
    myFrame.editText(text);
    myFrame.setDone(false); // Still need data
    while (!myFrame.getFinished()) {
      myFrame.setVisible(true);
      Thread.sleep(500);
    }
    return myFrame.getLastResult();
  }

  private static String askInfo(String text, String area) throws InterruptedException {
    myFrame.editText(text);
    myFrame.editTextArea(area);
    myFrame.setDone(false); // Still need data
    while (!myFrame.getFinished()) {
      myFrame.setVisible(true);
      Thread.sleep(500);
    }
    return myFrame.getLastResult();
  }

  private static void analyzeFile(String string)
      throws FileNotFoundException, IOException, InterruptedException {
    generate(new Scanner(new File(path + "\\"+ string)),
        new FileWriter(new File(".\\" + file_dir + "\\" + string)));
  }

  /**
   * Analyze parameter of classes here
   */
  private static void readParameter(Scanner scnr) {
    while (scnr.hasNextLine()) {
      String toWrite = scnr.nextLine();
      if (GenerateClassName.getClass(toWrite)) {
        GenerateClassName.analyzeParam(toWrite);
      }else if (GenerateClassName.getClassName(toWrite) != null) {
        GenerateClassName.analyzeParam(toWrite + " " +scnr.nextLine().trim());
    }
    }
    scnr.close();
  }

  public static boolean generate(Scanner scnr, FileWriter wr)
      throws IOException, InterruptedException {
    while (scnr.hasNextLine()) {
      // Do modification here
      String toWrite = scnr.nextLine();
      if (GenerateClassName.getClass(toWrite)) {
        toWrite = analyze(toWrite);
      } else if (GenerateClassName.getClassName(toWrite) != null) {
        toWrite = analyze(toWrite + " " +scnr.nextLine().trim());
      } else {
        String comments = meetCondition(toWrite);
        if (!comments.equals("//"))
          toWrite += comments;
      }
      if (toWrite.length() >= lineLimit)
        toWrite = getStr(toWrite);
      else
        toWrite = toWrite + "\r\n";
      wr.write(toWrite);
    }
    wr.close();
    scnr.close();
    return true;
  }

  // FIX ME
  public static String getStr(String inputString) {
    StringBuffer out = new StringBuffer();
    int counter = 0;
    for (int i = 0; inputString != null && i < inputString.length(); i++) {
      counter++;
      char c = inputString.charAt(i);
      if (counter > lineLimit) {
        out.append("\r\n");
        counter = 0;
      } else if ((inputString.length() - i > 1) && c == '\r' && inputString.charAt(i + 1) == '\n') {
        i += 1;
        counter = 0;
        out.append("\r\n");
      } else if (c == '\n') {
        counter = 0;
        out.append("\r\n");
      } else
        out.append(c);
    }
    return out.toString();
  }

  /**
   * 
   * @param buffer
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  private static String analyze(String buffer) throws IOException, InterruptedException {
    String aaa = "";
    if (GenerateClassName.returnType(buffer) != null)
      aaa = askInfo("A return type found for method" + buffer
          + "\r\n If you think this is an error, just click Finish. To say anything, describe below:");
    if (!aaa.equals(""))
      aaa = GenerateClassName.returnType(buffer) + aaa;
    String header = askInfo("The thing you want to put at javadoc area, At:\r\n " + "/**\r\n"
        + " * \r\n" + " */\r\n" + buffer, generateHeader(buffer));
    String comments = askInfo(
        "The thing you want to put at description area to describe method you are using, At:\r\n "
            + buffer + "/*\r\n" + " * \r\n" + " */",
        generateComment(buffer));
    String re = "  /**\r\n" + "   * " + header + "\r\n   *\r\n" + analyzeParam(buffer) + aaa
        + "\r\n" + "   */\r\n" + buffer + "\r\n     /*\r\n *" + comments + "\r\n     */\r\n";
    return re;
  }

  private static String generateHeader(String buffer) {
    String re;
    String name = GenerateClassName.getClassName(buffer);
    String constructor = GenerateClassName.getConstructor(buffer);
    re = "This is a class named as " + name;
    if (constructor != null) {
      return "This is a " + constructor + "named as " + name
          + ". It will initialize some basic components for this class.";
    } else if (name.contains("get")) {
      String ana = name.replace("get", "");
      return "This is an accessor class named as " + name + ". It will visit variable related to "
          + ana + " in this class and return to the place this method was called from.";
    } else if (name.contains("set")) {
      String ana = name.replace("set", "");
      return "This is a mutator class named as " + name + ". It will visit variable related to "
          + ana + " in this class and set it to the value passed in.";
    } else
      return re;
  }

  private static String generateComment(String buffer) {
    String re;
    String name = GenerateClassName.getClassName(buffer);
    String constructor = GenerateClassName.getConstructor(buffer);
    re = "This is a class named as " + name;
    if (constructor != null) {
      return "In this " + constructor
          + ", the appropriate supper class will be called at first. Then it will do some specific things in this method related to this method. ";
    } else if (name.contains("get")) {
      String ana = name.replace("get", "");
      return "In this accessor method, the variable related to " + ana
          + " will be visited. And then, it will be returned to the method called from by using return statement.";
    } else if (name.contains("set")) {
      String ana = name.replace("set", "");
      return "In this accessor method, the variable related to " + ana
          + " will be visited. And then it will use assignment to set up local variable related to "
          + ana + " appropriately.";
    } else
      return re;
  }

  private static String analyzeParam(String buffer) {
    // TODO Auto-generated method stub
    String re = "";
    String[] ana = buffer.replaceAll(".*\\(", "").replaceAll("\\).*$", "").split("\\s+");
    for (int i = 1; i < ana.length; i += 2) {
      re += "  * " + conf.get(ana[i].replace(",", "")) + "\r\n";
    }
    if (buffer.contains("throws")) {
      String[] ana1 = buffer.replaceAll(".*throws", "").replaceAll("\\{.*", "").split(",");
      for (int i = 0; i < ana1.length; i++) {
        re += "  * " + conf.get(ana1[i].trim()) + "\r\n";
      }
    }
    return re;
  }



  public static ArrayList<String> readFile(String path) {
    File dir = new File(path);
    ArrayList<String> files = new ArrayList<String>();
    File[] filesList = dir.listFiles();
    for (File file : filesList) {
      if (file.isFile()) {
        if (file.getName().trim().endsWith(".java"))
          files.add(file.getName());
      }
    }
    return files;
  }

  public static void help() {
    System.out.println(
        "To use this generator, put \n  java AutoCommenter {File}\n    {File} -- the path to the file("
            + "including suffix). File needs to be well formatted. For multiple files, put each file"
            + " name seperated with space. \n  java AutoCommenter -d {File_DIR} \n    {File_DIR} -- the path to a directory. Program will generate using java file in this directory. For multiple file path, put each path"
            + "name seperated with space." + "\nThe Modifiers can not be omited\n"
            + "For Default: java AutoCommenter -d .");
  }
}

