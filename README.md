# Approximately Adequate Quantity of Graves (AAQG)

A grave mod for Minecraft 1.12.2 on [Cleanroom](https://cleanroommc.com). When you die, your items are
stored in a grave block camouflaged as the ground beneath it, and you receive a death list that points
the way back. Sneak onto your grave to get everything back in its original slots.

This is a port of [TombManyGraves2](https://github.com/M4thG33k/TombManyGraves2) by M4thG33k and Tiffit
to the CleanroomMC mod template, with a new mod id (`aaqg`). Licensed under GPLv3, same as the original.

## Features

- Graves are placed at or near the place of death, rising out of liquids and optionally replacing plants and fluids.
- Per-grave lock and force/yield mode (right click and sneak-right-click the grave).
- Friends list so trusted players can open your graves.
- Death lists: an item showing what was in the grave, with a particle trail leading to it.
- Soulbound items: a configurable list of items that stay on you through death instead of going into the grave,
  with optional durability and experience penalties.
- Inventory backups as JSON files under `./aaqg/savedInventories/` for admin restore.
- Optional support for Baubles, Cosmetic Armor Reworked, Techguns, Galacticraft and Backpacks (Eydamos)
  inventories. Other mods can add their own through the `@GraveRegistry` / `IGraveInventory` API.

## Commands

| Command | Permission | Purpose |
|---|---|---|
| `/aaqg_getDeathList [player] <timestamp|latest>` (alias `/aaqg_deathlist`) | 0 | Spawn a death list from a backup |
| `/aaqg_friends add|remove|list [player]` | 0 | Manage your friends list |
| `/aaqg_toggle_render` | 0 | Toggle grave rendering client-side |
| `/aaqg_toggle_grave_pos` | 0 | Toggle grave position labels |
| `/aaqg_pop_grave <x> <y> <z>` | 2 | Drop a grave's contents |
| `/aaqg_steal <x> <y> <z>` | 2 | Take ownership of a grave |
| `/aaqg_restore <player> <timestamp> [receiver]` | 2 | Force-restore a backup |
| `/aaqg_drop <player> <timestamp> [receiver]` | 2 | Drop a backup's items |

Configuration lives in `config/aaqg.cfg`.

## Soulbound items

The `soulbound` category of the config controls what a player keeps on death. None of it applies while the
`keepInventory` gamerule is `true`.

| Option | Default | Purpose |
|---|---|---|
| `soulbound` | `aaqg:death_list` | Items that stay in the player's inventory on death, in their original slots |
| `soulbound_durability_penalty` | `10` | Percent of its maximum durability each soulbound item loses per death. Never breaks the item: it stops at 1 durability |
| `soulbound_exp_penalty` | `20` | Percent of experience levels lost on every death. The rest is kept and no orbs drop. `100` is vanilla behaviour |

Each `soulbound` entry is an item id, or an ore dictionary name with an `ore:` prefix (Minecraft 1.12 has no item
tags, so the ore dictionary fills that role):

```
soulbound {
    S:soulbound <
        aaqg:death_list
        minecraft:diamond_sword
        minecraft:elytra
        ore:ingotIron
     >
}
```

Entries that match no item or ore dictionary name are listed in an error in the log at startup and ignored.

## Building

```
./gradlew build
./gradlew runClient
```

Uses [CleanroomGradle](https://github.com/CleanroomMC/CleanroomGradle) and
[Conventions](https://github.com/CleanroomMC/Conventions). The version is computed from git tags.
