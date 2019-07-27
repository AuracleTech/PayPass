package ca.fastis;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class PayPass extends JavaPlugin{
	static Server server;
	static ConsoleCommandSender console;
	static Economy econ;
	private static Permission perms;
	
	@Override
	public void onEnable() {
		server = this.getServer();
		console = server.getConsoleSender();
		if (!setupEconomy() ) {
			console.sendMessage(ChatColor.DARK_RED + "[" + getDescription().getName() + "] Disabled due to no Vault dependency found!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		setupPermissions();
		server.getPluginManager().registerEvents(new EventListener(this), this);
		console.sendMessage(ChatColor.GREEN + "[" + getDescription().getName() + "] Enabled!");
	}
	
	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}
	
	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}
	
	@Override
	public void onDisable() {
		console.sendMessage(ChatColor.GREEN + "[" + getDescription().getName() + "] Disabled!");
	}
}
