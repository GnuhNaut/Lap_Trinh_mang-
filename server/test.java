package server;

import java.io.File;
import java.util.Scanner;
public class test {
    public static void main(String[ ] args) {
        File file = new File("File/hung");
        if (!file.exists()) {
          if (file.mkdir()) {
              System.out.println("Directory is created!");
          } else {
              System.out.println("Failed to create directory!");
          }
      }
    }
}
