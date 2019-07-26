package ca.fastis;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import net.milkbowl.vault.economy.Economy;

public class EventListener implements Listener {
	static Plugin plugin;
	static Server server;
	static ConsoleCommandSender console;
	private static Economy econ = null;
	public EventListener(PayPass plugin, Server server, ConsoleCommandSender console, Economy econ) {
		EventListener.plugin = plugin;
		EventListener.server = server;
		EventListener.console = console;
		EventListener.econ = econ;
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void Signit(SignChangeEvent event) {
		Player player = event.getPlayer();
		Block signBlock = event.getBlock();
		WallSign signData  = (WallSign) signBlock.getState().getBlockData();
		BlockFace attached = signData.getFacing().getOppositeFace();
		Block blockAttached = signBlock.getRelative(attached);
		if(event.getLine(0).toLowerCase().contains("paypass") && blockAttached.getType() == Material.GOLD_BLOCK) {
			try {
				int price = Integer.parseInt(event.getLine(3));
				player.sendMessage(ChatColor.GOLD + "PayPass created at " + ChatColor.GREEN + "$" + price);
				event.setLine(0, ChatColor.ITALIC + "PayPass" + ChatColor.RESET);
				String line3Text = "FREE";
				if(price > 0) { line3Text = Integer.toString(price); }
				event.setLine(2, ChatColor.BOLD + "" + player.getName());
				event.setLine(3, ChatColor.BOLD + "" + ChatColor.GREEN + "$" + line3Text);
			} catch(NumberFormatException ex) {
				player.sendMessage(ChatColor.RED + "You need a valid price on the last line to use PayPass");
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(e.getClickedBlock() != null) {
				Block block = e.getClickedBlock();
				Player plr = e.getPlayer();
				if(block.getState().getBlockData() instanceof WallSign){
					WallSign signData  = (WallSign) block.getState().getBlockData();
					BlockFace attached = signData.getFacing().getOppositeFace();
					Block blockAttached = block.getRelative(attached);
					if(blockAttached.getType() == Material.GOLD_BLOCK) {
						org.bukkit.block.Sign sign = (org.bukkit.block.Sign) block.getState();
						String[] ln = sign.getLines();
						if(ln[0].toLowerCase().contains(ChatColor.ITALIC + "paypass")){
							e.setCancelled(true);
							try {
								int price = ChatColor.stripColor(ln[3]).contains("$FREE") ? 0 :Integer.parseInt(ChatColor.stripColor(ln[3]).substring(1));
								String sellerName = (ChatColor.stripColor(ln[2]));
								if(econ.hasAccount(sellerName)) {
									if(!plr.getName().equals(sellerName)) {
										if(econ.has(plr, price)) {
											if(price > 0) {
												econ.withdrawPlayer(plr, price);
												econ.depositPlayer(sellerName, price);
												if(server.getPlayer(sellerName) != null) server.getPlayerExact(sellerName).sendMessage(ChatColor.GOLD + plr.getName() + ChatColor.GREEN + " just paid "  + ChatColor.GOLD + "$" + price + ChatColor.GREEN + " via PayPass");
											}
											plr.sendMessage(ChatColor.GOLD + "You just paid " + ChatColor.GREEN + "$" + price + ChatColor.GOLD + " on PayPass");
										} else {
											plr.sendMessage(ChatColor.RED + "You don't have enough money");
											return;
										}
									}
									blockAttached.getWorld().playSound(blockAttached.getLocation(), "block.lever.click", SoundCategory.MASTER, 0.3F, 0.6F);
									blockAttached.setType(Material.REDSTONE_BLOCK);
									Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
										@Override
										public void run() {
											if(blockAttached.getType() == Material.REDSTONE_BLOCK) { blockAttached.setType(Material.GOLD_BLOCK); blockAttached.getWorld().playSound(blockAttached.getLocation(), "block.lever.click", SoundCategory.MASTER, 0.3F, 0.5F); }
										}
									}, 4*20L);
								} else {
									plr.sendMessage(ChatColor.RED + "Impossible to find the player bank account");
								}
							} catch(NumberFormatException ex) {
								plr.sendMessage(ChatColor.RED + "The PayPass price it not valid");
							}
						}
					}
				}
			}
		}
	}
}