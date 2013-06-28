package denniss17.signinfo.addons.test;

import org.bukkit.block.Sign;

import denniss17.signinfo.InfoSignBase;

public class TestSign extends InfoSignBase {

	public TestSign(Sign sign, String type, String arg1, String arg2) {
		super(sign, type, arg1, arg2);
	}

	@Override
	public void updateSign() {
		String[] layout = getLayout();
		sign.setLine(0, layout[0]);
		sign.setLine(1, layout[1]);
		sign.setLine(2, layout[2]);
		sign.setLine(3, layout[3]);
		sign.update();
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
