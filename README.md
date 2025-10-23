# A set of tools for fighting games

A highly [modular](https://github.com/Sophon/Cornerman/wiki/Code-base-architecture) set of tools for the fighting game community.

```mermaid
graph LR
    subgraph "Bot clients"
        discordBot[Discord Bot]
        yourBot[Your Bot]
    end
    
    subgraph "Mobile clients"
        android[Android]
        iOS[iOS]
    end
    
    subgraph "Feature modules"
        glossaryInfil[Glossary Infil]
        wikiWavu[Wiki Wavu]
        yourWiki[Your Wiki]
    end
    
    discordBot -->|uses| glossaryInfil
    discordBot -->|uses| wikiWavu
    discordBot -.->|uses| yourWiki
    
    yourBot -.->|uses| yourWiki
    
    android -->|uses| wikiWavu
    
    iOS -->|uses| wikiWavu
    
    style discordBot fill:#3B82F6,stroke:#2563EB,color:#fff
    style yourBot fill:#6B7280,stroke:#4B5563,color:#fff,stroke-dasharray: 5 5
    style android fill:#A78BFA,stroke:#7C3AED,color:#fff,stroke-dasharray: 5 5
    style iOS fill:#A78BFA,stroke:#7C3AED,color:#fff,stroke-dasharray: 5 5
    style glossaryInfil fill:#10B981,stroke:#059669,color:#fff
    style wikiWavu fill:#10B981,stroke:#059669,color:#fff
    style yourWiki fill:#6B7280,stroke:#4B5563,color:#fff,stroke-dasharray: 5 5
```

### [CURRENT FEATURE MODULES](https://github.com/Sophon/Cornerman/wiki/Features#list-of-feature-modules)
- [Discord bot](./feat/botDiscord/src/main/kotlin/DiscordBot.kt)
- [fighting-game glossary](./feat/glossaryInfil/src/main/kotlin/InfilGlossary.kt) from [Infil](https://glossary.infil.net/)
- [Tekken 8 frame data](./feat/wikiWavu/src/main/kotlin/WavuWikiClient.kt) from [Wavu Wiki](https://wavu.wiki/)
   - frame data
   - power crush, heat or homing move-lists

# [CHECK THE WIKI](https://github.com/Sophon/Cornerman/wiki)


### [TODO's](https://github.com/Sophon/Cornerman/wiki/Features#planned-feature-module-improvements)
- Docker, pipelines, updates
- other feature modules
  - SuperCombo wiki
  - Dustloop wiki
- module configuration
- mobile clients


### [Long term goals](https://github.com/Sophon/Cornerman/wiki/Features#planned-feature-modules):
- Wank Wavu functionality
- Twitch bot

___
# [License](https://github.com/Sophon/Cornerman/blob/feat/sorry/license/LICENSE.txt)
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


