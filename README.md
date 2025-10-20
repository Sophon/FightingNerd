# A set of tools for fighting games

A shared code-base with various tools:
- [modular](https://github.com/Sophon/Cornerman/wiki/Extending-the-Discord-bot) - easily extend support for SuperCombo Wiki, Dustloop etc
- Tekken 8
  - [fighting-game glossary](./feat/glossaryInfil/src/main/kotlin/InfilGlossary.kt) from [Infil](https://glossary.infil.net/)
  - [frame data](./feat/wikiWavu/src/main/kotlin/WavuWikiClient.kt) and other functionalities from [Wavu Wiki](https://wavu.wiki/)
  - [Discord bot](./feat/botDiscord/src/main/kotlin/DiscordBot.kt)

# Features

### Modularity
```mermaid
graph LR
    subgraph Bots
        discordBot[Discord Bot]
        yourBot[Your Bot]
    end
    
    subgraph "Mobile Clients"
        android[Android]
        iOS[iOS]
    end
    
    subgraph Sources
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
    style android fill:#10B981,stroke:#059669,color:#fff
    style iOS fill:#A78BFA,stroke:#7C3AED,color:#fff,stroke-dasharray: 5 5
    style glossaryInfil fill:#10B981,stroke:#059669,color:#fff
    style wikiWavu fill:#10B981,stroke:#059669,color:#fff
    style yourWiki fill:#6B7280,stroke:#4B5563,color:#fff,stroke-dasharray: 5 5
```

<details>
<summary>Frame data</summary>

![Frame data](repoAssets/framedata.gif)

</details>

<details>
<summary>Fighting game glossary</summary>

![Glossary](repoAssets/glossary.gif)

</details>


### TODO's:
- Discord bot
  - frame data
    - `/ms` command 
      - moves that are the same start-up or faster
  - `/feedback` command
    - also banlist
  - reaction commands for Discord embeds
  - bot version
  - SuperCombo wiki
  - Dustloop wiki
  - config for modules
- mobile client

# Features
### Frame data
![Frame data](repoAssets/framedata.gif)

### Fighting game glossary
![Glossary](repoAssets/glossary.gif)


### Long term goals:
- Twitch bot
- Docker, pipelines, updates
- frame data for older games
- Wank Wavu functionality

