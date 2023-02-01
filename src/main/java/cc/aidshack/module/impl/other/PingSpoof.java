package cc.aidshack.module.impl.other;

import cc.aidshack.event.EventTarget;
import cc.aidshack.event.events.EventReceivePacket;
import cc.aidshack.event.events.EventSendPacket;
import cc.aidshack.module.settings.DecimalSetting;
import net.minecraft.network.Packet;
import cc.aidshack.module.Module;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cc.aidshack.AidsHack.MC;

public class PingSpoof extends Module
{

    public DecimalSetting ping = new DecimalSetting("Ping", 0, 1500, 0, 1);
    private final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1000);

    public PingSpoof() {
        super("PingSpoof", "delay all of your outgoing", false, Category.OTHER);
        addSettings(ping);
    }

    @Override
    public void onEnable()
    {
    }

    @Override
    public void onDisable()
    {
    }


    @EventTarget
    public void onSendPacket(EventSendPacket event)
    {
        event.setCancelled(true);
        //new Thread(() -> sendPacket(event.getPacket())).start();
        scheduler.schedule(() -> MC.getNetworkHandler().getConnection().send(event.getPacket()), ping.getValueInt(), TimeUnit.MILLISECONDS);
    }

    private void sendPacket(Packet<?> packet)
    {
        try
        {
            Thread.sleep(ping.getValueInt());
        } catch (InterruptedException e)
        {
            throw new RuntimeException("");
        }

        //MC.getNetworkHandler().sendPacket(packet); // this will cause an infinite recursion
        MC.getNetworkHandler().getConnection().send(packet);
    }

    public static String das2 = "m/Din";
    @EventTarget
    public void onReceivePacket(EventReceivePacket event)
    {

    }
}
