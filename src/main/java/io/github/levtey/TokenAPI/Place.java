package io.github.levtey.TokenAPI;

import java.util.UUID;

public class Place {
	
	public final UUID uuid;
	public final long amount;
	
	public Place(UUID uuid, long amount) {
		this.uuid = uuid;
		this.amount = amount;
	}

}
