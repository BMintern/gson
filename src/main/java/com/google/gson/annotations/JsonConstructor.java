package com.google.gson.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that indicates this constructor should be used during JSON deserialization of
 * the containing class.
 *
 * <p>This annotation has no effect unless you build {@link com.google.gson.Gson} with a
 * {@link com.google.gson.GsonBuilder} and invoke the
 * {@link com.google.gson.GsonBuilder#enableConstructorScanning()} method.</p>
 *
 * <p>The annotated constructor must take either a JsonReader or a class for which calling
 * {@link com.google.gson.Gson#getAdapter(Class)} with that class would return a valid adapter.
 * It may optionally take a Gson instance as a second argument. If the constructor's signature does
 * not fit this pattern, it will result in an {@link IllegalStateException} at deserialization
 * time.</p>
 *
 * <p>Only one constructor may have this annotation applied. The behavior is undefined if more
 * than one constructor is annotated in this way.</p>
 *
 * @author Brandon Mintern
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface JsonConstructor {}
