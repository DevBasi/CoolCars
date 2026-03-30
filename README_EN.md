<div align="center">
  <img src="https://mintcdn.com/coolcars/Nf4BiqjFcs6SEFCk/images/coolcars/logo-full.png?w=1100&fit=max&auto=format&n=Nf4BiqjFcs6SEFCk&q=85&s=519a7b4c36beba6e8c42eca5b5685fd6" width="800" alt="CoolCars Logo">
  <h1>CoolCars — Vehicle System for Minecraft</h1>
  
  <p align="center">
    <b>Advanced physics, modular customization, and high performance.</b>
  </p>

  [![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21+-62B06F?style=for-the-badge&logo=minecraft&logoColor=white)](https://www.minecraft.net/)
  [![Java Version](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
  [![Wiki](https://img.shields.io/badge/Wiki-Documentation-95A5A6?style=for-the-badge&logo=gitbook&logoColor=white)](https://coolcars.mintlify.app/ru/README)

  <br />

  [🇺🇸 **English**] | [🇷🇺 **Русский**](README.md)
</div>

---

## ⚙️ System Requirements

* **Server Version:** Minecraft **1.21** or higher (Paper, Purpur).
* **Runtime Environment:** **Java 21** or higher.
* **Dependencies:** [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/) (Required).

---

## 🚀 Core Features

### 🏎 Physics & Movement
* **Inertia Simulation:** Calculated acceleration, braking, and coasting mechanics.
* **Terrain Handling:** Smooth movement over slabs, stairs, and slopes.
* **Collision System:** Intelligent block and entity collision detection.
* **Suspension:** Visual chassis tilt animation during maneuvers.

### 🎨 Customization (YAML)
* **3D Model Support:** Easy `CustomModelData` integration from your resource pack.
* **Technical Tuning:** Individual max speed, acceleration, and fuel capacity settings for each model.
* **Sound Engine:** Support for custom engine start, driving, and signal sounds.

### 🎮 Gameplay Mechanics
* **Fuel System:** Configurable consumption and refueling system.
* **Access Control:** Key Item system to lock/unlock vehicles and prevent theft.
* **Storage:** Integrated trunk (inventory) for every vehicle.
* **Dynamic HUD:** Real-time speedometer and fuel level display in the Action Bar.

---

## ⌨️ Commands & Permissions

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/car spawn <model>` | Spawn a vehicle of the selected model | `coolcars.admin.spawn` |
| `/car givekey <player> <model>` | Give a vehicle key to a player | `coolcars.admin.givekey` |
| `/car list` | View all available vehicle models | `coolcars.player.list` |
| `/car reload` | Reload configuration files | `coolcars.admin.reload` |

---

<div align="center">
  <sub>Developed by <b>BasiDev</b> for modern Minecraft networks.</sub>
</div>
