package com.example.jler_chinese_system.common;

import java.util.*;

public final class ResponseMaps {

  private ResponseMaps() {}

  public static Map<String, Object> map(Object... args) {
    var m = new LinkedHashMap<String, Object>();
    for (int i = 0; i < args.length; i += 2) m.put(
      args[i].toString(),
      args[i + 1]
    );
    return m;
  }
}
