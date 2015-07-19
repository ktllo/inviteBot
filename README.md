# Introduction
This is a simple IRC bot to ask a simple math question before invite user to
another channel.

#Compile
You should able to compile using maven, by `mvn compile package`

#Bot command
To send command to the bot either
* Send PM to the bot
* Send in ANY channel the bot is in, with `escape`(Configureable, default is !) before the command
* Send in ANY channel the bot is in, with bot's nickname and a space before the command

##Command list
* User command
    * `ping` to see is bot alive

* Admin command
    * `invite <user>[ ...]` To invite listed user listed without answering question

#Admins
Global admins are who are IRCOp, `admin` set in the settings.properties.
Local admins are those set in `<key>.admin` set in settings.properties.
