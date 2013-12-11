package denniss17.signinfo.signs;


import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import denniss17.signinfo.InfoMultiSign;
import denniss17.signinfo.SignInfo;

public class PlayersInfoSign extends InfoMultiSign implements Listener {

	public PlayersInfoSign(Sign[][] signs, String type, String arg1, String arg2) {
		super(signs, type, arg1, arg2);
	}

	@Override
	public void updateSign() {
		this.setLine(0, "Online Players");
		int i = 1;
		for(Player player : SignInfo.instance.getServer().getOnlinePlayers()){
			try{
				this.setLine(i, player.getName());
				i++;
			}catch(IndexOutOfBoundsException e){
				// Sign is too small
			}
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

	@Override
	public boolean destroy() {
		SignInfo.instance.unregisterListener(this);
		return true;
	}

}
