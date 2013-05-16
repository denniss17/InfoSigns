package denniss17.signinfo.addons.test;

import org.bukkit.block.Sign;

import denniss17.signinfo.InfoSignBase;

public class TestSign extends InfoSignBase {

	public TestSign(Sign sign, String type, String arg1, String arg2) {
		super(sign, type, arg1, arg2);
	}

	@Override
	public void updateSign() {
		sign.setLine(2, "test");
		sign.update();
	}

	@Override
	public boolean initialize() {
		return true;
	}

	@Override
	public boolean destroy() {
		return true;
	}

}
