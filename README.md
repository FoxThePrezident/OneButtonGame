### Table of Contents

- [Player Actions](#player-actions)
    - [Actions](#actions)
        - [Movement](#movement)
        - [Sets](#sets)
- [Combat](#combat)
- [Enemy](#enemy)
- [Menu](#menu)
- [Level editor](#level-editor)

<hr>

<a id="player-actions"></a>

# Player Actions

Player actions are divided into two categories:

- **Actions:** Movement, inventory management, etc.
- **Sets:** Groups of actions organized together.

<a id="actions"></a>

## Actions

<a id="movement"></a>

### Movement

Your move direction is not determined by arrow keys or WASD, but it is determined by currently shown arrow.<br>
As soon as you press anything on keyboard, your character move according to your currently shown arrow
(arrow is pointing to the right, you press space, player will move to the right)<br>
It does not matter if you press key W, T, F or space key, it will count as a same input.

The game engine follows this process during movement:

1. The player's position is updated.
2. The game engine renders the tiles under the player.
3. Entities (like the player, signs, enemies, etc.) are updated accordingly.
   The player can move in only four directions: up, down, left, and right.

<a id="sets"></a>

## Sets

Sets are a way to group individual actions together, such as a movement set or inventory set. These sets are defined in
the [player_actions.json](desktop/src/main/resources/json/player_actions.json) file.<br>
The schema for this JSON file is located [here.](rules/schemas/player_actions_schema.json)

You can modify the sets as needed. The icons for the actions are stored in the Icons. Player class. The actions in a set
are ordered starting from the player's right.

<hr>
<a id="combat"></a>

# Combat

Combat begins automatically as soon when two entities occupy the same space.<br>
It happens as soon as something goes on top of something. Like player move to place where enemy is. Then enemy's HP will
subtract from player HP.
<details>
  <summary>Example</summary>

```
Player HP: 15
Enemy HP: 10

If they land on same place:
Player HP: 15 - 10 = 5
Enemy HP: 10 - 15 = -5

Because Enemy HP is in negative now, it will remove itself from listeners list.
It won`t be called in next screen refresh.
In case when player have suddenly negative HP, the game will ends.
```

</details>

<hr>
<a id="enemy"></a>

# Enemy

Enemy have simple pathfinding algorithm, similar to pack-mans ghosts.<br>
It only checks all valid directions, calculate how far is player from this position and pick one, that brings him closer
to player.<br>
Valid directions are directions, where enemy could move, like empty tile but not a wall.

Collision with player are checked twice. Once before any movement (case when player lands on top of them) and after
moving (case when enemy land on top of a player).<br>
Between these checks happen path finding algorithm.

Note: enemy don't move always, only every second turn and so on. These movement skips happen periodically. YOu could use
them to plan a way to move past them without them inflicting damage on you.

<hr>
<a id="menu"></a>

# Menu

Schema for menu is located [here.](rules/schemas/menu_schema.json) It is array of menu items objects. Each JSON
object is one menu item like "Create new game".

It consists of:

- label - which is display text that will be shown in game and for visibility logic
- itemType: to determine, if it should be treated as change menu or as a command
    - command - it will try to call method from
      class [MenuCommands.](common/src/main/java/com/common/menu/MenuCommands.java) Methods are predetermined in
      class [Menu.](common/src/main/java/com/common/menu/Menu.java)
- action - is optional but required in case that menu item is type of command. It is the name of method that will be
  called
- visibility - this item will be visible if current menu will be in this JSON array. Current menu is same as label of
  selected menu.

<hr>
<a id="level-editor"></a>

# Level editor

Editing or creating new map is done in game. But deleting or renaming maps must be done with directly in files.
Paths:

- Windows
    - %appdata%/OneButtonGame
- Linux
    - [user_home]/.local/share/OneButtonGame

Asides from maps stored in maps directory, it contains also settings for menu and player actions
<details>
  <summary>Keybindings</summary>

```
w - up
d - right
s - down
a - left
0 - void
1 - wall
2 - floor
3 - places player on current position
4 - places zombie
5 - places skeleton
6 - places health potion
7 - add sign
ENTER - saves the map
q - shows menu
```

</details>