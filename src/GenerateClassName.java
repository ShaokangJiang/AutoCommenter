import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

// This class is to generate a list of methods

/**
 * @author Shaokang Jiang
 *
 */
public class GenerateClassName {

  private final static String[] modifier = {"protected", "public", "private"};
  private static HashMap<String,HashSet<String>> parameter = new HashMap<String,HashSet<String>>();

  public static HashMap<String,HashSet<String>> getParameter() {
    return parameter;
  }
  
  public static Iterator<String[]> createIterator(){
    return new ConcreteIterator();
  }
  
  private static class ConcreteIterator implements Iterator<String[]> {

    private int cursor;  
    private Object[] keys = parameter.keySet().toArray();
    public void first() {
        cursor = 0;
    }
    
    public String[] getCurrentObj() {
      if(!hasNext()) throw new IndexOutOfBoundsException();
      Object pos1 = keys[cursor];
      StringBuilder pos2 = new StringBuilder();
      for(String a: parameter.get(pos1)) pos2.append(a+"\r\n");
      String[] re = {(String)pos1,pos2.toString()};
      return re;
    }
    
    @Override
    public boolean hasNext() {
        if(cursor<keys.length){
            return true;
        }
        return false;
    }

    public boolean isFirst() {
        return cursor==0?true:false;
    }

    public boolean isLast() {
        return cursor==(keys.length-1)?true:false;
    }

    @Override
    public String[] next() {
      try {
        String[] next = getCurrentObj();
        cursor ++;
        return next;
    } catch (IndexOutOfBoundsException e) {
        throw new NoSuchElementException();
    }
    }   
  }
  
  public static void analyzeParam(String buffer) {
    // TODO Auto-generated method stub
    if(getClass(buffer)) {
    String[] ana = buffer.replaceAll(".*\\(", "").replaceAll("\\).*$", "").split("\\s+");// solve
                                                                                         // ","
                                                                                         // issue
    for (int i = 1; i < ana.length; i += 2) {
      String key = "@param " + ana[i].replace(",", "").trim() + " -- A variable of type " + ana[i - 1];
      if(!parameter.containsKey(key)) parameter.put(key, new HashSet<String>());
      parameter.get(key).add(buffer);
    }
    if (buffer.contains("throws")) {
      String[] ana1 =
          buffer.replaceAll(".*throws", "").trim().replaceAll("\\{.*", "").split(",");
      for (int i = 0; i < ana1.length; i++) {
        String key = "@throws " + ana1[i].trim() + " -- throw " + ana1[i].trim() + " when ";
        if(!parameter.containsKey(key)) parameter.put(key, new HashSet<String>());
        parameter.get(key).add(buffer);
      }
    }}
  }

  public static boolean getClass(String content) {
    String analyze1 = "";
    if (content.contains(")"))
      analyze1 = content.substring(content.lastIndexOf(")"), content.length()).trim()
          .replaceAll("\\s+", "");
    if (containModifier(content) && analyze1.contains("{") && !(content.contains("="))) {
      System.out.println("AS class: "+ content);
      return true;
    }
    return false;
  }

  public static boolean containModifier(String a) {
    for (String b : modifier)
      if (a.contains(b))
        return true;
    return false;
  }

  public static String getClassName(String content) {
    if(content.contains("=")||!containModifier(content)||!content.contains("(")||content.contains(";")) return null;
    String[] analyzed = content.substring(0, content.lastIndexOf("(")).trim().split("\\s+");
    if(analyzed.length==1) return null;
    return analyzed[analyzed.length - 1];
  }
  
  public static String getConstructor(String content) {
    String[] analyzed = content.substring(0, content.lastIndexOf("(")).trim().split("\\s+");
    if(analyzed.length==1||analyzed.length==2) {
      if(content.substring(content.lastIndexOf("("), content.lastIndexOf(")")).split("\\s+").length == 0) return "Default constructor";
      return "Non-default constructor";
    }else return null;
  } 

  public static String returnType(String content) {
    if (content.contains("void"))
      return null;
    String[] analyzed = content.substring(0, content.lastIndexOf("(")).trim().split("\\s+");
    if(containModifier(analyzed[analyzed.length - 2]))
      return null;
    return "\r\n   * @return " + analyzed[analyzed.length - 2] +"\r\n";
  }
  
  public static void main(String[] args) {
    HashSet<String> a = new HashSet<String>();
    a.add("sdcsdvs");
    a.add("sdvfvdfvfd");
    a.add("sfvdfbfgdfv");
    parameter.put("sdvfvdf", a);
    parameter.put("vsdvfvdf", a);
    parameter.put("vsddvfvdf", a);
    
    Iterator<String[]> s = GenerateClassName.createIterator();
    while(s.hasNext()) {
      String[] v =  s.next();
      System.out.println(v[0]);
      System.out.println(v[1]);
    }
  }
}

