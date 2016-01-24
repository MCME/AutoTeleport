# AutoTeleport
Automated Teleportation System for Moria

The AutoTeleportation plugin teleports player when they move into a teleportation area to a relative location at a target area within the same world or an other world. Teleportations within a world keeps the velocity of a player. Teleportation between world stops movement of a player. All teleportations can keep the orientation of the player but it is also possible to define a fixed target orientation. 

Legend for command description:
<mandatoryArgument> Must be replaced with an argument text
[optionalArgument] Can be used for additional information, will use a default value if not specified.

Commands:
/atp set <AreaName> [shape] 
The location of the player issuing this command becomes the center location of the teleportation area with name <AreaName>. If an area with that name already exists it will be moved to the new location. There are two possible shapes: cuboid and sphere. Default type is cubboid. 
/atp target <AreaName> <type> [keepOrientation]
The location of the player issuing this command becomes the target location for the teleportation area with name <AreaName>. Teleportation of players always will be relative to center location of teleportation area and target location. A player who moves into the teleportation 3 block south of center location will be teleported to 3 blocks south of target location.
There are two types of teleportations: dynamic and static - static will stop movement of a player, dynamic keep velocity. Dynamic is available only for teleportations within a world. keepOrientation can be true or false.  Default is true and will not change the pitch and yaw of a player during teleportation. False is available only with static teleportation type. With keep orientation set to false the pitch and yaw of the player who issues the command will be saved. And pitch and yaw of a teleported players will be set to these values.
/atp size <AreaName> <size>
Defines the size of the teleportation area with name <AreaName>. If shape of the teleportation area is sphere <size> must be a single number which defined the radius of the sphere. For a cube teleportation area <size> must be 3 numbers separated with whitespaces. The three numbers defines the length of the cube edges along x- y- and z-Axis.
/atp delete <AreaName>
With (with confirmation query) the teleportation area with name <AreaName> will be deleted.
/atp list
Lists all teleportation areas with their target worlds. You can warp to center and target location by clicking at the area name and the target world name.
/atp details <AreaName>
Details (center, size, area type, static/dynamic, linking) about teleportation area with name <AreaName>. You can warp to center and target location by clicking at ‘Center’ and ‘Target’.
/atp on
Activates all automated teleportation.
/atp off
Deactivates all automated teleportation.
/atp exclude [who]
 Excludes player with name [who] from automated teleportation. With no optional argument the player who issues the command is excluded.. You can use 'list' instead of a player name to see a list of all excluded players.
/atp include [who]
Includes a player with name [who] to automated teleportation. You can use 'all' for [who] to include all excluded players. With no optional argument the player who issues the command is included.
/atp warp <AreaName> [where]
Warps the player who issues this command to teleport area <AreaName>. Optional argument [where] can be center or target. Default is center.
/atp help [subcommand]
With no optional argument lists short descriptions of all commands. With a given [subcommand] lists more detailed help for that command.
