package com.yurijware.bukkit.worldwarp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface ICommandable {
	boolean onCommand(CommandSender sender, Command command, String label, String[] args);
}