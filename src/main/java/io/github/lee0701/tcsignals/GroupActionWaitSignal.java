package io.github.lee0701.tcsignals;

import com.bergerkiller.bukkit.tc.actions.GroupActionWaitForever;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import org.bukkit.block.BlockFace;

public class GroupActionWaitSignal extends GroupActionWaitForever {

    private final BlockSection waitingFor;
    private Double launchVelocity;
    private Double slowVelocity;

    public GroupActionWaitSignal(BlockSection waitingFor, Double launchVelocity, Double slowVelocity) {
        this.waitingFor = waitingFor;
        this.launchVelocity = launchVelocity;
        this.slowVelocity = slowVelocity;
    }

    @Override
    public void bind() {
        if(this.launchVelocity == null) this.launchVelocity = this.getGroup().getAverageForce();
    }

    @Override
    public boolean update() {
        MinecartGroup group = waitingFor.getOccupyingGroup();
        if(group != null && group.isValid()) return super.update();
        else {
            double launchVelocity = this.launchVelocity;
            if(this.slowVelocity != null) {
                int count = (int) waitingFor.getEndSignals().stream()
                        .map(BlockSignal::getSection)
                        .filter(section -> section.getOccupyingGroup() != null && section.getOccupyingGroup().isValid())
                        .count();
                if(count > 0) launchVelocity = this.slowVelocity;
            }
            // The track is clear, occupy the section and launch.
            this.waitingFor.occupy(getGroup());
            getGroup().head().getActions().addActionLaunch(2.0, launchVelocity);
            return true;
        }
    }
}
