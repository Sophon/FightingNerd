# Privacy Policy for FightingNerd
**Last Updated:** 2026-06-26

## Overview
FightingNerd is a set of fighting game frame data tool.

It exists in two forms:
- a Discord bot for fetching fighting game frame data
- a mobile app for browsing fighting game frame data and move lists

This privacy policy explains what data is collected by each.

## Mobile App

The FightingNerd mobile app collects no data. No personal information, no usage analytics, no crash reports, and no identifiers of any kind are collected or transmitted.

Frame data displayed in the app is fetched from public game wikis (see Third-Party Services below). No user-identifying information is sent with these requests.

## Discord Bot

### General Usage
With `ewgf`, the user can **opt-in** to have their Discord ID and Tekken ID stored in a database.

The user can opt-out any time they want with `unregister` - data will be deleted.

With every other commands, no personal data is collected or stored. The bot:
- receives the command
- fetches the requested frame data
- responds in the channel

### Command Usage Statistics
Aggregate counts of command usage are tracked for operational purposes. This data is:
- anonymous — no user, server, or message identifiers are stored
- aggregate only — only a count per command type is recorded
- retained for 30 days in rolling daily summaries

### Error Logging
When the bot encounters an error processing a command, minimal debugging information is temporarily captured in platform logs:
- the command query that caused the error
- the server name

**Purpose:** Debugging and improving bot reliability  
**Retention:** Platform logs are retained for approximately 7 days, then automatically deleted  
**Access:** Only authorized developers can view logs  
**Not permanently stored:** This data is not saved to any database

### What bot data is never used for
Data collected by the bot is never:
- shared with third parties
- used for advertising
- sold
- used for tracking

## Third-Party Services

FightingNerd interacts with:
- **Discord API** — used by the bot to send and receive messages
- **Game Wikis** — publicly available frame data and related fighting game data is fetched from:
    - Wavu Wiki (Tekken)
    - SuperCombo (Capcom fighters, Mortal Kombat)
    - Dream Cancel (SNK fighters)
    - DustLoop (ArcSys fighters)
    - Infil Glossary (FGC glossary)
    - Mizuumi (poverty fighters)

Each of these services has its own privacy policy governing their public data.

## Your Rights

For bot users, the following rights apply:
- **Access** — error log data that may have been temporarily captured can be requested
- **Data minimization** — only minimal error information is captured, and it is automatically deleted after 7 days

For app users: no data is collected, so there is nothing to access, delete, or modify.

## Changes to This Policy
This privacy policy may be updated in the future.

## Contact
For privacy questions or data deletion requests:
- GitHub: [Sophon/FightingNerd](https://github.com/Sophon/FightingNerd)
- Discord: @[phd_cunnilingus]

## Children's Privacy
FightingNerd is not directed at children under 13. Data from children under 13 is not knowingly collected.
