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
				+ "tokens BIGINT,"
				+ "PRIMARY KEY (uuid)"
				+ ");");
		tokenCache = sql.createCache("balances", "tokens", "uuid");
		sql.setCommitInterval(plugin.getConfig().getInt("auto-save-ticks"));
	}
	
	public static long get(OfflinePlayer player) {
		UUID uuid = player.getUniqueId();
		Long bal = tokenCache.selectLong(uuid.toString());
		if (bal == null) {
			TokenPlugin.initUUID(uuid);
			return 0;
		}
		return bal;
	}
	
	public static void set(OfflinePlayer player, long amount) {
		tokenCache.update(Math.max(0, amount), player.getUniqueId().toString());
	}
	
	public static void give(OfflinePlayer player, long amount) {
		String uuid = player.getUniqueId().toString();
		tokenCache.update(tokenCache.selectLong(uuid) + amount, uuid);
	}
	
	public static void take(OfflinePlayer player, long amount) {
		String uuid = player.getUniqueId().toString();
		tokenCache.update(Math.max(0, tokenCache.selectLong(uuid) - amount), uuid);
	}
	
	public static boolean purchase(OfflinePlayer player, long amount) {
		String uuid = player.getUniqueId().toString();
		long balance = tokenCache.selectLong(uuid);
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
