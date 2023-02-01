package cc.aidshack.module.impl.combat;

import cc.aidshack.module.Module;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

import static cc.aidshack.AidsHack.disabledByCurrentServer;

public class CrystalPingBypass extends Module {
    public CrystalPingBypass() {
        super("CrystalPingBypass", "", false, Category.COMBAT);
    }


    public  boolean explodesClientSide(EndCrystalEntity crystal, DamageSource source, float amount) {
        if (!isEnabled() || disabledByCurrentServer || !crystal.world.isClient() || crystal.isRemoved() ||
                crystal.isInvulnerableTo(source) || source.getAttacker() instanceof EnderDragonEntity || amount <= 0) return false;

        // Hrukjang Studios Moment.
        if (source.getAttacker() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) source.getAttacker();
            if (player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) <= 0) return false;
            AttributeContainer map = player.getAttributes();
            for (StatusEffectInstance instance : player.getStatusEffects()) {
                instance.getEffectType().onApplied(player, map, instance.getAmplifier());
            }
            amount = Math.min(amount, (float) player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
            for (StatusEffectInstance instance : player.getStatusEffects()) {
                instance.getEffectType().onRemoved(player, map, instance.getAmplifier());
            }
            return amount > 0;
        }

        return true;
    }
}
