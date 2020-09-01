package com.sonicjumper.primecraft.votifier;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.sonicjumper.primecraft.main.PrimeCraftMain;
import com.sonicjumper.primecraft.main.player.PlayerProfile;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

public class VoterListener implements Listener {
	static int totalVotes;
	
	@EventHandler
	public void onVoteComeIn(VotifierEvent event) {
		Vote v = event.getVote();
		PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForName(v.getUsername());
		if(pp != null) {
			pp.getPlayerStats().addVote();
			totalVotes += 1;
			if(totalVotes % 100 == 0) {
				PrimeCraftMain.instance.game.bonusManager.addTimeToDoubleCoins(3600);
			}
		}
	}
	
	public static void saveVotes() {
		PrimeCraftMain.instance.getGameConfig().set("VoteTotal", totalVotes);
	}
	
	public static void loadVotes() {
		totalVotes = PrimeCraftMain.instance.getGameConfig().getInt("VoteTotal");
	}
}
