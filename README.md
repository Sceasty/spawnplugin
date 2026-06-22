# SpawnPlugin

Advanced spawn management plugin for Paper 1.21.4 (Java 21).

## Features

- `/spawn [world]` — Teleport to spawn with warmup and cooldown
- `/setspawn` — Set spawn at your current location
- `/delspawn` — Remove spawn for the current world
- `/spawnplugin reload` — Reload configuration
- Per-world spawn locations
- Teleport warmup with movement cancel
- Respawn at spawn on death
- First-join teleport (optional)
- Sound and particle effects
- Disabled-worlds list
- Permission-based bypasses for cooldown, warmup, and disabled worlds
- Fully customizable messages

## Commands

| Command | Description | Permission | Default |
|---|---|---|---|
| `/spawn [world]` | Teleport to spawn | `spawnplugin.spawn` | Everyone |
| `/setspawn` | Set spawn location | `spawnplugin.setspawn` | OP |
| `/delspawn` | Delete spawn location | `spawnplugin.setspawn` | OP |
| `/spawnplugin reload` | Reload config | `spawnplugin.admin` | OP |

## Permissions

| Permission | Description | Default |
|---|---|---|
| `spawnplugin.spawn` | Use /spawn | true |
| `spawnplugin.setspawn` | Set/delete spawn | op |
| `spawnplugin.admin` | Reload config | op |
| `spawnplugin.bypass.cooldown` | Bypass teleport cooldown | op |
| `spawnplugin.bypass.warmup` | Instant teleport | op |
| `spawnplugin.bypass.disabled-world` | Bypass disabled world restriction | op |

## Configuration

```yaml
settings:
  warmup: 3
  cooldown: 5
  teleport-on-first-join: false
  teleport-on-death: true
  sound-enabled: true
  particles-enabled: true
  cancel-on-move: true
  disabled-worlds: []
```

All messages are customizable under `messages:` using `&` color codes.

## Building

```bash
mvn clean package
```

JAR will be in `target/SpawnPlugin-1.0.0.jar`.

## Requirements

- Paper 1.21.4
- Java 21

## License

MIT
