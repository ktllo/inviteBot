package org.leolo.ircbot.inviteBot.util;

import java.util.Properties;
import java.util.Iterator;
import java.lang.reflect.Field;
import java.lang.annotation.Annotation;
import java.util.ArrayList;

public class PropertyMapper
{
	Object object;
	Class objClass;

	public PropertyMapper(Object object) {
		this.object = object;
		objClass = object.getClass();
	}

	public void map(Properties properties)
		throws PropertyMapperException
	{
		map("", properties);
	}

	public void fillDefaults()
		throws PropertyMapperException {
		Field objFields[] = objClass.getDeclaredFields();
		for(int i = 0; i < objFields.length; i++) {
			Field field = objFields[i];
			field.setAccessible(true);
			Property property = field.getAnnotation(Property.class);
			if(property == null)
				continue;
			setField(field, property.defaultValue());
			field.setAccessible(false);
		}
	}

	public void map(String prefix, Properties properties)
		throws PropertyMapperException
	{
		// Load properties into object
		Field objFields[] = objClass.getDeclaredFields();
		for(int i = 0; i < objFields.length; i++) {
			Field field = objFields[i];
			field.setAccessible(true);
			if(isFieldProperty(field) == false)
				continue;
			String propertyName = prefix + getFieldPropertyName(field);
			String propertyValue;
			assert(properties != null);
			assert(propertyName != null);
			propertyValue = properties.getProperty(propertyName);
			if(propertyValue == null)
				continue;
			setField(field, propertyValue);
			field.setAccessible(false);
		}

	}

	private static boolean isFieldProperty(Field field) {
		Property annotation = (Property)field.getAnnotation(Property.class);
		if(annotation == null)
			return false;
		return true;
	}

	private static String getFieldPropertyName(Field field) {
		Property annotation = (Property)field.getAnnotation(Property.class);
		if(annotation.name().equals(""))
			return field.getName();
		return annotation.name();
	}

	private void setField(Field field, String propertyValue)
		throws PropertyMapperException {
	try {

		Class fieldClass = field.getType();

		if(fieldClass.equals(String.class))
			field.set(object, propertyValue);
		else if(fieldClass.equals(boolean.class))
			field.setBoolean(object, Boolean.parseBoolean(propertyValue));
		else if(fieldClass.equals(int.class))
			field.setInt(object, Integer.parseInt(propertyValue));
		else if(fieldClass.equals(String[].class))
			field.set(object, propertyValue.split(","));
		else
			throw new PropertyMapperException(field.getType() + " fields not implemented yet");

	} catch(NumberFormatException e) {
		throw new PropertyMapperException("'" + propertyValue + "' in the '" + getFieldPropertyName(field) + "' field is not a valid number");
	} catch(IllegalAccessException e) {
		throw new PropertyMapperException("IllegalAccessException " + e.getMessage());
	} catch(SecurityException e) {
		panicSecurity();
	} /* try */ }

	public void checkRequired()
		throws PropertyMapperException
	{
		Field objFields[] = objClass.getDeclaredFields();
		for(int i = 0; i < objFields.length; i++) {
			Field field = objFields[i];
			field.setAccessible(true);
			checkRequiredProperty(field);
			field.setAccessible(false);
		}
	}

	private void checkRequiredProperty(Field field)
		throws PropertyMapperException {
	try {
		Property annotation = (Property)field.getAnnotation(Property.class);
		if(annotation == null)
			return;

		if(annotation.required() == false)
			return;

		Object value = field.get(object);
		if(value == null)
			throw new PropertyMapperException("Required field '" + field.getName() + "' not set");

	} catch(IllegalAccessException e) {
		panicSecurity();
	} catch(IllegalArgumentException e) {
		throw new PropertyMapperException("Likely PropertyMapper implementation error: Class/Object mismatch");
	} catch(ExceptionInInitializerError e) {
		throw new PropertyMapperException("Initialization of field '" + field.getName() + "' failed");
	} /* try */ }

	private static void panicSecurity() throws PropertyMapperException {
		throw new PropertyMapperException("PropertyMapper doesn't work in this security context");
	}

	public static Property[] getProperties(Class clazz) {
		ArrayList<Property> retval = new ArrayList<Property>();
		Property array[] = new Property[0];

		Field objFields[] = clazz.getDeclaredFields();
		for(int i = 0; i < objFields.length; i++) {
			Field field = objFields[i];
			Property annotation = (Property)field.getAnnotation(Property.class);
			if(annotation == null)
				continue;
			retval.add(new PropertyImpl(getFieldPropertyName(field), annotation));
		}

		return retval.toArray(array);
	}

	private static class PropertyImpl implements Property {
		Property property;
		String name;

		PropertyImpl(String name, Property property) {
			this.property = property;
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

		public String name() {
			return name;
		}

		public String description() {
			String desc = property.description();
			return desc;
		}

		public boolean required() {
			return property.required();
		}

		public String defaultValue() {
			return property.defaultValue();
		}

		public Class<? extends Annotation> annotationType() {
			return property.annotationType();
		}
	}
}

