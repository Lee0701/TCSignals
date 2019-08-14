package io.github.lee0701.tcsignals;

import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;

public class SignActionSignal extends SignAction {

    public static String[] TYPES = new String[] {"signal"};

    @Override
    public boolean match(SignActionEvent event) {
        return event.isType(TYPES);
    }

    @Override
    public void execute(SignActionEvent event) {
        BlockSignal signal = BlockSignal.of(event.getLocation(), event.getFacing());
        if(event.isAction(SignActionType.GROUP_ENTER) && event.hasGroup()) {
            Double launchForce = ParseUtil.parseDouble(event.getLine(2), event.getGroup().getAverageForce());
            event.getGroup().getActions().clear();
            event.getGroup().getActions().addAction(new GroupActionWaitSignal(
                    signal.getSection(),
                    launchForce,
                    ParseUtil.parseDouble(event.getLine(3), launchForce)));

        } if(event.isAction(SignActionType.GROUP_LEAVE) && event.hasGroup()) {
            BlockSection.SECTIONS.stream()
                    .filter(section -> section != signal.getSection() && section.getOccupyingGroup() == event.getGroup())
                    .forEach(BlockSection::unoccupy);

        }
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        boolean result = handleBuild(event, Permission.BUILD_WAIT, "train signal", "create block sections by combining multiple signs");
        if(result) {
            BlockSignal.of(event.getLocation(), event.getFacing());
        }
        return result;
    }

    @Override
    public void destroy(SignActionEvent event) {
        super.destroy(event);
        BlockSignal.SIGNALS.remove(BlockSignal.of(event.getLocation(), event.getFacing()));
    }
}
