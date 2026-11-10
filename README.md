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
- Grave glow: your own graves are outlined through walls once you are within 50 blocks (configurable) of them.
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
| `soulbound` | `aaqg:death_list`, `type:armor`, `type:tool`, `ore:torch` | Items that stay in the player's inventory on death, in their original slots |
| `soulbound_durability_penalty` | `10` | Percent of its maximum durability each soulbound item loses per death. Never breaks the item: it stops at 1 durability |
| `soulbound_exp_penalty` | `20` | Percent of experience levels lost on every death. The rest is kept and no orbs drop. `100` is vanilla behaviour |

Each `soulbound` entry is one of:

| Entry | Matches |
|---|---|
| `type:armor` | Anything worn in an armor slot: armor pieces, elytra, and modded armor |
| `type:tool` | Anything held and used: pickaxes, axes, shovels, hoes, swords, bows, shields, shears, fishing rods, flint and steel, and modded tools and weapons |
| `modid:item` | One item by id, such as `minecraft:totem_of_undying` |
| `ore:name` | Every item under an ore dictionary name, such as `ore:ingotIron` |

Minecraft 1.12 has no item tags, so the two types are worked out from how an item behaves (its class, equipment
slot, harvest tool classes and attributes) rather than from a list of ids. That is what lets them cover modded
gear without naming it.

```
soulbound {
    S:soulbound <
        aaqg:death_list
        type:armor
        type:tool
        ore:torch
        minecraft:totem_of_undying
     >
}
```

Entries that match nothing are listed in an error in the log at startup and ignored.

## Grave glow

A grave is camouflaged as the ground under it, so its owner gets a pulsing outline around it that shows through
blocks. Only the owner sees it: friends and the `allowGraveRobbing` option do not change that. These options are in
the `renderoptions` category of the config and are read by each client from its own file.

| Option | Default | Purpose |
|---|---|---|
| `enableGraveGlow` | `true` | Whether your own graves glow at all |
| `graveGlowRange` | `50` | How close, in blocks, you have to be for the glow to show (1-256) |
| `graveGlowColor` | `0xFFD54A` | The color of the glow |

Things to know:

- The glow is a convenience, not a secret. Each client decides whether to draw it, and the server already tells
  every client who owns a grave, so a modified client could highlight other players' graves.
- It can only show for graves in chunks your client has loaded. With a very low render distance it appears later
  than `graveGlowRange`, and it never shows for a grave in another dimension.
- Shader packs may tint or hide it.
- `/aaqg_toggle_render` hides the glow along with the grave.

## Building

```
./gradlew build
./gradlew runClient
```

Uses [CleanroomGradle](https://github.com/CleanroomMC/CleanroomGradle) and
[Conventions](https://github.com/CleanroomMC/Conventions). The version is computed from git tags.
