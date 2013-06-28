package denniss17.signinfo;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LayoutManager {
	private FileConfiguration config = null;
	private File configFile = null;
	
	public String[] getLayout(String type){
		return getLayout(type, "main");
	}
	
	public String[] getLayout(String type, String subtype){		
		String[] result = new String[4];
		String path = type + "." + subtype + ".";
		result[0] = get().getString(path+"0");
		result[1] = get().getString(path+"1");
		result[2] = get().getString(path+"2");
		result[3] = get().getString(path+"3");
		return result;
	}

	public void setLayout(String signtype, String subtype, String line0, String line1, String line2, String line3) {
		String path = signtype + "." + subtype + ".";
		get().set(path+"0", line0);
		get().set(path+"1", line1);
		get().set(path+"2", line2);
		get().set(path+"3", line3);
		save();		
	}

	public boolean exists(String signtype, String subtype) {
		return get().contains(signtype + "." + subtype);
	}

	protected void reload() {
		if (configFile == null) {
			configFile = new File(SignInfo.instance.getDataFolder(), "layouts.yml");
		}
		config = YamlConfiguration.loadConfiguration(configFile);
	}

	protected void save() {
		if (config == null || configFile == null) {
			return;
		}
		try {
			config.save(configFile);
		} catch (IOException ex) {
			SignInfo.instance.getLogger().log(Level.SEVERE,
					"Could not save config to " + configFile, ex);
		}
	}

	protected MemorySection get() {
		if (config == null) {
			reload();
		}
		return config;
	}
}
