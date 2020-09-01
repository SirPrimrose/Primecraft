package com.sonicjumper.primecraft.main.listeners;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sonicjumper.primecraft.main.PrimeCraftMain;
import com.sonicjumper.primecraft.main.effects.WorldEffectTimeStop;
import com.sonicjumper.primecraft.main.player.PlayerProfile;
import com.sonicjumper.primecraft.main.shop.ShopItem;
import com.sonicjumper.primecraft.main.util.PrimeUtility;
import com.sonicjumper.primecraft.main.weapons.ExplosiveArrow;
import com.sonicjumper.primecraft.main.weapons.KnockbackArrow;
import com.sonicjumper.primecraft.main.weapons.Mine;
import com.sonicjumper.primecraft.main.weapons.TimeStoppedArrow;

public class TieredItemsListener implements Listener {
	private Random rand = new Random();
	
	@EventHandler
	public void onPlayerPlacePressurePlate(BlockPlaceEvent event) {
		if(event.getBlockPlaced().getType().equals(Material.STONE_PLATE)) {
			if(event.getBlockReplacedState().getType().equals(Material.AIR)) {
				if(PrimeCraftMain.instance.game.isMapLoaded) {
					PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer(event.getPlayer());
					pp.decrementOffensiveCount(ShopItem.mine);
					PrimeCraftMain.instance.game.weaponHandler.addMineToList(new Mine(event.getBlockPlaced(), pp));
				}
			} else {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerPressPressurePad(PlayerInteractEvent event) {
		if(event.getAction().equals(Action.PHYSICAL)) {
			if(event.getClickedBlock().getType().equals(Material.STONE_PLATE)) {
				if(PrimeCraftMain.instance.game.isMapLoaded) {
					PrimeCraftMain.instance.game.currentGamemode.onPressurePlate(event.getClickedBlock(), PrimeCraftMain.instance.game.getProfileForPlayer(event.getPlayer()));
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerShootLightning(PlayerInteractEvent event) {
		if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Player p = event.getPlayer();
			if(p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasDisplayName()) {
				if(p.getItemInHand().getItemMeta().getDisplayName().contains("Lightning Rod")) {
					HashSet<Byte> set = new HashSet<Byte>();
					set.add((byte) Material.AIR.getId());
					set.add((byte) Material.VINE.getId());
					set.add((byte) Material.WATER.getId());
					set.add((byte) Material.LAVA.getId());
					set.add((byte) Material.DEAD_BUSH.getId());
					set.add((byte) Material.LADDER.getId());
					set.add((byte) Material.TORCH.getId());
					Block b = p.getTargetBlock(set, 100);
					if(b != null && !b.getType().equals(Material.AIR)) {
						if(p.getItemInHand().getAmount() - 1 == 0) {
							p.setItemInHand(null);
						} else {
							p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
						}
						PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer(p);
						PrimeCraftMain.instance.game.weaponHandler.addOwnerToLightning(p.getWorld().strikeLightning(b.getLocation()), pp);
						for(int i = 0; i < 3; i++) {
							PrimeCraftMain.instance.game.weaponHandler.addOwnerToLightning(p.getWorld().strikeLightning(b.getLocation().add(2.5D - rand.nextDouble() * 5.0D, 0.0D, 2.5D - rand.nextDouble() * 5.0D)), pp);
						}
						pp.decrementOffensiveCount(ShopItem.lightningRod);
					}
				} else if(p.getItemInHand().getItemMeta().getDisplayName().contains("Time Stopper")) {
					if(p.getItemInHand().getAmount() - 1 == 0) {
						p.setItemInHand(null);
					} else {
						p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
					}
					PlayerProfile user = PrimeCraftMain.instance.game.getProfileForPlayer(p);
					PrimeCraftMain.instance.specialEffectsHandler.activateWorldEffect(new WorldEffectTimeStop(p.getLocation(), 15, 20.0D));
					List<Entity> nearbyEntities = p.getNearbyEntities(20.0D, 20.0D, 20.0D);
					for(Entity e : nearbyEntities) {
						if(e instanceof Player) {
							PlayerProfile stopPlayer = PrimeCraftMain.instance.game.getProfileForPlayer((Player) e);
							if(!stopPlayer.getCurrentTeam().getTeamDetails().equals(user.getCurrentTeam().getTeamDetails())) {
								stopPlayer.setFrozenTicks(50);
								stopPlayer.getPlayer().sendMessage(ChatColor.AQUA + "You have been frozen in Time for a few seconds!");
							}
						}
						if(e instanceof Arrow) {
							Arrow stopArrow = (Arrow) e;
							if(stopArrow.getShooter() instanceof Player) {
								PlayerProfile arrowShooter = PrimeCraftMain.instance.game.getProfileForPlayer((Player) stopArrow.getShooter());
								if(!arrowShooter.getCurrentTeam().getTeamDetails().equals(user.getCurrentTeam().getTeamDetails())) {
									PrimeCraftMain.instance.game.weaponHandler.addSpecialArrow(new TimeStoppedArrow(stopArrow, arrowShooter, stopArrow.getVelocity(), 75));
								}
							} else {
								PrimeCraftMain.instance.game.weaponHandler.addSpecialArrow(new TimeStoppedArrow(stopArrow, null, stopArrow.getVelocity(), 75));
							}
						}
					}
					user.decrementTacticalCount(ShopItem.timeStopper);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFrozenPlayerMove(PlayerMoveEvent event) {
		if(PrimeCraftMain.instance.game.getProfileForPlayer(event.getPlayer()).isFrozen()) {
			Location from = event.getFrom();
			Location to = event.getTo();
			if(to.getY() >= from.getY()) {
				if(from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
					event.setTo(from);
				}
			}
		}
	}
	
	@EventHandler
	public void onFrozenPlayerAttack(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Player) {
			if(PrimeCraftMain.instance.game.getProfileForPlayer((Player) event.getDamager()).isFrozen()) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onFrozenPlayerSwitchInventory(PlayerItemHeldEvent event) {
		if(PrimeCraftMain.instance.game.getProfileForPlayer(event.getPlayer()).isFrozen()) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTeleportEnderPearl(PlayerTeleportEvent event) {
		if(event.getCause().equals(TeleportCause.ENDER_PEARL)) {
			PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer(event.getPlayer());
			if(PrimeCraftMain.instance.game.isLocationInSpawnRegion(event.getFrom()) || PrimeCraftMain.instance.game.isLocationInSpawnRegion(event.getTo())) {
				event.setCancelled(true);
			} else {
				if(pp.isFrozen()) {
					event.setCancelled(true);
				}
				pp.decrementTacticalCount(ShopItem.pearl);
			}
		}
	}
	
	@EventHandler
	public void onPlayerShootArrow(EntityShootBowEvent event) {
		if(event.getEntity() instanceof Player) {
			PlayerProfile shooter = PrimeCraftMain.instance.game.getProfileForPlayer((Player) event.getEntity());
			ItemStack arrowItem = shooter.getPlayer().getInventory().getItem(2);
			if(arrowItem != null && arrowItem.getItemMeta().hasDisplayName()) {
				if(arrowItem.getItemMeta().getDisplayName().equalsIgnoreCase("Explosive Arrow")) {
					PrimeCraftMain.instance.game.weaponHandler.addSpecialArrow(new ExplosiveArrow((Arrow) event.getProjectile(), shooter));
					shooter.decrementArrowCount(ShopItem.explosiveArrow);
				}
				if(arrowItem.getItemMeta().getDisplayName().equalsIgnoreCase("Knockback Arrow")) {
					PrimeCraftMain.instance.game.weaponHandler.addSpecialArrow(new KnockbackArrow((Arrow) event.getProjectile(), shooter));
					shooter.decrementArrowCount(ShopItem.knockbackArrow);
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerThrowPotion(PotionSplashEvent event) {
		if(event.getPotion().getShooter() instanceof Player) {
			PlayerProfile thrower = PrimeCraftMain.instance.game.getProfileForPlayer((Player) event.getPotion().getShooter());
			Potion p = Potion.fromItemStack(event.getPotion().getItem());
			for(PotionEffect pe : p.getEffects()) {
				if(pe.getType().equals(PotionEffectType.SLOW)) {
					thrower.decrementNegativePotionCount(ShopItem.slownessPotion);
				}
				if(pe.getType().equals(PotionEffectType.WEAKNESS)) {
					thrower.decrementNegativePotionCount(ShopItem.weaknessPotion);
				}
				if(pe.getType().equals(PotionEffectType.HARM)) {
					thrower.decrementNegativePotionCount(ShopItem.harmingPotion);
				}
				if(pe.getType().equals(PotionEffectType.WITHER) || pe.getType().equals(PotionEffectType.POISON)) {
					thrower.decrementNegativePotionCount(ShopItem.witherPotion);
				}
			}
			for(int i = 0; i < event.getAffectedEntities().size(); i++) {
				LivingEntity le = (LivingEntity) event.getAffectedEntities().toArray()[i];
				if(le instanceof Player) {
					PlayerProfile hitPlayer = PrimeCraftMain.instance.game.getProfileForPlayer((Player) le);
					if(hitPlayer.getCurrentTeam().getTeamDetails().equals(thrower.getCurrentTeam().getTeamDetails())) {
						event.setIntensity(le, 0.0D);
						thrower.getPlayer().sendMessage("That player is on your team.");
					} else {
						event.setIntensity(le, 1.0D);
						hitPlayer.addDamager(thrower);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerDrink(PlayerItemConsumeEvent event) {
		/*ItemStack usedItem = event.getItem();
		if(usedItem.getType().equals(Material.MILK_BUCKET)) {
			usedItem.setAmount(usedItem.getAmount() - 1);
			event.setItem(usedItem.getAmount() > 0 ? usedItem : null);
		}*/
		ItemStack usedItem = event.getItem();
		final PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer(event.getPlayer());
		if(usedItem.getType().equals(Material.MILK_BUCKET)) {
			pp.decrementTacticalCount(ShopItem.milkBucket);
			Bukkit.getScheduler().scheduleSyncDelayedTask(PrimeCraftMain.instance, new Runnable() {
				private Player player = pp.getPlayer();
				
				@Override
				public void run() {
					PrimeUtility.removeItemsFromPlayerInventory(player, Material.BUCKET);
				}
			}, 5);
		}
		if(usedItem.getType().equals(Material.POTION)) {
			Potion p = Potion.fromItemStack(usedItem);
			for(PotionEffect pe : p.getEffects()) {
				if(pe.getType().equals(PotionEffectType.SPEED)) {
					pp.decrementPositivePotionCount(ShopItem.speedPotion);
					pp.getPlayer().removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
					pp.getPlayer().removePotionEffect(PotionEffectType.REGENERATION);
					pp.getPlayer().removePotionEffect(PotionEffectType.HEALTH_BOOST);
				}
				if(pe.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
					pp.decrementPositivePotionCount(ShopItem.strengthPotion);
					pp.getPlayer().removePotionEffect(PotionEffectType.SPEED);
					pp.getPlayer().removePotionEffect(PotionEffectType.REGENERATION);
					pp.getPlayer().removePotionEffect(PotionEffectType.HEALTH_BOOST);
				}
				if(pe.getType().equals(PotionEffectType.HEAL)) {
					pp.decrementPositivePotionCount(ShopItem.healthPotion);
				}
				if(pe.getType().equals(PotionEffectType.REGENERATION)) {
					pp.decrementPositivePotionCount(ShopItem.regenPotion);
					pp.getPlayer().removePotionEffect(PotionEffectType.SPEED);
					pp.getPlayer().removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
				}
			}
			Bukkit.getScheduler().scheduleSyncDelayedTask(PrimeCraftMain.instance, new Runnable() {
				private Player player = pp.getPlayer();
				
				@Override
				public void run() {
					PrimeUtility.removeItemsFromPlayerInventory(player, Material.GLASS_BOTTLE);
				}
			}, 5);
		}
	}
}
