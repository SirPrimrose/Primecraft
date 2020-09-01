package com.sonicjumper.primecraft.main.gamemode;

import org.bukkit.block.Block;

import com.sonicjumper.primecraft.main.gamemode.CapturePoint.CaptureLevel;

public class CapturePointBlock {
	private Block pointBlock;
	private CaptureLevel activatingLevel;
	
	public CapturePointBlock(Block clickedBlock, CaptureLevel cl) {
		pointBlock = clickedBlock;
		activatingLevel = cl;
	}

	public Block getBlock() {
		return pointBlock;
	}
	
	public CaptureLevel getActivatingLevel() {
		return activatingLevel;
	}
}
