# 亡灵法杖：功能与本地测试说明

新增木制、石制、铁制、金制、钻石、下界合金亡灵法杖。右键成功召唤一只成年友方僵尸，消耗 1 点耐久；失败无消耗，创造模式免耐久。

## 数值

| 材质 | 耐久 | 共享上限 | 冷却 | 寿命 | 装备与生命 |
|---|---:|---:|---:|---:|---|
| 木制 | 32 | 1 | 8 秒 | 60 秒 | 无护甲，10% 木剑，20 生命 |
| 石制 | 48 | 1 | 7 秒 | 60 秒 | 每槽 15% 皮革甲，25% 石剑，20 生命 |
| 铁制 | 96 | 2 | 6 秒 | 90 秒 | 每槽 45% 铁甲，60% 铁剑，24 生命 |
| 金制 | 48 | 2 | 5 秒 | 90 秒 | 每槽 55% 金甲，70% 金剑，22 生命，移速 +10% |
| 钻石 | 192 | 3 | 4 秒 | 120 秒 | 每槽 80% 钻石甲，必带钻石剑，30 生命 |
| 下界合金 | 256 | 4 | 4 秒 | 120 秒 | 每槽必有护甲，75% 下界合金/25% 钻石，必带下界合金剑，36 生命，基础击退抗性 20% |

护甲四槽独立抽取。生命值 20 点 = 10 颗心。装备无随机附魔。六种法杖共享召唤总数和冷却；按本次使用材质检查上限，已有仆从不因换低级法杖而消失，达到上限阻止新增。

## 行为与资源

- 只在已加载区块内寻找玩家前方安全位置，找不到地面/空间时不生成。
- 归属以主人 UUID 和实体运行状态记录，命令标签为 `shouyun_workshop_summon`。
- 优先攻击主人 16 格内以主人为目标的敌对生物，再选择最近可见敌人；命中前重验目标，排除玩家、召唤物、驯服动物和主人队友。
- 无目标时 4 格外立即跟随，约 2.5 格内停止；每 5 tick 重算路径，24 格外尝试安全传送。
- 玩家主动攻击 32 格内的生物时，名下所有召唤僵尸获得 10 秒高优先级集火指令；可集火普通动物，但仍排除玩家、驯服动物、同队单位和友方召唤物。
- 不日晒燃烧、不溺尸转化、不增援、不拾取物品、不掉装备/战利品/经验；和平模式也可存在。
- 到期、主人死亡/离线/切维度时清理。实体不保存，区块卸载释放额度，重启和重新加载区块不恢复。
- 主人的重锤、剑气等使用 `CombatTargeting` 的范围伤害排除自己的召唤物和队友召唤物。
- 左上角状态栏显示共享数量、手持法杖上限、冷却以及各召唤物剩余时间；没有法杖、仆从或冷却时隐藏。服务端状态变化时更新，每秒校准，客户端倒计时。
- 前五档统一配方为 `材料 骨头 材料 / 空 腐肉 空 / 空 木棍 空`；下界合金使用升级模板、钻石法杖、下界合金锭锻造，保留原版组件继承。

六张 32×32 法杖 PNG、160×80 状态栏背景和两个 16×16 UI 图标均由内置生图工具分别生成，并分别调用生图编辑进行背景修正。输出仍含预览底色，因此使用资源导入脚本清理中性背景、保留前景覆盖并导出硬边 RGBA。没有使用手绘占位图。已检查小尺寸轮廓、材质区分及透明像素。

生图提示集：统一 Minecraft 原版像素风，斜向细长杖身，骨质/魂火冠部，有限调色板，无文字、无水印；木棕原始骨爪、灰石绿魂、银铁镶边、金色冠部、钻石青蓝核心、暗灰下界合金与青色魂火。UI 分别为石灰细边深色空面板、绿眼骨质僵尸头、金骨沙漏与青色沙粒。修正提示要求保留设计并移除棋盘格背景。

## 验证与本地使用

- 主端和客户端编译通过，数据生成通过；配方、解锁进度、中英文本及贴图已打入本地 JAR。
- 四项专属 GameTest 全部通过：耐久/共享冷却与上限、安全落点失败不消耗、友方过滤/精英装备/无掉落、到期清理。测试运行仅十余秒，没有运行原项目的大型全套测试。
- 原项目指定的 Fabric API `0.116.15+1.21.1` 本机未缓存，官方下载连接失败；本次通过命令行临时指定已缓存的 `0.116.17+1.21.1` 验证。没有修改 `gradle.properties`。本地 JAR 使用 0.116.17 编译，尚未在 0.116.15 实测。
- 未启动交互式客户端验收；请本地检查实际装备渲染、手持效果、寻路、不同 GUI 缩放、双人隔离，以及死亡/离线/切维度/卸载区块后的清理。
- 未提交、推送或上传。

本地测试包：`build/libs/shouyun-workshop-1.0.0.jar`。建议 Minecraft 1.21.1、Java 21、Fabric Loader 0.19.3 与 Fabric API 0.116.17；替换旧版模组文件，避免同时加载两份 Workshop。

仅运行新测试：

```powershell
.\gradlew.bat runGameTest -I scripts/necromancer-tests.init.gradle --offline '-Pfabric_api_version=0.116.17+1.21.1'
```

