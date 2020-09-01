package com.sonicjumper.primecraft.main.regionevents;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sonicjumper.primecraft.main.PrimeCraftMain;

public class RegionEventsListener implements Listener {
	private WorldGuardPlugin wgPlugin;
	private Map<Player, Set<ProtectedRegion>> playerRegions;

	public RegionEventsListener(WorldGuardPlugin wgPlugin) {
		this.wgPlugin = wgPlugin;

		this.playerRegions = new HashMap();
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent e) {
		Set<ProtectedRegion> regions = (Set)this.playerRegions.remove(e.getPlayer());
		if (regions != null) {
			for (ProtectedRegion region : regions)
			{
				RegionLeaveEvent leaveEvent = new RegionLeaveEvent(region, e.getPlayer(), MovementWay.DISCONNECT);
				RegionLeftEvent leftEvent = new RegionLeftEvent(region, e.getPlayer(), MovementWay.DISCONNECT);

				PrimeCraftMain.instance.getServer().getPluginManager().callEvent(leaveEvent);
				PrimeCraftMain.instance.getServer().getPluginManager().callEvent(leftEvent);
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Set<ProtectedRegion> regions = (Set)this.playerRegions.remove(e.getPlayer());
		if (regions != null) {
			for (ProtectedRegion region : regions)
			{
				RegionLeaveEvent leaveEvent = new RegionLeaveEvent(region, e.getPlayer(), MovementWay.DISCONNECT);
				RegionLeftEvent leftEvent = new RegionLeftEvent(region, e.getPlayer(), MovementWay.DISCONNECT);

				PrimeCraftMain.instance.getServer().getPluginManager().callEvent(leaveEvent);
				PrimeCraftMain.instance.getServer().getPluginManager().callEvent(leftEvent);
			}
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		e.setCancelled(updateRegions(e.getPlayer(), MovementWay.MOVE, e.getTo()));
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		e.setCancelled(updateRegions(e.getPlayer(), MovementWay.TELEPORT, e.getTo()));
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		updateRegions(e.getPlayer(), MovementWay.SPAWN, e.getPlayer().getLocation());
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		updateRegions(e.getPlayer(), MovementWay.SPAWN, e.getRespawnLocation());
	}

	private synchronized boolean updateRegions(final Player player, final MovementWay movement, Location to) {
		Set<ProtectedRegion> regions;
		if (this.playerRegions.get(player) == null) {
			regions = new HashSet();
		} else {
			regions = new HashSet((Collection)this.playerRegions.get(player));
		}
		Set<ProtectedRegion> oldRegions = new HashSet(regions);

		RegionManager rm = this.wgPlugin.getRegionManager(to.getWorld());
		if (rm == null) {
			return false;
		}
		ApplicableRegionSet appRegions = rm.getApplicableRegions(to);
		for (final ProtectedRegion region : appRegions) {
			if (!regions.contains(region))
			{
				RegionEnterEvent e = new RegionEnterEvent(region, player, movement);

				PrimeCraftMain.instance.getServer().getPluginManager().callEvent(e);
				if (e.isCancelled())
				{
					regions.clear();
					regions.addAll(oldRegions);

					return true;
				}
				new Thread()
				{
					public void run()
					{
						try
						{
							sleep(50L);
						}
						catch (InterruptedException ex) {}
						RegionEnteredEvent e = new RegionEnteredEvent(region, player, movement);

						PrimeCraftMain.instance.getServer().getPluginManager().callEvent(e);
					}
				}.start();
				regions.add(region);
			}
		}
		Collection<ProtectedRegion> app = (Collection)getPrivateValue(appRegions, "applicable");
		Iterator<ProtectedRegion> itr = regions.iterator();
		while (itr.hasNext())
		{
			final ProtectedRegion region = (ProtectedRegion)itr.next();
			if (!app.contains(region)) {
				if (rm.getRegion(region.getId()) != region)
				{
					itr.remove();
				}
				else
				{
					RegionLeaveEvent e = new RegionLeaveEvent(region, player, movement);

					PrimeCraftMain.instance.getServer().getPluginManager().callEvent(e);
					if (e.isCancelled())
					{
						regions.clear();
						regions.addAll(oldRegions);
						return true;
					}
					new Thread()
					{
						public void run()
						{
							try
							{
								sleep(50L);
							}
							catch (InterruptedException ex) {}
							RegionLeftEvent e = new RegionLeftEvent(region, player, movement);

							PrimeCraftMain.instance.getServer().getPluginManager().callEvent(e);
						}
					}.start();
					itr.remove();
				}
			}
		}
		this.playerRegions.put(player, regions);
		return false;
	}

	private Object getPrivateValue(Object obj, String name) {
		try
		{
			Field f = obj.getClass().getDeclaredField(name);
			f.setAccessible(true);
			return f.get(obj);
		}
		catch (Exception ex) {}
		return null;
	}
}
