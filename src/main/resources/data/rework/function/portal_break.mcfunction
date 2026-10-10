tellraw @s [{text:"<", color:red}, {"player": {"properties": [{ "name": "textures", "value": "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzIzM2Q2YTJlMmE2NDFhN2MwNGQzZjNkMjM1NmZmYWM0MTE1MzdhMjM5ZjM3YjMzZDYzZDY2YjQzYmMxZThmNSJ9fX0="}]}, color:white}, {text:" Guide Bunny> ", color:red}, {text:"You are not ready to enter", color:white}, {text:" The Nether ", color:dark_red}, {text:"yet. Complete Questline 3 first.", color:white}]
fill ~-1 ~-1 ~-1 ~1 ~1 ~1 air replace minecraft:nether_portal
advancement revoke @s only rework:hidden/nether_portal_check
