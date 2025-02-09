package me.thanhmagics.core;

import java.io.Serializable;
import java.util.UUID;

public class PlayerDT implements Serializable {

    public UUID uuid, inv = null;

    public String i1 = null,i2 = null;

    public PlayerDT(UUID uuid) {

        this.uuid = uuid;
    }


}
