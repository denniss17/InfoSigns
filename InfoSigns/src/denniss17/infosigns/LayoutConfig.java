package denniss17.infosigns;

import org.bukkit.configuration.ConfigurationSection;

/**
 * This class contains the layout for a single InfoSign
 * If a layout override exists in signs.yml, the values of this are used,
 * other values are fetched from layouts.yml
 * @author Denniss17
 */
public class LayoutConfig {
	private ConfigurationSection config;
	private InfoSign infoSign;
	
	protected LayoutConfig(InfoSign infoSign, ConfigurationSection config){
		this.infoSign = infoSign;
		this.config = config;
	}
	
	/**
	 * Get the lines from the config, used for the sign
	 * This is basicly a shortcut for getConfig().getString("0" till "3"), returned
	 * as an array
	 * @return String[] of length 4
	 */
	public String[] getLines(){
		String[] result = new String[4];
		result[0] = getConfig().getString("0");
		result[1] = getConfig().getString("1");
		result[2] = getConfig().getString("2");
		result[3] = getConfig().getString("3");
		return result;
	}
	
	/**
	 * Get the layoutconfiguration for this particular InfoSign
	 * Setting values in this config will override the default layout and will
	 * thus be saved in signs.yml. If you wish to change the default layout, use
	 * LayoutManager.getDefaultLayout() instead
	 * @return The config for this particular InfoSign
	 */
	public ConfigurationSection getConfig(){
		return config;
	}
	
	/**
	 * Returns the InfoSign this layout is for
	 * @return the InfoSign
	 */
	public InfoSign getInfoSign(){
		return infoSign;
	}

	/**
	 * Save the config (or any other data) for this InfoSign
	 */
	public void save(){
		infoSign.save();
	}
}
