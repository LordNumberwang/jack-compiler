package me.lordnumberwang;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface CodeWriter<T> {
  void write(Stream<T> parsedCode, Path outfile);
}