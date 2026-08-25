package com.shouyun.workshop.item;

import com.shouyun.workshop.ShouyunWorkshop;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;

public abstract class HammerItem extends Item {
	protected HammerItem(Settings settings) {
		super(settings);
	}

	public static AttributeModifiersComponent createAttributeModifiers(double totalDamage, double attacksPerSecond) {
		return AttributeModifiersComponent.builder()
				.add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
						new EntityAttributeModifier(Item.BASE_ATTACK_DAMAGE_MODIFIER_ID, totalDamage - 1.0,
								EntityAttributeModifier.Operation.ADD_VALUE),
						AttributeModifierSlot.MAINHAND)
				.add(EntityAttributes.GENERIC_ATTACK_SPEED,
						new EntityAttributeModifier(Item.BASE_ATTACK_SPEED_MODIFIER_ID, attacksPerSecond - 4.0,
								EntityAttributeModifier.Operation.ADD_VALUE),
						AttributeModifierSlot.MAINHAND)
				.build();
	}
}
