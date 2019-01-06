package org.ruminaq.tasks.javatask.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates Java Task Class. It is necessary to be parsed by Modeler.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface JavaTaskInfo {
    boolean atomic()         default true;
    boolean generator()      default false;
    boolean externalSource() default false;
    boolean constant()       default false;
    boolean onlyLocal()      default false;
}
