playsound minecraft:block.anvil.destroy master @s ~ ~ ~ 1 1
tellraw @s [{text:"<", color:red}, {"player": {"properties": [{ "name": "textures", "value": "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzIzM2Q2YTJlMmE2NDFhN2MwNGQzZjNkMjM1NmZmYWM0MTE1MzdhMjM5ZjM3YjMzZDYzZDY2YjQzYmMxZThmNSJ9fX0="}]}, color:white}, {text:" Guide Bunny> ", color:red}, {text:"No flying for you! To use ",color:white}, {type: "object", atlas: "minecraft:items", sprite:"item/elytra",color:white}, {text:" Elytra, complete Questline 5 first.",color:white}]
item modify entity @s armor.chest {"type": "minecraft:set_components", "components": {"minecraft:damage": 9999}}
advancement revoke @s only rework:hidden/elytra_check
