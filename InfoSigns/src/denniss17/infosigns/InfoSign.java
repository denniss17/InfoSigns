package denniss17.infosigns;

import java.util.Map;

import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;

import denniss17.infosigns.utils.Messager;

public abstract class InfoSign {
	public static final String SUBTYPE_DEFAULT = "default";

	/** The id of this sign, used internally for IO purposes. Do not change */
	private int id;
	
	/** The main sign of this InfoSign. If this InfoSign consists of multiple signs,
	 * this is null */
	protected Sign sign;
	
	/** The type of this InfoSign */
	protected String type;
	
	/** The subtype of this InfoSign. Used to distinguish between layouts. */
	protected String subtype;
	
	/** The first argument passed to the sign (line 2) */
	protected String arg1;
	/** The second argument passed to the sign (line 3) */
	protected String arg2;
	
	/** Some optional data of this sign */
	protected Map<String, Object> data;

	public InfoSign(Sign sign, String type, String arg1, String arg2){
		this.sign = sign;
		this.type = type;
		this.subtype = SUBTYPE_DEFAULT;
		this.arg1 = arg1;
		this.arg2 = arg2;
	}
	
	/**
	 * Update the sign
	 */
	public abstract void updateSign();
	
	/**
	 * Called when the sign is created and should start updating its display
	 * Use this function to register listeners, determine subtype and update the sign for the first time
	 * @return true if successfully initialized, false otherwise
	 */
	public abstract boolean initialize();
	
	/**
	 * Called when the sign is destroyed, either by a player or by block physics
	 * Use this function to unregister from listeners, unset timers and free resources.
	 * @return
	 */
	public abstract boolean destroy();
	
	public int getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public String getSubtype(){
		return subtype;
	}

	public Sign getSign() {
		return sign;
	}

	public String getFirstArgument() {
		return arg1;
	}

	public String getSecondArgument() {
		return arg2;
	}

	/**
	 * Returns the layout for this sign type (from layouts.yml) in the subtype of this sign.
	 * @return An String array of length 4. Elements could be null, which indicates the line
	 * is empty
	 */
	public String[] getLayout(){
		return InfoSigns.layoutManager.getLayout(this);
	}
	
	public ConfigurationSection getLayoutConfig(){
		return InfoSigns.layoutManager.getLayoutConfig(this);
	}

	public Map<String, Object> getData(){
		return data;
	}
	
	/** Set the data of this sign*/
	public void setData(Map<String, Object> options){
		this.data = options;
	}
	
	/** Save this sign, including the data, to the hard disk */
	public void save(){
		InfoSigns.signManager.saveInfoSign(this);
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public boolean isInfoMultiSign(){
		return sign==null;
	}

	public void setLine(int index, String string) throws IndexOutOfBoundsException {
		sign.setLine(index, string);
		sign.update();		
	}

	/**
	 * Parse the layout of the given subtype (from layouts.yml) with the given arguments
	 * and updates each line of the sign
	 * @param subtype The layout subtype to parse. Use "default" if it is the default layout
	 * @param args An even amount of Strings. The first string indicates the tag, the second string 
	 * what should replace the tag. Example:<br>
	 * Line: "{count}/{max}"<br>
	 * Method call: parseLayout("default", "count", "10", "max", "20")<br>
	 * Result: "10/20"
	 */
	protected void parseLayout(String... args){
		String[] output = getLayout();
		// Replace tags
		for(int i=0; i<args.length-1; i+=2){
			for(int j=0; j<4; j++){
				if(output[j]!=null) output[j] = output[j].replace("{"+args[i]+"}", args[i+1]);
			}
		}
		// Set lines
		for(int j=0; j<4; j++){
			if(output[j]!=null){
				sign.setLine(j, Messager.setTotalStyle(output[j]));;
			}else{
				sign.setLine(j, "");
			}
		}
		// And update the sign
		sign.update();
	}

	
	
}
