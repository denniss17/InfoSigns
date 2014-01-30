package denniss17.infosigns.signs;

import java.util.HashMap;

import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitTask;

import denniss17.infosigns.InfoSign;
import denniss17.infosigns.InfoSigns;

public class TimeInfoSign extends InfoSign{

	public TimeInfoSign(Sign sign, String type, String arg1, String arg2) {
		super(sign, type, arg1, arg2);
		data = new HashMap<String, Object>(1);
		data.put("interval", 16);
	}
	
	private BukkitTask timer;
	
	private String ticksToHuman(int ticks){
		int hours = ((ticks/1000)+6) % 24;
		int minutes = (6*(ticks%1000))/100;
		return (hours<10 ? "0" : "") + hours + ":" + (minutes<10 ? "0" : "") + minutes;
	}

	@Override
	public void updateSign() {
		parseLines("time", ticksToHuman((int) sign.getWorld().getTime()));
	}

	@Override
	public boolean initialize() {
		updateSign();
		int interval;
		try{
			interval = Integer.parseInt(data.get("interval").toString());
		}catch(NumberFormatException e){
			interval = 16;
		}
		
		timer = InfoSigns.instance.getServer().getScheduler().runTaskTimer(InfoSigns.instance, new TimeTimer(), interval, interval);
		return true;
	}

	@Override
	public boolean destroy() {
		timer.cancel();
		return true;
	}
	
	class TimeTimer implements Runnable{

		@Override
		public void run() {
			updateSign();			
		}		
	}

}
