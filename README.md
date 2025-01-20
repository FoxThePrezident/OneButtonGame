# Player Actions

Player actions are divided into two categories:

- **Actions:** Movement, inventory management, etc.
- **Sets:** Groups of actions organized together.

# Actions

### Movement

Your move direction is not determined by arrow keys or WASD, but it is determined by currently shown arrow.
As soon as you press anything on keyboard, your character move according to your currently shown arrow
(arrow is pointing to the right, you press space, player will move to the right)
It does not matter if you press key W, T, F or space key, it will count as a same input.

The game engine follows this process during movement:
1. The player's position is updated.
2. The game engine renders the tiles under the player.
3. Entities (like the player, signs, enemies, etc.) are updated accordingly.
   The player can move in only four directions: up, down, left, and right.

# Sets

Sets are a way to group individual actions together, such as a movement set or inventory set. These sets are defined in
the [player_actions.json](src/main/resources/json/player_actions.json) file.
The schema for this JSON file is located [here.](src/main/resources/json/schemas/player_actions.json)

You can modify the sets as needed. The icons for the actions are stored in the Icons.Player class. The actions in a set
are ordered starting from the player's right.

# Combat

Combat begins automatically as soon when two entities occupy the same space.
It happens as soon as something goes on top of something. Like player move to place where enemy is. Then enemy's HP will
subtract from player HP.
<details>
  <summary>Example:</summary>

Player HP: 15
Enemy HP: 10

If they land on same place:
Player HP: 15 - 10 = 5
Enemy HP: 10 - 15 = -5

Because Enemy HP is in negative now, it will remove itself from listeners list.
It won`t be called in next screen refresh.

In case when player have suddenly negative HP, the game will ends.
</details>

# Enemy

Enemy have simple pathfinding algorithm, similar to pack-mans ghosts.
It only checks all valid directions, calculate how far is player from this position and pick one, that brings him closer to player.
Valid directions are directions, where enemy could move, like empty tile but not a wall.

Collision with player are checked twice. Once before any movement (case when player lands on top of them) and after
moving (case when enemy land on top of a player).
Between these checks happen path finding algorithm.

Note: enemy don't move always, only every second turn and so on. These movement skips happen periodically. YOu could use
them to plan a way to move past them without them inflicting damage on you.