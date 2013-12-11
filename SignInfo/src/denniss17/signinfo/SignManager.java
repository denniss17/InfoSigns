package denniss17.signinfo;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public class SignManager {
	private FileConfiguration config = null;
	private File configFile = null;
	
	private Map<Integer, InfoSign> infoSigns;
	
	public SignManager(){
		this.infoSigns = new HashMap<Integer, InfoSign>();
	}

	public void addInfoSign(InfoSign infoSign) {
		if(infoSign.id == 0) infoSign.id = getNextId();
		infoSigns.put(infoSign.id, infoSign);		
	}

	public void removeInfoSign(InfoSign infoSign) {
		infoSign.destroy();
		get().set(String.valueOf(infoSign.id), null);
		save();
	}

	public InfoSign getInfoSign(int id){
		return infoSigns.get(id);
	}

	public InfoSign getInfoSign(Sign signBlock) {
		for(InfoSign infoSign : infoSigns.values()){
			if(infoSign.getSign().getLocation().equals(signBlock.getLocation())) return infoSign;
		}
		return null;
	}

	public void loadInfoSigns(){
		for(String key : get().getKeys(false)){
			try{
				int id = Integer.parseInt(key);
				loadInfoSign(id);
			}catch(NumberFormatException e){
				SignInfo.instance.getLogger().warning(key + " is not a valid InfoSign id (not a number)");
			}
		}
		SignInfo.instance.getLogger().info(infoSigns.size() + " InfoSigns loaded");
	}
	
	public String[] getLayoutOverride(int id){
		if(get().contains(id + ".layout")){
			String[] result = new String[4];
			String path = id + ".layout.";
			result[0] = get().getString(path+"0");
			result[1] = get().getString(path+"1");
			result[2] = get().getString(path+"2");
			result[3] = get().getString(path+"3");
			return result;
		}
		return null;
	}

	private void loadInfoSign(int id){
		Sign sign = stringToSign(get().getString(id + ".sign"), SignInfo.instance.getServer());
		if(sign==null){
			SignInfo.instance.getLogger().warning("The sign of InfoSign " + id + " does not exist!");
			return;
		}
		String type = get().getString(id + ".type");
		String arg1 = get().getString(id + ".arg1");
		String arg2 = get().getString(id + ".arg2");
		Map<String, Object> data = null;
		if(get().contains(id + ".data")){
			data = get().getConfigurationSection(id + ".data").getValues(false);
		}
		
		InfoSign infoSign = SignInfo.instance.createNewSign(sign, type, arg1, arg2);
		
		if(infoSign!=null){
			infoSign.id = id;
			if(data!=null){
				infoSign.setData(data);
			}
			infoSign.initialize();
			infoSigns.put(id, infoSign);
		}else{
			SignInfo.instance.getLogger().warning("InfoSign " + id + " could not be instantiated!");
		}
	}

	public void saveInfoSign(InfoSign signInfoSign) {
		if(signInfoSign.id ==0) signInfoSign.id = getNextId();
		int id = signInfoSign.id;
		get().set(id + ".sign", signToString(signInfoSign.getSign()));
		get().set(id + ".type", signInfoSign.getType());
		get().set(id + ".arg1", signInfoSign.getFirstArgument());
		get().set(id + ".arg2", signInfoSign.getSecondArgument());
		get().set(id + ".data", signInfoSign.getData());
		save();
	}

	private int getNextId(){
		int id = 1;
		while(get().contains(String.valueOf(id))){
			id++;
		}
		return id;
	}
	
	public static String signToString(Sign sign) {
		return locationToString(sign.getLocation());
	}
	
	public static Sign stringToSign(String save, Server server){
		Location location = stringToLocation(save, server);
		if(location==null) return null;
		if(location.getBlock().getType().equals(Material.SIGN)
				||location.getBlock().getType().equals(Material.WALL_SIGN)
				||location.getBlock().getType().equals(Material.SIGN_POST)){
			return (Sign) location.getBlock().getState();
		}
		return null;
	}
	
	/** 
	 * Serialize the given location to a String
	 * @param location The location to serialize
	 * @return The serialized location
	 */
	public static String locationToString(Location location) {
		if(location==null) return "null";
		World world = location.getWorld();
		return world.getName() + ";" + location.getBlockX() + ";"
				+ location.getBlockY() + ";" + location.getBlockZ();
	}
	
	/**
	 * Deserialize the string back to a location in the given server
	 * @param safe The string to deserialize
	 * @param server The server to find the location in
	 * @return The resulting Location or null if String is not right or World does not exist
	 */
	public static Location stringToLocation(String save, Server server){
		if(save==null || save.equals("null")) return null;
		try {
			String[] vars = save.split(";");
			if (vars.length != 4) {
				return null;
			}
			World world = server.getWorld(vars[0]);
			if(world==null) return null;
			int x = Integer.parseInt(vars[1]);
			int y = Integer.parseInt(vars[2]);
			int z = Integer.parseInt(vars[3]);
			return new Location(world, x, y, z);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	protected void reload() {
		if (configFile == null) {
			configFile = new File(SignInfo.instance.getDataFolder(), "signs.yml");
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
