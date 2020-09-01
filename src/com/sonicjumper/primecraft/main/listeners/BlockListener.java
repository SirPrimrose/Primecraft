package com.sonicjumper.primecraft.main.listeners;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.util.Vector;

import com.sonicjumper.primecraft.main.PrimeCraftMain;
import com.sonicjumper.primecraft.main.player.MoneyItem;
import com.sonicjumper.primecraft.main.util.PrimeUtility;

public class BlockListener implements Listener {
	private ArrayList<FallingBlock> fallingBlocks= new ArrayList<FallingBlock>();
	private Random rand = new Random();
	
	@EventHandler
	public void onPlayerBreakBlock(BlockBreakEvent event) {
		if(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
			event.getPlayer().sendMessage("Don't try to break things!");
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerPlaceBlock(BlockPlaceEvent event) {
		if(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
			if(!event.getBlockPlaced().getType().equals(Material.STONE_PLATE)) {
				event.getPlayer().sendMessage("Don't try to place things!");
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerBreakBlock(BlockDamageEvent event) {
		if(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerPressButton(PlayerInteractEvent event) {
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if(event.getClickedBlock().getType().equals(Material.STONE_BUTTON)) {
				if(PrimeCraftMain.instance.game.isMapLoaded) {
					PrimeCraftMain.instance.game.currentGamemode.onButtonPress(event.getClickedBlock(), PrimeCraftMain.instance.game.getProfileForPlayer(event.getPlayer()));
				}
			}
		}
	}
	
	@EventHandler
	public void onPaintingBreak(HangingBreakByEntityEvent event) {
		if(event.getCause().equals(RemoveCause.EXPLOSION)) {
			event.setCancelled(true);
		}
		if(event.getCause().equals(RemoveCause.ENTITY) && event.getRemover() instanceof Player) {
			Player p = (Player) event.getRemover();
			if(!p.getGameMode().equals(GameMode.CREATIVE)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void canRotate(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		Player player = event.getPlayer();
		if (!entity.getType().equals(EntityType.ITEM_FRAME))
			return;
		ItemFrame iFrame = (ItemFrame)entity;
		if ((iFrame.getItem().equals(null)) || (iFrame.getItem().getType().equals(Material.AIR)))
			return;
		if (!player.getGameMode().equals(GameMode.CREATIVE)) {
			event.setCancelled(true);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void ItemRemoval(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player && event.getEntity().getType().equals(EntityType.ITEM_FRAME)) {
			Player player = (Player)event.getDamager();
			if (!player.getGameMode().equals(GameMode.CREATIVE)) {
				event.setCancelled(true);
			}
		}

		if (event.getDamager() instanceof Projectile && event.getEntity().getType().equals(EntityType.ITEM_FRAME)) {
			Projectile proj = (Projectile)event.getDamager();
			Player player = (Player)proj.getShooter();
			if (!player.getGameMode().equals(GameMode.CREATIVE)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {
		if(!event.getCause().equals(IgniteCause.FLINT_AND_STEEL)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onRain(WeatherChangeEvent event) {
		if(event.toWeatherState()) {
			event.setCancelled(true);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onTNTExplosion(EntityExplodeEvent event) {
		ArrayList<Block> destroyed = (ArrayList<Block>) event.blockList();
		for (int i = 0; i < destroyed.size(); i++) {
			Block b = destroyed.get(i);
			if(!b.getType().equals(Material.AIR) && !b.getType().equals(Material.TNT)) {
				FallingBlock block = b.getWorld().spawnFallingBlock(b.getLocation(), b.getType(), b.getData());
				fallingBlocks.add(block);
				float x = (float) (Math.random() * 2.0D - 1.0D);
				float y = (float) (Math.random() * 3.0D + 1.5D);
				float z = (float) (Math.random() * 2.0D - 1.0D);
				block.setVelocity(new Vector(x, y, z));
				block.setDropItem(false);
			}
		}
		event.setCancelled(true);
		//event.setYield(0.0F);
		//event.blockList().clear();
		// Have these two lines because the blocks still get destroyed client-side, even though nothing actually breaks
		//event.setCancelled(true);
		//event.getLocation().getWorld().createExplosion(event.getLocation().getX(), event.getLocation().getY(), event.getLocation().getZ(), 4.0F, false, false);
	}
	
	@EventHandler
	public void onEntityChange(EntityChangeBlockEvent event) {
		if(event.getEntity() instanceof FallingBlock) {
			if(fallingBlocks.contains(event.getEntity())) {
				event.setCancelled(true);
				fallingBlocks.remove(event.getEntity());
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onIronGolemAttack(EntityDamageByEntityEvent event) {
		if(event.getDamager().getType().equals(EntityType.IRON_GOLEM) && event.getEntity() instanceof Player) {
			event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.BLAZE_BREATH, 1.0F, 0.5F);
			int blocks = 6;
			double increment = (Math.PI * 2.0D) / blocks;
			for(int i = 0; i < blocks; i++) {
				double x = Math.cos(i * increment);
				double z = Math.sin(i * increment);
				Location loc = new Location(event.getEntity().getWorld(), event.getEntity().getLocation().getX() + x, event.getEntity().getLocation().getY(), event.getEntity().getLocation().getZ() + z);
				FallingBlock block = (FallingBlock) event.getDamager().getWorld().spawnFallingBlock(loc, Material.ENDER_STONE, (byte) 0);
				block.setVelocity(new Vector(0.0D, 0.5D, 0.0D));
				block.setDropItem(false);
				fallingBlocks.add(block);
			}
		}
	}
	
	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		if(event.getEntityType().equals(EntityType.IRON_GOLEM) && event.getReason().equals(TargetReason.TARGET_ATTACKED_ENTITY)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if(event.getEntityType().equals(EntityType.IRON_GOLEM)) {
			event.setDroppedExp(0);
			event.getDrops().clear();
			for(int i = 0; i < 20; i++) {
				Item item = event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), PrimeUtility.getNonStackingItemStack(MoneyItem.getRandomMoneyItem().getMaterial()));
				item.setPickupDelay(40);
				item.setVelocity(new Vector(0.125D - (rand.nextDouble() * 0.25D), rand.nextDouble() * 1.0D + 1.0D, 0.125D - (rand.nextDouble() * 0.25D)));
			}
		}
		if(event.getEntityType().equals(EntityType.ZOMBIE) || event.getEntityType().equals(EntityType.PIG_ZOMBIE) || event.getEntityType().equals(EntityType.SKELETON)) {
			event.setDroppedExp(0);
			event.getDrops().clear();
			for(int i = 0; i < 4; i++) {
				Item item = event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), PrimeUtility.getNonStackingItemStack(MoneyItem.getRandomMoneyItem().getMaterial()));
				item.setPickupDelay(40);
				item.setVelocity(new Vector(0.125D - (rand.nextDouble() * 0.25D), rand.nextDouble() * 1.0D + 1.0D, 0.125D - (rand.nextDouble() * 0.25D)));
			}
		}
	}
}
