package denniss17.signinfo;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
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
	
	public void checkLayout()
	
	/*public void loadLayoutFromClasses(){
		 Map<String, Class<? extends InfoSignBase>> infoSignTypes = SignInfo.instance.getInfoSignTypes();
		 
		 for(Entry<String, Class<? extends InfoSignBase>> entry : infoSignTypes.entrySet()){
			 
			 
			 try {
				Method method = infoSign.getMethod("getDefaultLayouts");
				try {
					Object result = method.invoke(null, (Object[])null);
					if(result instanceof Map<?, ?>){
						
					}
					
					Map<String, String[]> defaultLayout = (Map<String, String[]>)result;
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
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 
	}*/
	
	protected void reload() {
		if (configFile == null) {
			configFile = new File(SignInfo.instance.getDataFolder(), "layout.yml");
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
