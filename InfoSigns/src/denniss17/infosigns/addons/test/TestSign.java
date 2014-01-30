package denniss17.infosigns.addons.test;

import org.bukkit.block.Sign;

import denniss17.infosigns.InfoSign;

public class TestSign extends InfoSign {

	public TestSign(Sign sign, String type, String arg1, String arg2) {
		super(sign, type, arg1, arg2);
	}

	@Override
	public void updateSign() {
		this.parseLines("default");
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
