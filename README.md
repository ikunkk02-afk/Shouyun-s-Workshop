# Shouyun's Workshop / 寿云工坊

Shouyun's Workshop is a Fabric content mod built from community and fan suggestions. The project is intended to grow over time with new items, enchantments, blocks, and focused gameplay features while keeping each feature independently maintainable.

寿云工坊是一个由社区与粉丝建议驱动的 Fabric 内容扩展模组。项目会持续加入物品、附魔、方块和小型玩法，并保持模块化结构，方便长期维护。

## Requirements / 运行环境

- Minecraft 1.21.1
- Fabric Loader 0.19.3 or newer
- Fabric API 0.116.15+1.21.1
- Java 21

## Installation / 安装

1. Install Fabric Loader for Minecraft 1.21.1.
2. Place Fabric API and the Shouyun's Workshop JAR in the Minecraft `mods` directory.
3. Start the game with the Fabric profile.

安装 Minecraft 1.21.1 对应的 Fabric Loader，将 Fabric API 与寿云工坊 JAR 放入游戏的 `mods` 目录，然后使用 Fabric 配置启动游戏。

## Current features / 当前功能

### Glass Hammer / 玻璃重锤

- A cheap, high-impact weapon crafted from six glass blocks and a stick.
- Breaks immediately after a successful hit.
- Its shattered head damages nearby creatures and deals a small amount of damage to the wielder.
- Uses glass sound and particle effects without explosions or terrain damage.

廉价而高爆发的一次性武器。有效命中后锤头立即破碎，以玻璃碎片伤害附近生物并使使用者受到少量自伤，不会破坏地形。

### Netherite Hammer / 下界合金重锤

- A durable, high-damage weapon with 2031 durability.
- Releases a damaging shockwave on hit.
- Pushes nearby targets outward and upward while excluding the wielder.
- Fireproof as an item and repairable with netherite ingots.

耐用的高伤害重型武器。命中时释放冲击波，对周围目标造成伤害并按各自方位向外、向上击飞；使用者不受自身冲击波影响。

### Whirlwind Slash / 旋风斩

- **Level I:** Right-click a sword to strike valid targets within a three-block radius.
- **Level II:** Expands the radius to four blocks and launches a short-lived, colliding sword-qi projectile.
- **Level III:** Expands the radius to five blocks, strengthens sword qi, and unlocks wind flight after standing still for about two seconds. Flight follows WASD and facing direction, does not provide downward control, and consumes one hunger point every seven seconds.

- **I 级：** 手持剑右键，对约 3 格内的有效目标发动范围斩击。
- **II 级：** 范围扩大至约 4 格，并释放具有真实飞行与碰撞过程的剑气。
- **III 级：** 范围扩大至约 5 格、强化剑气；原地站立约 2 秒后进入御风状态。御风按朝向响应 WASD，不提供主动下降，并且每 7 秒消耗 1 点饥饿值（半格饥饿图标）。

Combat effects are resolved by the server and respect normal PvP, team, pet ownership, damage, and knockback rules where applicable.

## Building / 构建

```text
gradlew.bat runDatagen
gradlew.bat runGameTestServer
gradlew.bat clean build
```

The release JAR is written to `build/libs/shouyun-workshop-1.0.0.jar`.

## License

This project is licensed under the [MIT License](LICENSE).
