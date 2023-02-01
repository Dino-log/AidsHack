package cc.aidshack.module.impl.combat;

import cc.aidshack.event.EventTarget;
import cc.aidshack.event.events.*;
import cc.aidshack.module.Module;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import static cc.aidshack.AidsHack.MC;

public class AutoPearlPhase extends Module
{

    public AutoPearlPhase()
    {
        super("AutoPearlPhase", "allow you to phase through blocks using ender pearls", false, Category.COMBAT);
    }



    @Override
    public void onDisable()
    {

        MC.player.noClip = false;
    }

    @EventTarget
    public void onPlayerTickMovement(EventMove event)
    {
        if (!collidingBlocks())
            return;
        ClientPlayerEntity player = MC.player;
        player.noClip = true;
    }

    @EventTarget
    public void onSetOpaqueCube(EventMarkChunkClosed event)
    {
        event.setCancelled(true);
    }

    @EventTarget
    public void onUpdate(EventUpdate event)
    {
        if (!collidingBlocks())
            return;

        ClientPlayerEntity player = MC.player;

        player.noClip = true;
        player.fallDistance = 0;
        player.setOnGround(true);

        player.getAbilities().flying = false;
        player.setVelocity(0, 0, 0);

        float speed = 0.02F;
        player.airStrafingSpeed = speed;

        if (MC.options.jumpKey.isPressed())
        {
            player.addVelocity(0, speed, 0);
        }
        if (MC.options.sneakKey.isPressed())
        {
            player.addVelocity(0, -speed, 0);
        }
    }

    @EventTarget
    public void onPlayerJump(EventPlayerJump event)
    {
        if (!collidingBlocks())
            return;
        event.setCancelled(true);
    }

    private boolean collidingBlocks()
    {
        ClientPlayerEntity player = MC.player;
        return
                wouldCollideAt(new BlockPos(player.getX() - (double)player.getWidth() * 0.35D, player.getY(), player.getZ() + (double)player.getWidth() * 0.35D)) ||
                        wouldCollideAt(new BlockPos(player.getX() - (double)player.getWidth() * 0.35D, player.getY(), player.getZ() - (double)player.getWidth() * 0.35D)) ||
                        wouldCollideAt(new BlockPos(player.getX() + (double)player.getWidth() * 0.35D, player.getY(),player.getZ() - (double)player.getWidth() * 0.35D)) ||
                        wouldCollideAt(new BlockPos(player.getX() + (double)player.getWidth() * 0.35D, player.getY(),player.getZ() + (double)player.getWidth() * 0.35D));
    }

    private boolean wouldCollideAt(BlockPos pos)
    {
        Box box = MC.player.getBoundingBox();
        Box box2 = (new Box(pos.getX(), box.minY, pos.getZ(), (double)pos.getX() + 1.0D, box.maxY, (double)pos.getZ() + 1.0D)).contract(1.0E-7D);
        return MC.world.canCollide(MC.player, box2);
    }
}
