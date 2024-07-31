package eu.sengy.blockjail.utils;

import eu.sengy.blockjail.JailBungee;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class Config {

    private static ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
    private static File file = new File(JailBungee.getInstance().getDataFolder(), "config_bungee.yml");
    private static Configuration config;

    public static void save(){
        try {
            provider.save(config, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Configuration getConfig(){
        return config;
    }

    public static void load() {

        try{

            if (!file.exists()) {
                Files.createDirectories(JailBungee.getInstance().getDataFolder().toPath());

                try (InputStream in = JailBungee.getInstance().getResourceAsStream("config_bungee.yml")) {
                    Files.copy(in, file.toPath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            config = provider.load(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
