package me.thanhmagics.utils;
import me.thanhmagics.ItemUpgrade;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
import java.nio.file.Files;

public class ObjectSerialization {

    private static void createDirectory() {
        File file = new File(ItemUpgrade.getInstance().getDataFolder(), "data");
        if (!file.exists()) {
            try {
                Files.createDirectories(file.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void saveObject(Object o,String uid) {createDirectory();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(ItemUpgrade.getInstance().getDataFolder(), "data/" + uid));
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(o);
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object getDataObject(String uid) {createDirectory();
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(ItemUpgrade.getInstance().getDataFolder(), "data/" + uid));
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            return objectInputStream.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    public static String isToString(ItemStack itemStack) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(itemStack);
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ItemStack stringToIs(String str) {
        ItemStack item;
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(str));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            item = (ItemStack) dataInput.readObject();
            dataInput.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return item;
    }

}
