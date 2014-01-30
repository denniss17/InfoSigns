package denniss17.infosigns;

import org.bukkit.block.Sign;

public abstract class InfoMultiSign extends InfoSign{
	/** Matrix of signs, in columns of signs (ie. signs[x][y]) */
	protected Sign[][] signs;

	public InfoMultiSign(Sign[][] signs, String type, String arg1, String arg2){
		super(null, type, arg1, arg2);
		this.signs = signs;
	}
	
	@Override
	protected void parseLines(String... args){
		return;
	}
	
	@Override
	public void setLine(int index, String string) throws IndexOutOfBoundsException{
		setLine(index/(signs[0].length*4), index%(signs[0].length*4), string);
	}
	
	public void setLine(int x, int y, String string) throws IndexOutOfBoundsException{
		signs[x][y/4].setLine(y%4, string==null ? "" : string);
		signs[x][y/4].update();
	}
	
	/**
	 * Returns the top left sign as the main sign. This sign is used to save the InfoSign
	 */
	@Override
	public Sign getSign() {
		return signs[0][0];
	}
	
}
