package io.github.nicknazari.PluginCarton;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.concurrent.ThreadLocalRandom;

public class PluginCarton extends JavaPlugin {
	@Override
	public void onEnable() {
		getLogger().info("Initialized PluginCarton");
	}
	
	public String[] Roll(String betColor, int numberBet, Player player) {
		
		String outputColor = null;
		int winnings = 0;
		
		int rand = ThreadLocalRandom.current().nextInt(0, 101);
		
		if (rand <= 45) {
			outputColor = "red";
		} else if (rand > 45 && rand <= 90) {
			outputColor = "black";
		} else if (rand > 90) {
			outputColor = "green";
		}
		
		if (outputColor.equalsIgnoreCase(betColor) && (betColor.equalsIgnoreCase("red") || betColor.equalsIgnoreCase("black"))) {
			winnings = (int) Math.ceil(2 * numberBet);
		} else if (outputColor.equalsIgnoreCase(betColor) && betColor.equalsIgnoreCase("green")) {
			winnings = (int) Math.ceil(10 * numberBet);
		} 
		
		int jackpotRand = ThreadLocalRandom.current().nextInt(0, 201);
		
		if (jackpotRand == 200) {
			getServer().broadcastMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + player.getName().toUpperCase() + " HAS WON THE JACKPOT OF " + getConfig().getInt("diamonds-lost-tally"));
			winnings = winnings + getConfig().getInt("diamonds-lost-tally");
			getConfig().set("diamonds-lost-tally", 0);
		}
		
		if (winnings == 0) {
			getConfig().set("diamonds-lost-tally", getConfig().getInt("diamonds-lost-tally") + numberBet) ;
		}
		
		return new String[] {outputColor, Integer.toString(winnings), player.getName()};
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("gamble")) {
			
			if (args.length == 0) {
				player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "GAMBLING");
				player.sendMessage(ChatColor.GOLD + "/gamble <game>");
				player.sendMessage(ChatColor.GOLD + "Available games: " + ChatColor.WHITE +  "roulette, coinflip");
				player.sendMessage(ChatColor.GOLD + "Current jackpot: " + ChatColor.WHITE + getConfig().getInt("diamonds-lost-tally") + " diamonds");
			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("roulette")) {
					player.sendMessage(ChatColor.GOLD + "\nRoulette syntax:");
					player.sendMessage(ChatColor.WHITE + "/gamble roulette <number of diamonds> <color>");
					player.sendMessage(ChatColor.WHITE + "Available colors: " + ChatColor.RED + "red (2x), " + ChatColor.GRAY + "black (2x), " + ChatColor.GREEN + "green (10x)");
				} else if (args[0].equalsIgnoreCase("coinflip")) {
					player.sendMessage(ChatColor.GOLD + "\nCoinflip syntax:");
					player.sendMessage(ChatColor.WHITE + "/gamble coinflip <number of diamonds> <vs>");
				}
						
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("roulette") || args[0].equalsIgnoreCase("coinflip")) {
					player.sendMessage(ChatColor.RED + "Invalid command");
				} else {
					player.sendMessage(ChatColor.RED + "Invalid command");
				}
				
			} else if (args.length == 3) {
				if (args[0].equalsIgnoreCase("roulette")) {
					int numberToBet = Integer.parseInt(args[1]);
					if (player.getInventory().contains(Material.DIAMOND, numberToBet)) {
						
						ItemStack betItems = new ItemStack(Material.DIAMOND, numberToBet);
						
						player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "STARTING BET");
						player.getInventory().removeItem(betItems);
						
						String[] outcome = Roll(args[2], Integer.valueOf(args[1]), player);
						
						player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "OUTCOME: " + ChatColor.WHITE + outcome[0]);
						player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "WINNINGS: " + ChatColor.RED + outcome[1]);
						
						ItemStack wonItems = new ItemStack(Material.DIAMOND, Integer.valueOf(outcome[1]));
						
						player.getInventory().addItem(wonItems);
						
						getLogger().info("ROULETTE | " + player.getName() + " | " + outcome[0].toUpperCase() + " | " + outcome[1] + " | jackpot draw:" + outcome[2]);
						
					} else {
						player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Not enough items.");
					}
					
					
					/* todo: 
					 * check if player has enough emerald - CHECK
					 * if yes: take emerald from player - CHECK
					 * roll and pick color
					 * green: 45%, red: 45%, black: 10%
					 * if color picked equals player choice,
					 * multiply played amount by color picked multiplier and give that many emerald
					 * if not, do nothing 
					 */
					
				} /* else if (args[1].equalsIgnoreCase("donate")) {
					
					int donation = Integer.valueOf(args[2]);

					if (player.getInventory().contains(Material.DIAMOND, donation)) {
						ItemStack itemsDonated = new ItemStack(Material.DIAMOND, donation);
						player.getInventory().remove(itemsDonated);
						getConfig().set("diamonds-lost-tally", getConfig().getInt("diamonds-lost-tally") + donation);
						player.sendMessage(ChatColor.GRAY + "You donated " + Integer.toString(donation) + " diamond(s) to the jackpot.");
					} else {
						player.sendMessage(ChatColor.RED + "You do not have enough diamond(s) to donate.");
					}
					// /gamble jackpot donate <amount>
					
				} */
			}

			return true;
		}
		
		return false;
	}
	
	@Override
	public void onDisable() {
		getLogger().info("Shut down PluginCarton");
	}
	
}
