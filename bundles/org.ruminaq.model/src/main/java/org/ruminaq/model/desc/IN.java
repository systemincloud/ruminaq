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
public @interface IN {
	String                      name();
	boolean                     asynchronous() default false;
	int                         group()        default -1;
    boolean                     hold()         default false;
    String                      queue()        default "1";
	Class<? extends DataType>[] type()         default {};
	Class<? extends EFactory>   factory()      default DatatypeFactory.class;
	boolean                     opt()          default false;
	int                         n()            default 1;
	NGroup                      ngroup()       default NGroup.SAME;
	Position                    pos()          default Position.LEFT;
	boolean                     label()        default true;
}
