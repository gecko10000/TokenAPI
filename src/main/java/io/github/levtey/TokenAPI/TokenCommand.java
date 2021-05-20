package io.github.levtey.TokenAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import redempt.redlib.commandmanager.ArgType;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.commandmanager.CommandParser;
import redempt.redlib.commandmanager.ContextProvider;
import redempt.redlib.misc.UserCache;
import redempt.redlib.sql.SQLHelper.Results;

public class TokenCommand {

	private TokenPlugin plugin;
	
	protected TokenCommand(TokenPlugin plugin) {
		this.plugin = plugin;
		UserCache.asyncInit();
		ArgType<OfflinePlayer> playerType = new ArgType<>("offlineplayer", UserCache::getOfflinePlayer)
				.tabStream(sender -> Bukkit.getOnlinePlayers().stream().map(Player::getName));
		new CommandParser(plugin.getResource("command.rdcml")).setArgTypes(playerType).parse().register("", this);
	}
	
	@CommandHook("check")
	public void check(CommandSender sender, OfflinePlayer player) {
		sendMessage(sender, "lang.check",
				"%player%", player.getName(),
				"%tokens%", TokenAPI.get(player));
	}
	
	@CommandHook("give")
	public void give(CommandSender sender, OfflinePlayer player, int amount) {
		if (player == null) return;
		TokenAPI.give(player, amount);
		sendMessage(sender, "lang.manage.give",
				"%player%", player.getName(),
				"%given%", amount,
				"%tokens%", TokenAPI.get(player));
	}
	
	@CommandHook("take")
	public void take(CommandSender sender, OfflinePlayer player, int amount) {
		if (player == null) return;
		TokenAPI.take(player, amount);
		sendMessage(sender, "lang.manage.take",
				"%player%", player.getName(),
				"%taken%", amount,
				"%tokens%", TokenAPI.get(player));
	}
	
	@CommandHook("set")
	public void set(CommandSender sender, OfflinePlayer player, int amount) {
		if (player == null) return;
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
	
	@CommandHook("top")
	public void top(CommandSender sender) {
		Results results = TokenAPI.sql.queryResults("SELECT uuid,tokens FROM balances ORDER BY tokens DESC LIMIT 10;");
		int[] count = {1};
		results.forEach(r -> {
			UUID uuid = UUID.fromString(r.getString(1));
			int balance = r.get(2);
			sendMessage(sender, "lang.top",
					"%place%", count[0],
					"%player%", Bukkit.getOfflinePlayer(uuid).getName(),
					"%tokens%", balance);
			count[0]++;
		});
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
