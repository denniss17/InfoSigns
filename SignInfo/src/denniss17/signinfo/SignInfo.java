package denniss17.signinfo;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import denniss17.signinfo.listeners.SignListener;
import denniss17.signinfo.signs.OnlinePlayersInfoSign;
import denniss17.signinfo.signs.PlayersInfoSign;
import denniss17.signinfo.signs.TimeInfoSign;
import denniss17.signinfo.utils.Messager;

public class SignInfo extends JavaPlugin {
	public static SignInfo instance;
	public static SignManager signManager;
	public static LayoutManager layoutManager;
	
	private Map<String, Class<? extends InfoSign>> infoSignTypes;
	
	@Override
	public void onEnable(){
		// Set variables
		instance = this;
		infoSignTypes = new HashMap<String, Class<? extends InfoSign>>();
		signManager = new SignManager();
		layoutManager = new LayoutManager();
		
		// Check addons directory
		File addonsfolder = new File(getDataFolder(), AddonManager.addonFolder);
		if(!addonsfolder.exists()){
			addonsfolder.mkdir();
		}
		
		// Load addons
		int count = AddonManager.loadAddons();
		SignInfo.instance.getLogger().info("Loaded signs from addons: " + count);
		
		// Add basic signtypes
		addCoreInfoSigns();
		
		// Load all currently existing InfoSigns and get them running
		signManager.loadInfoSigns();
		
		// Register listener
		this.getServer().getPluginManager().registerEvents(new SignListener(), this);
		
		// Save config
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	
	private void addCoreInfoSigns() {
		infoSignTypes.put("time", TimeInfoSign.class);
		infoSignTypes.put("online", OnlinePlayersInfoSign.class);
		infoSignTypes.put("players", PlayersInfoSign.class);
		
		// Check layouts of basic signstypes
		if(!layoutManager.exists("online", "default")){
			layoutManager.setLayout("online", "default", "[&9SignInfo&r]", "Online Players:", null, "{count}/{max}");
		}
		if(!layoutManager.exists("online", "world")){
			layoutManager.setLayout("online", "world", "[&9SignInfo&r]", "Online Players:", "{world}", "{count}");
		}
		if(!layoutManager.exists("time", "default")){
			layoutManager.setLayout("time", "default", "[&9SignInfo&r]", "Time:", "{time}", null);
		}
	}

	public Map<String, Class<? extends InfoSign>> getInfoSignTypes(){
		return infoSignTypes;
	}
	
	public void addInfoSignType(String signtype, Class<? extends InfoSign> clazz) {
		infoSignTypes.put(signtype, clazz);		
	}

	public void registerListener(Listener listener){
		this.getServer().getPluginManager().registerEvents(listener, this);
	}
	
	public void unregisterListener(Listener listener) {
		// TODO Auto-generated method stub
	}

	/**
	 * Try to make an InfoSign of the given sign, of the given type
	 * @param sign The sign on which the info should be displayed
	 * @param type The type of the sign
	 * @param arg1 Argument passed to the constructor of the InfoSign
	 * @param arg2 Argument passed to the constructor of the InfoSign
	 * @return An InfoSignBase instance
	 */
	public InfoSign createNewSign(Sign sign, String type, String arg1, String arg2){
		//getLogger().info("Trying to create sign " + type + " (" + arg1 + "," + arg2 + ")");
		// Get class
		Class<? extends InfoSign> signClass = infoSignTypes.get(type);
		
		if(signClass==null){
			getLogger().info("E: Type not existing: " + type);
			return null;
		}
		
		boolean isMultiSign = signClass.getSuperclass().equals(InfoMultiSign.class);
		// Find other signs if it is an InfoMultiSign
		Sign[][] signs = null;
		if(isMultiSign){
			signs = extendToMultiSign(sign);
		}
		
		
		Constructor<? extends InfoSign> constructor = null;
		
		// 1. Find constructor
		try {
			if(isMultiSign){
				constructor = signClass.getConstructor(Sign[][].class, String.class, String.class, String.class);
			}else{
				constructor = signClass.getConstructor(Sign.class, String.class, String.class, String.class);
			}
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(constructor==null){
			getLogger().info("E: Constructor not found");
			return null;
		}
		
		// 2. Call constructor
		try {
			InfoSign infoSign;
			if(isMultiSign){
				infoSign = constructor.newInstance(signs, type, arg1, arg2);
			}else{
				infoSign = constructor.newInstance(sign,  type, arg1, arg2);
			}
			return infoSign;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		getLogger().info("E: Unable to make instance");
		return null;
	}
	
	private Sign[][] extendToMultiSign(Sign sign) {
		org.bukkit.material.Sign signMaterial = (org.bukkit.material.Sign)sign.getData();
		Block signBlock = sign.getBlock();
		
		BlockFace facing = signMaterial.getFacing();
		
		// First go down untill block is no sign anymore
		
		
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandlabel, String[] args) {
		String types = "Available types: &7";
		for(String type : infoSignTypes.keySet()){
			types += type + ", ";
		}
		Messager.send(sender, types);
		
		
		return true;		
	}
}
