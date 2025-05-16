### Table of Contents

- [Player Actions](#player-actions)
    - [Actions](#actions)
        - [Movement](#movement)
        - [Sets](#sets)
- [Combat](#combat)
- [Enemy](#enemy)
- [Menu](#menu)
- [Files](#files)
- [Level editor](#level-editor)

<hr>
<a id="player-actions"></a>

# Player Actions

Player actions are divided into two categories:

- **Actions:** Movement, inventory management, etc.
- **Sets:** Groups of actions organized together.

<hr>
<a id="actions"></a>

## Actions

<hr>
<a id="movement"></a>

### Movement

The player does not use arrow keys or WASD to move.
Instead, the game shows an arrow indicating your next direction, and pressing any key (yes, any key) will move the
player in that direction.
For example:

- If the arrow points →, and you press space, the player moves right.
- If you press W, Enter, or even Z, the result is the same: movement in the shown direction.

The game engine follows this process during movement:

1. The player's position is updated.
2. The game engine renders the tiles under the player.
3. Entities (like the player, signs, enemies, etc.) are updated accordingly.
   The player can move in only four directions: up, down, left, and right.

<hr>
<a id="sets"></a>

## Sets

Sets are groups of related actions, such as movement or inventory management. These sets are defined in the [
`player_actions.json`](desktop/src/main/resources/json/player_actions.json) file.<br>
You can refer to the corresponding JSON schema [here](rules/schemas/player_actions_schema.json) for structure and
validation.

### How Sets Work

- Each set contains a list of actions that are executed in order, starting from the player's right.
- Action icons are defined in the `Icons.Player` class and visually represent each action in the game.
- The **first set listed** in the JSON file is automatically loaded at the start of the game.
    - This means the player spawns with this set active.
    - If it's an inventory set, the game begins in inventory mode.

### Special Rule for `changeSet` Action

- During initialization, the **first occurrence of a `changeSet` action** in the first set is automatically removed.
- This is intentional: that action is reserved for **long input** (e.g., holding a key, long-pressing a touch screen,
  etc.).
- The `changeSet` should ideally lead to a **menu set**, allowing the player to switch between sets like movement,
  inventory, or game options.

<hr>
<a id="combat"></a>

# Combat

Combat happens **automatically** when two entities (like the player and an enemy) occupy the **same tile** — whether the
player moves onto the enemy, or the enemy moves onto the player.

### How It Works
- As soon as they collide, **both entities exchange damage**.
- The **enemy takes damage equal to the player's HP**, while the player takes damage based on the enemy's strength.
- The player’s **armor** absorbs part of the incoming damage. Any remaining damage reduces the player's HP.

<hr>

# Armor Mechanics
- Armor blocks **75%** of incoming damage.
- All damage calculations are **rounded down** (floored).
- If the armor’s **durability is depleted**, excess damage is applied to the player’s health.

#### Example:

<details>
  <summary>Click to expand</summary>

```
Player HP: 15
Player Armor Durability: 5
Player Armor Block Percentage: 75%
Enemy HP: 10

Collision occurs (player and enemy land on same tile):

Incoming Damage to Player: 10
- Armor absorbs 10 * 0.75 = 7
- Remaining 3 goes to HP

Armor Durability: 5 - 7 = -2 (Overloaded by 2)
Excess damage (2) is added to unblocked damage: 3 + 2 = 5

Final Player HP: 15 - 5 = 10
Enemy HP: 10 - 15 = -5 (Enemy dies)

Outcome:
- Player survives with 10 HP.
- Enemy is removed from the game (won’t be updated in the next refresh).
- If the player's HP drops below 0, the game ends.
```

</details>

<hr>
<a id="enemy"></a>

# Enemy

Enemy have simple pathfinding algorithm, similar to [Pac-Man’s ghosts.](https://www.youtube.com/watch?v=ataGotQ7ir8)<br>
It only checks all valid directions, calculate how far is player from this position and pick one, that brings him closer
to player.<br>
Valid directions are directions, where enemy could move, like empty tile but not a wall.

Collision with player are checked twice. Once before any movement (case when player lands on top of them) and after
moving (case when enemy land on top of a player).<br>
Between these checks happen path finding algorithm.

Note: enemy don't move always, only every second turn and so on. These movement skips happen periodically. You could use
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
<a id="files"></a>

# Files

- **Windows**: `%appdata%/OneButtonGame`
- **Linux**: `~/.local/share/OneButtonGame`
- **Android**: `/Android/data/com.one_of_many_simons/`

Android

- files are visible at location /Android/data/com.one_of_many_simons
- to access them, you need to do the following
    - You need to have USB debugging on your phone to be enabled that is usually locate in System > Developer options
    - Then you need to connect your phone via cable
    - You get notification that after clicking on you need to change from "charging" to "file transfer" or something
      similar
    - After that you can view files of your phone on computer

<hr>
<a id="level-editor"></a>

# Level editor

Editing or creating new map is done in game.
But deleting or renaming maps must be done with directly in files that you can find [here](#files)

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