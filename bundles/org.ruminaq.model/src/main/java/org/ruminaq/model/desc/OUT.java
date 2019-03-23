package org.ruminaq.model.desc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.eclipse.emf.ecore.EFactory;
import org.ruminaq.model.dt.DatatypeFactory;
import org.ruminaq.model.ruminaq.DataType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OUT {
	String                      name();
	Class<? extends DataType>[] type()    default {};
	Class<? extends EFactory>   factory() default DatatypeFactory.class;
	boolean                     opt()     default false;
	int                         n()       default 1;
	Position                    pos()     default Position.RIGHT;
	boolean                     label()   default true;
}
