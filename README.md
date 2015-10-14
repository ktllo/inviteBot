# Introduction
This is a simple IRC bot to ask a simple math question before invite user to
another channel.

#Compile
You should able to compile using maven, by `mvn compile package`

#Bot command
To send command to the bot either
* Send PM to the bot
* Send in ANY channel the bot is in, with `escape`(Configurable, default is !) before the command
* Send in ANY channel the bot is in, begin with bot's nickname

##Command list
* User command
    * `ping` to see is bot alive
    * `resend` to resend the question
    * `version` returns the bot version
    * `info` returns basic bot information
* Admin command
    * `invite <user>[ ...]` To invite listed user listed without answering question
    * `nick <nick>` To change the bot's nick to the given nickname(For GLOBAL admin only)
    * `backup` To backup the configuration file(For GLOBAL admin only)

#Admins
Global admins are who are IRCOp, `admin` set in the settings.properties.
Local admins are those set in `<key>.admin` set in settings.properties.

#Contact us
You can find us on ##invitebot on freenode
