package io.github.lee0701.tcsignals;

import com.bergerkiller.bukkit.tc.cache.RailSignCache;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class BlockSignal {

    private static BlockFace[] DIRECTIONS = {BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH};

    public static List<BlockSignal> SIGNALS = new CopyOnWriteArrayList<>();

    public static BlockSignal of(Location signLocation, BlockFace facing) {
        return SIGNALS.stream()
                .filter(signal -> signal.signLocation.equals(signLocation) && signal.facing.equals(facing))
                .findAny()
                .orElseGet(() -> {
                    BlockSignal newSignal = new BlockSignal(signLocation, facing);
                    BlockSignal.SIGNALS.add(newSignal);
                    newSignal.repopulate();
                    return newSignal;
                });
    }

    public static void repopulateAll() {
        SIGNALS.forEach(BlockSignal::repopulate);
    }

    private final Location signLocation;
    private final BlockFace facing;

    // section starting with this sign.
    private BlockSection section;

    public BlockSignal(Location signLocation, BlockFace facing) {
        this.signLocation = signLocation;
        this.facing = facing;
    }

    public void repopulate() {
        if(this.section == null) {
            this.section = new BlockSection();
            this.section.getBeginSignals().add(this);
            BlockSection.SECTIONS.add(this.section);
        }

        if(signLocation.getBlock().getType() != Material.SIGN
                && signLocation.getBlock().getType() != Material.WALL_SIGN) {
            BlockSignal.SIGNALS.remove(this);
            return;
        }

        Block rails = RailSignCache.getRailsFromSign(signLocation.getBlock());
        if(rails == null) return;

        BlockFace direction = this.facing.getOppositeFace();

        repopulate(rails.getLocation().clone().add(direction.getModX(), direction.getModY(), direction.getModZ()), direction);

    }

    private void repopulate(Location blockLocation, BlockFace currentDirection) {
        if(!isRail(blockLocation.getBlock())) return;
        for(RailSignCache.TrackedSign sign : RailSignCache.getSigns(RailType.REGULAR, blockLocation.getBlock())) {
            SignActionEvent event = new SignActionEvent(sign.signBlock);
            if(!event.isType(SignActionSignal.TYPES)) continue;

            if(event.getFacing().equals(currentDirection.getOppositeFace())) {
                BlockSignal endingSignal = BlockSignal.of(sign.signBlock.getLocation(), currentDirection.getOppositeFace());
                section.getEndSignals().add(endingSignal);
                return;
            }

            if(event.getFacing().equals(currentDirection)) {
                BlockSignal startingSignal = BlockSignal.of(sign.signBlock.getLocation(), currentDirection);
                if(this.section != startingSignal.section) {
                    BlockSection.SECTIONS.remove(startingSignal.section);
                    startingSignal.section = this.section;
                }
                if(!section.getBeginSignals().contains(startingSignal)) {
                    section.getBeginSignals().add(startingSignal);
                }
                return;
            }

        }
        for(BlockFace direction : DIRECTIONS) {
            if(direction.equals(currentDirection.getOppositeFace())) continue;
            repopulate(blockLocation.clone().add(direction.getModX(), direction.getModY(), direction.getModZ()), direction);
        }
    }

    private boolean isRail(Block block) {
        return RailType.getType(block) != RailType.NONE;
    }

    public Location getSignLocation() {
        return signLocation;
    }

    public BlockFace getFacing() {
        return facing;
    }

    public BlockSection getSection() {
        return section;
    }

}
