package cc.aidshack.utils;

import cc.aidshack.event.events.EventMove;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import static cc.aidshack.AidsHack.MC;
import static cc.aidshack.AidsHack.mc;

public class PlayerUtils {


	public static boolean isMoving() {
		return mc.player.input.movementForward != 0 || mc.player.input.movementSideways != 0;
	}

	public static boolean isOnGround(double height) {
		if (mc.world.getBlockCollisions(mc.player, mc.player.getBoundingBox().offset(0.0D, -height, 0.0D)).iterator().hasNext()) {
			return true;
		} else {
			return false;
		}
	}
	public static void setTimerSpeed(float speed) {
		ReflectionHelper.setPrivateValue(RenderTickCounter.class, ReflectionHelper.getPrivateValue(MinecraftClient.class, mc, "renderTickCounter", "field_1728"), 1000.0F / (float) speed / 20, "tickTime", "field_1968");
	}

	public static void blinkToPos(Vec3d startPos, final BlockPos endPos, final double slack, final double[] pOffset) {
		double curX = startPos.x;
		double curY = startPos.y;
		double curZ = startPos.z;
		try {
			final double endX = endPos.getX() + 0.5;
			final double endY = endPos.getY() + 1.0;
			final double endZ = endPos.getZ() + 0.5;

			double distance = Math.abs(curX - endX) + Math.abs(curY - endY) + Math.abs(curZ - endZ);
			int count = 0;
			while (distance > slack) {
				distance = Math.abs(curX - endX) + Math.abs(curY - endY) + Math.abs(curZ - endZ);
				if (count > 120) {
					break;
				}
				final double diffX = curX - endX;
				final double diffY = curY - endY;
				final double diffZ = curZ - endZ;
				final double offset = ((count & 0x1) == 0x0) ? pOffset[0] : pOffset[1];
				if (diffX < 0.0) {
					if (Math.abs(diffX) > offset) {
						curX += offset;
					} else {
						curX += Math.abs(diffX);
					}
				}
				if (diffX > 0.0) {
					if (Math.abs(diffX) > offset) {
						curX -= offset;
					} else {
						curX -= Math.abs(diffX);
					}
				}
				if (diffY < 0.0) {
					if (Math.abs(diffY) > 0.25) {
						curY += 0.25;
					} else {
						curY += Math.abs(diffY);
					}
				}
				if (diffY > 0.0) {
					if (Math.abs(diffY) > 0.25) {
						curY -= 0.25;
					} else {
						curY -= Math.abs(diffY);
					}
				}
				if (diffZ < 0.0) {
					if (Math.abs(diffZ) > offset) {
						curZ += offset;
					} else {
						curZ += Math.abs(diffZ);
					}
				}
				if (diffZ > 0.0) {
					if (Math.abs(diffZ) > offset) {
						curZ -= offset;
					} else {
						curZ -= Math.abs(diffZ);
					}
				}
				mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(curX, curY, curZ, true));
				++count;
			}
		} catch (Exception e) {

		}
	}

	public static void teleport(BlockPos endPos){
		double dist = Math.sqrt(mc.player.squaredDistanceTo(endPos.getX(), endPos.getY(), endPos.getZ()));
		double packetDist = 5;
		double xtp, ytp, ztp = 0;

		if(dist> packetDist){
			double nbPackets = Math.round(dist / packetDist + 0.49999999999) - 1;
			xtp = mc.player.getX();
			ytp = mc.player.getY();
			ztp = mc.player.getZ();
			for (int i = 1; i < nbPackets;i++){
				double xdi = (endPos.getX() - mc.player.getX())/( nbPackets);
				xtp += xdi;

				double zdi = (endPos.getZ() - mc.player.getZ())/( nbPackets);
				ztp += zdi;

				double ydi = (endPos.getY() - mc.player.getY())/( nbPackets);
				ytp += ydi;
				PlayerMoveC2SPacket.PositionAndOnGround packet= new PlayerMoveC2SPacket.PositionAndOnGround(xtp, ytp, ztp, true);

				mc.player.networkHandler.sendPacket(packet);
			}

			mc.player.setPosition(endPos.getX() + 0.5, endPos.getY(), endPos.getZ() + 0.5);
		}else{
			mc.player.setPosition(endPos.getX(), endPos.getY(), endPos.getZ());
		}
	}


	public static void setMoveSpeed(EventMove event, final double speed) {
		double forward = mc.player.input.movementForward;
		double strafe = mc.player.input.movementSideways;
		float yaw = mc.player.getYaw();
		if (forward == 0.0 && strafe == 0.0) {
			event.setX(0.0);
			event.setZ(0.0);
		} else {
			if (forward != 0.0) {
				if (strafe > 0.0) {
					yaw += ((forward > 0.0) ? -45 : 45);
				} else if (strafe < 0.0) {
					yaw += ((forward > 0.0) ? 45 : -45);
				}
				strafe = 0.0;
				if (forward > 0.0) {
					forward = 1.0;
				} else if (forward < 0.0) {
					forward = -1.0;
				}
			}
			event.setX(forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f)));
			event.setZ(forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f)));
		}
	}
	public static void setSpeed(final double moveSpeed, double yVelocity, final float pseudoYaw, final double pseudoStrafe, final double pseudoForward) {
		double forward = pseudoForward;
		double strafe = pseudoStrafe;
		float yaw = pseudoYaw;
		if (pseudoForward != 0.0D) {
			if (pseudoStrafe > 0.0D) {
				yaw = pseudoYaw + (float)(pseudoForward > 0.0D ? -45 : 45);
			} else if (pseudoStrafe < 0.0D) {
				yaw = pseudoYaw + (float)(pseudoForward > 0.0D ? 45 : -45);
			}

			strafe = 0.0D;
			if (pseudoForward > 0.0D) {
				forward = 1.0D;
			} else if (pseudoForward < 0.0D) {
				forward = -1.0D;
			}
		}

		if (strafe > 0.0D) {
			strafe = 1.0D;
		} else if (strafe < 0.0D) {
			strafe = -1.0D;
		}

		double mx = Math.cos(Math.toRadians((double)(yaw + 90.0F)));
		double mz = Math.sin(Math.toRadians((double)(yaw + 90.0F)));
		double x = forward * moveSpeed * mx + strafe * moveSpeed * mz;
		double z = forward * moveSpeed * mz - strafe * moveSpeed * mx;
		mc.player.setVelocity(x, yVelocity, z);
	}
	public static void setMotion(double speed) {
		double forward = mc.player.input.movementForward;
		double strafe = mc.player.input.movementSideways;
		float yaw = mc.player.getYaw();
		if ((forward == 0.0D) && (strafe == 0.0D)) {
			mc.player.setVelocity(0, mc.player.getVelocity().getY(), 0);
		} else {
			if (forward != 0.0D) {
				if (strafe > 0.0D) {
					yaw += (forward > 0.0D ? -45 : 45);
				} else if (strafe < 0.0D) {
					yaw += (forward > 0.0D ? 45 : -45);
				}
				strafe = 0.0D;
				if (forward > 0.0D) {
					forward = 1;
				} else if (forward < 0.0D) {
					forward = -1;
				}
			}
			double x = forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F));
			double z = forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F));
			mc.player.setVelocity(x, mc.player.getVelocity().getY(), z);
		}
	}

	public static GameMode getGameMode(PlayerEntity player) {
		final PlayerListEntry playerListEntry = MC.getNetworkHandler().getPlayerListEntry(player.getUuid());
		if (playerListEntry == null)
			return GameMode.SPECTATOR;
		return playerListEntry.getGameMode();
	}

	public static int getPing(Entity player) {
		if (MC.getNetworkHandler() == null)
			return 0;
		final PlayerListEntry playerListEntry = MC.getNetworkHandler().getPlayerListEntry(player.getUuid());
		if (playerListEntry == null)
			return 0;
		return playerListEntry.getLatency();
	}
	public static int getHealth(Entity player) {
		if (MC.getNetworkHandler() == null)
			return 0;
		final PlayerListEntry playerListEntry = MC.getNetworkHandler().getPlayerListEntry(player.getUuid());
		if (playerListEntry == null)
			return 0;
		return playerListEntry.getHealth();
	}

	public static double distanceTo(Entity entity) {
		return distanceTo(entity.getX(), entity.getY(), entity.getZ());
	}

	public static double distanceTo(BlockPos blockPos) {
		return distanceTo(blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}

	public static double distanceTo(Vec3d vec3d) {
		return distanceTo(vec3d.getX(), vec3d.getY(), vec3d.getZ());
	}

	public static double distanceTo(double x, double y, double z) {
		float f = (float) (mc.player.getX() - x);
		float g = (float) (mc.player.getY() - y);
		float h = (float) (mc.player.getZ() - z);
		return MathHelper.sqrt(f * f + g * g + h * h);
	}

}
