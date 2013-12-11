package denniss17.signinfo.signs;


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
			parseLayout("world", 
					"world", arg1, 
					"count",  world==null ? "Error" : String.valueOf(world.getPlayers().size())
					);
		}else{
			parseLayout("default", 
				"count", String.valueOf(SignInfo.instance.getServer().getOnlinePlayers().length),
				"max", String.valueOf(SignInfo.instance.getServer().getMaxPlayers())
			);
		}
	}

	@Override
	public boolean initialize() {
		SignInfo.instance.registerListener(this);
		updateSign();
		return true;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		// If player joins, event.getPlayer().getWorld().getPlayers() doesn't contain the
		// current player yet. As a result, the sign would be wrong
		// Therefore the updated is delayed with 1 tick, so the joining players
		// is added to the players of the world
		SignInfo.instance.getServer().getScheduler().runTaskLater(SignInfo.instance, new Runnable(){
			@Override
			public void run() {
				updateSign();
			}
		}, 1);
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
