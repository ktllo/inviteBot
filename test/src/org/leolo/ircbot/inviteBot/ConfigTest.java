package org.leolo.ircbot.inviteBot;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.InputStream;
import java.io.InputStreamReader;

public class ConfigTest extends TestCase
{
	public ConfigTest( String testName )
	{
		super( testName );
	}

	public static Test suite()
	{
		return new TestSuite( ConfigTest.class );
	}

	public void testBasic() throws Exception {
		InputStream input = getClass().getResourceAsStream("/ConfigTest.properties");
		Config config = new Config(new InputStreamReader(input), "");

		assert(config.getNick().equals("butternut"));
		assert(config.getPort() == 7000);
		assert(config.getEscape().equals("$"));
		assert(config.getServer().equals("irc.freenode.net"));
	}

	public void testGetChannelList() throws Exception {
		InputStream input = getClass().getResourceAsStream("/ConfigTest.properties");
		Config config = new Config(new InputStreamReader(input), "");

		String channelList[] = config.getChannelList();
		boolean aChan = false,
		        bChan = false,
		        cChan = false;

		for(int i = 0; i < channelList.length; i++) {
			switch(channelList[i]) {
			case "#a":
				if(!aChan) {
					aChan = true;
					continue;
				}
				break;
			case "#b":
				if(!bChan) {
					bChan = true;
					continue;
				}
				break;
			case "#c":
				if(!cChan) {
					cChan = true;
					continue;
				}
				break;
			default:
				throw new Exception("Unexpected channel '" + channelList[i] + "'");
			}
			throw new Exception("Duplicate channel '" + channelList[i] + "'");
		}

		assert(aChan);
		assert(bChan);
		assert(cChan);
	}
}

