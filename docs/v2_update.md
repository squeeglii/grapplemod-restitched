# V2 Changelist Guide

### User facing changes

 - Merged some boolean customizations into multi-option customizations
   - They now have a button in the customization screen that cycles
 - Most customization translation keys have changed.


### Internal Customization Rewrite:

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
   - Datafixers will ignore old checksums and calculate new ones..

 - NBT structure has changed for grappling hooks.
   - Before:
     - "custom"
       - "property1": value
       - "property2": value
       - "crc32": checksum
   - After
     - "custom"
       - "properties"
         - "property1": value
         - "property2": value
       - "crc32"

 - NBT structure has changed for grappling hook modifier block entities.
   - Before:
       - "unlocked"
           - "1": true
           - "2": false
       -  "customization": <see old grapple hook "custom">
   - After:
       - "unlocked"
           - "namespaced:id1": true
           - "namespaced:id2": false
       -  "customization": <see new grapple hook "custom">