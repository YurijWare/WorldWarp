package com.yurijware.bukkit.worldwarp;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

/**
 * WorldWarp for Bukkit
 * 
 * @author YurijWare
 */
public class WorldWarp extends JavaPlugin {
	private final Logger log = Logger.getLogger("Minecraft");
	protected static PluginDescriptionFile pdfFile = null;
	protected static WorldWarp plugin = null;
	
	protected static File maindir = new File("plugins" + File.separatorChar + "WorldWarp");
	protected static File configFile = new File(maindir, "WorldWarp.yml");
	
	private Commands cmd = new Commands(this);
	private HashMap<String,String> worldPerm;
	private HashMap<String,Boolean> worldOpsOnly;
	private HashMap<String,Location> worldLoc;
	
	private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
	
	public void onEnable() {
		plugin = this;
		pdfFile = this.getDescription();
		
		loadWorlds();
		log.info("[" + pdfFile.getName() + "] Version " + pdfFile.getVersion()
				+ " is enabled!");
	}
	
	public void onDisable() {
		log.info("[" + pdfFile.getName() + "] " +
				"Plugin disabled!");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		return cmd.onCommand(sender, command, commandLabel, args);
	}
	
	public boolean isDebugging(final Player player) {
		if (debugees.containsKey(player)) {
			return debugees.get(player);
		} else {
			return false;
		}
	}
	
	public void setDebugging(final Player player, final boolean value) {
		debugees.put(player, value);
	}
	
	private void loadWorlds(){
		if(!configFile.exists()){
			return;
		}
		worldPerm = new HashMap<String,String>();
		worldOpsOnly = new HashMap<String,Boolean>();
		worldLoc = new HashMap<String,Location>();
		Configuration conf = new Configuration(configFile);
		conf.load();
		List<String> worlds = conf.getKeys("load-worlds");
		for(String w : worlds){
			ConfigurationNode node = conf.getNode("load-worlds");
			boolean nether = node.getBoolean(w + ".nether", false);
			boolean ops = node.getBoolean(w + ".only-ops", false);
			String perm = node.getString(w + ".permission-node");
			worldPerm.put(w, perm);
			getWorldOpsOnly().put(w, ops);
			World world = plugin.getServer().getWorld(w);
			if(world == null){
				log.info("[" + pdfFile.getName() + "] " +
						"Creating/Loading world '"+ w +"' with enviroment: " +
						(nether ? "Nether" : "Normal"));
				world = this.getServer().createWorld(w,
						(nether ? World.Environment.NETHER : World.Environment.NORMAL));
			}
			ConfigurationNode locNode = node.getNode(w + ".location");
			int x = 0, y = 0, z = 0;
			System.out.print("World '" + w + "' config: permNode=" + perm + " onlyOps=" + ops);
			if(locNode != null){
				String xString = locNode.getString("x");
				String yString = locNode.getString("y");
				String zString = locNode.getString("z");
				if(xString != null && yString != null && zString != null){
					x = Integer.parseInt(xString);
					y = Integer.parseInt(yString);
					z = Integer.parseInt(zString);
					Location loc = new Location(world, x, y, z);
					worldLoc.put(w, loc);
					System.out.print(" Loc: x=" + x + " y=" + y + " z=" + z);
				}
			}
			System.out.println();
		}
	}
	
	public HashMap<String,String> getWorldPerm() {
		return worldPerm;
	}
	
	public HashMap<String,Boolean> getWorldOpsOnly() {
		return worldOpsOnly;
	}
	
	public HashMap<String,Location> getWorldLoc() {
		return worldLoc;
	}
	
}
