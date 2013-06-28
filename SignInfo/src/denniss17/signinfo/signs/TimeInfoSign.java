package denniss17.signinfo.signs;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitTask;

import denniss17.signinfo.SignInfo;
import denniss17.signinfo.InfoSignBase;

public class TimeInfoSign extends InfoSignBase{

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
	
	public static Map<String, String[]> getDefaultLayouts() {
		Map<String, String[]> result = new HashMap<String, String[]>();
		String[] main = {"&8[&9SignInfo&8]", "Time:", "{time}", null};
		result.put("main", main);
		return result;
	}

	@Override
	public void updateSign() {
		sign.setLine(2, ticksToHuman((int) sign.getWorld().getTime()));
		sign.update();
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
		
		timer = SignInfo.instance.getServer().getScheduler().runTaskTimer(SignInfo.instance, new TimeTimer(), interval, interval);
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
