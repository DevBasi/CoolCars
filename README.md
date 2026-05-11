<div align="center">

<img src="https://mintcdn.com/coolcars/Nf4BiqjFcs6SEFCk/images/coolcars/logo-full.png?w=1100&fit=max&auto=format&n=Nf4BiqjFcs6SEFCk&q=85&s=519a7b4c36beba6e8c42eca5b5685fd6" width="720" alt="CoolCars">

# CoolCars

**Плагин на продвинутые системы транспорта для Minecraft**

Настраиваемая физика · Модульная кастомизация · Высокая производительность

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21%2B-62B06F?style=flat-square&logo=minecraft&logoColor=white)](https://www.minecraft.net/)
[![Java](https://img.shields.io/badge/Java-21%2B-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![ProtocolLib](https://img.shields.io/badge/Requires-ProtocolLib-5865F2?style=flat-square)](https://www.spigotmc.org/resources/protocollib.1997/)
[![Wiki](https://img.shields.io/badge/Docs-Wiki-95A5A6?style=flat-square&logo=gitbook&logoColor=white)](https://coolcars.mintlify.app/ru/README)

[🇷🇺 **Русский**] · [🇺🇸 **English**](README_EN.md)

</div>

---

## Возможности

| | |
|---|---|
| 🏎 **Физика** | Инерция, разгон, торможение, накат |
| 🌄 **Рельеф** | Плавное движение по слябам, ступеням и склонам |
| 💥 **Коллизии** | Детекция столкновений с блоками и сущностями |
| 🎨 **Кастомизация** | YAML-конфиги, CustomModelData, кастомные звуки |
| ⛽ **Топливо** | Настраиваемый расход и заправка |
| 🔑 **Ключи** | Физические key-предметы — защита от угона |
| 🎒 **Багажник** | Встроенный инвентарь в каждом автомобиле |
| 📊 **HUD** | Спидометр и уровень топлива в Action Bar |

---

## Системные требования

- **Ядро:** Paper или Purpur **1.21+**
- **Java:** **21** или выше
- **Зависимость:** [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/) — обязательно

---

## Установка

```bash
# 1. Установите ProtocolLib (если ещё нет)
# 2. Скопируйте плагин в папку сервера
cp CoolCars-1.0.0.jar /your-server/plugins/

# 3. Перезапустите сервер
```

4. Убедитесь, что ресурспак активен — без него 3D-модели не отображаются.

---

## Команды

| Команда | Описание | Право |
|:---|:---|:---|
| `/car spawn <model>` | Призвать автомобиль | `coolcars.admin.spawn` |
| `/car givekey <player> <model>` | Выдать ключ игроку | `coolcars.admin.givekey` |
| `/car list` | Список доступных моделей | `coolcars.player.list` |
| `/car reload` | Перезагрузить конфиги | `coolcars.admin.reload` |

---

## Детали

<details>
<summary><b>Физика и движение</b></summary>

- **Симуляция инерции** — расчёт разгона, торможения и наката
- **Обработка поверхностей** — плавное движение по полублокам, ступеням и склонам
- **Столкновения** — система детекции коллизий с блоками и сущностями
- **Подвеска** — визуальная анимация наклона кузова при манёврах

</details>

<details>
<summary><b>Кастомизация (YAML)</b></summary>

- **3D-модели** — привязка `CustomModelData` из вашего ресурспака
- **Характеристики** — индивидуальные параметры скорости, ускорения и объёма бака
- **Звуки** — кастомные звуки двигателя, езды и сигналов

</details>

<details>
<summary><b>Игровые механики</b></summary>

- **Топливо** — настраиваемый расход и возможность заправки
- **Ключи** — система физических key-предметов для защиты от угона
- **Багажник** — встроенный инвентарь в каждом автомобиле
- **HUD** — спидометр и уровень топлива в Action Bar в реальном времени

</details>

---

## Баги и поддержка

Нашли проблему? Пишите в личные сообщения Discord: **devbasi**

---

<div align="center">
  <sub>Разработано <b>PenguinTeam & DevBasi</b> для современных Minecraft серверов</sub>
</div>
