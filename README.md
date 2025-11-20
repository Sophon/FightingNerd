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

### ADD THE BOT TO YOUR DISCORD SERVER HERE
<a href="https://discord.com/oauth2/authorize?client_id=1438716136790429776&permissions=346112&integration_type=0&scope=applications.commands+bot">
  <img src="https://i.imgur.com/HtuRwva.png" alt="discord" width="200">
</a>

I don't drink coffee but feel free to support the server costs!

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/V7V01OEMXE)


### [CURRENT FEATURE MODULES](https://github.com/Sophon/FightingNerd/wiki/Features#list-of-feature-modules)

```mermaid
graph LR
  subgraph Bots
    discordBot[Discord Bot]
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
    wikiWavu[Wavu]
    supercombo[SuperCombo]
    2xko[2XKO]
    dustloop[Dustloop]
    dreamcancel[DreamCancel]
  end

  subgraph Ranked service
    ewgf[EWGF]
  end

  android -->|uses| communityWiki
  iOS -->|uses| communityWiki

  discordBot -->|uses| glossaryInfil
  discordBot -->|uses| communityWiki
  discordBot -->|uses| elo

  communityWiki --> wikiWavu
  communityWiki --> supercombo
  communityWiki --> 2xko
  communityWiki --> dreamcancel
  communityWiki --> dustloop

  elo --> ewgf

  wikiWavu --> t8[Tekken 8]
  supercombo --> sf6[Street Fighter 6]
  supercombo --> mk1[Mortal Kombat 1]
  supercombo --> sc6[SoulCalibur VI]
  dustloop --> ggst[Guilty Gear: Strive]
  dustloop --> dbfz[Dragon Ball FighterZ]
  dustloop --> gbvsr[Granblue Fantasy Versus: Rising]
  2xko --> xko[2XKO]
  dreamcancel --> kof15[King of Fighters XV]
  dreamcancel --> cotw[Fatal Fury: City of the Wolves]


  style discordBot fill:#3B82F6,stroke:#2563EB,color:#fff
  style android fill:#10B981,stroke:#059669,color:#fff
  style iOS fill:#10B981,stroke:#059669,color:#fff
  style glossaryInfil fill:#059669,color:#fff
  style communityWiki fill:#3366CC,color:#fff
  style wikiWavu fill:#059669,color:#fff
  style supercombo fill:#F8F9FA,color:#000000
  style 2xko fill:#059669,color:#fff
  style t8 fill:#059669, color:#fff
  style sf6 fill:#059669, color:#fff
  style xko fill:#059669, color:#fff
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


