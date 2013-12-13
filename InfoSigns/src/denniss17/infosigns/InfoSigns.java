package denniss17.infosigns;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import denniss17.infosigns.listeners.SignListener;
import denniss17.infosigns.signs.MultiLineTestInfoSign;
import denniss17.infosigns.signs.OnlineInfoSign;
import denniss17.infosigns.signs.PlayersInfoSign;
import denniss17.infosigns.signs.TimeInfoSign;
import denniss17.infosigns.utils.Messager;

public class InfoSigns extends JavaPlugin {
	public static InfoSigns instance;
	public static SignManager signManager;
	public static LayoutManager layoutManager;
	
	public static Economy economy;
	public static Permission permission;
	public static Chat chat;
	
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
		getLogger().info("Loaded signs from addons: " + count);
		
		// Add basic signtypes
		addCoreInfoSigns();
		
		// Check Vault
		if(isVaultEnabled()){
			if(!loadEconomy()) 		getLogger().info("Economy plugin not found. Some signs may be disabled");
			if(!loadPermission()) 	getLogger().info("Permission plugin not found. Some signs may be disabled");
			if(!loadChat()) 		getLogger().info("Chat plugin not found. Some signs may be disabled");
		}else{
			getLogger().info("Vault not found. Some signs may be disabled");
		}
		
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
		infoSignTypes.put("online", OnlineInfoSign.class);
		infoSignTypes.put("players", PlayersInfoSign.class);
		infoSignTypes.put("multitest", MultiLineTestInfoSign.class);
		
		// Check layouts of basic signstypes
		if(!layoutManager.exists("online", "default")){
			layoutManager.setLayout("online", "default", "[&9InfoSign&r]", "Online Players:", null, "{count}/{max}");
		}
		if(!layoutManager.exists("online", "world")){
			layoutManager.setLayout("online", "world", "[&9InfoSign&r]", "Online Players:", "{world}", "{count}");
		}
		if(!layoutManager.exists("time", "default")){
			layoutManager.setLayout("time", "default", "[&9InfoSign&r]", "Time:", "{time}", null);
		}
	}
	
	public boolean isVaultEnabled(){
		return getServer().getPluginManager().getPlugin("Vault")!=null;
	}
	
	/** Load the economy via Vault */
	private boolean loadEconomy(){
		RegisteredServiceProvider<Economy> provider = 
				getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (provider != null) {
			economy = provider.getProvider();
		}		
		return (economy != null);
	}
	
	/** Load the permission via Vault */
	private boolean loadPermission(){
		RegisteredServiceProvider<Permission> provider = 
				getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (provider != null) {
			permission = provider.getProvider();
		}		
		return (permission != null);
	}
	
	/** Load the chat via Vault */
	private boolean loadChat(){
		RegisteredServiceProvider<Chat> provider = 
				getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
		if (provider != null) {
			chat = provider.getProvider();
		}		
		return (chat != null);
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
		//Block signBlock = sign.getBlock();
		
		BlockFace facing = signMaterial.getFacing();
		BlockFace right = null;
		if(facing.equals(BlockFace.NORTH)) 	right=BlockFace.WEST;
		if(facing.equals(BlockFace.WEST)) 	right=BlockFace.SOUTH;
		if(facing.equals(BlockFace.SOUTH)) 	right=BlockFace.EAST;
		if(facing.equals(BlockFace.EAST)) 	right=BlockFace.NORTH;
		
		this.getLogger().info("Right of sign = " + right);
		
		List<List<Sign>> signs = new ArrayList<List<Sign>>();
		
		List<Sign> column;
		Block other;
		
		// First go down untill block is no sign anymore
		column = getSignColumn(sign);
		signs.add(column);
		
		// Set vars
		int columnHeight = column.size();
		int columnIndex = 1;
		boolean isValid = true;
		
		// Go to the right until there are not enough signs anymore
		while(isValid){
			other = sign.getBlock().getRelative(right, columnIndex);
			if(other!=null && other.getType().equals(Material.WALL_SIGN)){
				column = getSignColumn((Sign) other.getState());
			}else{
				column = null;
			}
			if(column==null || column.size()<columnHeight){
				isValid = false;
			}else{
				signs.add(column.subList(0, columnHeight));
				columnIndex++;
			}
		}
		
		Sign[][] result = new Sign[signs.size()][signs.get(0).size()];
		for(int i=0; i<signs.size(); i++){
			signs.get(i).toArray(result[i]);
		}
		return result;
	}

	private List<Sign> getSignColumn(Sign sign) {
		List<Sign> result = new ArrayList<Sign>();
		
		result.add(sign);
		
		Block other = sign.getBlock();
		boolean isValid = true;
		while(isValid){
			other = other.getRelative(BlockFace.DOWN);
			if(other.getType().equals(Material.WALL_SIGN)){
				result.add((Sign)other.getState());
			}else{
				isValid = false;
			}
				
		}
		
		return result;
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
