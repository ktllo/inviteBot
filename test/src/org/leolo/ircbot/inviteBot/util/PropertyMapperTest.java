package org.leolo.ircbot.inviteBot.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyMapperTest extends TestCase
{
	public PropertyMapperTest( String testName )
	{
		super( testName );
	}

	public static Test suite()
	{
		return new TestSuite( PropertyMapperTest.class );
	}

	private static class Configuration {

		@Property(
			description = "The IRC server to connect to",
			required    = true
		)
		String server;


		@Property(
			description = "The server's port to connect to"
		)
		int port = 6999;


		@Property(
			description = "The bot's IRC nickname"
		)
		String nick = "inviteBot";


		@Property(
			description = "Whether to use a secure TLS/SSL connection"
		)
		boolean ssl = false;


		@Property(
			description = "One or more admins"
		)
		String[] admin;
	}

	public void testMapper() throws PropertyMapperException, IOException
	{
		Configuration config = new Configuration();
		InputStream input = getClass().getResourceAsStream("/configuration.properties");
		Properties properties = new Properties();
		properties.load(input);
		PropertyMapper mapper = new PropertyMapper(config);
		mapper.map(properties);

		assert(config.server.equals("irc.freenode.net"));
		assert(config.port == 7000);
		assert(config.nick.equals("BotI"));
		assert(config.ssl == true);

		String admin[] = { "ivartj" };
		assert(config.admin.length == admin.length);
		for(int i = 0; i < admin.length; i++)
			assert(config.admin[i].equals(admin[i]));
	}

	private static class ConfigurationWithRequiredField {

		@Property(
			description = "The IRC server to connect to",
			required    = true
		)
		String server;


		@Property(
			description = "The server's port to connect to"
		)
		int port = 6999;


		@Property(
			description = "The bot's IRC nickname"
		)
		String nick = "inviteBot";


		@Property(
			description = "Whether to use a secure TLS/SSL connection"
		)
		boolean ssl = false;


		@Property(
			description = "A comma-separated list of admins"
		)
		String[] admin;


		@Property(
			description = "A required field",
			required    = true
		)
		String requiredField;
	}


	public void testRequired() throws PropertyMapperException, IOException
	{
		ConfigurationWithRequiredField config = new ConfigurationWithRequiredField();
		InputStream input = getClass().getResourceAsStream("/configuration.properties");
		Properties properties = new Properties();
		properties.load(input);
		boolean caught = false;
		PropertyMapper mapper = new PropertyMapper(config);

		try {
			mapper.map(properties);
			mapper.checkRequired();

		// TODO: Check against more specific exception
		} catch(PropertyMapperException e) {
			caught = true;
		}

		assert(caught);
	}
}
