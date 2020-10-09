SOFT2201 Assignment 2
=====================

## Style guide
Code style: Google Java Style Guide https://google.github.io/styleguide/javaguide.html

Code style tooling: https://github.com/google/google-java-format

The style guide is only used for coding _style_ and documentation is applied to methods and
classes at the discretion of the developer.

## Configuration
The configuration is by default loaded from "LevelOne.json" included in the source code.

If there is an error with the configuration an exception will be thrown on startup,
so be sure to ensure that the configuration is well formed.

Each configuration contains a path to a new Level file. If this path is invalid, or the file specified
has an invalid configuration, an error will be thrown once the Player reaches this point in the game.
To check a file path is correct or a file in configured properly, you may change the source code to load this
level first.

Some configurations are provided as well, to try them use use the command `gradle run --args
 "[path to file]"`

 * `example.json` -- The example configuration, it includes a small level with a few enemies
 * `big_stickamn_fast_clouds.json` -- A configuration where the stickman is big and the clouds
  are faster
 * `broken_config.json` -- A JSON file that is not a well formed configuration, missing all the
  required information. This will cause an error on loading
 * `broken_config_2.json` -- Not even a JSON file. This will also cause an error on loading

### Configuration format
The format for the configuration is reasonably straight forward.
At the top level of the json we set some "global" configuration settings, things that are constant
 across all the levels. Some of these are the same from the previous assignment such as
  cloudVelocity and stickmanSize.
  There is a new object, Level, which contains all the level specific information:
  * Stickman X position: `stickmanPos.x`
  * Level height: `height`
  * Level width: `width`
  * Floor height: `floorHeight`
  * Level name: `levelName`
  * Next level: `nextLevel`
  * Target time: `idealTime`


### Entities
 entities is a list of individual entities such as clouds, slimes, goals and platforms
  * `name`: cloud, slime, goal or staticPlatform
  * `position.x`: x position of the entity
  * `position.y`: y position of the entity
  * (optional) `movement`: Only applies for the slimes, random, guard or stay

### Movement strategies
  The different movements have different behaviors
  * `guard`: the slime will walk back and forth over a spot, to guard it
  * `random`: the slime will move randomly, either left, right, jump or staying still. It may
   combine jumping with a direction
  * `stay`: stay will keep the slime in its starting position

## Enemies
* Enemies will cause damage to the player for standing near them
* Enemies can hurt each other, there is no loyalty among the slimes!

## Controls
* **Left arrow** Move left
* **Right arrow** Move right
* **Up arrow** Jump
* **Q** Quicksave current game state
* **R** Reload state which has been Quicksaved.

## Additional Resources
All additional resources were drawn by Joel Aquilina with the GNU Image Manipulation Program
