<img width="2400" height="1200" alt="banner-no_bg" src="https://github.com/user-attachments/assets/0a3ff265-08ef-408f-8264-10527e8d56cf" />


# FIGHTING NERD - FGC frame data

Frame data targeting Discord bot and mobile apps:
- 🌐 multiplatform:
  - Play Store [![Play Store](https://img.shields.io/badge/Play_Store-01875F?style=for-the-badge&logo=googleplay&logoColor=white)](https://play.google.com/store/apps/details?id=io.github.sophon.fightingnerd)
  - App Store [![App Store](https://img.shields.io/badge/App_Store-555555?style=for-the-badge&logo=appstore&logoColor=white)](https://apps.apple.com/us/app/fighting-nerd/id6793185357)
  - Discord [![Discord](https://img.shields.io/badge/Discord-5865F2?style=for-the-badge&logo=discord&logoColor=white)](https://discord.com/discovery/applications/1438716136790429776)
- 🧩 highly extensible
  - 📘 glossary: Infil Glossary
  - 📚 community wikis: [DustLoop](https://www.dustloop.com/wiki/), [DreamCancel](https://dreamcancel.com/wiki/Main_Page), [Mizuumi](https://mizuumi.wiki/w/Main_Page), [SuperCombo](https://wiki.supercombo.gg/w/Main_Page), [Wavu](https://wavu.wiki/), [DragDown](https://dragdown.wiki/wiki/Main_Page), [2xko](https://wiki.play2xko.com/en-us/)
  - ELO service: [EWGF.gg](https://ewgf.gg/)


[![ko-fi](https://img.shields.io/badge/Support_on_Ko--fi-FF5E5B?style=for-the-badge&logo=kofi&logoColor=white)](https://ko-fi.com/sorryuken)

[![Buy Me a Coffee](https://img.shields.io/badge/Buy_Me_a_Coffee-FFDD00?style=for-the-badge&logo=buymeacoffee&logoColor=black)](https://buymeacoffee.com/sophon)

This README serves as a technical overview for the project.


### Tech stack

- core
  - language: `Kotlin`
  - concurrency: `Coroutines` and `Flow`
  - networking: `Ktor`
  - serialization: `kotlinx.serialization`
  - DI: `Koin`
- platforms
  - `Kotlin Multiplatform` - Android, iOS and JVM
  - Discord API - `Kord`
- logging - `Napier`
- data
  - DB - `SQLDelight`
  - preferences - `DataStore`
- testing and quality
  - unit tests - `JUnit 4`, `AssertK`
  - architecture testing - `Konsist`
  - lint: `Detekt`
- mobile
  - UI: `Compose Multiplatform` 
  - navigation: `navigation 3`
  - media: `Coil` for images, `Compose Media Player` for videos
  - IAP (donations): `RevenueCat`
  - store: `Play Review`
- Discord bot
  - containerization: `Docker`
  - cloud hosting: `Fly.io`
- others
  - versioning: `BuildKonfig`

### Architecture

- Hexagonal architecture + DDD: see [ARCHITECTURE.md](docs/ARCHITECTURE.md)
- multi-module: see [modules.mmd](docs/modules.mmd)
  - core 
  - composeApp - mobile clients
  - bot/discord - Discord bot
  - feat/admin - administrative tasks
  - feat/ewgf - ELO service
  - feat/glossaryInfil - FGC glossary
  - feat/stats - usage tracking, daily reports
  - feat/wiki - frame data from various wikis

### AI disclaimer

- October 2025 (project start) - June 2026: completely human coded.
- June 2026 - onwards: AI assisted.

Specific breakdown:

- completely human created/decided:
  - architecture
  - interfaces
  - code reviews and commits
  - features
  - documentation
  - releases
  - all project assets - banners, icons, screenshots etc.
- AI assisted, complete human supervision:
  - unit tests
  - minor refactoring using IDE MCP
  - comments
  - CICD
- AI written, minimal human supervision:
  - data models from json

### Current features

```mermaid
graph LR
  classDef green fill:green,color:#000
  classDef grey fill:lightgrey,color:#000
  classDef yellow fill:#FFEB3B,color:#000

  subgraph Bots
    discordBot[Discord bot]
    twitchBot[Twitch bot]
  end

  subgraph "Mobile Clients"
    android[Android]
    iOS[iOS]
  end

  subgraph Features
    glossaryInfil[Glossary Infil]
    communityWiki[Community Wiki]
    elo[Ranked stats]
  end

  subgraph Community Wiki
    wikiWavu[Wavu + Tekken Docs]
    supercombo[SuperCombo]
    2xko[2XKO]
    dustloop[Dustloop]
    dreamcancel[DreamCancel]
    mizuumi[Mizuumi]
    dragdown[DragDown]
  end

  subgraph Games
    t8[Tekken 8]
    sf6[Street Fighter 6]
    mk1[Mortal Kombat 1]
    ggst[Guilty Gear: Strive]
    dbfz[Dragon Ball FighterZ]
    gbvsr[Granblue Fantasy Versus: Rising]
    kof15[King of Fighters XV]
    cotw[Fatal Fury: City of the Wolves]
    xko[2XKO]
    melty[Melty Blood: Type Lumina]
    bbcf[BlazBlue: Central Fiction]
    uni2[Under Night In-Birth II Sys:Celes]
    vsav[Vampire Savior]
    avl[Avatar: Legends]
    roa[Rivals of Aether 2]
    mtfs[Marvel Tokon: Fighting Spirit]
  end

  subgraph Ranked service
    ewgf[EWGF]
    cfn[CFN]
  end

  android -->|uses| communityWiki
  iOS -->|uses| communityWiki

  discordBot -->|uses| glossaryInfil
  discordBot -->|uses| communityWiki
  discordBot -->|uses| elo
  twitchBot -->|uses| communityWiki

  communityWiki --> wikiWavu
  communityWiki --> supercombo
  communityWiki --> 2xko
  communityWiki --> dreamcancel
  communityWiki --> dustloop
  communityWiki --> mizuumi
  communityWiki --> dragdown

  elo --> ewgf
  elo --> cfn

  wikiWavu --> t8
  supercombo --> sf6
  supercombo --> mk1
  supercombo --> avl
  dustloop --> ggst
  dustloop --> dbfz
  dustloop --> gbvsr
  dustloop --> bbcf
  dustloop --> mtfs
  2xko --> xko
  dreamcancel --> kof15
  dreamcancel --> cotw
  mizuumi --> melty
  mizuumi --> uni2
  mizuumi --> vsav
  dragdown --> roa


  style discordBot fill:green, color:#fff
  style android fill:green, color:#fff
  style iOS fill:green, color:#fff
  style glossaryInfil fill:green, color:#fff
  style communityWiki fill:green, color:#fff
  style wikiWavu fill:green, color:#fff
  style supercombo fill:green, color:#fff
  style dreamcancel fill:green, color:#fff
  style dustloop fill:green, color:#fff
  style mizuumi fill:green, color:#fff
  style dragdown fill:green, color:#fff
  style 2xko fill:green, color:#fff
  style t8 fill:green, color:#fff
  style sf6 fill:green, color:#fff
  style xko fill:green, color:#fff
  style kof15 fill:green, color:#fff
  style cotw fill:green, color:#fff
  style ggst fill:green, color:#fff
  style mk1 fill:green, color:#fff
  style dbfz fill:green, color:#fff
  style gbvsr fill:green, color:#fff
  style melty fill:green, color:#fff
  style bbcf fill:green, color:#fff
  style uni2 fill:green, color:#fff
  style vsav fill:green, color:#fff
  style ewgf fill:green, color:#fff
  style avl fill:yellow, color:#000
  style mtfs fill:yellow, color:#000
  style roa fill:green, color:#fff
```

### [Long term goals](https://github.com/Sophon/FightingNerd/wiki/Features#planned-feature-modules):
- Twitch bot
- ELO rank services

___
# [License](LICENSE.txt)
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


