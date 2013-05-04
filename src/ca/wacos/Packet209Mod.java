package ca.wacos;

import net.minecraft.server.v1_5_R3.Packet209SetScoreboardTeam;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Written by Levi Webb
 * <p/>
 * Date: 03/05/13
 * Time: 12:20 AM
 */
class Packet209Mod {

    Packet209SetScoreboardTeam packet;

    @SuppressWarnings("unchecked")
    Packet209Mod(String name, String prefix, String suffix, Collection players, int paramInt) {

        packet = new Packet209SetScoreboardTeam();

        packet.a = name;
        packet.f = paramInt;

        if ((paramInt == 0) || (paramInt == 2)) {
            packet.b = name;
            packet.c = prefix;
            packet.d = suffix;
            packet.g = 1;
        }
        if (paramInt == 0)
            packet.e.addAll(players);
    }
    @SuppressWarnings("unchecked")
    Packet209Mod(String name, Collection players, int paramInt) {

        packet = new Packet209SetScoreboardTeam();

        if ((paramInt != 3) && (paramInt != 4)) throw new IllegalArgumentException("Method must be join or leave for player constructor");
        if ((players == null) || (players.isEmpty())) players = new ArrayList<String>();

        packet.f = paramInt;
        packet.a = name;
        packet.e.addAll(players);
    }
}
