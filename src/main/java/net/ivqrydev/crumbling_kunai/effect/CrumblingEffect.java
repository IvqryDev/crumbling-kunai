package net.ivqrydev.crumbling_kunai.effect;

import net.ivqrydev.crumbling_kunai.config.ModConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class CrumblingEffect extends MobEffect {

    private static final ResourceLocation MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath("crumbling_kunai", "effect.crumbling.armor");

    public CrumblingEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void addAttributeModifiers(AttributeMap attributeMap, int amplifier) {
        AttributeInstance instance = attributeMap.getInstance(Attributes.ARMOR);
        if (instance != null) {
            instance.removeModifier(MODIFIER_ID);
            instance.addOrReplacePermanentModifier(new AttributeModifier(
                    MODIFIER_ID,
                    -ModConfig.ARMOR_REDUCTION.get(),
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            ));
        }
    }

    @Override
    public void removeAttributeModifiers(AttributeMap attributeMap) {
        AttributeInstance instance = attributeMap.getInstance(Attributes.ARMOR);
        if (instance != null) {
            instance.removeModifier(MODIFIER_ID);
        }
    }
}