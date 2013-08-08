package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonConstructor;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

/**
 * Provides a {@link TypeAdapter} and {@link TypeAdapterFactory} for deserializing those types
 * that have a constructor annotated with {@link JsonConstructor}.
 *
 * @author Brandon Mintern
 */
public class JsonConstructorTypeAdapter<T> extends TypeAdapter<T> {
  public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
      Class<? super T> rawType = type.getRawType();
      for (Constructor<?> constructor: rawType.getDeclaredConstructors()) {
        if (constructor.isAnnotationPresent(JsonConstructor.class)) {
          return new JsonConstructorTypeAdapter<T>(gson, type, (Constructor<T>) constructor);
        }
      }
      return null;
    }
  };

  // holds the Gson instance if the constructor requires one for deserialization; otherwise, null
  private final Gson gson;
  private final Constructor<T> constructor;
  // the delegate adapter to use during serialization
  private final TypeAdapter<T> delegateAdapter;
  // the adapter to use for deserializing the first constructor argument, or null if the first
  // argument is a JsonReader
  private final TypeAdapter<?> argAdapter;

  private JsonConstructorTypeAdapter(Gson gson, TypeToken<T> type, Constructor<T> constructor) {
    Type[] params = constructor.getGenericParameterTypes();
    if (params.length < 1 || params.length > 2) {
      throw new IllegalStateException("Constructor annotated with @JsonConstructor must take 1 or "
          + "2 arguments.");
    }
    if (params[0] == JsonReader.class) {
      argAdapter = null;
    } else {
      try {
        argAdapter = gson.getAdapter(TypeToken.get(params[0]));
      } catch (IllegalArgumentException e) {
        throw new IllegalStateException("First argument of constructor annotated with " +
            "@JsonConstructor is not deserializable by Gson.", e);
      }
    }
    if (params.length > 1) {
      if (params[1] == Gson.class) {
        this.gson = gson;
      } else {
        throw new IllegalStateException("Second (optional) argument of constructor annotated with "
            + "@JsonConstructor must be of type Gson.");
      }
    } else {
      this.gson = null;
    }
    if (!constructor.isAccessible()) {
      constructor.setAccessible(true);
    }
    this.constructor = constructor;
    delegateAdapter = gson.getDelegateAdapter(FACTORY, type);
  }

  @Override
  public void write(JsonWriter out, T value) throws IOException {
    delegateAdapter.write(out, value);
  }

  @Override
  public T read(JsonReader in) throws IOException {
    Object[] args;
    if (gson == null) {
      args = new Object[1];
    } else {
      args = new Object[2];
      args[1] = gson;
    }
    if (argAdapter == null) {
      args[0] = in;
    } else {
      args[0] = argAdapter.read(in);
    }
    try {
      return constructor.newInstance(args);
    } catch (InstantiationException e) {
      // TODO: JsonParseException ?
      throw new RuntimeException("Failed to invoke " + constructor + " with " + args.length
          + " args", e);
    } catch (InvocationTargetException e) {
      Throwable cause = e.getCause();
      if (cause instanceof RuntimeException) {
        throw (RuntimeException) cause;
      }
      if (cause instanceof Error) {
        throw (Error) cause;
      }
      if (cause instanceof IOException) {
        throw (IOException) cause;
      }
      // TODO: JsonParseException ?
      throw new RuntimeException("Failed to invoke " + constructor + " with " + args.length
          + "args", cause);
    } catch (IllegalAccessException e) {
      throw new AssertionError(e);
    }
  }
}
