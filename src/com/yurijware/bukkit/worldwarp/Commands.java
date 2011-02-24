package com.yurijware.bukkit.worldwarp;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Commands{
	private final Plugin plugin;
	
	public Commands(Plugin plugin){
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if(args.length == 1){
			if(args[0].equalsIgnoreCase("list")){
				ListWorlds(sender);
				return true;
			}else if(args[0].equalsIgnoreCase("help")){
				sender.sendMessage("§9   World Warp help");
				sender.sendMessage("§a========================");
				sender.sendMessage("§9/ww list - List availible worlds");
				sender.sendMessage("§9/ww <warp|tp> [worldname] - Warp to another world");
				sender.sendMessage("§9/ww <warp|tp> [playername] [worldname] - Warp another player to another world");
				return true;
			}
		}else if(args.length == 2){
			if(args[0].equalsIgnoreCase("warp") || args[0].equalsIgnoreCase("tp")){
				if(sender instanceof Player){
					WarpWorld(sender, args[1]);
				}else{
					sender.sendMessage("§4You are not a valid player");
				}
				return true;
			}
		}else if(args.length == 3){
			if(args[0].equalsIgnoreCase("warp") || args[0].equalsIgnoreCase("tp")){
				Player player = plugin.getServer().getPlayer(args[1]);
				if(player != null){
					WarpPlayerToWorld(sender, player, args[2]);
				}else{
					sender.sendMessage("§4Not a valid player");
				}
				return true;
			}
		}
		return false;
	}
	
	private boolean CanWarpToWorld(CommandSender sender, String worldname){
		try{
//			String perm = ((WorldWarp)plugin).getWorldPerm().get(worldname);
			Boolean opsOnly = ((WorldWarp)plugin).getWorldOpsOnly().get(worldname);
			if(opsOnly){
				if(sender instanceof Player) return ((Player)sender).isOp();
				return false;
			}else{
				return true;
			}
		}catch(Exception e){
			
		}
		return false;
	}
	
	private boolean CanWarpPlayerToWorld(CommandSender sender, Player player, String worldname){
		if(sender.isOp()){
			return true;
		}
		return false;
	}
	
	private void ListWorlds(CommandSender sender){
		List<World> worlds = plugin.getServer().getWorlds();
		sender.sendMessage("§aList of Worlds:");
		if(worlds.size() > 1){
			for(World w : worlds){
				boolean current = false;
				if(sender instanceof Player){
					World currentWorld = ((Player)sender).getWorld();
					if(w.getName().equals(currentWorld.getName())) current = true;
				}
				sender.sendMessage("§b" + w.getName() + (current ? " §c(current)" : ""));
			}
		}else{
			sender.sendMessage("§4Could not find additional worlds");
		}
	}
	
	private void WarpWorld(CommandSender sender, String worldname){
		World world = plugin.getServer().getWorld(worldname);
		Location loc = null;
		try{
			loc = ((WorldWarp)plugin).getWorldLoc().get(worldname);
		}catch(Exception e){
			
		}
		if(world != null){
			if(CanWarpToWorld(sender, worldname)){
				World current = ((Player)sender).getWorld();
				if(!current.getName().equals(world.getName())){
					if(loc == null) loc = world.getSpawnLocation();
					((Player)sender).teleportTo(loc);
					((Player)sender).sendMessage("§3You were warped to world '§6" + world.getName() + "§3'");
				}else{
					sender.sendMessage("§4You are already in that world");
				}
			}else{
				sender.sendMessage("§4Permission denied");
			}
		}else{
			sender.sendMessage("§4Not a valid world");
		}
	}
	
	private void WarpPlayerToWorld(CommandSender sender, Player player, String worldname){
		World world = plugin.getServer().getWorld(worldname);
		Location loc = null;
		try{
			loc = ((WorldWarp)plugin).getWorldLoc().get(worldname);
		}catch(Exception e){
			
		}
		if(world != null){
			if(CanWarpPlayerToWorld(sender, player, worldname)){
				if(!player.getWorld().getName().equals(world.getName())){
					if(loc == null) loc = world.getSpawnLocation();
					player.teleportTo(loc);
					String playermsg = "§3You were warped to world '§6" + world.getName() + "§3'";
					if(sender instanceof Player) playermsg += " by §6" + ((Player)sender).getDisplayName();
					player.sendMessage(playermsg);
					sender.sendMessage("§3You warped by '§6" + player.getDisplayName() +
							"§3' to world '§6" + world.getName() + "§3'");
					
				}else{
					sender.sendMessage("§4" + player.getDisplayName() + " is already in that world");
				}
			}else{
				sender.sendMessage("§4Permission denied");
			}
		}else{
			sender.sendMessage("§4Not a valid world");
		}
	}
	
}
