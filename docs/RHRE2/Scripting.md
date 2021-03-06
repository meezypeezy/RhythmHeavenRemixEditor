## Note: this is an archived page for RHRE2. This feature was cut from `v3.0.0` onwards.

RHRE2 has **experimental** Lua scripting support, starting in `v2.9.0`. [Learn about Lua here](https://www.lua.org/), and [look at the style here](https://github.com/Olivine-Labs/lua-style-guide). (It should be noted that the style used here tends to lean towards Java's lowerCamelCase and using double-quotes for strings.)

## A note on immutability
You **must** use the `add/remove/change` (ex: `changeCueDuration`) methods for the objects provided. Changing their value directly **does not affect anything**, and will lead to **undefined behaviours!**

# Types

### Entity
The entity type is what represents a cue or pattern.

| Field | Type | Description |
| ------|------|-------------|
| `beat` | `double` | Beat position of the entity. |
| `duration` | `double` | Duration of the entity in beats. |
| `track` | `int` | 1-based track position of the entity. Origin is bottom-left. |
| `id` | `string` | The ID of the entity. |
| `isPattern` | `bool` | If the entity is a pattern. |
| `semitone` | `int` | The adjusted pitch in semitones. May be negative to indicate lower notes. |

### Tempo Change
The tempo change type is what represents a BPM change in the remix.

| Field | Type | Description |
| ------|------|-------------|
| `beat` | `float` | Beat position of the entity. |
| `seconds` | `float` | Seconds representation of the beat position. |
| `tempo` | `float` | The **new** tempo to switch to. |

### Game
The game type is what represents a databased game in the registry with all its cues and patterns.

| Field | Type | Description |
| ------|------|-------------|
| `id` | `string` | ID of the game. |
| `name` | `string` | Human-readable name of the game. |
| `cues` | `list<cue>` | Cue table of all the cues implemented for this game. |
| `patterns` | `list<pattern>` | Pattern table of all the patterns implemented for this game. |
| `series` | `string` | Gets the series for this game. May be OTHER, TENGOKU, DS, FEVER, MEGAMIX, SIDE, or CUSTOM. |
| `priority` | `int` | (`v2.17.0`) The ordering priority for the game list. Count-ins have a higher priority than normal games. Non-canon games have a lower priority. |
| `isRealGame` | `bool` | (`v2.17.0`) If the game is a real game or not. Count-ins are not real games. This is used to determine if the game should be displayed in the current game renderer (presentation mode). |

### Cue
The cue type holds basic information about a cue **in the database** (and not an Entity!).

| Field | Type | Description |
| ------|------|-------------|
| `id` | `string` | ID of the cue. |
| `name` | `string` | Human-readable name of the cue. |
| `duration` | `float` | Beat duration. |
| `deprecated` | `list<string>` | Returns a (possibly empty) list of deprecated IDs. |
| `canAlterPitch` | `bool` | True if you can scroll to change the pitch. |
| `canAlterDuration` | `bool` | True if you can stretch/shrink this cue. |
| `introSound` | `string` | Returns the sound ID if this has an intro sound, nil otherwise. For example, Moai Doo-Wop's "ooo" SFX has an intro sound for the "d" sound at the start. |
| `baseBpm` | `float` | Returns the base BPM this sound is supposed to be played at, 0.0 otherwise. This is used for Manzai Birds (speed up with BPM). |
| `loops` | `bool` | True if this sound loops, false otherwise. |
| `pan` | `float` | (v2.10.4+) The pan of the sound, -1.0 to 1.0. |

### Pattern
The Pattern object holds information about a pattern **in the database**.

| Field | Type | Description |
|-|-|-|
| `id` | `string` | ID of the pattern. |
| `name` | `string` | Human-readable name of the pattern. |
| `deprecated` | `list<string>` | Returns a (possibly empty) list of deprecated IDs. |
| `canAlterDuration` | `bool` | True if you can stretch/shrink this pattern. |
| `patternCues` | `list<pattern cue>` | List of pattern cues (see below). Note: these are not the same as normal Cue objects, as they have more metadata (positional, etc.). |
| `autoGenerated` | `bool` | (`v2.10.0`) True if auto-generated by the editor. |

### Pattern Cue
These are a data object for Patterns. Do not confuse them with normal Cues.

| Field | Type | Description |
|-|-|-|
| `id` | `string` | ID of the cue to use. |
| `beat` | `float` | The relative beat position. |
| `track` | `int` | The relative track position (starts at 1). |
| `duration` | `float` | The duration of the cue. If less or equal to zero, it indicates inheritance. |
| `semitone` | `int` | The semitone for the cue. Usually zero. |


# Global Variables
Here are some global variables provided in the context of each script.

## `registry`
The Registry table. Contains access to the cues and patterns **in the registry**.

### Fields
| Fields | Type | Description |
|-|-|-|
| `games` | `table<string, game>` | Map of all the games. |
| `cues` | `table<string, cue>` | Map of all the cues (ID -> Cue object). |
| `patterns` | `table<string, pattern>` | Map of all the patterns (ID -> Pattern object). |

----

## `remix`
The Remix table. Contains access to the track, as well as adding/removing entities.

### Fields
| Field | Type | Description |
|-------|------|-------------|
| `entities` | `list<entity>` | Immutable table of all the cues. The key is the index (1-based), the value is an Entity. See more info on Entities above. To modify, use the functions defined in `remix`. |
| `playbackStart` | `beat: float` | Returns the playback start in beats. |
| `musicStart` | `seconds: float` | Returns the music start in **seconds**. |
| `tempoChanges` | `table<int, tempo change>` | Immutable table of all the tempo changes. The key is the index (1-based), the value is a Tempo Change. To modify, use the functions defined in `remix`. |
| `length` | `beatLength: float` | Gets the length of the remix in beats. |
| `musicVolume` | `volume: float` | Gets the volume of the music as a percentage (0.0 to 1.0). |
| `entityCount` | `count: int` | Returns the number of entities in the remix (cues and patterns). |
| `gamesUsed` | `games: list<string>` | Returns a list of games used by ID. |

### Functions

All functions are member functions (you must invoke it through `remix:func`).

| Function | Parameters | Return Type | Description |
|----------|----------|-------------|-------------|
| `beatsToSeconds` | `beats: float` | `seconds: float` | Converts beats to seconds. |
| `secondsToBeats` | `seconds: float` | `beats: float` | Converts seconds to beats. |
| `addCue` | `id: string, beat: float, track: int, [duration: float]` | `index: int` | Adds a cue to the remix at the specified beat, track level, and optional duration. The ID determines if it is a pattern or not (patterns use underscores, every other ID uses a forward slash). Tracks are 1-based, and the origin is bottom-left. This will allow intersections. Duration may be less than 0 to indicate inheriting the default duration. Returns the index, or -1 if it failed (duration too short). |
| `removeCue` | `index: int` | `successful: bool` | Removes a cue based on its index. Returns true if removed, false otherwise. |
| `removeAllCues` | `nothing` | `removed: int` | Removes all cues from the remix. Returns the number of cues removed. |
| `moveCue` | `index: int, newPos: float` | `successful: bool` | Moves a cue. Returns true if successful, false otherwise. |
| `changeCueDuration` | `index: int, newDuration: float` | `successful: bool` | Changes a cue's duration. Returns true if successful, false otherwise. This can fail if the duration is too short (less than 0.125). |
| `changeCueSemitone` | `index: Int, newSemitone: int` | `successful: bool` | Change's a cue's semitone. Returns true if successful, false otherwise. This will fail if the limit has been reached, or if the cue cannot be changed. |
| `changePlaybackStart` | `newPos: double` | `oldPlayback: float` | Changes the playback tracker head. |
| `changeMusicStart` | `newPos: double` | `oldMusicStart: float` | Changes the music tracker head. |
| `changeMusicVolume` | `volume: double` | `oldVolume: float` | Changes the music volume as a percentage (0.0 to 1.0). |
| `changeMusic` | `absolutePath: string` | `successful: bool` | Changes the music, loading from an **absolute** path. Returns true if successful, false otherwise (errors printed to console). |
| `addTempoChange` | `beats: float, tempo: float` | `successful: bool` | Add a tempo change at the specified beat. Returns the new index, or -1 if it failed. Note that this is the only way to change a tempo change's tempo (remove and re-add).|
| `removeTempoChange` | `beat: float` | `successful: bool` | Remove a tempo change by its beat position. |
| `findTempoChange` | `beat: float` | `tempoChange: tempo change` | Find a tempo change by the beat it's on. Returns it if found, nil otherwise. |
