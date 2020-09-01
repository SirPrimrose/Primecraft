package com.sonicjumper.primecraft.main.player;

public class Prize {
	//public static Prize[] prizeList = new Prize[64];
	
	/*public static Prize starterPrize = new Prize(0, "Start Parkour");
	public static Prize beginnerPrize = new Prize(1, "Beginner Area");
	public static Prize fenceJumping = new Prize(2, "Fence Jumping");
	public static Prize niceTry = new Prize(3, "Nice Try");
	public static Prize glassPanes = new Prize(4, "Glass Panes");
	public static Prize parkourLobby = new Prize(6, "Found Parkour");
	public static Prize twoFortEasterEgg = new Prize(7, "2Fort Prize");
	public static Prize overVoidEasterEgg = new Prize(8, "OverVoid Prize");
	public static Prize nuketownEasterEgg = new Prize(9, "Nuketown Prize");
	public static Prize battlefieldEasterEgg = new Prize(10, "Battle Prize");
	public static Prize redVsBlueEasterEgg = new Prize(11, "RedVsBlue Prize");
	public static Prize desertConquestEasterEgg = new Prize(12, "Desert Prize");
	public static Prize forestWarfareEasterEgg = new Prize(13, "Forest Prize");
	public static Prize aviatorEasterEgg2 = new Prize(14, "Cockpit");
	public static Prize aviatorEasterEgg = new Prize(15, "Aviator Prize");
	public static Prize goldCow1 = new Prize(16, "Golden Cow I");
	public static Prize mineshaftLoot = new Prize(17, "Minecart Loot");
	public static Prize lavaPrize = new Prize(18, "Lava Prize");
	public static Prize lostSoulPrize = new Prize(19, "Lost Soul Prize");
	public static Prize tracksPrize = new Prize(20, "Tracks Prize");
	public static Prize goldCow2 = new Prize(21, "Golden Cow II");
	public static Prize hothPrize = new Prize(22, "Hoth Prize");
	public static Prize rebelPrize = new Prize(23, "Rebel Prize");
	public static Prize imperialPrize = new Prize(24, "Imperial Prize");
	public static Prize goldRiceBowl = new Prize(25, "Gold Rice Bowl");
	public static Prize chopsticks = new Prize(26, "Chopsticks");
	public static Prize skyFortPrize = new Prize(27, "SkyFort Prize");
	public static Prize orbitalPrize = new Prize(28, "Orbital Prize");
	public static Prize dirigible1Prize = new Prize(29, "Dirigible 1");
	public static Prize dirigible2Prize = new Prize(30, "Dirigible 2");
	public static Prize dirigiblePrize = new Prize(31, "Dirigible Prize");
	public static Prize zoo1Prize = new Prize(32, "Zoo I");
	public static Prize zoo2Prize = new Prize(33, "Zoo II");
	public static Prize zoo3Prize = new Prize(34, "Zoo III");
	public static Prize turbinePrize = new Prize(35, "Turbine Prize");
	public static Prize terminalPrize = new Prize(36, "Terminal Prize");
	public static Prize terminal2Prize = new Prize(37, "Terminal 2");
	public static Prize terminal3Prize = new Prize(38, "Cashier Change");
	public static Prize genericName = new Prize(39, "Generic Name");
	public static Prize blahBlahBlah = new Prize(40, "blah blah blah");
	public static Prize ruinsPrize = new Prize(41, "Ruins Prize");
	public static Prize lavaPrizeII = new Prize(42, "Lava Prize II");
	public static Prize wellTreasure = new Prize(43, "Well Treasure");*/
	
	/*public int prizeId;
	public String prizeName;
	//For now control the amount by the sign
	//private int prizeAmount;
	
	public Prize(int id, String name) {
		prizeId = id;
		prizeName = name;
		
		if(prizeList[id] == null) {
			prizeList[id] = this;
		} else {
			throw new IllegalArgumentException("Duplicate Prize id: " + id);
		}
	}*/
	
	public static int getPrizeIdForString(String name) {
		int idScore = 0;
		for(char c : name.toCharArray()) {
			idScore += (int) c;
		}
		return idScore;
	}
}
