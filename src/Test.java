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
  static String path = ".";
  private static HashMap<String, HashMap<String, String>> info =
      new HashMap<String, HashMap<String, String>>();
  // first element would be comment, second would be header
  private static HashMap<String, String> conf = new HashMap<String, String>();
  private static int lineLimit = 90;
  private static int seperator = -1;
  static ArrayList<Integer> seps = new ArrayList<Integer>();

  public static void main(String[] args) throws InterruptedException {
    init();
    try {	
      if (args[0].contains("-d")) {
        for (int i = 1; i < args.length; i++) {
          path = args[i];
          analyzeDirectory(args[i]);
        }
      } else {
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
    return getStr(System.getProperty("line.separator") + comments.replaceAll("\\{", "").replaceAll("\\(", "")
        .replaceAll("\\}", "").replaceAll("\\)", ""), "  //");
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
          } catch (Exception e) {
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

  public static void init() {
    Scanner scnr = new Scanner(System.in);
    System.out.println("Type e for English listening, c for Chinese listening...");
    String language = scnr.nextLine();
    if(language.charAt(0) == 'e' || language.charAt(0) == 'E' ) {
      MyFrame.setLanguage("en_us");
      System.out.print("System start as English listening...");
    }else {
      System.out.print("System start as Chinese listening...");
    }
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
      readParameter(new Scanner(new File(string + "//" + a)));
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
      conf.put(v[0].split(" ")[1].trim(),
          v[0] + askInfo("There is a case described as " + System.getProperty("line.separator") + v[0] + System.getProperty("line.separator") + "exists in " + v[1]
              + System.getProperty("line.separator") + "Type below for whatever you want to say after "+ System.getProperty("line.separator") + v[0]));
    }
  }

  private static String askInfo(String text) throws InterruptedException {
    myFrame.editText(text);
    myFrame.setDone(false); // Still need data
    while (!myFrame.getFinished()) {
      myFrame.setVisible(true);
      Thread.sleep(300);
    }
    return myFrame.getLastResult();
  }

  private static String askInfo(String text, String area) throws InterruptedException {
    myFrame.editText(text);
    myFrame.editTextArea(area);
    myFrame.setDone(false); // Still need data
    while (!myFrame.getFinished()) {
      myFrame.setVisible(true);
      Thread.sleep(300);
    }
    return myFrame.getLastResult();
  }

  private static void analyzeFile(String string)
      throws FileNotFoundException, IOException, InterruptedException {
    generate(new Scanner(new File(path + File.separator + string)),
        new FileWriter(new File("."+ File.separator + file_dir + File.separator + string)));
  }

  /**
   * Analyze parameter of classes here
   */
  private static void readParameter(Scanner scnr) {
    while (scnr.hasNextLine()) {
      String toWrite = scnr.nextLine();
      if (GenerateClassName.getClass(toWrite)) {
        GenerateClassName.analyzeParam(toWrite);
      } else if (GenerateClassName.getClassName(toWrite) != null) {
        GenerateClassName.analyzeParam(toWrite + " " + scnr.nextLine().trim());
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
        seperator = toWrite.length();
        toWrite = analyze(toWrite + " " + scnr.nextLine().trim());
      } else {
        toWrite = readComments(toWrite, scnr);
        String comments = meetCondition(toWrite);
        toWrite = formateToWrite(toWrite);
        if (!comments.equals(System.getProperty("line.separator")+"//"))
          toWrite += comments;
      }
      wr.write(toWrite + System.getProperty("line.separator"));
    }
    wr.close();
    scnr.close();
    return true;
  }

  private static String formateToWrite(String toWrite) {
    // TODO Auto-generated method stub
    if(seps.isEmpty()) return toWrite;
    else {
      StringBuilder re = new StringBuilder();
      int pre = 0;
      for(Integer a : seps) {
        re.append(toWrite.substring(pre, a)+System.getProperty("line.separator"));
        pre = a;
      }
      re.append(toWrite.substring(pre, toWrite.length()));
      seps.clear();
      return re.toString();
    }
  }


  private static String readComments(String toWrite, Scanner scnr) {
    // TODO Auto-generated method stub
    int pre = 0;
    int back = 0;
    int start = 0;
    while (scnr.hasNextLine()) {
      for (int i = start; i < toWrite.length(); i++) {
        char a = toWrite.charAt(i);
        if (a == '(')
          pre++;
        else if (a == ')')
          back++;
      }
      //System.out.println("pre:"+pre+"back:"+back);
      if (pre == back)
        break;
      else {
        seps.add(toWrite.length());
        start = toWrite.length();
        toWrite += " " + scnr.nextLine().trim();
        //System.out.println(toWrite);
      }
    }
    return toWrite;
  }


  // FIX ME
  public static String getStr(String inputString, String sep) {
    StringBuffer out = new StringBuffer();
    int counter = 0;
    for (int i = 0; inputString != null && i < inputString.length(); i++) {
      counter++;
      char c = inputString.charAt(i);
      if (counter > lineLimit) {
        out.append(System.getProperty("line.separator") + sep + c);
        counter = 0;
      } else if ((inputString.length() - i > 1) && c == '\r' && inputString.charAt(i + 1) == '\n') {
        i += 1;
        counter = 0;
        out.append(System.getProperty("line.separator"));
      } else if (c == '\n') {
        counter = 0;
        out.append(System.getProperty("line.separator"));
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
          + System.getProperty("line.separator") + " If you think this is an error, just click Finish. To say anything, describe below:");
    if (!aaa.equals(""))
      aaa = getStr(GenerateClassName.returnType(buffer) + aaa, "  *");
    else
      aaa = "  *";
    String header = askInfo("The thing you want to put at javadoc area, At: " + System.getProperty("line.separator") + "/**" + System.getProperty("line.separator")+
         " * "+System.getProperty("line.separator") + " */" + System.getProperty("line.separator") + buffer, generateHeader(buffer));
    String comments = askInfo(
        "The thing you want to put at description area to describe method you are using, At: " + System.getProperty("line.separator")
            + buffer + "/*"+System.getProperty("line.separator") + " * "+System.getProperty("line.separator") + " */",
        generateComment(buffer));
    String para = analyzeParam(buffer);
    if (seperator != -1) {
      buffer =
          buffer.substring(0, seperator) + System.getProperty("line.separator") + buffer.substring(seperator, buffer.length());
      seperator = -1;
    }
    String re = "  /**" + System.getProperty("line.separator") + "   * " + getStr(header, "  *") + System.getProperty("line.separator")+"   * " +System.getProperty("line.separator") + para + aaa
        + System.getProperty("line.separator") + "   */" +System.getProperty("line.separator") + buffer + System.getProperty("line.separator")+"     /*" +System.getProperty("line.separator")+" *" + getStr(comments, "  *")
        + System.getProperty("line.separator")+"     */"+System.getProperty("line.separator");
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
    StringBuilder re = new StringBuilder();
    String[] ana = buffer.replaceAll(".*\\(", "").replaceAll("\\).*$", "").split("\\s+");
    for (int i = 1; i < ana.length; i += 2) {
      re.append(getStr("  * " + conf.get(ana[i].replace(",", "").trim()) + System.getProperty("line.separator"), "  * "));
    }
    if (buffer.contains("throws")) {
      String[] ana1 = buffer.replaceAll(".*throws", "").replaceAll("\\{.*", "").split(",");
      for (int i = 0; i < ana1.length; i++) {
        re.append(getStr("  * " + conf.get(ana1[i].trim()) + System.getProperty("line.separator"), "  * "));
      }
    }
    return re.toString();
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

