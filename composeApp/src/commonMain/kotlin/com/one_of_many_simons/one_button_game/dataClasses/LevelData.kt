package com.one_of_many_simons.one_button_game.dataClasses

/**
 * For storing map and other level related information
 */
class LevelData {
    var player: PlayerMapData = PlayerMapData()
    var map: MapData = MapData()
}

/**
 * Settings related to player for level
 */
class PlayerMapData {
    var position: Position = Position()
}

/**
 * Map data for level
 */
class MapData {
    var walls: ArrayList<Position> = ArrayList()
    var interactive: ArrayList<Interactive> = ArrayList()
    var ground: ArrayList<Position> = ArrayList()
}

/**
 * Interactive things on map.<br></br>
 * Like ENEMY, potions, ...<br></br>
 * used in storing data for a map.
 */
class Interactive {
    var entityType: String = ""
    var position: Position = Position()
    var text: String = ""
}
