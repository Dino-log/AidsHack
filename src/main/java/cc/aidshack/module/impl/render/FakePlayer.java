package cc.aidshack.module.impl.render;

import cc.aidshack.module.Module;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import static cc.aidshack.AidsHack.MC;

public class FakePlayer extends Module {

	public FakePlayer() {
		super("FakePlayer", "nigge",false, Category.RENDER);
	}


	public static String fakePlayerName = "George Floyd";
	int id;

	@Override
	public void onEnable()
	{
		OtherClientPlayerEntity player = new OtherClientPlayerEntity(MC.world, new GameProfile(null, fakePlayerName));
		Vec3d pos = MC.player.getPos();
		player.setInvulnerable(false);
		player.setHealth(100);
		player.updateTrackedPosition(pos);
		player.updatePositionAndAngles(pos.x, pos.y, pos.z, MC.player.getYaw(), MC.player.getPitch());
		player.resetPosition();
		MC.world.addPlayer(player.getId(), player);
		id = player.getId();
	}

	@Override
	public void onDisable()
	{
		MC.world.removeEntity(id, Entity.RemovalReason.DISCARDED);
	}


	public static int setFakePlayerName(String newFakePlayerName){
		fakePlayerName = newFakePlayerName;
		return 0;
	}

	public static String ads2d = "g/oiasdnaijadsidln90/j";

}