选择测试的脚本只改构建输出中的测试入口，不改源码元数据。之后正常 `remapJar` 会恢复完整测试入口并打包。

生图导入脚本 `scripts/import-necromancer-art.ps1` 读取一个 JSON 文件，将 `wooden/stone/iron/golden/diamond/netherite/panel/soul/cooldown` 映射到对应生成图的绝对路径；本次映射位于 `build/necromancer-art-inputs.json`。脚本用于 Windows PowerShell 7.6 的资源再导入，不是运行模组的依赖。

## 修改文件清单

见下列项目相对路径；数值集中在 `NecromancerTier`，归属和 AI 位于 `SummonedZombieEntity`，共享限制与清理位于 `NecromancerSummons`。

- `docs/necromancer-staff.md`
- `scripts/import-necromancer-art.ps1`
- `scripts/necromancer-tests.init.gradle`
- `src/client/java/com/shouyun/workshop/client/NecromancerHud.java`
- `src/client/java/com/shouyun/workshop/client/ShouyunWorkshopClient.java`
- `src/client/java/com/shouyun/workshop/datagen/ModChineseLanguageProvider.java`
- `src/client/java/com/shouyun/workshop/datagen/ModEnglishLanguageProvider.java`
- `src/client/java/com/shouyun/workshop/datagen/ModRecipeProvider.java`
- `src/client/java/com/shouyun/workshop/datagen/NecromancerTranslations.java`
- `src/main/generated/assets/shouyun_workshop/lang/en_us.json`
- `src/main/generated/assets/shouyun_workshop/lang/zh_cn.json`
- `src/main/generated/data/shouyun_workshop/advancement/recipes/combat/diamond_necromancer_staff.json`
- `src/main/generated/data/shouyun_workshop/advancement/recipes/combat/golden_necromancer_staff.json`
- `src/main/generated/data/shouyun_workshop/advancement/recipes/combat/iron_necromancer_staff.json`
- `src/main/generated/data/shouyun_workshop/advancement/recipes/combat/netherite_necromancer_staff.json`
- `src/main/generated/data/shouyun_workshop/advancement/recipes/combat/stone_necromancer_staff.json`
- `src/main/generated/data/shouyun_workshop/advancement/recipes/combat/wooden_necromancer_staff.json`
- `src/main/generated/data/shouyun_workshop/recipe/diamond_necromancer_staff.json`
- `src/main/generated/data/shouyun_workshop/recipe/golden_necromancer_staff.json`
- `src/main/generated/data/shouyun_workshop/recipe/iron_necromancer_staff.json`
- `src/main/generated/data/shouyun_workshop/recipe/netherite_necromancer_staff.json`
- `src/main/generated/data/shouyun_workshop/recipe/stone_necromancer_staff.json`
- `src/main/generated/data/shouyun_workshop/recipe/wooden_necromancer_staff.json`
- `src/main/java/com/shouyun/workshop/entity/ModEntities.java`
- `src/main/java/com/shouyun/workshop/entity/SummonedZombieEntity.java`
- `src/main/java/com/shouyun/workshop/gametest/NecromancerGameTests.java`
- `src/main/java/com/shouyun/workshop/item/ModItems.java`
- `src/main/java/com/shouyun/workshop/item/NecromancerStaffItem.java`
- `src/main/java/com/shouyun/workshop/item/NecromancerTier.java`
- `src/main/java/com/shouyun/workshop/network/ModNetworking.java`
- `src/main/java/com/shouyun/workshop/network/NecromancerStatePayload.java`
- `src/main/java/com/shouyun/workshop/ShouyunWorkshop.java`
- `src/main/java/com/shouyun/workshop/summon/NecromancerSummons.java`
- `src/main/java/com/shouyun/workshop/summon/SummonPlacement.java`
- `src/main/java/com/shouyun/workshop/util/CombatTargeting.java`
- `src/main/resources/assets/shouyun_workshop/models/item/diamond_necromancer_staff.json`
- `src/main/resources/assets/shouyun_workshop/models/item/golden_necromancer_staff.json`
- `src/main/resources/assets/shouyun_workshop/models/item/iron_necromancer_staff.json`
- `src/main/resources/assets/shouyun_workshop/models/item/netherite_necromancer_staff.json`
- `src/main/resources/assets/shouyun_workshop/models/item/stone_necromancer_staff.json`
- `src/main/resources/assets/shouyun_workshop/models/item/wooden_necromancer_staff.json`
- `src/main/resources/assets/shouyun_workshop/textures/gui/necromancer_cooldown.png`
- `src/main/resources/assets/shouyun_workshop/textures/gui/necromancer_panel.png`
- `src/main/resources/assets/shouyun_workshop/textures/gui/necromancer_soul.png`
- `src/main/resources/assets/shouyun_workshop/textures/item/diamond_necromancer_staff.png`
- `src/main/resources/assets/shouyun_workshop/textures/item/golden_necromancer_staff.png`
- `src/main/resources/assets/shouyun_workshop/textures/item/iron_necromancer_staff.png`
- `src/main/resources/assets/shouyun_workshop/textures/item/netherite_necromancer_staff.png`
- `src/main/resources/assets/shouyun_workshop/textures/item/stone_necromancer_staff.png`
- `src/main/resources/assets/shouyun_workshop/textures/item/wooden_necromancer_staff.png`
- `src/main/resources/fabric.mod.json`
