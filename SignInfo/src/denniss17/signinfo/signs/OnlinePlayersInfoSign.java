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
			String[] layout = this.getLayout("world");
			sign.setLine(0, layout[0].replace("{world}", arg1).replace("{count}", world==null ? "Error" : String.valueOf(world.getPlayers().size())));
			sign.setLine(1, layout[1].replace("{world}", arg1).replace("{count}", world==null ? "Error" : String.valueOf(world.getPlayers().size())));
			sign.setLine(2, layout[2].replace("{world}", arg1).replace("{count}", world==null ? "Error" : String.valueOf(world.getPlayers().size())));
			sign.setLine(3, layout[3].replace("{world}", arg1).replace("{count}", world==null ? "Error" : String.valueOf(world.getPlayers().size())));
		}else{
			String[] layout = this.getLayout();
			sign.setLine(0, layout[0].replace("{count}", String.valueOf(SignInfo.instance.getServer().getOnlinePlayers().length)).replace("{max}", String.valueOf(SignInfo.instance.getServer().getMaxPlayers())));
			sign.setLine(1, layout[1].replace("{count}", String.valueOf(SignInfo.instance.getServer().getOnlinePlayers().length)).replace("{max}", String.valueOf(SignInfo.instance.getServer().getMaxPlayers())));
			sign.setLine(2, layout[2].replace("{count}", String.valueOf(SignInfo.instance.getServer().getOnlinePlayers().length)).replace("{max}", String.valueOf(SignInfo.instance.getServer().getMaxPlayers())));
			sign.setLine(3, layout[3].replace("{count}", String.valueOf(SignInfo.instance.getServer().getOnlinePlayers().length)).replace("{max}", String.valueOf(SignInfo.instance.getServer().getMaxPlayers())));
		}
		sign.update();
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
