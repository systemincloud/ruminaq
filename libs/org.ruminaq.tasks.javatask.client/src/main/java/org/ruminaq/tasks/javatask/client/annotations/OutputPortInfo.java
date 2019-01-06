package org.ruminaq.tasks.javatask.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.ruminaq.tasks.javatask.client.data.Control;
import org.ruminaq.tasks.javatask.client.data.Data;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface OutputPortInfo {
    String name();
    Class<? extends Data>[] dataType() default { Control.class };
}
