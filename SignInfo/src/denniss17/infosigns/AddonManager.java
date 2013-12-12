package denniss17.infosigns;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

import org.bukkit.configuration.file.YamlConfiguration;

public class AddonManager {
	/**
	 * The path of the folder, relative to plugin.getDataFolder(), where the addons
	 * are stored
	 */
	public static final String addonFolder = "addons";
	
	/**
	 * Class used to filter the jar files in the addons folder
	 */
	private static class jarFilter implements FilenameFilter{
		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(".jar");
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public static int loadAddons(){
		int count = 0;
		Logger logger = InfoSigns.instance.getLogger();
		File[] addonFiles = listAddons();
		
		
		// Look to every .jar file in the addons folder
		for(File file : addonFiles){
			
			JarFile jarFile = null;
			try {
				// Load file as JarFile
				jarFile = new JarFile(file);
				
				// Open addon.yml inside the jar
				ZipEntry addonYAML = jarFile.getEntry("addon.yml");
				if(addonYAML == null){
					InfoSigns.instance.getLogger().warning("addon.yml not found in " + file.getName());
					continue;
				}
				InputStream inputStream = jarFile.getInputStream(addonYAML);
				YamlConfiguration addonConfiguration = YamlConfiguration.loadConfiguration(inputStream);
				
				// Check addon.yml
				if(!checkAddonConfiguration(addonConfiguration)){
					InfoSigns.instance.getLogger().warning("addon.yml in " + file.getName() + " is not correct");
					continue;
				}
				
				// Initialize ClassLoader
				URLClassLoader loader = new URLClassLoader(new URL[] {file.toURI().toURL()} , InfoSigns.class.getClassLoader());
				
				// Load signtypes and classes
				for(String signtype : addonConfiguration.getKeys(false)){
					// 1. Get the path of the class
					String classpath = addonConfiguration.getString(signtype + ".class");
					if(classpath==null){
						logger.warning("Signtype " + signtype + " didn't specify a class! (addon: " + file.getName() + ")");
						continue;
					}
					
					// 2. Load the class
					Class<?> clazz = loader.loadClass(classpath);
					if(clazz!=null && clazz.getSuperclass().equals(InfoSign.class)){
						// Success!
						// 3. Add the sign to the list of signs
						InfoSigns.instance.addInfoSignType(signtype, (Class<? extends InfoSign>) clazz);
						// Check if layout is in layouts.yml
						checkLayouts(addonConfiguration, signtype);	
						count++;									
					}else{
						logger.warning("Class " + clazz.getSimpleName() + " doesn't extend the right class (addon: " + file.getName() + ")");
					}
					
					
				}
				
				// Cleanup
				if (System.getProperty("java.version").startsWith("1.7")) {
					// This method exists since 1.7
					loader.close();
				}
				inputStream.close();
				jarFile.close();
				
			} catch(MalformedURLException e){
				logger.warning("Something went wrong on loading " + file.getName() + " (MalformedURL exception)");
				continue;
			} catch (IOException e) {
				logger.warning("Something went wrong on loading " + file.getName() + " (I/O exception)");
				continue;
			} catch (ClassNotFoundException e) {
				logger.warning("One of the classes as specified in addon.yml of " + file.getName() + " doesn't exists!");
				logger.warning("Details: " + e.getMessage());
				continue;
			}
		}
		
		return count;
	}
	
	/**
	 * Looks in the addons folder and searches for all jars in this directory
	 * @return A list of .jar files in the addons folder
	 */
	private static File[] listAddons(){
		File addonsDir = new File(InfoSigns.instance.getDataFolder(), AddonManager.addonFolder);
		File[] files = addonsDir.listFiles(new jarFilter());
		return files==null ? new File[0] : files;
	}

	private static boolean checkAddonConfiguration(YamlConfiguration addonConfiguration) {
		// TODO add check if addon.yml is correct
		return true;
	}

	/**
	 * Checks if the layout of the addon is in the main layouts.yml file
	 * If not, it is added to it, so the user can configure the layout of these signs
	 * @param addonConfiguration The addon.yml file
	 * @param signtype The type of the sign
	 */
	private static void checkLayouts(YamlConfiguration addonConfiguration, String signtype) {
		for(String key : addonConfiguration.getConfigurationSection(signtype + ".layouts").getKeys(false)){
			if(!InfoSigns.layoutManager.exists(signtype, key)){
				// Layout is not in layouts.yml => add it
				String line0 = addonConfiguration.getString(signtype + ".layouts." + key + ".0");
				String line1 = addonConfiguration.getString(signtype + ".layouts." + key + ".1");
				String line2 = addonConfiguration.getString(signtype + ".layouts." + key + ".2");
				String line3 = addonConfiguration.getString(signtype + ".layouts." + key + ".3");
				InfoSigns.layoutManager.setLayout(signtype, key, line0, line1, line2, line3);
			}
		}
			
			
	}
	
}
