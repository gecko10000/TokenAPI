package io.github.levtey.TokenAPI;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listeners implements Listener {
	
	private TokenPlugin plugin;
	
	public Listeners(TokenPlugin plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void on(PlayerJoinEvent evt) {
		TokenPlugin.initUUID(evt.getPlayer().getUniqueId());
	}

}
