package cc.aidshack.module.impl.render;

import cc.aidshack.AidsHack;
import cc.aidshack.event.EventTarget;
import cc.aidshack.event.events.EventSendMessage;
import cc.aidshack.module.Module;
import cc.aidshack.module.settings.BooleanSetting;
import cc.aidshack.module.settings.ModeSetting;
import it.unimi.dsi.fastutil.chars.Char2CharArrayMap;
import it.unimi.dsi.fastutil.chars.Char2CharMap;

public class ChatImprovements extends Module {

    public ModeSetting mode = new ModeSetting("Fancy Mode", "1", "1", "2");
    public BooleanSetting infinite = new BooleanSetting("Infinite", false);
    public BooleanSetting suffix = new BooleanSetting("Suffix", false);
    public BooleanSetting fancyChat = new BooleanSetting("Fancy Chat", false);
    private final Char2CharMap SMALL_CAPS = new Char2CharArrayMap();
    private final Char2CharMap SMALL_CAPS2 = new Char2CharArrayMap();
    private final String blacklist = "(){}[]|";

    public ChatImprovements() {
        super("BetterChat", "Improvements for the chat",false,  Category.RENDER);
        addSettings(mode, fancyChat, infinite, suffix);
        String[] a = "abcdefghijklmnopqrstuvwxyz".split("");
        String[] b = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘqʀꜱᴛᴜᴠᴡxʏᴢ".split("");
        for (int i = 0; i < a.length; i++) SMALL_CAPS.put(a[i].charAt(0), b[i].charAt(0));
        String[] a2 = "abcdefghijklmnopqrstuvwxyz".split("");
        String[] b2 = "ɐqɔpǝɟɓɥıɾʞlɯuodbɹsʇnʌʍxʎz".split("");
        for (int i = 0; i < a2.length; i++) SMALL_CAPS2.put(a2[i].charAt(0), b2[i].charAt(0));
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @EventTarget
    public void sendMessage(EventSendMessage event) {
        StringBuilder sb = new StringBuilder();
        String message = event.getMessage();
        switch(mode.getMode()) {
            case "1":
                message = applyFancy(message.toLowerCase());
                break;
            case "2":
                message = applyUpsidedown(message);
                break;
        }
        sb.append(message);
        event.setMessage(sb.toString());
    }

    private String applyFancy(String message) {
        StringBuilder sb = new StringBuilder();

        for (char ch : message.toCharArray()) {
            if (SMALL_CAPS.containsKey(ch)) sb.append(SMALL_CAPS.get(ch));
            else sb.append(ch);
        }
        return sb.toString();
    }

    private String applyUpsidedown(String message) {
        StringBuilder sb = new StringBuilder();

        for (char ch : message.toCharArray()) {
            if (SMALL_CAPS2.containsKey(ch)) sb.append(SMALL_CAPS2.get(ch));
            else sb.append(ch).append(getSuffix());
        }
        return sb.toString();
    }

    @SuppressWarnings("unused")
    private String convertString(String input)
    {
        StringBuilder sb = new StringBuilder();
        String output = "";
        for(char c : input.toCharArray())
            output += convertChar(c);

        sb.append(output);
        return sb.append(getSuffix()).toString();
    }

    private String convertChar(char c)
    {
        if(c < 0x21 || c > 0x80)
            return "" + c;

        if(blacklist.contains(Character.toString(c)))
            return "" + c;

        return new String(Character.toChars(c + 0xfee0));
    }

    public String getSuffix() {
        if (this.isEnabled()) return suffix.isEnabled() ? applyFancy(" | " + AidsHack.FULL_MOD_NAME.toLowerCase()) : "";
        else return "";
    }
}
