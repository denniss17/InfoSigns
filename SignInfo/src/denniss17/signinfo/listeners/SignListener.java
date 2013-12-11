package denniss17.signinfo.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.SignChangeEvent;

import denniss17.signinfo.SignInfo;
import denniss17.signinfo.InfoSignBase;
import denniss17.signinfo.utils.Messager;

public class SignListener implements Listener {
	
	/**
	 * Listen to a sign change in order to detect if it is a InfoSign
	 * @param event
	 */
	@EventHandler(ignoreCancelled = true)
	public void onSignChange(SignChangeEvent event){
		if(event.getLine(0).equals(SignInfo.instance.getConfig().getString("general.firstline"))){
			// Ok it is an InfoSign
			
			// Get type
			String type = event.getLine(1);
			if(!event.getPlayer().hasPermission("signinfo.create." + type) && !event.getPlayer().hasPermission("signinfo.create.*")){
				// No permission
				return;
			}
			if(type.equals("")){
				Messager.sendConfig(event.getPlayer(), "sign_creation_unknown_type");
				return;
			}
			
			// Get arguments
			Sign sign = (Sign)event.getBlock().getState();
			String arg1 = event.getLine(2);
			String arg2 = event.getLine(3);
			if(arg1.equals("")) arg1 = null;
			if(arg2.equals("")) arg2 = null;
			
			// Create sign
			InfoSignBase infoSign = SignInfo.instance.createNewSign(sign, type, arg1, arg2);
			if(infoSign!=null){
				Messager.send(event.getPlayer(), Messager.getConfigMessage("sign_creation_success").replace("{type}", type));
				SignInfo.instance.getLogger().info("InfoSign of type '" + type + "' created by " + event.getPlayer().getName());
				SignInfo.signManager.addInfoSign(infoSign);
				infoSign.initialize();
				SignInfo.signManager.saveInfoSign(infoSign);
				event.setCancelled(true);
			}else{
				Messager.sendConfig(event.getPlayer(), "sign_creation_failed");
			}			
		}
	}
	
	/**
	 * Check if an InfoSign is destroyed
	 * @param event
	 */
	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent event){
		if (event.getBlock().getType() == Material.SIGN_POST || event.getBlock().getType() == Material.WALL_SIGN) {
			// Material
			org.bukkit.material.Sign sign = (org.bukkit.material.Sign)event.getBlock().getState().getData();
			// Block
			Sign signBlock = (Sign)event.getBlock().getState();
			
			InfoSignBase infoSign = SignInfo.signManager.getInfoSign(signBlock);
			if(infoSign!=null){
				Block attachedBlock = event.getBlock().getRelative(sign.getAttachedFace());
				if(attachedBlock.isEmpty()){
					// Block removed
					SignInfo.signManager.removeSign(infoSign);
					SignInfo.instance.getLogger().info("InfoSign " + infoSign.id + " removed. (type:" + infoSign.getType() + ")");
					// Unable to send message to player, as player is not attached to event
				}
			}			
		}		
	}
	
	/**
	 * Check if an InfoSign is destroyed
	 * @param event
	 */
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		if (event.getBlock().getType() == Material.SIGN_POST || event.getBlock().getType() == Material.WALL_SIGN) {
			Sign signBlock = (Sign)event.getBlock().getState();
			
			InfoSignBase infoSign = SignInfo.signManager.getInfoSign(signBlock);
			if(infoSign!=null){
				// Block removed
				SignInfo.signManager.removeSign(infoSign);
				SignInfo.instance.getLogger().info("InfoSign " + infoSign.id + " removed. (type:" + infoSign.getType() + ")");
				Messager.sendConfig(event.getPlayer(), "sign_broken");
			}			
		}		
	}
}
