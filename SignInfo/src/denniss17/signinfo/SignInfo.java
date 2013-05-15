package denniss17.signinfo;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.Sign;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import denniss17.signinfo.listeners.SignListener;
import denniss17.signinfo.signs.OnlinePlayersInfoSign;
import denniss17.signinfo.signs.TimeInfoSign;

public class SignInfo extends JavaPlugin {
	public static SignInfo instance;
	public static SignManager manager;
	public static LayoutManager layoutManager;
	
	private Map<String, Class<? extends InfoSignBase>> infoSignTypes;
	
	@Override
	public void onEnable(){
		// Set variables
		instance = this;
		infoSignTypes = new HashMap<String, Class<? extends InfoSignBase>>();
		manager = new SignManager();
		layoutManager = new LayoutManager();
		
		File addonsfolder = new File(getDataFolder(), "addons");
		if(!addonsfolder.exists()){
			addonsfolder.mkdir();
		}		
		
		// Add basic signtypes
		infoSignTypes.put("time", TimeInfoSign.class);
		infoSignTypes.put("players", OnlinePlayersInfoSign.class);
		
		manager.loadInfoSigns();
		
		
		
		// Listeners
		this.getServer().getPluginManager().registerEvents(new SignListener(), this);
		
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	
	public Map<String, Class<? extends InfoSignBase>> getInfoSignTypes(){
		return infoSignTypes;
	}
	
	public void registerListener(Listener listener){
		this.getServer().getPluginManager().registerEvents(listener, this);
	}
	
	public void unregisterListener(Listener listener) {
		// TODO Auto-generated method stub
	}

	public InfoSignBase createNewSign(Sign sign, String type, String arg1, String arg2){
		getLogger().info("Trying to create sign " + type + " (" + arg1 + "," + arg2 + ")");
		
		Class<? extends InfoSignBase> signClass = infoSignTypes.get(type);
		
		if(signClass==null){
			getLogger().info("E: Type not existing");
			return null;
		}
		
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
		
		try {
			InfoSignBase infoSign = constructor.newInstance(sign, type, arg1, arg2);
			getLogger().info("Success!");
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
}
