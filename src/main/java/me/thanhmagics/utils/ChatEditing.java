package me.thanhmagics.utils;

import me.thanhmagics.ItemUpgrade;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatEditing implements Listener {

    private static Map<UUID,Handler> handlers = new HashMap<>();

    public static void newHandler(Handler handler,UUID player) {
  //      if (!b) run();
        if (!handlers.containsKey(player)) {
            handlers.put(player,handler);
        }
        if (!reg)
            new ChatEditing().reg();
        Bukkit.getPlayer(player).closeInventory();
    }

    private static boolean reg = false;

    private void reg() {
        ItemUpgrade.getInstance().getServer().getPluginManager().registerEvents(this,ItemUpgrade.getInstance());
        reg = true;
    }

//    private static boolean b = false;
//
//    private static void run() {
//        b = true;
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                if (handlers.isEmpty()) return;
//                handlers.forEach((k,v) -> {
//                    Bukkit.getPlayer(k).sendTitle(Utils.applyColor("chat '-cancel' để thoát chế độ chỉnh sửa"),"");
//                });
//            }
//        }.runTaskTimer(MagicsSlayer.getInstance(),0,20);
//    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (handlers.containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendTitle(Utils.applyColor("chat '-cancel' để thoát chế độ chỉnh sửa"),"");
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEvent(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        if (handlers.containsKey(player.getUniqueId())) {
            Handler handler = handlers.get(player.getUniqueId());
            handlers.remove(player.getUniqueId());
            if (message.equalsIgnoreCase("-cancel")) {
                event.setCancelled(true);
                handler.onRefuse(player);
                return;
            }
            boolean b = handler.onChat(player,message);
            if (b) event.setCancelled(true);
        }
    }


    public static abstract class Handler {

        public int toInteger(String input,String whenNot,Player player) {
            try {
                return Integer.parseInt(input);
            } catch (Exception e) {
                Handler handler = handlers.get(player.getUniqueId());
                player.sendMessage(Utils.applyColor(whenNot));
                handler.onRefuse(player);
            }
            return Integer.MAX_VALUE;
        }

        public abstract boolean onChat(Player player,String input);

        public abstract void onRefuse(Player player);
    }



}
