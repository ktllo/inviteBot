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
import org.ivartj.args.InvalidOptionException;
import org.ivartj.args.MissingParameterException;

public class InviteBot{
	
	final static Logger logger = LoggerFactory.getLogger(InviteBot.class);
	private static Properties properties;

	public static String getVersion() {
		return properties.getProperty("application.version");
	}

	public static String getName() {
		return properties.getProperty("application.name");
	}

	public static Config parseArguments(String args[]) throws Exception{
		Help help = new Help()
			.usage("inviteBot [OPTIONS]... [CONFIG]")

			.header("DESCRIPTION")
			.wrap("  ", ""
			+	"InviteBot manages invitations to IRC channels. "
			+	"The configuration is by default "
			+	"collected from ./settings.properties."
			)

			.header("OPTIONS")
			.option("-h, --help", "Prints help message.")
			.option("--version",  "Prints version.")
		;

		Lexer lex = new Lexer(args);
		String configFilename = null;

		while(lex.hasNext()) {
			String token = lex.next();

			if(lex.isOption(token))
			switch(token) {
			case "--help":
				help.print(System.out);
				System.exit(0);
			case "--version":
				// TODO: Make version reflect version in pom.xml
				System.out.printf("%s version %s",
					getName(),
					getVersion()
				);
				System.exit(0);
			default:
				throw new InvalidOptionException(token);
			} else if(configFilename == null)
				configFilename = token;
			else
				throw new InvalidOptionException(token);
		}

		Config config;

		if(configFilename != null)
			config = new Config(configFilename);
		else
			config = new Config();

		return config;
	}

	public static Properties loadResourceProperties(String resourcePath) throws Exception {
		InputStream propertyStream = InviteBot.class.getResourceAsStream(resourcePath);
		if(propertyStream == null)
			throw new Exception("Failed to load properties from JAR.");
		Properties properties = new Properties();
		properties.load(InviteBot.class.getResourceAsStream(resourcePath));
		return properties;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String [] args) throws Exception{
		properties = loadResourceProperties("/application.properties");
		Config config = parseArguments(args);
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
		}
	}
}
