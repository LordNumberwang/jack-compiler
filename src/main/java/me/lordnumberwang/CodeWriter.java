package me.lordnumberwang;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface CodeWriter<T> {
  void write(Stream<T> parsedCode, Path outfile) throws IOException;
}