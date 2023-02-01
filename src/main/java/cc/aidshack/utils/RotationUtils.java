package cc.aidshack.utils;

import cc.aidshack.core.Pool;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

import static cc.aidshack.AidsHack.MC;
import static cc.aidshack.AidsHack.mc;

public enum RotationUtils {
        ;

    private static final Pool<Rotation> rotationPool = new Pool<>(Rotation::new);

    private static final List<Rotation> rotations = new ArrayList<>();

    public static float serverYaw;

    public static float serverPitch;
    public static boolean isCustomPitch = false;
    public static boolean isCustomYaw = false;

    public static void setSilentPitch(float pitch) {
        RotationUtils.serverPitch = pitch;
        isCustomPitch = true;
    }

    public static void setSilentYaw(float yaw) {
        RotationUtils.serverYaw = yaw;
        isCustomYaw = true;
    }

    public static void setSilentRotations(LivingEntity target, float yaw, float pitch) {
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround((float) RotationUtils.getRotations(target)[0], (float) RotationUtils.getRotations(target)[1], mc.player.isOnGround()));
        RotationUtils.serverPitch = pitch;
        isCustomPitch = true;
        RotationUtils.serverYaw = yaw;
        isCustomYaw = true;
    }
    private static class Rotation {
        public double yaw, pitch;
        public int priority;
        @SuppressWarnings("unused")
        public boolean clientSide;
        public Runnable callback;

        public void set(double yaw, double pitch, int priority, boolean clientSide, Runnable callback) {
            this.yaw = yaw;
            this.pitch = pitch;
            this.priority = priority;
            this.clientSide = clientSide;
            this.callback = callback;
        }

        @SuppressWarnings("unused")
        public void sendPacket() {
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround((float) yaw, (float) pitch, mc.player.isOnGround()));
            runCallback();
        }

        public void runCallback() {
            if (callback != null) callback.run();
        }
    }
    public static void rotate(double yaw, double pitch, int priority, boolean clientSide, Runnable callback) {
        Rotation rotation = rotationPool.get();
        rotation.set(yaw, pitch, priority, clientSide, callback);

        int i = 0;
        for (; i < rotations.size(); i++) {
            if (priority > rotations.get(i).priority) break;
        }

        rotations.add(i, rotation);
    }
    public static void rotate(double yaw, double pitch, int priority, Runnable callback) {
        rotate(yaw, pitch, priority, false, callback);
    }

    public static void rotate(double yaw, double pitch, Runnable callback) {
        rotate(yaw, pitch, 0, callback);
    }

    public static void rotate(double yaw, double pitch) {
        rotate(yaw, pitch, 0, null);
    }

    public static void setSilentRotations(float yaw, float pitch) {
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, mc.player.isOnGround()));
        RotationUtils.serverPitch = pitch;
        isCustomPitch = true;
        RotationUtils.serverYaw = yaw;
        isCustomYaw = true;
    }

    public static float[] getRotationFromPosition(double x, double z, double y) {
        double xDiff = x - mc.player.getX();
        double zDiff = z - mc.player.getZ();
        double yDiff = y - mc.player.getY() - 1.2;

        double dist = MathHelper.sqrt((float) (xDiff * xDiff + zDiff * zDiff));
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / 3.141592653589793D) - 90.0F;
        float pitch = (float) -(Math.atan2(yDiff, dist) * 180.0D / 3.141592653589793D);
        return new float[]{yaw, pitch};
    }

    public static float[] getRotations(LivingEntity ent) {
        double x = ent.getX();
        double z = ent.getZ();
        double y = ent.getY() + ent.getStandingEyeHeight() / 2.0F;
        return getRotationFromPosition(x, z, y);
    }

    public static float[] getRotations(Entity ent) {
        double x = ent.getX();
        double z = ent.getZ();
        double y = ent.getY() + ent.getStandingEyeHeight() / 2.0F;
        return getRotationFromPosition(x, z, y);
    }

        public static Vec3d getEyesPos() {
            return getEyesPos(MC.player);
        }

        public static Vec3d getEyesPos(PlayerEntity player) {
//		return new Vec3d(player.getX(),
//				player.getY() + player.getEyeHeight(player.getPose()),
//				player.getZ());
            return RenderUtils.getCameraPos();
        }
    public static double getYaw(Vec3d pos) {
        return mc.player.getYaw() + MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(pos.getZ() - mc.player.getZ(), pos.getX() - mc.player.getX())) - 90f - mc.player.getYaw());
    }

    public static double getPitch(Vec3d pos) {
        double diffX = pos.getX() - mc.player.getX();
        double diffY = pos.getY() - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffZ = pos.getZ() - mc.player.getZ();

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        return mc.player.getPitch() + MathHelper.wrapDegrees((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)) - mc.player.getPitch());
    }

    public static double getYaw(BlockPos pos) {
        return mc.player.getYaw() + MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(pos.getZ() + 0.5 - mc.player.getZ(), pos.getX() + 0.5 - mc.player.getX())) - 90f - mc.player.getYaw());
    }

    public static double getPitch(BlockPos pos) {
        double diffX = pos.getX() + 0.5 - mc.player.getX();
        double diffY = pos.getY() + 0.5 - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffZ = pos.getZ() + 0.5 - mc.player.getZ();

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        return mc.player.getPitch() + MathHelper.wrapDegrees((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)) - mc.player.getPitch());
    }

        public static BlockPos getEyesBlockPos() {
            return new BlockPos(getEyesPos());
        }

        public static Vec3d getPlayerLookVec(PlayerEntity player) {
            float f = 0.017453292F;
            float pi = (float) Math.PI;

            float f1 = MathHelper.cos(-player.getYaw() * f - pi);
            float f2 = MathHelper.sin(-player.getYaw() * f - pi);
            float f3 = -MathHelper.cos(-player.getPitch() * f);
            float f4 = MathHelper.sin(-player.getPitch() * f);

            return new Vec3d(f2 * f3, f4, f1 * f3).normalize();
        }


        public static void resetPitch() {
            isCustomPitch = false;
        }

        public static void resetYaw() {
          isCustomYaw = false;
        }


        public static Vec3d getClientLookVec() {
            return getPlayerLookVec(MC.player);
        }



}
