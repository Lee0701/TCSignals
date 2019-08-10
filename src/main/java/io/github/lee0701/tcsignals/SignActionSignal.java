package io.github.lee0701.tcsignals;

import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
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

    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        boolean result = handleBuild(event, Permission.BUILD_WAIT, "train signal", "control train flows");
        if(result) {
            BlockSignal signal = new BlockSignal(event.getLocation(), event.getFacing());
            BlockSignal.add(signal);
            signal.repopulate();
        }
        return result;
    }

    @Override
    public void destroy(SignActionEvent event) {
        super.destroy(event);
        BlockSignal.of(event.getLocation(), event.getFacing()).ifPresent(BlockSignal::remove);
    }
}
