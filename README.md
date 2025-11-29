# Homes++  
[![Ko-fi](https://raw.githubusercontent.com/Fluxoah/Banners/main/kofi.png)](https://ko-fi.com/fluxoah)
[![Ko-fi](https://raw.githubusercontent.com/Fluxoah/Banners/main/available_for_paper.png)](https://modrinth.com/plugin/tree-cutter+/versions?l=paper)
[![Ko-fi](https://raw.githubusercontent.com/Fluxoah/Banners/main/available_for_pupur.png)](https://modrinth.com/plugin/tree-cutter+/versions?l=purpur)
[![Ko-fi](https://raw.githubusercontent.com/Fluxoah/Banners/main/available_on_modrinth.png)](https://modrinth.com/plugin/tree-cutter+)
[![Ko-fi](https://raw.githubusercontent.com/Fluxoah/Banners/main/available_on_github.png)](https://github.com/Fluxoah/Homes-)


Lightweight, survival-focused homes plugin for Paper / Purpur / Spigot / Bukkit (1.21.x).  
Players can create named homes, teleport with a countdown (cancels on move), and admins can inspect/teleport to player homes using a polished GUI. Works with LuckPerms if present — and **still works without LuckPerms**.

---

## Table of contents
- [Quick links](#quick-links)  
- [Features](#features)  
- [Requirements](#requirements)  
- [Installation (server)](#installation-server)  
- [Build from source (Maven / IntelliJ)](#build-from-source-maven--intellij)  
- [Configuration](#configuration)  
  - [config.yml (example)](#configyml-example)  
  - [messages.yml (example)](#messagesyml-example)  
  - [plugin.yml (snippet)](#pluginyml-snippet)  
- [Commands & usage](#commands--usage)  
- [Permissions & LuckPerms](#permissions--luckperms)  
  - [Permission nodes explained](#permission-nodes-explained)  
  - [LuckPerms examples](#luckperms-examples)  
- [Admin GUI](#admin-gui)  
- [Persistence & data files](#persistence--data-files)  
- [Development notes](#development-notes)  
  - [Project layout to keep in git](#project-layout-to-keep-in-git)  
  - [Recommended .gitignore](#recommended-gitignore)  
- [Publishing / License](#publishing--license)  
- [Troubleshooting](#troubleshooting)  
- [Contributing](#contributing)

---

## Quick links
- Jump to configuration example → [config.yml (example)](#configyml-example)  
- Jump to messages → [messages.yml (example)](#messagesyml-example)  
- Jump to commands → [Commands & usage](#commands--usage)  
- Jump to permissions → [Permissions & LuckPerms](#permissions--luckperms)

---

## Features
- `/sethome <name>` — set a named home at your current location (configurable max, safety checks).  
- `/home <name>` — teleport to a named home (delayed; actionbar countdown; cancels on move).  
- `/homes` — list homes in clickable chat GUI with `[Go]` buttons.  
- `/delhome <name>` — delete your named home.  
- `/homesadmin <player>` / `/phomes <player>` — admin GUI to view a player’s homes, see creation time and safety status, and teleport to homes (requires admin permission).  
- Safe-location checks (configurable list of forbidden blocks; checks the **feet block**).  
- Permission-based max-homes via `homesplusplus.homes.<N>` nodes — uses `player.hasPermission(...)` so it works with or without LuckPerms.  
- Optional reflective read of LuckPerms `maxhomes` meta (safe; only used if LuckPerms is present).  
- Saves homes to `homes.yml` with creation timestamp and safe flag.

---

## Requirements
- Java 17  
- Paper / Purpur / Spigot / Bukkit server (1.21.x recommended)  
- (Optional) LuckPerms for rank-based homes; plugin works without it

---

## Installation (server)
1. Build the plugin JAR (see next section) or download the JAR you built.  
2. Place `homesplusplus-<version>.jar` into your server `plugins/` folder.  
3. Start or restart the server. On first run the plugin will create its data folder and default `config.yml` / `messages.yml`.  
4. Edit `config.yml` and `messages.yml` in the plugin data folder (`plugins/HomesPlusPlus/`) to customize.

---

## Build from source (Maven & IntelliJ)

### Build with Maven (terminal)
```bash
mvn -U clean package
