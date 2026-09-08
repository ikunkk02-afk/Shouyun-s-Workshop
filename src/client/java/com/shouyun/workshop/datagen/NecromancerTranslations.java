package com.shouyun.workshop.datagen;

import com.shouyun.workshop.item.ModItems;
import com.shouyun.workshop.item.NecromancerTier;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider.TranslationBuilder;

final class NecromancerTranslations {
	static void add(TranslationBuilder builder, boolean chinese) {
		String[] en = {"Wooden", "Stone", "Iron", "Golden", "Diamond", "Netherite"};
		String[] zh = {"木制", "石制", "铁制", "金制", "钻石", "下界合金"};
		String[] effectsEn = {"Unarmored; rarely carries a wooden sword", "Occasional leather armor and stone sword", "Partial iron armor and iron sword", "Golden equipment; 10% faster movement", "Usually diamond armor; always a diamond sword", "Elite: netherite/diamond armor and netherite sword"};
		String[] effectsZh = {"无护甲，极少携带木剑", "低概率皮革甲与石剑", "概率装备铁甲与铁剑", "概率装备金甲与金剑，移速提高10%", "高概率钻石甲，必带钻石剑", "精英：下界合金/钻石护甲，必带下界合金剑"};
		for (var tier : NecromancerTier.values()) {
			int i = tier.ordinal();
			String key = tier.name().toLowerCase(java.util.Locale.ROOT);
			builder.add(ModItems.NECROMANCER_STAVES.get(tier), chinese ? zh[i] + "亡灵法杖" : en[i] + " Necromancer Staff");
			builder.add("tier.shouyun_workshop." + key, chinese ? zh[i] : en[i]);
			builder.add("tooltip.shouyun_workshop.staff." + key, chinese ? effectsZh[i] : effectsEn[i]);
		}
		put(builder, chinese, "entity.shouyun_workshop.summoned_zombie", "召唤僵尸", "Summoned Zombie");
		put(builder, chinese, "tooltip.shouyun_workshop.staff", "共享上限：%s 只 | 冷却：%s 秒 | 持续：%s 秒", "Shared limit: %s | Cooldown: %ss | Duration: %ss");
		put(builder, chinese, "message.shouyun_workshop.staff.cooldown", "亡灵法杖正在冷却", "Your necromancer staves are cooling down");
		put(builder, chinese, "message.shouyun_workshop.staff.limit", "已达到这根法杖的共享召唤上限", "This staff's shared summon limit has been reached");
		put(builder, chinese, "message.shouyun_workshop.staff.blocked", "附近没有安全的召唤位置", "No safe summoning position nearby");
		put(builder, chinese, "hud.shouyun_workshop.staff.count", "亡灵仆从：%s", "Undead servants: %s");
		put(builder, chinese, "hud.shouyun_workshop.staff.limit", "亡灵仆从：%s / %s", "Undead servants: %s / %s");
		put(builder, chinese, "hud.shouyun_workshop.staff.ready", "法杖已就绪", "Staff ready");
		put(builder, chinese, "hud.shouyun_workshop.staff.cooldown", "冷却：%s 秒", "Cooldown: %ss");
		put(builder, chinese, "hud.shouyun_workshop.staff.summon", "%s · 剩余 %s 秒", "%s · %ss remaining");
	}
	private static void put(TranslationBuilder builder, boolean chinese, String key, String zh, String en) { builder.add(key, chinese ? zh : en); }
	private NecromancerTranslations() {}
}
