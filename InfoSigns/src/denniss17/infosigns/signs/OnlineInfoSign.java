package denniss17.infosigns.signs;


import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import denniss17.infosigns.InfoSign;
import denniss17.infosigns.InfoSigns;

public class OnlineInfoSign extends InfoSign implements Listener {

	private static final String SUBTYPE_WORLD = "world";

	public OnlineInfoSign(Sign sign, String type, String arg1, String arg2) {
		super(sign, type, arg1, arg2);
	}

	@Override
	public void updateSign() {
		if(getSubtype().equals(SUBTYPE_WORLD)){
			World world = InfoSigns.instance.getServer().getWorld(arg1);
			parseLines( 
				"world", arg2==null ? arg1 : arg2, 
				"count",  world==null ? "Error" : String.valueOf(world.getPlayers().size())
			);
		}else{
			parseLines( 
				"count", String.valueOf(InfoSigns.instance.getServer().getOnlinePlayers().length),
				"max", String.valueOf(InfoSigns.instance.getServer().getMaxPlayers())
			);
		}
	}

	@Override
	public boolean initialize() {
		if(arg1!=null){
			subtype = SUBTYPE_WORLD;
		}else{
			subtype = SUBTYPE_DEFAULT;
		}
		
		InfoSigns.instance.registerListener(this);
		updateSign();
		return true;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		// If player joins, event.getPlayer().getWorld().getPlayers() doesn't contain the
		// current player yet. As a result, the sign would be wrong
		// Therefore the updated is delayed with 1 tick, so the joining players
		// is added to the players of the world
		InfoSigns.instance.getServer().getScheduler().runTaskLater(InfoSigns.instance, new Runnable(){
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
		if(getSubtype().equals(SUBTYPE_WORLD)) updateSign();
	}

	@Override
	public boolean destroy() {
		InfoSigns.instance.unregisterListener(this);
		return true;
	}

}
