package com.yurijware.bukkit.worldwarp;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

class CommandManager {
	private final Logger log = Logger.getLogger("Minecraft");
	private Plugin plugin;
	private PluginDescriptionFile desc;
	private Map<String, Class<? extends ICommandable>> commands = new Hashtable<String, Class<? extends ICommandable>>();
	
	public CommandManager(Plugin plugin) {
		this.plugin = plugin;
		this.desc = plugin.getDescription();
	}
	
	public void loadFromDescription(ClassLoader loader) {
		Object object = desc.getCommands();
		if (object == null)
			return;
		
		@SuppressWarnings("unchecked")
		Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) object;
		
		for (Entry<String, Map<String, Object>> entry : map.entrySet()) {
			String label = entry.getKey();
			String classname = entry.getValue().get("class").toString();
			
			try {
				Class<?> klass = Class.forName(classname, true, loader);
				Class<? extends ICommandable> commandClass = klass.asSubclass(ICommandable.class);
				addCommand(label, commandClass);
			} catch (ClassNotFoundException e) {
				log.severe("[" + desc.getName() + "] " +
						"Unable to load command class for command /" + label);
			} 
		}
	}
	
	public void addCommand(String label, Class<? extends ICommandable> klass) {
		commands.put(label, klass);
	}
	
	public boolean dispatch(CommandSender sender, Command command, String label, String[] args) {
		if (!commands.containsKey(label))
			return false;
		
		boolean handled = true;
		
		Class<? extends ICommandable> klass = commands.get(label);
		try {
			Constructor<? extends ICommandable> ctor = klass.getConstructor(Plugin.class);
			ICommandable c = ctor.newInstance(plugin);
			handled = c.onCommand(sender, command, label, args);
		} catch (NoSuchMethodException e) {
			log.warning("[" + desc.getName() + "] " +
					"No constructor that accepts a Plugin.");
		} catch (InstantiationException e) {
			log.warning("[" + desc.getName() + "] " +
					"Error while creating a Commandable object.");
		} catch (IllegalAccessException e) {
			log.warning("[" + desc.getName() + "] " +
					"Illegal access to the Commandable object constructor.");
		} catch (InvocationTargetException e) {
			log.warning("[" + desc.getName() + "] " +
					e.getCause().getMessage());
		}
		
		return handled;
	}
}