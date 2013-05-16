package denniss17.signinfo;

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

public class AddonLoader {
	public static final String addonFolder = "addons";
	
	class jarFilter implements FilenameFilter{
		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(".jar");
		}
		
	}
	
	public void test(){
		int count = loadAddons();
		SignInfo.instance.getLogger().info("Loaded signs: " + count);
	}
	
	@SuppressWarnings("unchecked")
	public int loadAddons(){
		int count = 0;
		Logger logger = SignInfo.instance.getLogger();
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
					SignInfo.instance.getLogger().warning("addon.yml not found in " + file.getName());
					continue;
				}
				InputStream inputStream = jarFile.getInputStream(addonYAML);
				YamlConfiguration addonConfiguration = YamlConfiguration.loadConfiguration(inputStream);
				
				// Initialize ClassLoader
				URLClassLoader loader = new URLClassLoader(new URL[] {file.toURI().toURL()} , SignInfo.class.getClassLoader());
				
				// Load signtypes and classes
				for(String signtype : addonConfiguration.getKeys(false)){
					String classpath = addonConfiguration.getString(signtype + ".class");
					if(classpath==null){
						logger.warning("Signtype " + signtype + " didn't specify a class! (addon: " + file.getName() + ")");
						continue;
					}
					Class<?> clazz = loader.loadClass(classpath);
					if(clazz.getSuperclass().equals(InfoSignBase.class)){
						// Success!
						SignInfo.instance.addInfoSignType(signtype, (Class<? extends InfoSignBase>) clazz);
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
	
	private void checkLayouts(YamlConfiguration addonConfiguration, String signtype) {
		for(String key : addonConfiguration.getConfigurationSection(signtype + ".layouts").getKeys(false)){
			if(!SignInfo.layoutManager.exists(signtype, key)){
				String line0 = addonConfiguration.getString(signtype + ".layouts." + key + ".0");
				String line1 = addonConfiguration.getString(signtype + ".layouts." + key + ".1");
				String line2 = addonConfiguration.getString(signtype + ".layouts." + key + ".2");
				String line3 = addonConfiguration.getString(signtype + ".layouts." + key + ".3");
				SignInfo.layoutManager.setLayout(signtype, key, line0, line1, line2, line3);
			}
		}
			
			
	}

	public File[] listAddons(){
		File addonsDir = new File(SignInfo.instance.getDataFolder(), AddonLoader.addonFolder);
		File[] files = addonsDir.listFiles(new jarFilter());
		return files;
	}
	
}
