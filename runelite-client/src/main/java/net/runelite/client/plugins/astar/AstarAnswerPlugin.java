package net.runelite.client.plugins.astar;

import java.io.IOException;
import java.util.*;
import org.json.JSONObject;
import java.util.Arrays;
import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.devtools.MovementFlag;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@PluginDescriptor(
        name = "Astar Map",
        description = "Show if a tile can be walked on",
        tags = {"map"}
)

public class AstarAnswerPlugin extends Plugin {
    public int[][] astarArray;
    private boolean AstarSelected = false;
    private final Set<Skill> warnedSkills = new HashSet<>();
    public boolean isAstarSelected()
    {
        return AstarSelected;
    }

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private AstarAnswerOverlay overlay;

    @Inject
    private AstarAnswerConfig config;

    @Inject
    private client_udp client_UDP;

    @Provides
    AstarAnswerConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(AstarAnswerConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);

        if (client.getGameState() == GameState.LOGGED_IN)
        {
            clientThread.invoke(this::start);
        }
    }

    private void start() {

    }

    @Override
    protected void shutDown() {
        overlayManager.remove(overlay);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) throws IOException {
        WorldPoint localWorld = client.getLocalPlayer().getWorldLocation();
        LocalPoint localPoint = client.getLocalPlayer().getLocalLocation();
        //System.out.println(localPoint.getSceneX() + ", " + localPoint.getSceneY());
        //System.out.println(localWorld.getX() + ", " + localWorld.getY() + ", " + client.getPlane());

        Hashtable<String, Integer> loc_dict = new Hashtable<String, Integer>();
        loc_dict.put("WorldX", localWorld.getX());
        loc_dict.put("WorldY", localWorld.getY());
        loc_dict.put("WorldZ", client.getPlane());
        loc_dict.put("SceneX", localPoint.getSceneX());
        loc_dict.put("SceneY", localPoint.getSceneY());



        Hashtable<String, int[]> moveflag_dict = new Hashtable<String, int[]>();

        if (event.getGroup().equals("astarDisplay"))
        {
            boolean enabled = Boolean.TRUE.toString().equals(event.getNewValue());
            switch (event.getKey())
            {
                case "AstarOn":
                    updateWarnedSkills(enabled, Skill.DEFENCE);
                    if (isAstarSelected()){
                        int[][] temp =  astarArrayGen();
                        String[][] flagArray = new String[63][63];
                        int x = 1;//int x = 0;
                        int y = 1;//int y = 0;
                        while(x < 63) {
                            while(y < 63) {
                                Set<MovementFlag> movementFlags = MovementFlag.getSetFlags(temp[x][y]);
                                //AtomicBoolean empty_Bool = new AtomicBoolean(false);
                                if (movementFlags.isEmpty()) {

                                    //System.out.print("x " + x + ", y " + y + ": ");
                                    //System.out.println("Empty");
                                    //flagArray[x][y] = "Empty";
                                } else {
                                    //System.out.print("x " + x + ", y " + y + ": ");
                                    //movementFlags.forEach(flag -> System.out.print(flag.toString() + ", "));

                                    AtomicReference<String> movenames = new AtomicReference<>("");
                                    //movementFlags.forEach(flag -> {
                                    //    movenames.set(flag.toString());
                                    //});


                                    System.out.println();
                                    int finalX = x;
                                    int finalY = y;

                                    movementFlags.forEach(flag -> {
                                        movenames.set(flag.toString());
                                        //String s = flag.toString() + ", ";
                                        //if (movementFlags.isEmpty()) {
                                        //  flagArray[finalX][finalY] = "Empty";
                                        //}

                                        //if (flagArray[finalX][finalY] != null && !movementFlags.isEmpty()) {
                                        if (flagArray[finalX][finalY] != null && !movementFlags.isEmpty()) {
                                            flagArray[finalX][finalY] = flagArray[finalX][finalY] + " " + String.valueOf(movenames);

                                        } else {
                                            //flagArray[finalX][finalY] = "Empty";
                                            if (!movementFlags.isEmpty()) {
                                                if (String.valueOf(movenames) == null) {
                                                    flagArray[finalX][finalY] = "Empty";
                                                } else {
                                                    flagArray[finalX][finalY] = String.valueOf(movenames);
                                                }
                                            } else {
                                                flagArray[finalX][finalY] = "Nothing";
                                            }
                                            //flagArray[finalX][finalY] = String.valueOf(movenames);
                                        }
                                    });
                                }
                                String xstr;
                                String ystr;
                                if (x < 10) {
                                    xstr = Integer.toString(x);
                                    xstr = "0" + xstr;
                                } else {
                                    xstr = Integer.toString(x);
                                }
                                if (y < 10) {
                                    ystr = Integer.toString(y);
                                    ystr = "0" + ystr;
                                } else {
                                    ystr = Integer.toString(y);
                                }

                                if (flagArray[x][y] == null) {
                                    //System.out.println(xstr + ystr);
                                    int[] z = new int[1];
                                    z[0] = 0;
                                    moveflag_dict.put(xstr + ystr, z);//empty
                                } else {

                                    int flaglength = flagArray[x][y].split(" ").length;

                                    int[] flagsplitnum = new int[flaglength];
                                    String[] flagsplit = flagArray[x][y].split(" ");
                                    for (int i = 0; i < flaglength; i++) {

                                        //System.out.println(flagsplit[i]);
                                        //System.out.println(flagsplit[i].getClass().getSimpleName());
                                        if (flagsplit[i].equals("BLOCK_MOVEMENT_OBJECT")) {
                                            flagsplitnum[i] = 1;
                                        } else if (flagsplit[i].equals("BLOCK_MOVEMENT_FLOOR_DECORATION")) {
                                            flagsplitnum[i] = 2;
                                        } else if (flagsplit[i].equals("BLOCK_MOVEMENT_FLOOR")) {
                                            flagsplitnum[i] = 3;
                                        } else if (flagsplit[i].equals("BLOCK_MOVEMENT_FULL")) {
                                            flagsplitnum[i] = 4;
                                        } else if (flagsplit[i].equals("BLOCK_MOVEMENT_NORTH_WEST")) {
                                            flagsplitnum[i] = 5;
                                        } else if (flagsplit[i].equals("BLOCK_MOVEMENT_NORTH")) {
                                            flagsplitnum[i] = 6;
                                        } else if (flagsplit[i].equals("BLOCK_MOVEMENT_NORTH_EAST")) {
                                            flagsplitnum[i] = 7;
                                        } else if (flagsplit[i].equals("BLOCK_MOVEMENT_EAST")) {
                                            flagsplitnum[i] = 8;
                                        } else if (flagsplit[i].equals("BLOCK_MOVEMENT_SOUTH_EAST")) {
                                            flagsplitnum[i] = 9;
                                        } else if (flagsplit[i].equals("BLOCK_MOVEMENT_SOUTH")) {
                                            flagsplitnum[i] = 10;
                                        } else if (flagsplit[i].equals("BLOCK_MOVEMENT_SOUTH_WEST")) {
                                            flagsplitnum[i] = 11;
                                        } else if (flagsplit[i].equals("BLOCK_MOVEMENT_WEST")) {
                                            flagsplitnum[i] = 12;
                                        } else {
                                            flagsplitnum[i] = 15;
                                        }

                                        //System.out.println(flagsplitnum[i]);
                                    }


                                    //System.out.println(xstr + ystr);
                                    Arrays.sort(flagsplitnum);

                                    moveflag_dict.put(xstr + ystr, flagsplitnum);
                                }

                                y += 1;

                            }
                        y = 1;//y = 0;
                        x += 1;
                        }
                        //System.out.println(Arrays.deepToString(temp));
                        //System.out.println(Arrays.deepToString(flagArray));

                        JSONObject jsonloc =  new JSONObject(loc_dict);

                        //System.out.printf( "JSON: %s", jsonloc);
                        String check = client_UDP.sendandRec(jsonloc.toString(),1400);
                        if (check.equals("valid")) {
                            JSONObject json =  new JSONObject(moveflag_dict);

                            //System.out.printf( "JSON: %s", json);
                            client_UDP.sendEcho(json.toString(),1400);
                        }
                        else {
                            JSONObject jsonerror =  new JSONObject("not valid location");

                            System.out.printf( "JSON: %s", jsonerror);
                            client_UDP.sendEcho(jsonerror.toString(),1400);
                        }



                        //System.out.println("\nValue at key = 0360 : " + Arrays.toString(moveflag_dict.get("0360")));
                        //System.out.println("\nValue at key = 0101 : " + Arrays.toString(moveflag_dict.get("0101")));
                        //System.out.println("\nValue at key = 0102 : " + Arrays.toString(moveflag_dict.get("0102")));
                        //System.out.println("\nValue at key = 6232 : " + Arrays.toString(moveflag_dict.get("6232")));
                    }

                    break;
            }
            //processWidgets();
        }
    }






    private void updateWarnedSkills(boolean enabled, Skill skill) {
        if (enabled) {
            warnedSkills.add(Skill.DEFENCE);
        } else {
            warnedSkills.remove(Skill.DEFENCE);
        }
        updateWarning(false);
    }
    private void updateWarning(boolean weaponSwitch)
    {
        AstarSelected = false;

        if (warnedSkills.contains(Skill.DEFENCE)) {
            AstarSelected = true;
        }
    }

    public int[][] astarArrayGen() {
        WorldPoint localWorld = client.getLocalPlayer().getWorldLocation();
        AstarAnswer test = new AstarAnswer(localWorld, client);
        astarArray = test.collisionArray();
        return astarArray;

    }



}
