# Minecraft Speedrun Plugin

With this Minecraft plugin, you can manage your speedrun with your friends. You can time, stop or pause your speedrun. Once you have defeated the ender dragon, your time and the participating players will be saved, so you can view the results at a later time.

The plugin is ideally designed for up to eight players, but more players are also possible.

Additionally, with the position-command, players can quickly share their position with other players, display their position in the action bar, and figure out the positions of other players.

<div align="center">
  <img width="700" src="https://user-images.githubusercontent.com/80370750/227615986-8b72c983-1aa6-4512-a107-db466fd004cf.png"/>
</div>

## Quickstart
Clone the template project to your system:
````bash
git clone https://github.com/Flodreey/Minecraft-Speedrun-Plugin.git
````

This project uses [Maven](https://maven.apache.org/) for building. So run `maven package` (IDE, terminal, ...) and the resulting JAR-File is called `speedrun-1.0-SNAPSHOT.jar`

Put this JAR-File into the plugins folder of your minecraft server. Start your Minecraft client and your Minecraft Paper-Server and try out the implemented commands of the next section.

## Commands

### Command `/speedrun`
- `/speedrun start` : Starts the speedrun, generates the spawn cages and teleports the players into these cages. Then the 10 seconds countdown starts and after that the timer on the action bar.
- `/speedrun stop` : Stops the speedrun and discards the current speedrun time. Now players can no longer place or destroy blocks or interact with entities until a new speedrun is started.
- `/speedrun pause` / `/speedrun continue`: Pauses / Continues the speedrun and the timer. During this pause, the whole world is frozen, i.e. players and entities can no longer move and the Minecraft time also stands still. 
- `/speedrun list` : Lists all past speedruns in the form of a ranking. The speedrun time, the players involved, the date and the time are displayed.
- `/speedrun help` : Provides an overview of the arguments of the command `/speedrun`

### Command `/position`
- `/position` : Informs every other online player about your position.
- `/position <player name>` : Provides you with the position of the specified player if he is online.
- `/position on` / `/position off`: Enables / Disables the display of the own position on the action bar (instead of the speedrun timer).
- `/position help`: Provides an overview of the arguments of the command `/position`

## Contributions
I am new to Minecraft plugins. So contributions and suggestions for improvement are always welcome! 

## Screenshots
Generating spawn cages depending on the number of players
<div>
  <img width="500" src="https://user-images.githubusercontent.com/80370750/227616475-3667d5d5-820b-46b8-b74f-2fa173ac777a.png"/>
</div>

<br>
Starting the speedrun with `/speedrun start`:
<div>
  <img width="500" src="https://user-images.githubusercontent.com/80370750/227574760-29f4776c-c0ef-4154-823a-77d238beda9a.png"/>
</div>

<br>
Displaying position (instead of timer) on the action bar with `/position on`:
<div>
  <img width="500" src="https://user-images.githubusercontent.com/80370750/227579228-d97a28e1-f5b0-4a81-8483-a265ed723bdd.png"/>
</div>

<br>
Saving speedrun time and participating players when ender dragon is defeated:
<div>
  <img width="500" src="https://user-images.githubusercontent.com/80370750/227582814-0af3c829-37a2-4dbe-a7de-97123c446e26.png"/>
</div>

