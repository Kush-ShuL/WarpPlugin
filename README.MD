# WarpPlugin

A simple and powerful warp/teleport management plugin for Minecraft servers.

## Features

- Set warps at your current location
- Teleport to any saved warp instantly
- Manage warps with administrative commands
- Automatic Tab completion for warp names
- YAML-based data storage
- Flexible permission system

## Commands

### Player Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/setwarp <name>` | `warpplugin.set` | Create a new warp at your location |
| `/warp <name>` | `warpplugin.warp.<name>` or `warpplugin.warpall` | Teleport to a warp |
| `/delwarp <name>` | `warpplugin.set` | Delete a warp |

### Admin Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/warpadmin list` | `warpplugin.warpadmin` | List all warps with details |
| `/warpadmin del <name>` | `warpadmin` | Delete a warp |
| `/warpadmin setowner <name> <player>` | `warpadmin` | Change warp owner |
| `/warpadmin reload` | `warpadmin` | Reload configuration |

## Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `warpplugin.set` | `true` | Create/delete warps |
| `warpplugin.warpall` | `op` | Teleport to any warp |
| `warpplugin.warp.<name>` | `true` | Teleport to specific warp |
| `warpplugin.warpadmin` | `op` | Admin commands access |

## Installation

1. Download the latest `.jar` file from [Releases]
2. Place it in your server's `plugins` folder
3. Restart or reload your server
4. Done!

## Configuration

Warp data is automatically saved to `plugins/WarpPlugin/data.yml`

```yaml
warps:
  'spawn':
    creator: Admin
    world: world
    public: true
    x: 0.0
    y: 64.0
    z: 0.0
    yaw: 0.0
    pitch: 0.0
  'home':
    creator: Player123
    world: world_nether
    public: false
    x: 100.5
    y: 50.0
    z: -200.3
    yaw: 90.0
    pitch: 0.0
```

## Support

For issues and feature requests, please open an issue on GitHub.

## License

This project is licensed under the LGPL License.