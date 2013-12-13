package denniss17.infosigns.signs;


import org.bukkit.block.Sign;
import org.bukkit.event.Listener;

import denniss17.infosigns.InfoMultiSign;

public class MultiLineTestInfoSign extends InfoMultiSign implements Listener {

	public MultiLineTestInfoSign(Sign[][] signs, String type, String arg1, String arg2) {
		super(signs, type, arg1, arg2);
	}

	@Override
	public void updateSign() {
		this.setLine(0, "multisign test");
		int i = 1;
		try{
			// Fill all signs
			while(true){
				this.setLine(i, "line " + i);
				i++;
			}				
		}catch(IndexOutOfBoundsException e){
			// All lines filled
		}
	}

	@Override
	public boolean initialize() {
		updateSign();
		return true;
	}

	@Override
	public boolean destroy() {
		return true;
	}

}
