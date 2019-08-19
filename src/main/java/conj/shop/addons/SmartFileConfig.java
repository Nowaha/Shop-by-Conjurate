package conj.shop.addons;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public class SmartFileConfig {
    private SmartFile sf;

    public SmartFileConfig(final SmartFile sf) {
        this.sf = sf;
    }

    public void overrideData() {
        this.sf.reset();
        try {
            this.sf.getConfig().save(this.sf.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveData() {
        try {
            this.sf.getConfig().save(this.sf.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getCategories() {
        return this.sf.getConfig().getConfigurationSection("").getKeys(false);
    }

    public Object getValue(final String path) {
        final Iterator<String> iterator = this.getCategories().iterator();
        if (iterator.hasNext()) {
            final String s = iterator.next();
            return this.sf.getConfig().get(s + "." + path);
        }
        return null;
    }
}
