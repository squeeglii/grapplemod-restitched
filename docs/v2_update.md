# V2 Changelist Guide

## Additions

### Blueprints & Template Table

 - Added Blueprints
   - Stores the properties from a Grappling Hook Modification Bench onto an item
   - Can be used to restore a configuration or to modify a grappling hook
   - Allows for customizations to be shared and transported a little easier.
 - Added the Template Table for storing blueprints
   - Right-clicking the table with a hook copies the properties of the starred blueprint
   - The texture updates to indicate how full it is


### Hook State is now saved.

 - Hooks now remain hooked to blocks after leaving and joining a world.
   - When hooked to a block, the state of the hooks are stored
   - Saved to NBT so states persist across restarts.
   - Should work in server crashes too if hooked for long enough.

### Compatibility API

 - To make solving conflicts with mods easier, there's a new compatability API!
   - It grants access to basic utility actions like enabling + disabling hooks
   - It has events for mods to listen in on which should help reducing physics issues.

### More Customizations!

 - Added the 'Style' category
   - Allows for you to customize the texture of your rope
   - Allows you to make your rope glow in the dark.

### Advancements

 - Added 8 new advancements to follow mod progression
   - Cover the path to customizing your first Grappling Hook
   - Highlights items like the Long Fall Boots, Forcefield, + Porta-Rocket
   - Has a few little challenges using all the items


### Plus Other Stuff Like...

 - Added the 'Porta-Rocket' to match items like the Ender Staff + Forcefield.
 - Added data-driven access to hook physics data
 - You can now always climb towards the hook on a forcefield hook.


## Changes

### User-Facing

 - Improved the Grappling Hook Modification Bench
   - The block has seen a re-texture to put it in line with vanilla
   - The UI has been altered to improve the layout
 - Merged some boolean customizations into multi-option customizations
   - They now have a button in the customization screen that cycles
   - This should be a bit more user-friendly
 - Item Tooltips (The text you see on hover) have been cleaned up.
   - More consistent formatting + colouring
   - Controls should be a bit clearer.


### Resource Packs

 - Most customization translation keys have changed.


### Internal Rewrite of Customizations:

 - Properties & Categories now use a registry, thus they're much more extensible.

 - All grappling hook properties/configuration ids have been changed
   - All are now namespaced (starting with `grapplemod:`)
   - Some have seen changes for consistency `enderstaff` -> `ender_staff`
   - There's now a hard limit of 128 characters on their length.

 - All grappling hook category ids are now namespaced

 - Properties are no longer restricted to booleans and doubles.
   - Some boolean pairs of properties have been merged together into a new Enum type.
   - Any type is now possible, as long as an encoder, decoder, and checksum calculator can be written for it.
   - Any hardcoded checks for booleans or doubles should've been abstracted away into common interfaces.
 
 - Packets & NBT (because of CustomizationVolume changes) no longer store all properties
   - If a value is equal to a property's default, it is omitted.
   - Packets now include a property's id, followed by its value.
   - Packets no longer send the keys in a fixed order
 
 - Calculations for crc32 have been changed.
   - Shouldn't affect packets but all previous checksums in NBT will no longer match
   - Datafixers will ignore old checksums and calculate new ones.

 - Hook Templates now store their custom names in NBT
   - This template metadata is erased when a hook is modified
   - It stores the template identifier as well as the display name
   - This allows erasing a name at an anvil to default back to the template (and removes the italics!)

 - NBT structure has changed for grappling hooks.
   - Before:
     - "custom"
       - "property1": value
       - "property2": value
       - ... - all properties registered with the game have a value
       - "crc32": checksum
   - After
     - "hook_template"
       - "id": value 
       - "display_name": value
     - "mod_data_version"
           - "grapplemod": 2
     - "customization"
       - "properties"
         - "property1": value
         - "property2": value
         - ... - only modified properties have a value.
       - "crc32": checksum

 - NBT structure has changed for grappling hook modifier block entities.
   - Before:
       - "unlocked"
           - "0": true
           - "1": false
           - "2": true
           - ... - all customizations have an entry.
       -  "customization": <see old grapple hook "custom">
   - After:
       - "mod_data_version"
           - "grapplemod": 2
       - "unlocked"
           - "namespaced:id1": true
           - "namespaced:id2": true
           - ... - only unlocked categories have an entry saved.
       -  "customization": <see new grapple hook "custom"

### Upgrader-Upper!

Because Mojang's [DataFixerUpper](https://github.com/Mojang/DataFixerUpper) is a nightmare to figure out & there's
no longer a supported Fabric/Quilt API for it, there's a home-made replacement for it built into the mod.

It's not nearly as sophisticated, but it seems to do the job. With it, any new updates to the NBT data of the mod will
change a version number now stored in NBT. Any previous versions of the mod (Fabric 1.x / Forge) will be upgraded to
v2 and be marked accordingly.

*TLDR: The upgrade from older versions to v2 should be seamless unless you're messing with NBT directly.*


### Other Internal Changes Like

 - Grappling Hook Physics Controllers now have ResourceLocation ids
 - Key Enums have been replaced with just vanilla KeyMappings
 - Cleaned Up + Refactored a lot of the older classes.