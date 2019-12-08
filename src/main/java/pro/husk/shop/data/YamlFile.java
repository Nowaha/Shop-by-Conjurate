package pro.husk.shop.data;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class YamlFile {

    private Plugin main;
    private YamlConfiguration config;

    String filename;

    /***
     * Create a new file with a specific file name which will allow you to make custom
     * configuration files to contain any data you would like in the YAML format.
     *
     * @param main The plugin class of this plugin.
     * @param filename The name the file should have, excluding ".yml".
     */
    public YamlFile(Plugin main, String filename) {
        this.main = main;
        this.filename = filename + ".yml";
        reloadConfig();
    }

    /***
     * To make changes to the configuration file you will need to call this method first.
     * You can then continue to apply the changed to the data this returns.
     * Make sure to save the data with saveConfig() once you are done.
     *
     * @return The YamlConfiguration variable of this file
     */
    public YamlConfiguration getConfig() {
        return config;
    }

    /***
     * This will save any changes made to the data through the YamlConfiguration variable.
     * It is good practise to call this whenever you finish modifying data inside of the
     * configuration variable. If you don't save it it will get lost if not saved later.
     *
     * If the file is missing it will first attempt to load a default file from
     * the "resources" folder, and if there is none available it will create a
     * brand new file instead.
     */
    public void saveConfig() {
        try {
            main.saveResource(filename, false);
        } catch (Exception ex) {
            if (!(new File(main.getDataFolder(), filename).exists())) {
                try {
                    new File(main.getDataFolder(), filename).createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            config.save(new File(main.getDataFolder(), filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * This method will re-load the file into the configuration variable.
     * Any changes made to the file will now be applied.
     * This will void any unsaved data (saveConfig()).
     *
     * If the file is missing it will first attempt to load a default file from
     * the "resources" folder, and if there is none available it will create a
     * brand new file instead.
     */
    public void reloadConfig() {
        try {
            main.saveResource(filename, false);
        } catch (Exception ex) {
            if (!(new File(main.getDataFolder(), filename).exists())) {
                try {
                    new File(main.getDataFolder(), filename).createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        config = YamlConfiguration.loadConfiguration(new File(main.getDataFolder(), filename));
    }
}
