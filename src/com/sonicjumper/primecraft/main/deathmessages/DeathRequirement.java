package com.sonicjumper.primecraft.main.deathmessages;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public abstract class DeathRequirement {
	/**
	 * Does the given player and killer meets the requirements for this death message to fire
	 * @param playerKilled Player who is dying.
	 * @param killer Player who is killing; null if not PvP kill.
	 */
	public abstract boolean meetsRequirement(PlayerProfile playerKilled, PlayerProfile killer);
}
