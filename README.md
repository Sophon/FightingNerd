<img width="2560" height="1280" alt="banner" src="https://github.com/user-attachments/assets/3522ec27-e456-4f32-966d-c611e51cf957" />


# FightingNerd

A set of tools for the Fighting Game Community:
- 🧩 highly extensible
  - 📘 glossary
  - 📚 [any community wiki](https://github.com/Sophon/FightingNerd/wiki/Features#list-of-feature-modules):
     - 📊 frame data
     - 📋 move categories
- 🌐 multiplatform:
  - 🤖 Android
  - 🍏 iOS
  - 💬 Discord
  - 🖥️ desktop

I don't drink coffee but feel free to support the server costs!

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/V7V01OEMXE)


### [CURRENT FEATURE MODULES](https://github.com/Sophon/FightingNerd/wiki/Features#list-of-feature-modules)
- [Discord bot](./bot/discord/src/jvmMain/kotlin/io/github/sophon/discord/DiscordBot.kt)
- [fighting-game glossary](./feat/glossaryInfil/src/commonMain/kotlin/io/github/sophon/glossaryinfil/InfilGlossary.kt) from [Infil](https://glossary.infil.net/)
- global frame data sourced from all feature modules
- [Wavu Wiki](https://wavu.wiki/)
  - [Tekken 8](./feat/wikiWavu/src/commonMain/kotlin/io/github/sophon/wikiwavu/WavuWikiClient.kt) 
    - frame data
    - power crush, heat or homing move-lists
- [SuperCombo](https://wiki.supercombo.gg/w/Main_Page)
  - [Street Fighter 6](./feat/wikiSupercombo/src/commonMain/kotlin/io/github/sophon/wikiSuperCombo/SuperComboWikiClient.kt) 
    - frame data
    - character data

# [CHECK THE WIKI](https://github.com/Sophon/FightingNerd/wiki)


### [TODO's](https://github.com/Sophon/FightingNerd/wiki/Features#planned-feature-module-improvements)
- Docker, pipelines, updates
- other feature modules
  - [2XKO wiki](https://wiki.play2xko.com/en-us/) feature
  - [DustLoop wiki](https://www.dustloop.com/wiki/) feature for
    - GGST
    - DBFZ
    - GBVSR
  - [DreamCancel wiki](https://dreamcancel.com/wiki/Main_Page)
    - COTW
    - KOF15
  - [SuperCombo](https://wiki.supercombo.gg/w/Main_Page)
    - MK1
    - SoulCalibur 6
  - [Wavu Wiki](https://wavu.wiki/)


### [Long term goals](https://github.com/Sophon/FightingNerd/wiki/Features#planned-feature-modules):
- Twitch bot

___
# [License](https://github.com/Sophon/FightingNerd/blob/feat/sorry/license/LICENSE.txt)
```
License: Apache 2.0 with Commons Clause

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Additional restriction: Commons Clause - you may not sell the software 
or offer it as a service. See LICENSE.txt for full terms.

© 2025 Sophon
```


