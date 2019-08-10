package io.github.lee0701.tcsignals;

import com.bergerkiller.bukkit.tc.signactions.SignAction;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class TCSignalsPlugin extends JavaPlugin {

    public static TCSignalsPlugin getInstance() {
        return getPlugin(TCSignalsPlugin.class);
    }

    private File dataFile = new File(getDataFolder(), "data.yml");
    private YamlConfiguration dataConfiguration;

    private void reload() {
        dataConfiguration = YamlConfiguration.loadConfiguration(dataFile);
    }

    private void save() {
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
