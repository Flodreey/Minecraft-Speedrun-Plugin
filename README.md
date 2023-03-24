# Minecraft Speedrun Plugin

With this Minecraft plugin, you can manage your speedrun with your friends. You can time, stop or pause your speedrun. Once you have defeated the ender dragon, your time and the participating players will be saved, so you can view the results at a later time.

The plugin is ideally designed for up to eight players, but more players are also possible.

Additionally, with the position-command, players can quickly share their position with other players, display their position in the action bar, and figure out the positions of other players.

## Quickstart
Clone the template project to your system:
````bash
git clone https://github.com/joe-akeem/minecraft-plugin-template.git
````

This project uses [Maven](https://maven.apache.org/) for building. So run `maven package` (IDE, terminal, ...) and the resulting JAR-File is called `speedrun-1.0-SNAPSHOT.jar`

Put this JAR-File into the plugins folder of your minecraft server. Start your Minecraft client and your Minecraft Paper-Server and try out the implemented commands of the next section.

## Commands

### Command `/speedrun`
- `/speedrun start` : 
- `/speedrun stop` : 
- `/speedrun pause` : 
- `/speedrun list` : 
- `/speedrun help` : 

### Command `/position`
- `/position` : 
- `/position <player name>` : 
- `/position on` :
- `/position off` :

## Contributions
I am new to Minecraft plugins. So contributions and suggestions for improvement are always welcome! 

## Screenshots
Generating spawn cages depending on the number of players
<div>
  <img width="500" src="https://user-images.githubusercontent.com/80370750/227575107-4a37b2df-6e3f-47dc-9c5f-644ec4a2affd.png"/>
</div>

Starting the speedrun with `/speedrun start`:
<div>
  <img width="500" src="https://user-images.githubusercontent.com/80370750/227574760-29f4776c-c0ef-4154-823a-77d238beda9a.png"/>
</div>

Displaying position (instead of timer) on the action bar with `/position on`:
<div>
  <img width="500" src="https://user-images.githubusercontent.com/80370750/227579228-d97a28e1-f5b0-4a81-8483-a265ed723bdd.png"/>
</div>

Saving speedrun time and participating players when ender dragon is defeated:
<div>
  <img width="500" src="https://user-images.githubusercontent.com/80370750/227582814-0af3c829-37a2-4dbe-a7de-97123c446e26.png"/>
</div>

