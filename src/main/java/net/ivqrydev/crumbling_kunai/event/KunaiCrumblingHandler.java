package net.ivqrydev.crumbling_kunai.event;

import net.ivqrydev.crumbling_kunai.config.ModConfig;
import net.ivqrydev.crumbling_kunai.effect.ModEffects;
import net.ivqrydev.crumbling_kunai.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = "crumbling_kunai")
public class KunaiCrumblingHandler {

    //Obtain the thrown kunai.
    private static final ResourceLocation KUNAI_ENTITY_TYPE =
            ResourceLocation.fromNamespaceAndPath("caverns_and_chasms", "kunai");

    //Start tracking UUID of antagonistic players.
    private static final Map<UUID, UUID> KUNAI_SHOOTERS = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        DamageSource source = event.getSource();

        if (!(source.getDirectEntity() instanceof Projectile projectile)) return;

        ResourceLocation entityTypeId = projectile.getType().builtInRegistryHolder().key().location();
        if (!entityTypeId.equals(KUNAI_ENTITY_TYPE)) return;

        LivingEntity target = event.getEntity();
        if (target.level().isClientSide()) return;

        //Apply crumbling and fetch config options.
        target.addEffect(new MobEffectInstance(
                ModEffects.CRUMBLING_EFFECT,
                ModConfig.CRUMBLING_DURATION.get() * 20, //Multiply by 20 to convert to ticks.
                0,
                true,
                ModConfig.CRUMBLING_PARTICLES.get()
        ));

        if (projectile.getOwner() instanceof Player shooter) {
            KUNAI_SHOOTERS.put(target.getUUID(), shooter.getUUID());
        } else {
            KUNAI_SHOOTERS.remove(target.getUUID());
        }
    }

    @SubscribeEvent
    public static void onEffectExpire(MobEffectEvent.Expired event) {
        if (!ModConfig.REFRESH_SOUND.get()) return;
        if (!event.getEffectInstance().is(ModEffects.CRUMBLING_EFFECT)) return;

        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;

        //Fetch config options and play refresh sound effect at the player's position.
        UUID shooterId = KUNAI_SHOOTERS.remove(entity.getUUID());
        Player shooter = shooterId != null ? entity.level().getPlayerByUUID(shooterId) : null;

        //If the player is null for whatever reason, like if they briefly log out, fall back onto the enemy's position.
        BlockPos soundPos = (shooter != null) ? shooter.blockPosition() : entity.blockPosition();
        SoundSource soundCategory = (shooter != null) ? SoundSource.PLAYERS : entity.getSoundSource();

        entity.level().playSound(
                null,
                soundPos,
                ModSounds.REFRESH.value(),
                soundCategory,
                1.0f,
                1.0f
        );
    }
}