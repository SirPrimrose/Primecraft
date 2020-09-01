package com.sonicjumper.primecraft.main;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.sonicjumper.primecraft.main.player.PlayerProfile;
import com.sonicjumper.primecraft.main.teams.PlayerTeam.TeamDetails;

public class SoundManager {
	public enum PrimeSound {
		RedTeamJoin(Sound.BLAZE_BREATH),
		BlueTeamJoin(Sound.BLAZE_DEATH),
		YellowTeamJoin(Sound.BLAZE_HIT),
		PurpleTeamJoin(Sound.CAT_HISS),
		RedTeamScore(Sound.CAT_HIT),
		BlueTeamScore(Sound.CAT_MEOW),
		YellowTeamScore(Sound.CAT_PURR),
		PurpleTeamScore(Sound.CAT_PURREOW),
		RedNearWin(Sound.CHICKEN_IDLE),
		BlueNearWin(Sound.CHICKEN_HURT),
		YellowNearWin(Sound.CHICKEN_WALK),
		PurpleNearWin(Sound.CHICKEN_EGG_POP),
		RedFlagReset(Sound.COW_IDLE),
		BlueFlagReset(Sound.COW_HURT),
		YellowFlagReset(Sound.COW_WALK),
		PurpleFlagReset(Sound.CREEPER_HISS),
		ArtifactReset(Sound.CREEPER_DEATH),
		CommandPost1Captured(Sound.ENDERMAN_DEATH),
		CommandPost2Captured(Sound.ENDERMAN_HIT),
		CommandPost3Captured(Sound.ENDERMAN_IDLE),
		CommandPost4Captured(Sound.ENDERMAN_TELEPORT),
		CommandPost5Captured(Sound.ENDERMAN_SCREAM),
		CommandPost6Captured(Sound.ENDERMAN_STARE),
		CommandPost7Captured(Sound.GHAST_SCREAM2),
		CommandPost8Captured(Sound.GHAST_CHARGE),
		CommandPost9Captured(Sound.GHAST_DEATH),
		KillStreak10(Sound.GHAST_FIREBALL),
		KillStreak15(Sound.GHAST_MOAN),
		KillStreak20(Sound.GHAST_SCREAM),
		KillStreak25(Sound.IRONGOLEM_DEATH),
		KillStreak50(Sound.IRONGOLEM_HIT),
		MultiKill2(Sound.IRONGOLEM_THROW),
		MultiKill3(Sound.IRONGOLEM_WALK),
		MultiKill4(Sound.MAGMACUBE_WALK),
		MultiKill5(Sound.MAGMACUBE_JUMP),
		MultiKill6(Sound.MAGMACUBE_WALK2),
		MultiKill7(Sound.PIG_IDLE),
		MultiKill8(Sound.PIG_DEATH),
		MultiKill9(Sound.PIG_WALK),
		PlayerTiered(Sound.SHEEP_IDLE),
		PlayerRebirth(Sound.SHEEP_WALK);
		
		private Sound representedSound;
		
		PrimeSound(Sound sound) {
			representedSound = sound;
		}
	}
	
	private static void playGlobalSoundEffect(PrimeSound sound, float pitch, float volume) {
		playGlobalSoundEffect(sound.representedSound, pitch, volume);
	}
	
	/**
	 * Sends a sound effect enum to every player on the server. Checks to make sure players have PrimeSounds on.
	 * @param sound Sound effect from Sound enum
	 * @param pitch Pitch of sound
	 * @param volume Volume of sound
	 */
	private static void playGlobalSoundEffect(Sound sound, float pitch, float volume) {
		for(Player p : Bukkit.getOnlinePlayers()) {
			PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer(p);
			if(pp.isUsingSounds()) {
				p.playSound(p.getLocation(), sound, volume, pitch);
			}
		}
	}

