package org.leolo.ircbot.inviteBot.util;

import java.util.Properties;
import java.util.Iterator;
import java.lang.reflect.Field;
import java.lang.annotation.Annotation;
import java.util.ArrayList;

public class PropertyMapper
{
	Properties properties;
	Object object;
	Class objClass;

	public PropertyMapper(Object object) {
		this.properties = properties;
		this.object = object;
		objClass = object.getClass();
	}

	public void map(Properties properties)
		throws PropertyMapperException
	{
		// Load properties into object
		Iterator<String> propertyFields = properties.stringPropertyNames().iterator();
		while(propertyFields.hasNext()) {
			String propertyField = propertyFields.next();
			setField(propertyField, properties.getProperty(propertyField));
		}

	}

	private void setField(String propertyField, String propertyValue)
		throws PropertyMapperException {
	try {

		Field field = objClass.getDeclaredField(propertyField);

		Property annotation = (Property)field.getAnnotation(Property.class);
		if(annotation == null)
			return;
	
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
		throw new PropertyMapperException("'" + propertyValue + "' in the '" + propertyField + "' field is not a valid number");
	} catch(IllegalAccessException e) {
		panicSecurity();
	} catch(SecurityException e) {
		panicSecurity();
	} catch(NoSuchFieldException e) {
		// In order to allow for flexibility, we ignore this exception
		// throw new PropertyMapperException("No such field, '" + propertyField + "'");
		return;
	} /* try */ }

	public void checkRequired()
		throws PropertyMapperException
	{
		Field objFields[] = objClass.getDeclaredFields();
		for(int i = 0; i < objFields.length; i++)
			checkRequiredProperty(objFields[i]);
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

	public Property[] getProperties() {
		ArrayList<Property> retval = new ArrayList<Property>();
		Property array[] = new Property[0];

		Field objFields[] = objClass.getDeclaredFields();
		for(int i = 0; i < objFields.length; i++) {
			Field field = objFields[i];
			Property annotation = (Property)field.getAnnotation(Property.class);
			if(annotation == null)
				continue;
			retval.add(new PropertyImpl(field.getName(), annotation));
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

		public String description() {
			return property.description();
		}

		public boolean required() {
			return property.required();
		}

		public Class<? extends Annotation> annotationType() {
			return property.annotationType();
		}
	}
}

