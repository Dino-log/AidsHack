package cc.aidshack.mixins;

import cc.aidshack.command.CommandManager;
import cc.aidshack.event.events.EventReceivePacket;
import cc.aidshack.event.events.EventSendPacket;
import cc.aidshack.module.ModuleManager;
import cc.aidshack.module.impl.combat.MarlowCrystalOptimizer;
import cc.aidshack.utils.math.Wrapper;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(method = "send(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V", at = @At("HEAD"), cancellable = true)
    public void send(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> callback, CallbackInfo ci) {
        if(packet instanceof ChatMessageC2SPacket && ((ChatMessageC2SPacket) packet).getChatMessage().startsWith(CommandManager.get().getPrefix())) {
            try {
                CommandManager.get().dispatch(((ChatMessageC2SPacket) packet).getChatMessage().substring(CommandManager.get().getPrefix().length()));
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
                Wrapper.tellPlayer(e.getMessage());
            }
            ci.cancel();
        }
        EventSendPacket event = new EventSendPacket(packet);
        event.call();
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    public void receive(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        EventReceivePacket event = new EventReceivePacket(packet);
        event.call();
        if(event.isCancelled()) ci.cancel();
        if (packet instanceof GameJoinS2CPacket) {
//    		try {
//				api.setOnline(mc.getSession().getUsername());
//			} catch (IOException | InterruptedException e) {
//				e.printStackTrace();
//			}
        }
        if (packet instanceof DisconnectS2CPacket) {
//    		try {
//				api.remOnline(mc.getSession().getUsername());
//			} catch (IOException | InterruptedException e) {
//				e.printStackTrace();
//			}
        }
    }

    @Inject(method = {"send(Lnet/minecraft/network/Packet;)V"}, at = {@At("HEAD")})
    private void onPacketSend(Packet<?> packet, CallbackInfo info) {
        if (!ModuleManager.INSTANCE.getModule(MarlowCrystalOptimizer.class).isEnabled())
        {return;}



            final MinecraftClient mc = MinecraftClient.getInstance();
            if (packet instanceof PlayerInteractEntityC2SPacket) {
                PlayerInteractEntityC2SPacket interactPacket = (PlayerInteractEntityC2SPacket)packet;
                interactPacket.handle(new PlayerInteractEntityC2SPacket.Handler() {
                    public void interact(Hand hand) {}

                    public void interactAt(Hand hand, Vec3d pos) {}

                    public void attack() {
                        HitResult hitResult = mc.crosshairTarget;
                        if (hitResult == null)
                            return;
                        if (hitResult.getType() == HitResult.Type.ENTITY) {
                            EntityHitResult entityHitResult = (EntityHitResult)hitResult;
                            Entity entity = entityHitResult.getEntity();
                            if (entity instanceof EndCrystalEntity) {
                                StatusEffectInstance weakness = mc.player.getStatusEffect(StatusEffects.WEAKNESS);
                                StatusEffectInstance strength = mc.player.getStatusEffect(StatusEffects.STRENGTH);
                                if (weakness != null && (strength == null || strength.getAmplifier() <= weakness.getAmplifier()))
                                    if (!ClientConnectionMixin.this.isTool(mc.player.getMainHandStack()))
                                        return;
                                entity.kill();
                                entity.setRemoved(Entity.RemovalReason.KILLED);
                                entity.onRemoved();
                            }
                        }
                    }
                });
            }
        }

    private boolean isTool(ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof ToolItem) || itemStack.getItem() instanceof net.minecraft.item.HoeItem)
            return false;
        ToolMaterial material = ((ToolItem)itemStack.getItem()).getMaterial();
        return (material == ToolMaterials.DIAMOND || material == ToolMaterials.NETHERITE);
    }
}
