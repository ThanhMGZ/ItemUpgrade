package me.thanhmagics.command;

import me.thanhmagics.ItemUpgrade;
import me.thanhmagics.core.UpgradeGUI;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class OpenUpgradeGUI implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if (strings.length == 2 && strings[0].equals("play") && player.hasPermission("op")) {
            player.sendMessage("playing...");
            int speed = Integer.parseInt(strings[1]);
            new BukkitRunnable() {
                double t = 0;
                int i = 0;
                @Override
                public void run() {
                    i++;
                    if (i > 20 * 30) cancel();
                    Location playerLoc = player.getLocation();
                    if (!player.isOnline()) {
                        cancel();
                        return;
                    }
                    double x = Math.cos(t);
                    double z = Math.sin(t);
                    //spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, T data)
                    playerLoc.getWorld().spawnParticle(Particle.FLAME,
                        playerLoc.getX() + (x * 0.5),playerLoc.getY(),
                            playerLoc.getZ() + (z * 0.5),0,
                            x * 0.1,0,z * 0.1);

                    x = Math.cos(t + Math.PI);
                    z = Math.sin(t + Math.PI);

                    playerLoc.getWorld().spawnParticle(Particle.FLAME,
                            playerLoc.getX() + (x * 0.5),playerLoc.getY(),
                            playerLoc.getZ() + (z * 0.5),0,
                            x * 0.1,0,z * 0.1);
                    t += Math.PI / 10;
                }
            }.runTaskTimer(ItemUpgrade.getInstance(),0,1);
        } else {
            UpgradeGUI.open(player);
        }
        return true;
    }
}
