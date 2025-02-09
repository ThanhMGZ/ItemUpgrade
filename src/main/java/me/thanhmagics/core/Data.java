package me.thanhmagics.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class Data implements Serializable {

    public Map<UUID, PlayerDT> playerDT = new HashMap<>();

    public String upGem = null;

    public LinkedList<UpgradeInfo> upgradeInfos = new LinkedList<>();

}
