package org.ruminaq.tasks.javatask.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declarations of parameters.
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE})
public @interface SicParameters {
	public SicParameter[] value() default {};
}
