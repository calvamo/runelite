package net.runelite.client.plugins.astar;

import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;

import java.awt.*;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class AstarAnswerOverlay extends OverlayPanel {

    private final AstarAnswerPlugin plugin;
    private final AstarAnswerConfig config;

    @Inject
    private AstarAnswerOverlay(AstarAnswerPlugin plugin, AstarAnswerConfig config)
    {
        super(plugin);
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        this.plugin = plugin;
        this.config = config;
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Astar Map"));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        final String attackStyleString = "hi im working";
        boolean astarSelected = plugin.isAstarSelected();

        panelComponent.getChildren().add(TitleComponent.builder()
                .text(attackStyleString)
                .color(astarSelected ? Color.RED : Color.WHITE)
                .build());

        panelComponent.setPreferredSize(new Dimension(
                graphics.getFontMetrics().stringWidth(attackStyleString) + 10,
                0));

        return super.render(graphics);

    }



}
