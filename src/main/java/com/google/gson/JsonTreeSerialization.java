package com.google.gson;

/**
 * @author Brandon Mintern
 */
public interface JsonTreeSerialization {

  /**
   * Serializes this by returning a JsonElement. May return null.
   */
  JsonElement toJsonTree(Gson gson);
}
