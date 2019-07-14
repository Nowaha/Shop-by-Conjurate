package conj.UA.api.files;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class SmartFile {
    private String name;
    private String directory;
    private String ext;

    public SmartFile(final String directory, final String name, final String ext) {
        this.name = name;
        this.directory = directory;
        this.ext = ext;
    }

    public File getFile() {
        final File f = new File(this.directory + "/" + this.name + this.ext);
        if (!f.exists()) {
            this.create();
        }
        return f;
    }

    public FileConfiguration getConfig() {
        final File file = this.getFile();
        return YamlConfiguration.loadConfiguration(file);
    }

    public File create() {
        final File f = new File(this.directory + "/" + this.name + this.ext);
        f.getParentFile().mkdirs();
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return f;
    }

    public boolean exists() {
        return new File(this.directory + "/" + this.name + this.ext).exists();
    }

    public File reset() {
        final File f = this.getFile();
        f.delete();
        return this.create();
    }

    public boolean save(final FileConfiguration fc) {
        try {
            fc.save(this.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