	/**
	 * Sends a sound effect enum the given player. Checks to make sure the player has PrimeSounds on.
	 * @param p Player which to send the effect to
	 * @param sound Sound effect from Sound enum
	 * @param pitch Pitch of sound
	 * @param volume Volume of sound
	 */
	private static void playPlayerSoundEffect(Player p, PrimeSound sound, float pitch, float volume) {
		playPlayerSoundEffect(p, sound.representedSound, pitch, volume);
	}
	
	private static void playPlayerSoundEffect(Player p, Sound sound, float pitch, float volume) {
		PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer(p);
		if(pp.isUsingSounds()) {
			p.playSound(p.getLocation(), sound, volume, pitch);
		}
	}
	
	/**
	 * Sends a named sound effect to every player on the server. Not version friendly.
	 * @param name Sound name, EX: note.pling
	 * @param pitch Pitch of sound
	 * @param volume Volume of sound
	 */
	/*private void playGlobalSoundEffect(String name, float pitch, float volume) {
		for(Player p : Bukkit.getOnlinePlayers()) {
			PacketPlayOutNamedSoundEffect soundEffect = new PacketPlayOutNamedSoundEffect(name, p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), pitch, volume);
			
			ReflectionUtil.sendPacket(p, soundEffect);
		}
	}*/
	
	public static void playTeamJoin(Player player, TeamDetails team) {
		if(team.equals(TeamDetails.Red)) {
			playPlayerSoundEffect(player, PrimeSound.RedTeamJoin, 1.0F, 1.0F);
		} else if(team.equals(TeamDetails.Blue)) {
			playPlayerSoundEffect(player, PrimeSound.BlueTeamJoin, 1.0F, 1.0F);
		} else if(team.equals(TeamDetails.Yellow)) {
			playPlayerSoundEffect(player, PrimeSound.YellowTeamJoin, 1.0F, 1.0F);
		} else if(team.equals(TeamDetails.Purple)) {
			playPlayerSoundEffect(player, PrimeSound.PurpleTeamJoin, 1.0F, 1.0F);
		}
	}

	public static void playTeamScore(TeamDetails team) {
		if(team.equals(TeamDetails.Red)) {
			playGlobalSoundEffect(PrimeSound.RedTeamScore, 1.0F, 1.0F);
		} else if(team.equals(TeamDetails.Blue)) {
			playGlobalSoundEffect(PrimeSound.BlueTeamScore, 1.0F, 1.0F);
		} else if(team.equals(TeamDetails.Yellow)) {
			playGlobalSoundEffect(PrimeSound.YellowTeamScore, 1.0F, 1.0F);
		} else if(team.equals(TeamDetails.Purple)) {
			playGlobalSoundEffect(PrimeSound.PurpleTeamScore, 1.0F, 1.0F);
		}
	}
	
	public static void playTeamNearWin(TeamDetails team) {
		if(team.equals(TeamDetails.Red)) {
			playGlobalSoundEffect(PrimeSound.RedNearWin, 1.0F, 1.0F);
		} else if(team.equals(TeamDetails.Blue)) {
			playGlobalSoundEffect(PrimeSound.BlueNearWin, 1.0F, 1.0F);
		} else if(team.equals(TeamDetails.Yellow)) {
			playGlobalSoundEffect(PrimeSound.YellowNearWin, 1.0F, 1.0F);
		} else if(team.equals(TeamDetails.Purple)) {
			playGlobalSoundEffect(PrimeSound.PurpleNearWin, 1.0F, 1.0F);
		}
	}
	
	public static void playTeamFlagReset(TeamDetails team) {
		if(team.equals(TeamDetails.Red)) {
			playGlobalSoundEffect(PrimeSound.RedFlagReset, 1.0F, 1.0F);
		} else if(team.equals(TeamDetails.Blue)) {
			playGlobalSoundEffect(PrimeSound.BlueFlagReset, 1.0F, 1.0F);
		} else if(team.equals(TeamDetails.Yellow)) {
			playGlobalSoundEffect(PrimeSound.YellowFlagReset, 1.0F, 1.0F);
		} else if(team.equals(TeamDetails.Purple)) {
			playGlobalSoundEffect(PrimeSound.PurpleFlagReset, 1.0F, 1.0F);
		} else if(team.equals(TeamDetails.Neutral)) {
			playGlobalSoundEffect(PrimeSound.ArtifactReset, 1.0F, 1.0F);
		}
	}
	
