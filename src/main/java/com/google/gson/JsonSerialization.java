package com.google.gson;

import com.google.gson.stream.JsonWriter;
import java.io.IOException;

/**
 * @author Brandon Mintern
 */
public interface JsonSerialization {
  void toJson(JsonWriter out, Gson gson) throws IOException;
}
