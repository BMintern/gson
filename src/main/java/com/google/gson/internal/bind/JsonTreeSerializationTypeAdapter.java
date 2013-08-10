package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonTreeSerialization;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

/**
 * @author Brandon Mintern
 */
public class JsonTreeSerializationTypeAdapter<T extends JsonTreeSerialization>
    extends  TypeAdapter<T> {
  public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory(){
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
      Class<? super T> rawType = type.getRawType();
      if (JsonTreeSerialization.class.isAssignableFrom(rawType)) {
        return new JsonTreeSerializationTypeAdapter(type, gson);
      }
      return null;
    }
  };

  private final Gson gson;
  private final TypeToken<T> typeToken;
  // initialized lazily to avoid fetching unneeded delegate adapters
  private TypeAdapter<T> delegate;

  public JsonTreeSerializationTypeAdapter(TypeToken<T> type, Gson gson) {
    this.gson = gson;
    typeToken = type;
  }

  @Override
  public void write(JsonWriter out, T value) throws IOException {
    Streams.write(value.toJsonTree(gson), out);
  }

  @Override
  public T read(JsonReader in) throws IOException {
    return delegate().read(in);
  }

  private TypeAdapter<T> delegate() {
    if (delegate == null) {
      delegate = gson.getDelegateAdapter(FACTORY, typeToken);
    }
    return delegate;
  }
}
