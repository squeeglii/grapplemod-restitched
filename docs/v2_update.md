# V2 Changelist Guide

### User facing changes
 - Merged boolean customizations now use a drop down.
 - Most customization translation keys have changed.

### Internal Customization Rewrite:
 - Properties & Categories now use a registry, thus they're much more extensible.
 - All grappling hook properties/configuration ids have been changed
   - All are now namespaced (starting with `grapplemod:`)
   - Some have seen changes for consistency `enderstaff` -> `ender_staff`
   - There's now a hard limit of 128 characters on their length.
 - All grappling hook category ids are now namespaced
 - Packets & NBT no longer store all properties
   - If a value is equal to a property's default, it is omitted.
   - Packets now include a property's id, followed by its value.
   - Packets no longer send the keys in a fixed order
 - Properties are no longer restricted to booleans and doubles.
   - Some boolean pairs of properties have been merged together into a new Enum type.

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