package me.thanhmagics.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class UpgradeInfo implements Serializable {

    public String id;

    public String type;

    public Map<Integer, Double> successPercent = new LinkedHashMap<>();

    public Map<Integer, Integer> upGemCost = new LinkedHashMap<>();

    public UpgradeInfo(String id, String type) {
        this.id = id;
        this.type = type;
        for (int i = 1; i < 10; i++) {
            successPercent.put(i,(double) i);
            upGemCost.put(i, i);
        }
    }
}
