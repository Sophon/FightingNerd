<img width="2560" height="1280" alt="banner" src="https://github.com/user-attachments/assets/3522ec27-e456-4f32-966d-c611e51cf957" />


# FightingNerd

Frame data targeting Discord bot and mobile apps:
- 🧩 highly extensible
  - 📘 glossary
  - 📚 [any community Wikis](https://github.com/Sophon/FightingNerd/wiki/Features#list-of-feature-modules)
- 🌐 multiplatform:
  - 🤖 Android
  - 🍏 iOS
  - 💬 Discord

[![Add Bot](https://img.shields.io/badge/Add_to_Discord-5865F2?style=for-the-badge&logo=discord&logoColor=white)](https://discord.com/oauth2/authorize?client_id=1438716136790429776&permissions=346112&integration_type=0&scope=applications.commands+bot)

[![ko-fi](https://img.shields.io/badge/Support_on_Ko--fi-FF5E5B?style=for-the-badge&logo=kofi&logoColor=white)](https://ko-fi.com/sorryuken)


### [CURRENT FEATURE MODULES](https://github.com/Sophon/FightingNerd/wiki/Features#list-of-feature-modules)

```mermaid
graph LR
    classDef green fill:green,color:#000
    classDef grey fill:lightgrey,color:#000

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

    elo --> ewgf
    elo --> cfn

    wikiWavu --> t8
    supercombo --> sf6
    supercombo --> mk1
    dustloop --> ggst
    dustloop --> dbfz
    dustloop --> gbvsr
    dustloop --> bbcf
    2xko --> xko
    dreamcancel --> kof15
    dreamcancel --> cotw
    mizuumi --> melty
    mizuumi --> uni2
    mizuumi --> vsav


    style discordBot fill:green,stroke:#2563EB, color:#fff
    style android fill:green,stroke:green, color:#fff
    style iOS fill:green,stroke:green, color:#fff
    style glossaryInfil fill:green, color:#fff
    style communityWiki fill:green, color:#fff
    style wikiWavu fill:green, color:#fff
    style supercombo fill:green, color:#fff
    style dreamcancel fill:green, color:#fff
    style dustloop fill:green, color:#fff
    style mizuumi fill:green, color:#fff
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
  ```

# [CHECK THE WIKI](https://github.com/Sophon/FightingNerd/wiki)

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


