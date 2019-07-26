package ca.fastis;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class PayPass extends JavaPlugin{
	static Server server;
	ConsoleCommandSender console;
	private static Economy econ = null;
	private static Permission perms = null;
	private static Chat chat = null;
	
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
		setupChat();
		server.getPluginManager().registerEvents(new EventListener(this, server, console, econ), this);
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
	
	private boolean setupChat() {
		RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
		chat = rsp.getProvider();
		return chat != null;
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
