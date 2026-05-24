# Custom Constructors / Initialization / Custom variable Changes

This document is made to simply keep track of the custom variables, constructors and such that have been added and used.
The reason for this is because upstream vanilla spd will continue to use old constructors, which may not throw a exception and go undetected

Current types of files to check during merging:
1. level floors/regions (optional)
2. mob sprites (95% of cases replace with new constructor)
3. items/mobs/other that cast projectiles (these should error)
4. any form of music usage (ALWAYS replace with new constructor)


## detailed versions:

## 1. level floors/regions
these won't necessarily be broken or cause issues, but have some variables that make life easier.

for the constructor:
1. canSpawnItems = bool. toggles if the generated floor spawns any items at all. false applies to guritems below too
2. canSpawnGurItems = bool. toggles if the generated floor spawns limited items like SOU, POS, etc
3. additionalItemsToSpawn = int. the additional items to spawn ontop of the already amount to spawn
4. canSpawnFood = bool. toggles if the generated floor guaranteed spawns a ration of food on the floor



## 2. mob sprites
not having this use the custom constructor will cause the animation speed setting to not work for that mob

old constructor: new MovieClip.Animation( fps = int, looped = bool );

new constructor: createAnimation(id = string, fps = int, looped = bool, ismob = bool, isboss = bool);

id is usually set to whatever we call the animation in-code.

ismob is optional, default is true. hero's and npc's should be FALSE for This.

isboss is optional, default is false. bosses should be TRUE, and minibosses should be FALSE.

okay, so why does this matter?
using the id, we can choose to speed up only certain animations without breaking the game. it also gives the freedom to mess with animations more than vanilla allowed

again: using vanilla contructor breaks nothing, and is fine for sprites with a 1 or 0 frame animation. not using it will break the animation speed setting though.



## 3. items/mobs/other that cast projectiles (these should error)
some mobs cast projectiles, and as part of the animation speed setting, should speed up.

some items actually use those projectiles too though, so we changed the constructor to require a new argument, ismob = bool. the usage should speak for itself



## 4. any form of music usage
when playing music, we wish to say in GLOG what is currently playing

while you might be confused as to why other than it's cool, the reason is simple: copyright.
i am not quite a music producer and while paying someone to produce some seems logical, i dont expect this modded version to blow up up to the point where thats considerable.
as a result, we might use music from other games or sources. we of course need to credit them, and thats one of the reasons for this system.

old constructor: Music.INSTANCE.(some action method)

new constructor: MusicAnnouncer.(some action method)

their usage remains the same and nothing additional will need to be done; the MusicAnnouncer class handles it. 
play, playTracks, end, and fadeOut methods are added. if you miss any and really need it, add it to MusicAnnouncer and implement it correctly. the main reason we go through this class is to correctly reset and reinitialize it.

if the code doesnt use the new constructor: credits GLOG wont appear and music might not correctly play or break completely.