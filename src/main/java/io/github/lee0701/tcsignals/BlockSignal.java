package io.github.lee0701.tcsignals;

import com.bergerkiller.bukkit.tc.cache.RailSignCache;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

public class BlockSignal implements ConfigurationSerializable {

    public static List<BlockSignal> SIGNALS = new ArrayList<>();

    public static Optional<BlockSignal> of(Location signLocation, BlockFace facing) {
        return SIGNALS.stream()
                .filter(signal -> signal.signLocation.equals(signLocation) && signal.facing.equals(facing))
                .findAny();
    }

    public static BlockSignal create(Location signLocation, BlockFace facing) {
        BlockSignal signal = new BlockSignal(signLocation, facing);
        SIGNALS.add(signal);
        return signal;
    }

    public static void repopulateAll() {
        SIGNALS.forEach(BlockSignal::repopulate);
    }

    private final Location signLocation;
    private final BlockFace facing;

    // section starting with this sign.
    private BlockSection section;

    private static BlockFace[] DIRECTIONS = {BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH};

    public BlockSignal(Location signLocation, BlockFace facing) {
        this.signLocation = signLocation;
        this.facing = facing;
    }

    public void repopulate() {
        BlockSection.SECTIONS.remove(this.section);
        this.section = new BlockSection();
        this.section.getBeginSignals().add(this);
        BlockSection.SECTIONS.add(this.section);

        Block rails = RailSignCache.getRailsFromSign(signLocation.getBlock());
        BlockFace direction = this.facing;
        if(rails == null) return;

        repopulate(rails.getLocation(), direction.getOppositeFace());

        System.out.println("Begin:");
        for(BlockSignal signal : section.getBeginSignals()) System.out.println(" - " + signal.getSignLocation() + ", " + signal.getFacing());
        System.out.println("End:");
        for(BlockSignal signal : section.getEndSignals()) System.out.println(" - " + signal.getSignLocation() + ", " + signal.getFacing());
    }

    private void repopulate(Location blockLocation, BlockFace currentDirection) {
        if(!isRail(blockLocation.getBlock())) return;
        for(RailSignCache.TrackedSign sign : RailSignCache.getSigns(RailType.REGULAR, blockLocation.getBlock())) {
            SignActionEvent event = new SignActionEvent(sign.signBlock);
            if(!event.isType(SignActionSignal.TYPES)) continue;

            Optional<BlockSignal> ending = BlockSignal.of(sign.signBlock.getLocation(), currentDirection.getOppositeFace());
            ending.ifPresent(endingSignal -> {
                section.getEndSignals().add(endingSignal);
            });

            Optional<BlockSignal> starting = BlockSignal.of(sign.signBlock.getLocation(), currentDirection);
            starting.ifPresent(startingSignal -> {
                if(!section.getBeginSignals().contains(startingSignal)) {
                    section.getBeginSignals().add(startingSignal);
                    BlockSection.SECTIONS.remove(startingSignal.section);
                    startingSignal.section = this.section;
                }
            });

            if(ending.isPresent() || starting.isPresent()) return;

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

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();
        result.put("sign-location", signLocation);
        result.put("facing", facing.name());
        return result;
    }

    public static BlockSignal deserialize(Map<String, Object> map) {
        Location signLocation = (Location) map.get("sign-location");
        BlockFace facing = BlockFace.valueOf((String) map.get("facing"));
        BlockSignal signal = BlockSignal.create(signLocation, facing);
        return signal;
    }

}
