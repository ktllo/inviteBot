package org.leolo.ircbot.inviteBot.util;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {
	public String name() default "";
	public String description();
	public String defaultValue() default "";
	public boolean required() default false;
}