	public static void playCommandPostTaken(int postID) {
		switch(postID) {
		case 1:
			playGlobalSoundEffect(PrimeSound.CommandPost1Captured, 1.0F, 1.0F);
			break;
		case 2:
			playGlobalSoundEffect(PrimeSound.CommandPost2Captured, 1.0F, 1.0F);
			break;
		case 3:
			playGlobalSoundEffect(PrimeSound.CommandPost3Captured, 1.0F, 1.0F);
			break;
		case 4:
			playGlobalSoundEffect(PrimeSound.CommandPost4Captured, 1.0F, 1.0F);
			break;
		case 5:
			playGlobalSoundEffect(PrimeSound.CommandPost5Captured, 1.0F, 1.0F);
			break;
		case 6:
			playGlobalSoundEffect(PrimeSound.CommandPost6Captured, 1.0F, 1.0F);
			break;
		case 7:
			playGlobalSoundEffect(PrimeSound.CommandPost7Captured, 1.0F, 1.0F);
			break;
		case 8:
			playGlobalSoundEffect(PrimeSound.CommandPost8Captured, 1.0F, 1.0F);
			break;
		case 9:
			playGlobalSoundEffect(PrimeSound.CommandPost9Captured, 1.0F, 1.0F);
			break;
		}
	}
	
	public static void playKillStreak(Player p, int killStreakCount) {
		switch(killStreakCount) {
		case 10:
			playPlayerSoundEffect(p, PrimeSound.KillStreak10, 1.0F, 1.0F);
			break;
		case 15:
			playPlayerSoundEffect(p, PrimeSound.KillStreak15, 1.0F, 1.0F);
			break;
		case 20:
			playPlayerSoundEffect(p, PrimeSound.KillStreak20, 1.0F, 1.0F);
			break;
		case 25:
			playPlayerSoundEffect(p, PrimeSound.KillStreak25, 1.0F, 1.0F);
			break;
		case 50:
			playPlayerSoundEffect(p, PrimeSound.KillStreak50, 1.0F, 1.0F);
			break;
		}
	}
	
	public static void playMultiKill(Player p, int multiKillCount) {
		switch(multiKillCount) {
		case 2:
			playPlayerSoundEffect(p, PrimeSound.MultiKill2, 1.0F, 1.0F);
			break;
		case 3:
			playPlayerSoundEffect(p, PrimeSound.MultiKill3, 1.0F, 1.0F);
			break;
		case 4:
			playPlayerSoundEffect(p, PrimeSound.MultiKill4, 1.0F, 1.0F);
			break;
		case 5:
			playPlayerSoundEffect(p, PrimeSound.MultiKill5, 1.0F, 1.0F);
			break;
		case 6:
			playPlayerSoundEffect(p, PrimeSound.MultiKill6, 1.0F, 1.0F);
			break;
		case 7:
			playPlayerSoundEffect(p, PrimeSound.MultiKill7, 1.0F, 1.0F);
			break;
		case 8:
			playPlayerSoundEffect(p, PrimeSound.MultiKill8, 1.0F, 1.0F);
			break;
		case 9:
			playPlayerSoundEffect(p, PrimeSound.MultiKill9, 1.0F, 1.0F);
			break;
		}
	}
	
	public static void playPlayerTiered() {
		playGlobalSoundEffect(PrimeSound.PlayerTiered, 1.0F, 1.0F);
	}
	
	public static void playPlayerRebirth() {
		playGlobalSoundEffect(PrimeSound.PlayerRebirth, 1.0F, 1.0F);
	}
}
