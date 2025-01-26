package me.lordnumberwang;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface CodeWriter<T> {
  void write(Stream<T> parsedCode, String className, Path outfile) throws IOException;
  void write(String line) throws IOException;
  void write(String s, BufferedWriter writer) throws IOException;
}