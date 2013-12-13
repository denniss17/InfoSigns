package denniss17.infosigns.signs;


import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import denniss17.infosigns.InfoMultiSign;
import denniss17.infosigns.InfoSigns;

public class PlayersInfoSign extends InfoMultiSign implements Listener {

	private static final String CONSTRAINT_PERMISSION = "permission";

	public PlayersInfoSign(Sign[][] signs, String type, String arg1, String arg2) {
		super(signs, type, arg1, arg2);
	}
	
	private boolean hasPermission(Player player, String permission){
		if(InfoSigns.permission!=null){
			return InfoSigns.permission.has(player, "infosigns.permission." + permission);
		}else{
			return player.hasPermission("infosigns.permission." + permission);
		}
	}

	@Override
	public void updateSign() {
		this.setLine(0, "Online Players");
		int i = 1;
		for(Player player : InfoSigns.instance.getServer().getOnlinePlayers()){
			if(		arg1!=null && 
					arg1.equals(CONSTRAINT_PERMISSION) &&
					!hasPermission(player, arg2) ){
				// Do not display
			}else{
				try{
					this.setLine(i, player.getName());
					i++;
				}catch(IndexOutOfBoundsException e){
					// Sign is too small
				}
			}
			
		}
		
		// Clear other lines
		try{
			while(true){
				this.setLine(i, "");
				i++;
			}			
		}catch(IndexOutOfBoundsException e){
			// All lines cleared
		}
	}

	@Override
	public boolean initialize() {
		InfoSigns.instance.registerListener(this);
		updateSign();
		return true;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
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

	@Override
	public boolean destroy() {
		InfoSigns.instance.unregisterListener(this);
		return true;
	}

}
