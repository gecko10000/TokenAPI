package io.github.levtey.TokenAPI;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TokenPlugin extends JavaPlugin {
	
	private static TokenPlugin instance;
	public static TokenPlugin getInstance() { return instance; }

	public void onEnable() {
		saveDefaultConfig();
		new TokenAPI(this);
		new TokenCommand(this);
		new Listeners(this);
		Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).forEach(TokenPlugin::initUUID);
	}
	
	public void onDisable() {
		TokenAPI.save();
		TokenAPI.sql.close();
	}
	
	protected static void initUUID(UUID uuid) {
		TokenAPI.sql.execute("INSERT OR IGNORE INTO balances (uuid, tokens) "
				+ "VALUES (?, ?);", uuid.toString(), 0);
		TokenAPI.sql.commit();
	}
	
}
