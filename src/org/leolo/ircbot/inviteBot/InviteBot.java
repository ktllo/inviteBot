package org.leolo.ircbot.inviteBot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.Properties;
import java.io.InputStream;

import org.pircbotx.Configuration;
import org.pircbotx.Configuration.Builder;
import org.pircbotx.PircBotX;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.cap.SASLCapHandler;
import org.pircbotx.exception.IrcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ivartj.args.Help;
import org.ivartj.args.Lexer;
import org.ivartj.args.ArgumentException;
import org.ivartj.args.InvalidOptionException;
import org.leolo.ircbot.inviteBot.util.PropertyMapperException;
import org.leolo.ircbot.inviteBot.util.Property;
import org.leolo.ircbot.inviteBot.util.PropertyMapper;

public class InviteBot{
	
	final static Logger logger = LoggerFactory.getLogger(InviteBot.class);
	private static Properties properties;
	private static String name = null;
	private static String version = null;

	public static String getVersion() {
		if(version != null)
			return version;
		version = properties.getProperty("application.version");
		return version;
	}

	public static String getName() {
		if(name != null)
			return name;
		name = properties.getProperty("application.name");
		return name;
	}

	private static Help getHelpMessage() {
		Help help = new Help()
			.usage("inviteBot [OPTIONS]... [CONFIG]")

			.header("DESCRIPTION")
			.pg("  InviteBot manages invitations to IRC channels. ")

			.header("OPTIONS")
			.pg("  The following are command-line options. ")

			.option("-h, --help", "Prints help message.")
			.option("--version",  "Prints version.")

			.header("GLOBAL SETTINGS")
			.pg("  Settings are inn the form:")

			.pg("    field=value")

			.pg("  "
			+	"The configuration is by default collected from "
			+	"./settings.properties."
			);

		addSettingsToHelp(help, Config.getGlobalSettings());

		help.header("CHANNEL SETTINGS")
			.pg("  "
			+	"The following are channel-specific settings. "
			+	"Channel-specific seetings are prefixed with the keys "
			+	"given in the global 'key' setting."
			);

		addSettingsToHelp(help, Config.getChannelSettings());

		return help;
	}

	private static void addSettingsToHelp(Help help, Property settings[]) {

		for(int i = 0; i < settings.length; i++) {
			Property setting = settings[i];
			String desc = setting.description();
			if(setting.required())
				desc += "\nRequired.";
			if(!setting.defaultValue().equals(""))
				desc += "\nDefault: \"" + setting.defaultValue() + "\"";
			help.option(setting.toString() + (setting.required() ? "*" : ""), desc);
		}

		help.pg("  "
		+	"Settings marked with * are required."
		);
	}

	private static Config parseArguments(String args[]) throws IOException, PropertyMapperException {
		Help help = getHelpMessage();
		Lexer lex = new Lexer(args);
		String configFilename = null;

		while(lex.hasNext()) {
		try {
			String token = lex.next();

			if(lex.isOption(token))
			switch(token) {
			case "-h":
			case "--help":
				help.print(System.out);
				System.exit(0);
			case "--version":
				System.out.printf("%s version %s\n",
					getName(),
					getVersion()
				);
				System.exit(0);
			default:
				throw new InvalidOptionException(token);
			} else if(configFilename == null)
				configFilename = token;
			else
				throw new ArgumentException("Unexpected argument '" + token + "'");

		} catch(ArgumentException e) {
			System.err.println("Error when parsing command-line arguments:");
			System.err.println("  " + e.getMessage());
			System.exit(1);
		} /* try */ }

		Config config;

		if(configFilename != null)
			config = new Config(configFilename);
		else
			config = new Config();

		return config;

	}

	private static Properties loadResourceProperties(String resourcePath) throws IOException {
		InputStream propertyStream = InviteBot.class.getResourceAsStream(resourcePath);
		if(propertyStream == null)
			throw new IOException("Failed to load properties from JAR.");
		Properties properties = new Properties();
		properties.load(propertyStream);
		return properties;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String [] args) throws PropertyMapperException{
		Config config = null;

		try {
			properties = loadResourceProperties("/application.properties");
			config = parseArguments(args);
		} catch(IOException e) {
			System.err.println("Failed to initialize InviteBot:");
			System.err.println(e.getMessage());
			System.exit(1);
		} 

		Inviter inviter = new Inviter(config);
		Builder b = new Configuration.Builder()
		.setName(config.getNick()) //Nick of the bot.
		.setLogin(config.getIdent()) //Login part of hostmask, eg name:login@host
		.setAutoNickChange(true) //Automatically change nick when the current one is in use
		.setServer(config.getServer(), config.getPort())
		.addListener(inviter)
		.setAutoReconnect(true)
		.addListener(new Console(config,inviter));
		if(config.isSSL()){
			b = b.setSocketFactory(new UtilSSLSocketFactory().disableDiffieHellman().trustAllCertificates());
			b = b.setCapEnabled(true).addCapHandler(new SASLCapHandler(config.getUsername(), config.getPassword()));
		}
		b = b.addCapHandler(new EnableCapHandler("extended-join",false));
		PircBotX myBot = new PircBotX(b.buildConfiguration());
		try {
			myBot.startBot();
		} catch (IrcException e) {
			logger.error(e.toString(), e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.toString(), e);
			e.printStackTrace();
		}
	}
}
