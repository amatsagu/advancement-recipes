playsound minecraft:entity.fishing_bobber.splash master @s ~ ~ ~ 0.7 1
tellraw @s [{text:"<", color:red}, {"player": {"properties": [{ "name": "textures", "value": "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzIzM2Q2YTJlMmE2NDFhN2MwNGQzZjNkMjM1NmZmYWM0MTE1MzdhMjM5ZjM3YjMzZDYzZDY2YjQzYmMxZThmNSJ9fX0="}]}, color:white}, {text:" Guide Bunny> ", color:red}, {text:"That seems a bit too heavy for you! To use Buckets, complete \"Heavy Metal\" advancement in Questline 3 first.",color:white}]
execute if items entity @s weapon.mainhand #rework:filled_buckets run item replace entity @s weapon.mainhand with minecraft:bucket
execute if items entity @s weapon.offhand #rework:filled_buckets run item replace entity @s weapon.offhand with minecraft:bucket
advancement revoke @s only rework:hidden/bucket_check
