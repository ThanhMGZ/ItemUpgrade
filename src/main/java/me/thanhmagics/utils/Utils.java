package me.thanhmagics.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Utils {
    public static int random(int min, int max) {
        return new Random().nextInt(max - min) + min;
    }

    public static String applyColor(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
    public static String unColor(String s) {
        return ChatColor.stripColor(s);
    }
    public List<String> unColor(List<String> strings) {
        List<String> ss = new ArrayList<>();
        for (String s : strings)
            ss.add(unColor(s));
        return ss;
    }

    public static List<String> applyColor(List<String> s) {
        List<String> rs = new ArrayList<>();
        for (String ss : s)
            rs.add(applyColor(ss));
        return rs;
    }

    public static Location getLocation(Location lLocation) {
        return new Location(lLocation.getWorld(),lLocation.getX(),lLocation.getY(),lLocation.getZ());
    }
    public static double distance(Location l1, Location l2) {
        return Math.sqrt(Math.pow(l2.getX() - l1.getX(),2) + Math.pow(l2.getY() - l1.getY(),2) + Math.pow(l2.getZ() - l1.getZ(),2));
    }
    public static String formatMilisec(int ms) {
        try {
            int h = ms / 3600000;
            int m = (ms % 3600000) / 60000;
            return h + "h" + m + "m";
        } catch (Exception e) {
            return "0h0m";
        }
    }

    public static Location str2Loc(String raw) {
        String[] splited = raw.split(",");
        return new Location(Bukkit.getWorld(splited[0]),
                Double.parseDouble(splited[1]),
                Double.parseDouble(splited[2]),
                Double.parseDouble(splited[3]));
    }

    public static String loc2Str(Location location) {
        return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ();
    }

    public static List<String> stringList(List<String> list, int limit) {
        List<String> rs = new ArrayList<>();
        int line = 0;
        StringBuilder sb = new StringBuilder();
        for (String str : list) {
            if (line + str.length() >= limit) {
                sb.deleteCharAt(sb.length() - 1);
                rs.add(sb.toString());
                sb = new StringBuilder();
                line = 0;
            } else {
                sb.append(applyColor(str)).append("&f, ");
                line += str.length();
            }
        }

        return rs;
    }


    public static Date getTime() {
        Calendar  calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return calendar.getTime();
    }

    public static boolean insideBox(Location point, Location l1, Location l2, int deviation) {
        if (!l1.getWorld().getName().equals(l2.getWorld().getName())) return false;
        if (!point.getWorld().getName().equals(l1.getWorld().getName())) return false;
        return insideBox(point.getBlockX(), point.getBlockY(), point.getBlockZ(), l1.getBlockX(), l1.getBlockY(), l1.getBlockZ(), l2.getBlockX(), l2.getBlockY(), l2.getBlockZ(), deviation);
    }

    public static boolean insideBox(int x1, int y1, int z1, int x2, int y2, int z2, int x3, int y3, int z3, int deviation) {
        int maxX = Math.max(x2, x3) + deviation, minX = Math.min(x2, x3) - deviation;
        int maxY = Math.max(y2, y3) + deviation, minY = Math.min(y2, y3) - deviation;
        int maxZ = Math.max(z2, z3) + deviation, minZ = Math.min(z2, z3) - deviation;
        if (x1 >= minX && x1 <= maxX) {
            if (y1 >= minY && y1 <= maxY) {
                if (z1 >= minZ && z1 <= maxZ) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Object getKeyByValue(Map<Object, Object> map, Object value) {
        for (Object key : map.keySet())
            if (map.get(key).equals(value))
                return key;
        return null;
    }

    public static boolean isInvFull(Inventory inventory) {
        ItemStack[] is = inventory.getContents();
        for (int i = 0; i < is.length; i++) {
            if (is[i] == null)
                return false;
        }
        return true;
    }

    public static long stringToMs(String input) {
        long l1 = Long.parseLong(input.split("h")[0]);
        long l2 = Long.parseLong(input.split("m")[0].split("h")[1]);
        return (l1 * 3600000) + (l2 * 60000);
    }
    public static Object[] addElementToArray(Object[] o,Object e) {
        if (o.length > 0 && !o[0].equals(e)) return o;
        Object[] n = new Object[o.length + 1];
        System.arraycopy(o, 0, n, 0, o.length);
        n[o.length] = e;
        return n;
    }

    public static Class<?>[] addElementToArray(Class<?>[] clzz,Class<?> e) {
        Class<?>[] n = new Class<?>[clzz.length + 1];
        for (int i = 0; i < clzz.length; i++) {
            n[i] = clzz[i];
        }
        n[clzz.length] = e;
        return n;
    }

    public static String deleteChar(String s,int l) {
        if (s.length() >= l) {
            StringBuilder stringBuilder = new StringBuilder(s);
            stringBuilder.deleteCharAt(l);
            return stringBuilder.toString();
        }
        return s;
    }
}
