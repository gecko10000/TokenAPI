package io.github.levtey.TokenAPI;

import java.sql.Connection;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

import redempt.redlib.sql.SQLCache;
import redempt.redlib.sql.SQLHelper;

public class TokenAPI {
	
	protected static SQLCache tokenCache;
	protected static SQLHelper sql;
	
	protected TokenAPI(TokenPlugin plugin) {
		Connection connection = SQLHelper.openSQLite(plugin.getDataFolder().toPath().resolve("balances.db"));
		sql = new SQLHelper(connection);
		sql.execute("CREATE TABLE IF NOT EXISTS balances ("
				+ "uuid STRING,"
				+ "tokens INT,"
				+ "PRIMARY KEY (uuid)"
				+ ");");
		tokenCache = sql.createCache("balances", "tokens", "uuid");
		sql.setCommitInterval(plugin.getConfig().getInt("auto-save-ticks"));
	}
	
	public static int get(OfflinePlayer player) {
		UUID uuid = player.getUniqueId();
		Integer bal = tokenCache.select(uuid.toString());
		if (bal == null) {
			TokenPlugin.initUUID(uuid);
			return 0;
		}
		return bal;
	}
	
	public static void set(OfflinePlayer player, int amount) {
		tokenCache.update(Math.max(0, amount), player.getUniqueId().toString());
	}
	
	public static void give(OfflinePlayer player, int amount) {
		String uuid = player.getUniqueId().toString();
		tokenCache.update((Integer) tokenCache.select(uuid) + amount, uuid);
	}
	
	public static void take(OfflinePlayer player, int amount) {
		String uuid = player.getUniqueId().toString();
		tokenCache.update(Math.max(0, (Integer) tokenCache.select(uuid) - amount), uuid);
	}
	
	public static boolean purchase(OfflinePlayer player, int amount) {
		String uuid = player.getUniqueId().toString();
		int balance = tokenCache.select(uuid);
		if (balance >= amount) {
			tokenCache.update(balance - amount, uuid);
			return true;
		}
		return false;
	}
	
	public static void save() {
		sql.commit();
	}
	
}
