package denniss17.infosigns;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LayoutManager {
	private final static String FILENAME = "layouts.yml";
	private FileConfiguration config = null;
	private File configFile = null;
	/**
	 * Get the layout of this InfoSign and its subtype
	 * This is done by first checking if this sign has a layout override
	 * If not, the layout is loaded from layouts.yml
	 * @param infoSign The InfoSign to get the layout for
	 * @param subtype The layout subtype
	 * @return String[] of length 4
	 */
	public String[] getLayout(InfoSign infoSign){
		if(InfoSigns.signManager.getLayoutOverride(infoSign.getId())!=null){
			return InfoSigns.signManager.getLayoutOverride(infoSign.getId());
		}
		return getLayout(infoSign.getType(), infoSign.getSubtype());
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
	
	public void setLayout(InfoSign infoSign, String line0, String line1, String line2, String line3) {
		setLayout(infoSign.getType(), infoSign.getSubtype(), line0, line1, line2, line3);
	}

	/**
	 * Set the layout for this type of sign and this subtype to the given lines (in layouts.yml)
	 * @param signtype The signtype
	 * @param subtype The subtype
	 * @param line0 The first line of the layout, could be null
	 * @param line1 The second line of the layout, could be null
	 * @param line2 The third line of the layout, could be null
	 * @param line3 The fourth line of the layout, could be null
	 */
	public void setLayout(String signtype, String subtype, String line0, String line1, String line2, String line3) {
		 String path = signtype + "." + subtype + ".";
         get().set(path+"0", line0);
         get().set(path+"1", line1);
         get().set(path+"2", line2);
         get().set(path+"3", line3);
         saveConfig();	
	}
	
	/**
	 * Get the ConfigurationSection containing the layout for this sign
	 * If this particular sign has an layout override, that one is returned instead
	 * This makes is possible to add custom key/value config items for a layout
	 * @param signtype
	 * @param subtype
	 * @return
	 */
	public ConfigurationSection getLayoutConfig(InfoSign infoSign){
		if(InfoSigns.signManager.hasLayoutOverride(infoSign.getId())){
			return InfoSigns.signManager.getLayoutConfigOverride(infoSign.getId());
		}
		return getLayoutConfig(infoSign.getType(), infoSign.getSubtype());
	}
	
	/**
	 * Get the ConfigurationSection containing the layout for this sign and this subtype
	 * This makes is possible to add custom key/value config items for a layout
	 * @param signtype
	 * @param subtype
	 * @return
	 */
	public ConfigurationSection getLayoutConfig(String signtype, String subtype){
		String path = signtype + "." + subtype;
		return get().getConfigurationSection(path);
	}

	/**
	 * Check if a layout for this signtype and subtype exists in layouts.yml
	 * @param signtype
	 * @param subtype
	 * @return true if it exists, false otherwise
	 */
	public boolean exists(String signtype, String subtype) {
		return get().contains(signtype + "." + subtype);
	}

	/**
	 * Reload the configuration from layouts.yml
	 */
	protected void reload() {
		if (configFile == null) {
			configFile = new File(InfoSigns.instance.getDataFolder(), FILENAME);
		}
		config = YamlConfiguration.loadConfiguration(configFile);
	}

	/**
	 * Save the config to layouts.yml
	 */
	public void saveConfig() {
		if (config == null || configFile == null) {
			return;
		}
		try {
			config.save(configFile);
		} catch (IOException ex) {
			InfoSigns.instance.getLogger().log(Level.SEVERE,
					"Could not save config to " + configFile, ex);
		}
	}

	/**
	 * Get the config from layouts.yml
	 * @return The config from layouts.yml
	 */
	protected MemorySection get() {
		if (config == null) {
			reload();
		}
		return config;
	}
}
