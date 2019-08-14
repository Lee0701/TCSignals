package io.github.lee0701.tcsignals;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;

import java.util.ArrayList;
import java.util.List;

public class BlockSection {

    public static List<BlockSection> SECTIONS = new ArrayList<>();

    public static BlockSection get(MinecartGroup occupyingGroup) {
        return SECTIONS.stream().filter(section -> section.occupyingGroup == occupyingGroup).findAny().orElse(null);
    }

    private MinecartGroup occupyingGroup;

    private List<BlockSignal> beginSignals = new ArrayList<>();
    private List<BlockSignal> endSignals = new ArrayList<>();

    public void occupy(MinecartGroup occupyingGroup) {
        setOccupyingGroup(occupyingGroup);
        this.beginSignals.forEach(signal -> signal.setLever(false));
    }

    public void unoccupy() {
        setOccupyingGroup(null);
        this.beginSignals.forEach(signal -> signal.setLever(true));
    }

    public MinecartGroup getOccupyingGroup() {
        return occupyingGroup;
    }

    protected void setOccupyingGroup(MinecartGroup occupyingGroup) {
        this.occupyingGroup = occupyingGroup;
    }

    public List<BlockSignal> getBeginSignals() {
        return beginSignals;
    }

    public List<BlockSignal> getEndSignals() {
        return endSignals;
    }
}
