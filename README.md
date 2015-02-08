# ThermalSmeltery
This is a very short mod that adds the capability to melt metals in the Thermal Expansion Crucible into Tinker's Construct Metals.

This mod basically adapts every TiCon Smeltery recipe to the Magma Crucible. In theory it should add recipes from mods adding TiCon Smeltery Recipe as well, namely Extra Tinker's Construct.

##[Download](Download)
Downloads are at [CurseForge](http://minecraft.curseforge.com/mc-mods/227661-thermal-smeltery/files).

[Download](http://minecraft.curseforge.com/mc-mods/227661-thermal-smeltery/files)

##Issue Reporting

Please include the following:

* Minecraft version
* Thermal Smeltery version
* Forge version/build
* Versions of any mods potentially related to the issue
* Any relevant screenshots are greatly appreciated.
* For crashes:
 * Steps to reproduce
 * ForgeModLoader-client-0.log (the FML log) from the root folder of the client 

##Development Setup

1. Fork/Clone and run `gradlew [setupDevWorkspace|setupDecompWorkspace] [eclipse|idea]`. This process will automatically download all the dependencies and set them up in your workspace.
2. Open in IDE of your choice.
3. Add `-Dfml.coreMods.load=cofh.asm.LoadingPlugin` to your launch options.
