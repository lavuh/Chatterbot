Chatterbot
=======================

Casino is a lightweight plugin that integrates a chatbot into the server chat to perform helpful functions.

Contributors
------------
* [lavuh](https://github.com/lavuh) (Plugin development)

Features
--------
- Different 'Modules' that serve different purposes within the bot:
- `Welcome Module`: This module privately messages everyone in a CommandBook message format notifying them of any first-time players that join. Rewards are also given to players that welcome the new player.
- `Insult Module`: This module is designed to drive off trolls and other chat-related rulebreakers. When targeted, the player will be bombarded with a random insult every 0.5 seconds, and will not be able to chat. Their chat will still be recorded in console for humor purposes.
- `GitHub Module`: This module listens to a predefined list of GitHub repositories, and broadcasts any new commits that are pushed.

Compiling
---------
You will need to install [Maven](https://maven.apache.org/) to compile this program.

**Note**:  You will need to install the Spigot/CraftBukkit libraries by using [BuildTools](https://www.spigotmc.org/wiki/buildtools/).

Once you have these libraries compiled, on your commandline, type the following.
```
cd /path/to/Chatterbot
mvn clean install
```
Maven automatically downloads the other required dependencies.
Output JAR will be placed in the `/target` folder which can be then put into the plugins folder.

Dependencies
------------
Chatterbot will not run without the presence of the [FasterXML Jackson Core](https://mvnrepository.com/artifact/com.fasterxml.jackson.core) libraries. These are present within the Skyblock plugin, but in order to run the bot standalone you will need to include these libraries in a [shaded Uber JAR](https://maven.apache.org/plugins/maven-shade-plugin/).

To include these dependencies in Maven, include the following:
```
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-core</artifactId>
    <version>2.9.8</version>
</dependency>

<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-annotations</artifactId>
    <version>2.9.8</version>
</dependency>

<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.9.8</version>
</dependency>
```
