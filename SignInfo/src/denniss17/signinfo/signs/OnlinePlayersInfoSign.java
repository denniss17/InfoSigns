package denniss17.signinfo.signs;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import denniss17.signinfo.SignInfo;
import denniss17.signinfo.InfoSignBase;

public class OnlinePlayersInfoSign extends InfoSignBase implements Listener {

	public OnlinePlayersInfoSign(Sign sign, String type, String arg1, String arg2) {
		super(sign, type, arg1, arg2);
	}

	@Override
	public void updateSign() {
		if(arg1!=null){
			World world = SignInfo.instance.getServer().getWorld(arg1);
			sign.setLine(1, "Online Players");
			sign.setLine(2, arg1);
			if(world!=null){
				sign.setLine(3, String.valueOf(world.getPlayers().size()));
			}else{
				sign.setLine(3, "Doesn't exist");
			}
		}else{
			sign.setLine(1, "Online Players");
			sign.setLine(3, 
					String.valueOf(SignInfo.instance.getServer().getOnlinePlayers().length) + 
					"/" + 
					String.valueOf(SignInfo.instance.getServer().getMaxPlayers())
				);
		}
		sign.update();
	}
	
	public static Map<String, String[]> getDefaultLayouts() {
		Map<String, String[]> result = new HashMap<String, String[]>();
		String[] main = {"&8[&9SignInfo&8]", "Online Players:", "{count}/{max}", null};
		String[] worldspecific = {"&8[&9SignInfo&8]", "Players in", "{world}", "{count}"};
		result.put("main", main);
		result.put("worldspecific", worldspecific);
		return result;
	}

	@Override
	public boolean initialize() {
		SignInfo.instance.registerListener(this);
		updateSign();
		return true;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		updateSign();
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		updateSign();
	}
	
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event){
		if(arg1!=null) updateSign();
	}

	@Override
	public boolean destroy() {
		SignInfo.instance.unregisterListener(this);
		return true;
	}

}
