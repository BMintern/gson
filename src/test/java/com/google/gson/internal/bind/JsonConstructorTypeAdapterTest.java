package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.annotations.JsonConstructor;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;

public class JsonConstructorTypeAdapterTest extends TestCase {
  public void testReaderOnly() {
    assertEquals("hello, world", gson().fromJson("\"world\"", ReaderOnly.class).toString());
  }

  public void testElementOnlyWithNullValue() {
    assertEquals("hello, null", gson().fromJson("null", ElementOnly.class).toString());
  }

  public void testReaderAndGsonWithPrivateConstructor() {
    assertEquals("hello, private", gson().fromJson("\"private\"", ReaderAndGson.class).toString());
  }

  public void testElementAndGson() {
    assertEquals("hello, bob", gson().fromJson("\"bob\"", ElementAndGson.class).toString());
  }

  public void testString() {
    assertEquals("hello, greeter", gson().fromJson("\"greeter\"", Greeter.class).toString());
  }

  public void testNonAnnotated() {
    assertEquals("hi!", gson().fromJson("{\"message\":\"hi!\"}", NotAnnotated.class).toString());
  }

  public void testNonScanning() {
    try {
      // The following should throw an error since ReaderOnly has no default constructor.
      new Gson().fromJson("\"foo\"", ReaderOnly.class);
      fail();
    } catch (Throwable t) {}
  }

  public void testGeneric() {
    assertEquals("[1, 2]", gson().fromJson("[1,2]", Generic.class).toString());
  }

  private Gson gson() {
    return new GsonBuilder().enableConstructorScanning().create();
  }

  private static class NotAnnotated {
    private String message;
    @Override
    public String toString() {
      return message;
    }
  }

  private static class ReaderOnly extends Greeter {
    @JsonConstructor
    public ReaderOnly(JsonReader in) throws IOException {
      super(in.nextString());
    }
  }
  private static class ElementOnly extends Greeter {
    @JsonConstructor
    public ElementOnly(JsonElement j) {
      super(j.isJsonNull() ? null : j.getAsString());
    }
  }
  private static class ReaderAndGson extends ReaderOnly {
    @JsonConstructor
    private ReaderAndGson(JsonReader in, Gson gson) throws IOException {
      super(in);
    }
  }
  private static class ElementAndGson extends ElementOnly {
    @JsonConstructor
    public ElementAndGson(JsonElement j, Gson gson) {
      super(j);
    }
  }
  private static class Greeter {
    private final String name;
    @JsonConstructor
    protected Greeter(String n) {
      name = n;
    }
    public String toString() {
      return "hello, " + name;
    }
  }
  private static class Generic {
    private final List<Integer> ints;
    @JsonConstructor
    Generic(List<Integer> list) {
      ints = list;
    }
    public String toString() {
      return Arrays.toString(ints.toArray());
    }
  }
}
