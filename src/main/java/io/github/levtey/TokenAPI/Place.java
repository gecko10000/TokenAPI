package io.github.levtey.TokenAPI;

import java.util.UUID;

public class Place {
	
	public final UUID uuid;
	public final int amount;
	
	public Place(UUID uuid, int amount) {
		this.uuid = uuid;
		this.amount = amount;
	}

}
