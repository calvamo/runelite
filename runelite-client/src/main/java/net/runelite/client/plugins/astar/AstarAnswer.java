package net.runelite.client.plugins.astar;

import lombok.Getter;
import net.runelite.api.CollisionData;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;

import javax.inject.Inject;

public class AstarAnswer {

    @Getter
    private Client client;

    @Getter
    private int plane;

    public AstarAnswer(WorldPoint location, Client client) {
        this.plane = location.getPlane();
        this.client = client;
    }
    public int[][] collisionArray() {
        //CollisionData[] collisionData = client.getCollisionMaps()[client.getPlane()];
        //assert collisionData != null;
        int[][] collisionDataFlags = client.getCollisionMaps()[client.getPlane()].getFlags();
        return collisionDataFlags;
    }
}
