# Change Log

All notable changes to the "nightworld" Minecraft mod will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Crystal Nest Semantic Versioning](https://crystalnest.it/#/versioning).

## [Unreleased]

- Monsters in the Nightworld can now spawn with a light level of up to 12.
- The Nightworld now has its own separate world clock.
- Baby villagers in the Nightworld will always try to hide.
- Slimes now have a higher chance to spawn in the Nightworld.

## [v5.0.0] - 2025/12/31

- Ported to 1.21.11.

## [v5.0.0] - 2025/11/29

- Ported to 1.21.10.

## [v5.0.0] - 2025/07/23

- Ported to 1.21.6, 1.21.7, and 1.21.8.

## [v5.0.0] - 2025/05/05

- Ported to 1.21.5.

## [v5.0.0] - 2025/02/05

- 1.20.4 and below only.
- Updated to support SSP v2.0.1.
- This is a BREAKING CHANGE! The Nightworld dimension will be reset.
- It's now possible to change the item required to light up Nightworld portals.
- Changed the namespace (from `server_sided_portals` to `nightworld`) under which configuration tags must be located.
- Added compatibility with `BetterNether` mod.

## [v5.0.0] - 2025/02/02

- Ported to 1.21.4.

## [v5.0.0] - 2025/01/01

- 1.21 and above only.
- Updated to support Cobweb v1.3.0 and SSP v2.0.0.
- This is a BREAKING CHANGE! The Nightworld dimension will be reset.
- It's now possible to change the item required to light up Nightworld portals.
- Changed the namespace (from `server_sided_portals` to `nightworld`) under which configuration tags must be located.
- Added compatibility with `BetterNether` mod (1.21 and 1.21.1 only, as `BetterNether` is not available for later versions at the time of writing).

## [v4.0.1] - 2024/12/16

- Ported to 1.21.3.
 
## [v4.0.1] - 2024/07/30

- 1.21 and above only.
- Added support for 1.21.1.

## [v4.0.0] - 2024/07/18

- Refactor to remove most of the code in favor of Server Sided Portals dependency.
- Implemented [#11](https://github.com/Crystal-Nest/nightworld/issues/11), custom portal frame with block tag.

## [v3.0.0] - 2024/07/16

- Ported to 1.21.
- Officially dropped support for Forge.
- Removed many mixins now useless.
- Removed `EntityPortal` and `Teleportable` interfaces from the `api` package.
- Skimmed the interface `NightworldPortalChecker`.

## [v2.0.1] - 2024/07/04

- Fix mod loader declared incompatibilities.
- Fix Nightworld mob spawn.

## [v2.0.0] - 2024/06/26

- Updated to a multiloader environment.
- Fixed [#2](https://github.com/Crystal-Nest/nightworld/issues/2).
- Fixed [#4](https://github.com/Crystal-Nest/nightworld/issues/4).

<details>
  <summary>Legacy</summary>

### [1.20.4-1.0.0.0] - 2023/12/16

- Port to 1.20.4.

### [1.20.2-1.0.0.0] - 2023/10/13

- First version.
- Add Nightworld dimension and its portal frame.
- Add spawn of zombies and skeletons in Nightworld portals.

### [1.20.1-1.0.0.0] - 2023/10/13

- First version.
- Add Nightworld dimension and its portal frame.
- Add spawn of zombies and skeletons in Nightworld portals.

### [1.19.4-1.0.0.0] - 2023/10/13

- First version.
- Add Nightworld dimension and its portal frame.
- Add spawn of zombies and skeletons in Nightworld portals.

### [1.19.2-1.0.0.0] - 2023/10/13

- First version.
- Add Nightworld dimension and its portal frame.
- Add spawn of zombies and skeletons in Nightworld portals.

</details>

[Unreleased]: https://github.com/crystal-nest/nightworld

[v5.0.0]: https://github.com/crystal-nest/nightworld/releases?q=5.0.0
[v4.0.1]: https://github.com/crystal-nest/nightworld/releases?q=4.0.1
[v4.0.0]: https://github.com/crystal-nest/nightworld/releases?q=4.0.0
[v3.0.0]: https://github.com/crystal-nest/nightworld/releases?q=3.0.0
[v2.0.1]: https://github.com/crystal-nest/nightworld/releases?q=2.0.1
[v2.0.0]: https://github.com/crystal-nest/nightworld/releases?q=2.0.0

[1.20.4-1.0.0.0]: https://github.com/crystal-nest/nightworld/releases/tag/v1.20.4-1.0.0.0
[1.20.2-1.0.0.0]: https://github.com/crystal-nest/nightworld/releases/tag/v1.20.2-1.0.0.0
[1.20.1-1.0.0.0]: https://github.com/crystal-nest/nightworld/releases/tag/v1.20.1-1.0.0.0
[1.19.4-1.0.0.0]: https://github.com/crystal-nest/nightworld/releases/tag/v1.19.4-1.0.0.0
[1.19.2-1.0.0.0]: https://github.com/crystal-nest/nightworld/releases/tag/v1.19.2-1.0.0.0
