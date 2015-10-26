# Introduction
This is a simple IRC bot to ask a simple math question before inviting the user to
another channel.

#Compile
You should be able to compile using maven, by `mvn compile package`

#Bot command
To send a command to the bot either
* Send a PM to the bot
* Send it in ANY channel the bot is in, with `escape`(Configurable, default is !) before the command
* Send it in ANY channel the bot is in, prefixed with bot's nickname

##Command list
* User commands
    * `ping` to see if the bot is alive
    * `resend` to resend the question
    * `version` returns the bot version
    * `info` returns basic bot information
* Admin commands
    * `invite <user>[ ...]` To invite the given user(s) without answering question
    * `nick <nick>` To change the bot's nick to the given nickname(For GLOBAL admin only)
    * `backup` To backup the configuration file(For GLOBAL admin only)

#Admins
Global admins are those set in `admin` in settings.properties.
Local admins are those set in `<key>.admin` in settings.properties.

#Contact us
You can find us on ##invitebot on freenode
