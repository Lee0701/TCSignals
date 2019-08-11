package io.github.lee0701.tcsignals;

import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.tc.Direction;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import org.bukkit.block.BlockFace;

public class SignActionSignal extends SignAction {

    public static String[] TYPES = new String[] {"signal"};
    private static BlockFace[] DIRECTIONS = {BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH};

    @Override
    public boolean match(SignActionEvent event) {
        return event.isType(TYPES);
    }

    @Override
    public void execute(SignActionEvent event) {
        BlockSignal signal = BlockSignal.of(event.getLocation(), event.getFacing()).orElse(null);
        if(signal == null) return;
        if(event.isAction(SignActionType.GROUP_ENTER) && event.hasGroup()) {
            event.getGroup().getActions().addAction(new GroupActionWaitSignal(signal.getSection()));

        } if(event.isAction(SignActionType.GROUP_LEAVE) && event.hasGroup()) {
            BlockSection.SECTIONS.forEach(section -> {
                if(section != signal.getSection() && section.getOccupyingGroup() == event.getGroup()) section.setOccupyingGroup(null);
            });
        }
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        boolean result = handleBuild(event, Permission.BUILD_WAIT, "train signal", "control train flows");
        if(result) {
            BlockSignal signal = new BlockSignal(event.getLocation(), event.getFacing());
            BlockSignal.SIGNALS.add(signal);
            signal.repopulate();
        }
        return result;
    }

    @Override
    public void destroy(SignActionEvent event) {
        super.destroy(event);
        BlockSignal.of(event.getLocation(), event.getFacing()).ifPresent(BlockSignal.SIGNALS::remove);
    }
}
