package denniss17.signinfo;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import denniss17.signinfo.listeners.SignListener;
import denniss17.signinfo.signs.OnlinePlayersInfoSign;
import denniss17.signinfo.signs.TimeInfoSign;
import denniss17.signinfo.utils.Messager;

public class SignInfo extends JavaPlugin {
	public static SignInfo instance;
	public static SignManager signManager;
	public static LayoutManager layoutManager;
	
	private Map<String, Class<? extends InfoSignBase>> infoSignTypes;
	
	@Override
	public void onEnable(){
		// Set variables
		instance = this;
		infoSignTypes = new HashMap<String, Class<? extends InfoSignBase>>();
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
		infoSignTypes.put("time", TimeInfoSign.class);
		infoSignTypes.put("players", OnlinePlayersInfoSign.class);
		
		// Check layouts of basic signstypes
		if(!layoutManager.exists("players", "default")){
			layoutManager.setLayout("players", "default", "&8[&9SignInfo&8]", "Online Players:", null, "{count}/{max}");
		}
		if(!layoutManager.exists("players", "world")){
			layoutManager.setLayout("players", "world", "&8[&9SignInfo&8]", "Online Players:", "{world}", "{count}");
		}
		if(!layoutManager.exists("time", "default")){
			layoutManager.setLayout("time", "default", "&8[&9SignInfo&8]", "Time:", "{time}", null);
		}
		
		// Load all currently existing InfoSigns and get them running
		signManager.loadInfoSigns();
		
		// Register listener
		this.getServer().getPluginManager().registerEvents(new SignListener(), this);
		
		// Save config
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	
	public Map<String, Class<? extends InfoSignBase>> getInfoSignTypes(){
		return infoSignTypes;
	}
	
	public void addInfoSignType(String signtype, Class<? extends InfoSignBase> clazz) {
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
	public InfoSignBase createNewSign(Sign sign, String type, String arg1, String arg2){
		//getLogger().info("Trying to create sign " + type + " (" + arg1 + "," + arg2 + ")");
		// Get class
		Class<? extends InfoSignBase> signClass = infoSignTypes.get(type);
		
		if(signClass==null){
			getLogger().info("E: Type not existing: " + type);
			return null;
		}
		
		// Get constructor
		Constructor<? extends InfoSignBase> constructor = null;
		try {
			constructor = signClass.getConstructor(Sign.class, String.class, String.class, String.class);
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
		
		// Call constructor
		try {
			InfoSignBase infoSign = constructor.newInstance(sign, type, arg1, arg2);
			//getLogger().info("Success!");
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
