package io.github.lee0701.tcsignals;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;

import java.util.ArrayList;
import java.util.List;

public class BlockSection {

    private static List<BlockSection> SECTIONS = new ArrayList<>();

    public static void add(BlockSection section) {
        SECTIONS.add(section);
    }

    public static void remove(BlockSection section) {
        SECTIONS.remove(section);
    }

    private MinecartGroup occupyingGroup;

    private List<BlockSignal> beginSignals = new ArrayList<>();

    private List<BlockSignal> endSignals = new ArrayList<>();

    public MinecartGroup getOccupyingGroup() {
        return occupyingGroup;
    }

    public void setOccupyingGroup(MinecartGroup occupyingGroup) {
        this.occupyingGroup = occupyingGroup;
    }

    public List<BlockSignal> getBeginSignals() {
        return beginSignals;
    }

    public List<BlockSignal> getEndSignals() {
        return endSignals;
    }
}
