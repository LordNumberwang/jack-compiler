package me.lordnumberwang.languageapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

//Code taken from Tom Gregory Tech YouTube channel Gradle Course for Beginning as a starting point
public class SayHello {
  public static void main(String[] args) throws IOException {
    String language = args[0];

    InputStream resourceStream = SayHello.class.
        getClassLoader().getResourceAsStream(language + ".txt");
    assert resourceStream != null;
    BufferedReader bufferedInputStream = new BufferedReader(
        new InputStreamReader(resourceStream, StandardCharsets.UTF_8));

    System.out.println(bufferedInputStream.readLine());
  }
}