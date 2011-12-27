package com.stuffwithstuff.bulfinch;

import java.io.*;
import java.nio.charset.Charset;

public class BulfinchScript {
  public BulfinchScript(String path) throws IOException {
    mPath = path;
    mSource = readFile(path);
  }

  public String getPath() {
    return mPath;
  }

  public String getSource() {
    return mSource;
  }

  private static String readFile(String path) throws IOException {
    FileInputStream stream = new FileInputStream(path);

    try {
      InputStreamReader input = new InputStreamReader(stream,
          Charset.defaultCharset());
      Reader reader = new BufferedReader(input);

      StringBuilder builder = new StringBuilder();
      char[] buffer = new char[8192];
      int read;

      while ((read = reader.read(buffer, 0, buffer.length)) > 0) {
        builder.append(buffer, 0, read);
      }

      return builder.toString();
    } finally {
      stream.close();
    }
  }

  private final String mPath;
  private final String mSource;
}
