package io.github.lee0701.tcsignals;

import com.bergerkiller.bukkit.tc.signactions.SignAction;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class TCSignalsPlugin extends JavaPlugin {

    static {
        ConfigurationSerialization.registerClass(BlockSignal.class, "BlockSignal");
    }

    public static TCSignalsPlugin getInstance() {
        return getPlugin(TCSignalsPlugin.class);
    }

    private File dataFile = new File(getDataFolder(), "data.yml");
    private YamlConfiguration dataConfiguration;

    private void reload() {
//        dataConfiguration = YamlConfiguration.loadConfiguration(dataFile);
//        if(dataConfiguration.isList("signals")) dataConfiguration.getList("signals");
        BlockSignal.repopulateAll();
    }

    private void save() {
//        try {
//            dataConfiguration.set("signals", BlockSignal.SIGNALS);
//            dataConfiguration.save(dataFile);
//        } catch(IOException ex) {
//            ex.printStackTrace();
//        }
    }

    @Override
    public void onEnable() {
        getDataFolder().mkdirs();

        reload();

        getCommand("trainsignal").setExecutor(new CommandTrainSignal());

        SignAction.register(new SignActionSignal());

    }

    @Override
    public void onDisable() {
        save();
    }

}
