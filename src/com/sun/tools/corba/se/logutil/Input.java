

package com.sun.tools.corba.se.logutil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.LinkedList;
import java.util.Queue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Input {

  
  private String packageName;

  
  private String className;

  
  private String groupName;

  
  private Queue<InputException> exceptions;

  
  private enum State
  {
    OUTER,
    IN_CLASS,
    IN_EXCEPTION_LIST
  };

  
  private static final Pattern EXCEPTION_INFO_REGEX =
    Pattern.compile("(\\w+)\\s*(\\d+)\\s*(\\w+)");

  
  public Input(final String filename)
  throws FileNotFoundException, IOException {
    BufferedReader r =
      new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
    State state = State.OUTER;
    InputException current = null;
    exceptions = new LinkedList<InputException>();
    String line;
    while ((line = r.readLine()) != null) {
      
      if (line.startsWith(";"))
        continue;

      int index = line.indexOf("(");
      if (index == -1)
        continue;

      switch (state) {
      case OUTER:
        state = State.IN_CLASS;
        String[] classInfo = line.substring(index).split(" ");
        packageName = classInfo[0].substring(2, classInfo[0].length() - 1);
        className = classInfo[1].substring(1, classInfo[1].length() - 1);
        groupName = classInfo[2];
        break;
      case IN_CLASS:
        state = State.IN_EXCEPTION_LIST;
        break;
      case IN_EXCEPTION_LIST:
        boolean inQuote = false;
        boolean inCode = false;
        boolean end = false;
        int start = index + 1;
        Queue<String> lines = new LinkedList<String>();
        for (int a = start; a < line.length(); ++a) {
          if (line.charAt(a) == '(' && !inCode && !inQuote) {
            if (current == null)
              current =
                new InputException(line.substring(start, a).trim());
            start = a + 1;
            inCode = true;
          }
          if (line.charAt(a) == '"')
            inQuote = !inQuote;
          if (line.charAt(a) == ')' && !inQuote) {
            if (inCode) {
              lines.offer(line.substring(start, a));
              inCode = false;
            } else
              end = true;
          }
          if (!end && a == line.length() - 1)
            line += r.readLine();
        }
        for (String l : lines) {
          int stringStart = l.indexOf("\"") + 1;
          int stringEnd = l.indexOf("\"", stringStart);
          Matcher matcher = EXCEPTION_INFO_REGEX.matcher(l.substring(0, stringStart));
          if (matcher.find())
            current.add(new InputCode(matcher.group(1),
                                      Integer.parseInt(matcher.group(2)),
                                      matcher.group(3),
                                      l.substring(stringStart, stringEnd)));
        }
        exceptions.offer(current);
        current = null;
        break;
      }
    }
  }

  
  public String getGroupName()
  {
    return groupName;
  }

  
  public String getPackageName()
  {
    return packageName;
  }

  
  public String getClassName()
  {
    return className;
  }

  
  public Queue<InputException> getExceptions() {
    return exceptions;
  }

  
  public String toString() {
    return getClass().getName() +
      "[packageName=" + packageName +
      ",className=" + className +
      ",groupName=" + groupName +
      ",exceptions=" + exceptions +
      "]";
  }

}
