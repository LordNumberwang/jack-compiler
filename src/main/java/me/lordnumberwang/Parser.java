package me.lordnumberwang;

import java.util.stream.Stream;

public interface Parser<T> {
  //Stream<T> where <T> is intermediate class of a language command
  // e.g. VmCommand
  Stream<T> parse(Stream<String> stringStream);
}