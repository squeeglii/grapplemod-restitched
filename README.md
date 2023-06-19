[![](./media/banner.png)](https://github.com/CloudG360/grapplemod-restitched)

[![Minecraft Version](https://img.shields.io/badge/Minecraft-v1.20_pre5-blue?style=flat-square)](https://www.minecraft.net/en-us)
[![Fabric Loader Version](https://img.shields.io/badge/Fabric_Loader-v0.14.19-AA8554?style=flat-square)](https://fabricmc.net/use/installer/)
[![Cloth Config Version](https://img.shields.io/badge/Cloth_Config-v11.0.97-pink?style=flat-square)](https://modrinth.com/mod/cloth-config)
[![Mod Menu Version](https://img.shields.io/badge/Mod_Menu-v7.0.0_beta.2-indigo?style=flat-square)](https://modrinth.com/mod/modmenu)
[![GPL-3.0](https://img.shields.io/badge/License-GNU_GPL_3.0-mint?style=flat-square)](https://www.gnu.org/licenses/gpl-3.0.en.html)

[![Modrinth](https://img.shields.io/modrinth/dt/f4hp6FTb?logo=modrinth&style=flat-square)](https://modrinth.com/mod/grappling-hook-mod-fabric)
[![Curseforge](https://cf.way2muchnoise.eu/short_grappling-hook-restitched.svg?badge_style=flat)](https://www.curseforge.com/minecraft/mc-mods/grappling-hook-restitched)

---

# ⚠️ Development Notice

This branch is currently in active development and hasn't yet had a public release.
This means it is **NOT** stable. So depending on your use-case:

- If you want to port the mod to another version of Minecraft, do *not* use this.
    - Use the [Stable-V1 branch](https://github.com/CloudG360/grapplemod-restitched/tree/stable-v1) instead.
- If you want to translate for the mod, *I highly recommend that you use this branch!*
    - v2 has changed a lot of translation ids
    - Most of the translations have settled, minus changes to config translations.
- If you want to add a feature to the mod, *I highly recommend that you use this branch!*
    - It may take a bit of time to be merged but it will eventually.
    - Code added to v1 will not to added to v2 unless it is a small patch fix.

---

# Project Overview

A classic Grappling Hook mod with great physics and a wide range of customizations! This Minecraft mod is written for
the Fabric & Quilt mod loaders for modern versions of Minecraft (1.18.2+) - Changes tend to focus on making it more
extendable, as well as improving compatibility with other mods. Improvements to older features are also being sprinkled
in from time to time.

This mod's official project pages can be found below:

- [Modrinth](https://modrinth.com/mod/grappling-hook-mod-fabric) (Preferred)
- [CurseForge](https://curseforge.com/minecraft/mc-mods/grappling-hook-restitched) 

Fabric compatability *comes first* due to its current adoption in the Minecraft Modding community, however Quilt is
supported by the mod currently! In the case that either Mod Loader encounters a problem, make sure to
[submit a bug report!](https://github.com/CloudG360/grapplemod-restitched)



## 🔗 Origins

This repository is an unofficial fork of Yyon's [Grappling Hook Mod](https://github.com/yyon/grapplemod), which was 
written to support the Forge mod loader. It has a history stretching back all the way to its MCreator roots in 2015,
implementing improved physics, upgrades, and better integrations since.

You can find the links to its official project pages (and the downloads) here:

- [Modrinth](https://modrinth.com/mod/grappling-hook-mod/versions)
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/grappling-hook-mod) (More up-to-date? Last checked 19/6/23)

This fork's root stems from the 1.19.2 version of v13, created under the original scope of providing an alternative
under the Fabric & Quilt ecosystems, as well as updating these ports to future versions. With the original scope met,
future versions now focus on enhancements on top of the original work, as well as perpetual support into the future.


## 📜 Credits:

See [ATTRIBUTIONS.md](/ATTRIBUTIONS.md) for credits with attached licenses, such as for sounds and code.
There are some smaller credits also found on [the original Forge repository!](https://github.com/yyon/grapplemod/)
which have been omitted here.

### Technical Contributions

- **Original Mod** - Yyon
- **Textures** - Mayesnake
- **Forge 1.18 / 1.19 Updates** - Nyfaria
- **Fabric/Quilt 1.18.2+** - CG360

### Translations

- **Russian** - Blueberryy
- **French** - Neerwan
- **Brazilian Portuguese** - Eufranio


--- 


# Contributing

Pull Requests and Issues are always welcome! Try to stick to templates where available but deviate if some components
don't apply. Detail is important when debugging an issue or trying to implement a new system however so prioritise
that!

Thanks for any help in advance! :)  -- I keep an eye out for Issues and PRs fairly regularly.


## 📦 Building/Running the project

The project can be built with:

- `gradle clean build` / `gradlew clean build` depending on your install.
- This should build to `/build/libs/`

If running the mod in a dev environment, runs should be created automatically. If not, consult the
[Fabric Loom Wiki](https://fabricmc.net/wiki/documentation:fabric_loom) on how to generate these through gradle.

Once the runs are generated, running them should place the environments for each in the following isolated folders:

- Client `/run/client/`
- Server `/run/server/`



## 📈 Updating Versions / Adding Dependencies

> Note: Configs intentionally don't work outside of release versions due to a lack of
> ClothConfig support. There is a warning in-game for this.

A lot of this project is streamlined to make version updates quicker by reducing the amount of redundant version
strings. All mod dependencies should have their versions listed in the `gradle.properties` file, using variables
to drop them into files such as `fabric.mod.json` & this README when needed. Minecraft & Fabric versions are handled in
the exact same way for the same reasons.


### For Minecraft Version Updates:

- Check [the Fabric Develop utility](https://fabricmc.net/develop/) to get the version strings for a version
    - do NOT use `yarn_mappings` -- this project uses Mojmaps
- Copy the versions found into the appropriate entries found in `gradle.properties`
    - `minecraft_long_version` is the same as `minecraft_version` for __release__ versions and __snapshot__ versions
    - For __pre-releases__ and __release candidates__, they should have an extra dot (`1.20-pre1` -> `1.20-pre.1`)
    - This is because the loaded Fabric dependency and the mappings are named with slightly different schemes. :(
- Run `gradle updateDocTemplates` / `gradlew updateDocTemplates` to update any documentation that lists versions


### For Updating Dependencies:

- Change the dependency version found in `gradle.properties`
- Run `gradle updateDocTemplates` / `gradlew updateDocTemplates` to update any documentation that lists versions


### For New Dependencies:

- Add a new entry to `gradle.properties` with the dependency's version.
- Add the dependency inside `build.gradle`, using a project placeholder referencing the `gradle.properties` property
- Add the dependency to the `fabric.mod.json`, using a placeholder referencing the `gradle.properties` property
- Add a new badge to `/template_docs/README.md`, using a placeholder referencing the `gradle.properties` property
- Run `gradle updateDocTemplates` / `gradlew updateDocTemplates` to update any documentation that lists versions

---