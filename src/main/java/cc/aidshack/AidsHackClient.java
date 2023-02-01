package cc.aidshack;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.Entity;

import java.util.HashMap;

public class AidsHackClient implements ModInitializer {



    public static final HashMap<Entity, Integer> toKill = new HashMap<>();
    @Override
    public void onInitialize() {
        try{
            AidsHack.CLIENT.register();
            AidsHack.CLIENT.loadFiles();
            toKill.clear();
        }catch (Exception ignored)
        {
        }
    }


}
