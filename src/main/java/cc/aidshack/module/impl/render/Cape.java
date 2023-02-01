package cc.aidshack.module.impl.render;

import cc.aidshack.module.Module;
import cc.aidshack.module.settings.ModeSetting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class Cape extends Module {

    public ModeSetting mode = new ModeSetting("Texture", "AidsHack", "AidsHack", "ChingChang", "Niksa", "Aqua5", "Tits", "Ass");

    public Cape() {
        super("Cape", "", false, Category.RENDER);
        addSettings(mode);
    }

    public Identifier getTexture(PlayerEntity player){
        if (this.isEnabled() && (player == mc.player)){
            return getIdentifier();
        }else {
            return null;
        }
    }


    public Identifier getIdentifier(){
        Identifier iden = new Identifier("aidshack", "capes/aidshackcape.png");
        if (mode.isMode("AidsHack")){
            iden = new Identifier("aidshack", "capes/aidshackcape.png");
        } else if (mode.isMode("ChingChang")) {
            iden = new Identifier("aidshack", "capes/chingchangcape.png");
        } else if (mode.isMode("Niksa")) {
            iden = new Identifier("aidshack", "capes/niksacape.png");
        } else if (mode.isMode("Tits")) {
            iden = new Identifier("aidshack", "capes/titscape.png");
        } else if (mode.isMode("Ass")) {
            iden = new Identifier("aidshack", "capes/asscape.png");
        } else if (mode.isMode("Aqua5")) {
            iden = new Identifier("aidshack", "capes/aqua5cape.png");
        }
        return iden;
    }
    public static String sssda = "o-lo" ;
}
