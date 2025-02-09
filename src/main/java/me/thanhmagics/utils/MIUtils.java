package me.thanhmagics.utils;

import io.lumine.mythic.lib.api.item.NBTItem;
import me.thanhmagics.ItemUpgrade;
import me.thanhmagics.core.UpgradeInfo;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class MIUtils {
    public static MMOItem fromStack(ItemStack item) {
        if (item == null)
            return null;
        NBTItem itemNBT = NBTItem.get(item);
        if (MMOItems.getType(itemNBT) != null)
            return (MMOItem)new LiveMMOItem(item);
        return null;
    }

    public static MMOItem fromStrings(String id, String type) {
        Type typee = MMOItems.plugin.getTypes().getOrThrow(type.toUpperCase().replace("-", "_"));
        MMOItemTemplate template = MMOItems.plugin.getTemplates().getTemplateOrThrow(typee, id.toUpperCase().replace("-", "_"));
        return template.newBuilder().build();
    }

    public static MMOItem upgrade(MMOItem mmoItem) {
        mmoItem.getUpgradeTemplate().upgrade(mmoItem);
        return mmoItem;
    }

    private static final Map<UpgradeInfo,MMOItem> mmoiByUI = new HashMap<>();

    static {
        for (UpgradeInfo info : ItemUpgrade.getInstance().data.upgradeInfos) {
            getMMOItemByUpgradeInfo(info);
        }
    }

    public static MMOItem getMMOItemByUpgradeInfo(UpgradeInfo upgradeInfo) {
        if (mmoiByUI.containsKey(upgradeInfo)) return mmoiByUI.get(upgradeInfo);
        MMOItem mmoItem = fromStrings(upgradeInfo.id,upgradeInfo.type);
        if (mmoItem == null) return null;
        mmoiByUI.put(upgradeInfo,mmoItem);
        return mmoItem;
    }

    public static UpgradeInfo getByMMOItem(MMOItem mmoItem) {
        for (UpgradeInfo info : ItemUpgrade.getInstance().data.upgradeInfos) {
            MMOItem mmoItem1 = getMMOItemByUpgradeInfo(info);
            if (mmoItem1 == null) continue;
            if (mmoItem1.getType().equals(mmoItem.getType()) && mmoItem1.getId().equals(mmoItem.getId())) return info;
        }
        return null;
    }

    public static boolean isUpgradableItem(ItemStack itemStack) {
        MMOItem mmoItem = fromStack(itemStack);
        if (mmoItem == null) return false;
        for (MMOItem v : mmoiByUI.values())
            if (v.getId().equals(mmoItem.getId()) &&
            v.getType().equals(mmoItem.getType())) return true;
        return false;
    }

}
