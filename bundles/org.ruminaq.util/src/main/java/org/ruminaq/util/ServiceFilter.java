package org.ruminaq.util;

import java.util.function.Predicate;

public @interface ServiceFilter {

	Class<? extends Predicate<ServiceFilterArgs>> value();
}
