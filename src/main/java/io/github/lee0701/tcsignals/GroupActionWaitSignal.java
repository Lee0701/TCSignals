package io.github.lee0701.tcsignals;

import com.bergerkiller.bukkit.tc.actions.GroupActionWaitForever;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import org.bukkit.block.BlockFace;

public class GroupActionWaitSignal extends GroupActionWaitForever {

    private final BlockSection waitingFor;
    private Double launchVelocity;

    public GroupActionWaitSignal(BlockSection waitingFor, Double launchVelocity) {
        this.waitingFor = waitingFor;
        if(launchVelocity != null) this.launchVelocity = launchVelocity;
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
            // The track is clear, occupy the section and launch.
            this.waitingFor.setOccupyingGroup(getGroup());
            getGroup().head().getActions().addActionLaunch(2.0, launchVelocity);
            return true;
        }
    }
}
