# A set of tools for fighting games

A fully [modular](https://github.com/Sophon/Cornerman/wiki/Adding-new-features) set of tools for the fighting game community.

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
    
    subgraph Features
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

### FEATURES
- [🌟 frame data commands](https://github.com/Sophon/Cornerman/wiki/Tekken-8-features)
- [🌟 fighting game glossary](https://github.com/Sophon/Cornerman/wiki/Fighting-game-glossary)

### CURRENT FEATURE MODULES
- [Discord bot](./feat/botDiscord/src/main/kotlin/DiscordBot.kt)
- [fighting-game glossary](./feat/glossaryInfil/src/main/kotlin/InfilGlossary.kt) from [Infil](https://glossary.infil.net/)
- [Tekken 8 frame data](./feat/wikiWavu/src/main/kotlin/WavuWikiClient.kt) from [Wavu Wiki](https://wavu.wiki/)
- Tekken 8 `pc` and `heat` commands

# [CHECK THE WIKI](https://github.com/Sophon/Cornerman/wiki/Adding-new-features)


### TODO's:
- Discord bot
  - frame data
    - `/ms` command 
      - moves that are the same start-up or faster
  - `/feedback` command
    - also banlist
  - reaction commands for Discord embeds
  - bot version
  - other wikis
    - SuperCombo wiki
    - Dustloop wiki
  - module configuration
- Docker, pipelines, updates
- mobile client


### Long term goals:
- Wank Wavu functionality
- Twitch bot
- frame data for older games

