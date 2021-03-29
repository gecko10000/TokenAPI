package io.github.levtey.TokenAPI;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import redempt.redlib.commandmanager.ArgType;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.commandmanager.CommandParser;

public class TokenCommand {

	private TokenPlugin plugin;
	
	protected TokenCommand(TokenPlugin plugin) {
		this.plugin = plugin;
		ArgType<Player> playerType = new ArgType<>("player", Bukkit::getPlayer);
		new CommandParser(plugin.getResource("command.rdcml")).setArgTypes(playerType).parse().register("", this);
	}
	
	@CommandHook("check")
	public void check(CommandSender sender, Player player) {
		if (player == null && sender instanceof Player) {
			sendMessage(sender, "lang.check.self",
					"%tokens%", TokenAPI.get((Player) sender));
		} else if (player != null) {
			sendMessage(sender, "lang.check.others",
					"%player%", player.getName(),
					"%tokens%", TokenAPI.get(player));
		} else {
			sendMessage(sender, "lang.no.player");
		}
	}
	
	@CommandHook("give")
	public void give(CommandSender sender, Player player, int amount) {
		TokenAPI.give(player, amount);
		sendMessage(sender, "lang.manage.give",
				"%player%", player.getName(),
				"%given%", amount,
				"%tokens%", TokenAPI.get(player));
	}
	
	@CommandHook("take")
	public void take(CommandSender sender, Player player, int amount) {
		TokenAPI.take(player, amount);
		sendMessage(sender, "lang.manage.take",
				"%player%", player.getName(),
				"%taken%", amount,
				"%tokens%", TokenAPI.get(player));
	}
	
	@CommandHook("set")
	public void set(CommandSender sender, Player player, int amount) {
		TokenAPI.set(player, amount);
		sendMessage(sender, "lang.manage.set",
				"%player%", player.getName(),
				"%tokens%", TokenAPI.get(player));
	}
	
	@CommandHook("reload")
	public void reload(CommandSender sender) {
		plugin.saveDefaultConfig();
		plugin.reloadConfig();
		sendMessage(sender, "lang.reload");
	}
	
	private void sendMessage(CommandSender sender, String configPath, Object...objects) {
		String toSend = plugin.getConfig().getString(configPath);
		List<String> toReplace = new ArrayList<>();
		List<String> replacements = new ArrayList<>();
		for (int i = 0; i < (objects.length % 2 == 0 ? objects.length : objects.length - 1); i++) {
			(i % 2 == 0 ? toReplace : replacements).add(objects[i].toString());
		}
		for (int i = 0; i < toReplace.size(); i++) {
			toSend = toSend.replace(toReplace.get(i), replacements.get(i));
		}
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', toSend));
	}
	
}
