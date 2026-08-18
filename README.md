<img width="3600" height="1200" alt="banner" src="https://github.com/user-attachments/assets/cb97083b-8ecc-4496-852b-c5ed48514b1f" />



# FightingNerd

Frame data targeting Discord bot and mobile apps:
- 🧩 highly extensible
  - 📘 glossary
  - 📚 [any community Wikis](https://github.com/Sophon/FightingNerd/wiki/Features#list-of-feature-modules)
- 🌐 multiplatform:
  - [![Play Store](https://img.shields.io/badge/Play_Store-01875F?style=for-the-badge&logo=googleplay&logoColor=white)](https://play.google.com/store/apps/details?id=io.github.sophon.fightingnerd)
  - [![App Store](https://img.shields.io/badge/App_Store-555555?style=for-the-badge&logo=appstore&logoColor=white)](https://apps.apple.com/us/app/fighting-nerd/id6793185357)
  -  [![Discord](https://img.shields.io/badge/Discord-5865F2?style=for-the-badge&logo=discord&logoColor=white)](https://discord.com/discovery/applications/1438716136790429776)


[![ko-fi](https://img.shields.io/badge/Support_on_Ko--fi-FF5E5B?style=for-the-badge&logo=kofi&logoColor=white)](https://ko-fi.com/sorryuken)

[![Buy Me a Coffee](https://img.shields.io/badge/Buy_Me_a_Coffee-FFDD00?style=for-the-badge&logo=buymeacoffee&logoColor=black)](https://buymeacoffee.com/sophon)


## Sample Usage

All commands are case **insensitive** - uppercase or lowercase or anything in between is identical.

All commands can be used in one of two ways:
1. tag - `@bot [command] [query]`
2. slash - `/command [query]`

In the signatures below, `<param>` is required and `[param]` is optional.

- **frame data** - `fd <character> <move>`
  - `<move>` can be input or move name
  - default command: `@bot <character> <move>` and `@bot fd <character> <move>` are identical
  - game specific variants: `fdTK`, `fdSF`, `fdGG` etc.
  - examples: `@bot ak f21` or `@bot ken 236pp` or `@bot sol fafnir`
- **character data** - `char <game> <character>`
  - game specific variants: `charGG`, `charSF`, `charAV` etc.
  - examples: `@bot charsf akuma` or `@bot charroa olympia`
- **game specific commands**
  - Tekken 8
    - `Heat <character>`, `Homing <character>`, `PC <character>`
    - `Stance <character> [stance]`
      - examples: `@bot stance steve` or `@bot stance king jgs`
    - `Strings <character> <input>`
      - examples: `@bot strings eddy u4`
  - `Inv <character>` - invincible moves
- **ELO**
  - Tekken 8
    - `EWGF [operation] [tekkenID]` - https://ewgf.gg/
      - operations:
        - `register` (or `+`), requires `<tekkenID>`: registers your Discord account
          - example: `@bot ewgf + 3DgB8jQ82rJ4`
        - (no operation): displays your last 50 matches
          - example: `@bot ewgf`
        - `unregister` (or `-`): deletes your Discord account from the database
          - example: `@bot ewgf -`
- **utility commands**
  - `Alias` - displays character aliases for a game
  - `Commands` - lists all available commands
  - `Donate` - displays links to support the project
  - `Examples` - displays example bot usage
  - `Feedback <message>` - sends feedback or bug report to the author
  - `Invite` - displays the invitation link
  - `Join <steamURL> [password|name]` - creates a clickable link to join a Steam lobby
    - default bot command: `@bot steam://joinlobby/...` or `@bot join steam://joinlobby/...` are identical
  - `Modules` - displays all enabled modules, be it games or otherwise
  - `Repo` - displays the GitHub link to the project

### [CURRENT FEATURE MODULES](https://github.com/Sophon/FightingNerd/wiki/Features#list-of-feature-modules)

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

## [CHECK THE WIKI](https://github.com/Sophon/FightingNerd/wiki)

### [Long term goals](https://github.com/Sophon/FightingNerd/wiki/Features#planned-feature-modules):
- Twitch bot
- ELO rank services

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


