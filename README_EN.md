<div align="center">

<img src="https://mintcdn.com/coolcars/Nf4BiqjFcs6SEFCk/images/coolcars/logo-full.png?w=1100&fit=max&auto=format&n=Nf4BiqjFcs6SEFCk&q=85&s=519a7b4c36beba6e8c42eca5b5685fd6" width="720" alt="CoolCars">

# CoolCars

**An advanced vehicle system plugin for Minecraft**

Customizable physics · Modular customization · High performance

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21%2B-62B06F?style=flat-square&logo=minecraft&logoColor=white)](https://www.minecraft.net/)
[![Java](https://img.shields.io/badge/Java-21%2B-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![ProtocolLib](https://img.shields.io/badge/Requires-ProtocolLib-5865F2?style=flat-square)](https://www.spigotmc.org/resources/protocollib.1997/)
[![Wiki](https://img.shields.io/badge/Docs-Wiki-95A5A6?style=flat-square&logo=gitbook&logoColor=white)](https://coolcars.mintlify.app/ru/README)

[🇺🇸 **English**] · [🇷🇺 **Русский**](README.md)

</div>

---

## Features

| | |
|---|---|
| 🏎 **Physics** | Inertia, acceleration, braking, and coasting |
| 🌄 **Terrain** | Smooth movement over slabs, stairs, and slopes |
| 💥 **Collisions** | Intelligent block and entity collision detection |
| 🎨 **Customization** | YAML configs, CustomModelData, custom sounds |
| ⛽ **Fuel System** | Configurable consumption and refueling |
| 🔑 **Key Items** | Physical key system — lock and prevent theft |
| 🎒 **Trunk** | Built-in inventory in every vehicle |
| 📊 **HUD** | Real-time speedometer and fuel level in Action Bar |

---

## Requirements

- **Server:** Paper or Purpur **1.21+**
- **Java:** **21** or higher
- **Dependency:** [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/) — required

---

## Installation

```bash
# 1. Install ProtocolLib (if not already installed)
# 2. Copy the plugin into your server folder
cp CoolCars-1.0.0.jar /your-server/plugins/

# 3. Restart the server
```

4. Make sure your server resource pack is active — 3D models won't render without it.

---

## Commands

| Command | Description | Permission |
|:---|:---|:---|
| `/car spawn <model>` | Spawn a vehicle | `coolcars.admin.spawn` |
| `/car givekey <player> <model>` | Give a vehicle key to a player | `coolcars.admin.givekey` |
| `/car list` | View all available models | `coolcars.player.list` |
| `/car reload` | Reload configuration files | `coolcars.admin.reload` |

---

## Details

<details>
<summary><b>Physics & Movement</b></summary>

- **Inertia simulation** — calculated acceleration, braking, and coasting mechanics
- **Terrain handling** — smooth movement over slabs, stairs, and slopes
- **Collision system** — intelligent block and entity collision detection
- **Suspension** — visual chassis tilt animation during maneuvers

</details>

<details>
<summary><b>Customization (YAML)</b></summary>

- **3D models** — easy `CustomModelData` integration from your resource pack
- **Technical tuning** — individual max speed, acceleration, and fuel capacity per model
- **Sound engine** — custom engine start, driving, and signal sounds

</details>

<details>
<summary><b>Gameplay Mechanics</b></summary>

- **Fuel system** — configurable consumption and refueling
- **Key items** — physical key system to lock/unlock vehicles and prevent theft
- **Trunk** — integrated inventory for every vehicle
- **Dynamic HUD** — real-time speedometer and fuel level in the Action Bar

</details>

---

## Bug Reports

Found an issue? Reach out via Discord DM: **devbasi**

---

<div align="center">
  <sub>Developed by <b>PenguinTeam & BasiDev</b> for modern Minecraft networks</sub>
</div>
