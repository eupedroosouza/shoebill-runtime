/**
 * Copyright (C) 2011-2012 MK124
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.gtaun.shoebill;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.gtaun.shoebill.object.Dialog;
import net.gtaun.shoebill.object.Label;
import net.gtaun.shoebill.object.Menu;
import net.gtaun.shoebill.object.Pickup;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.PlayerLabel;
import net.gtaun.shoebill.object.PlayerObject;
import net.gtaun.shoebill.object.SampObject;
import net.gtaun.shoebill.object.Server;
import net.gtaun.shoebill.object.Textdraw;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.shoebill.object.World;
import net.gtaun.shoebill.object.Zone;
import net.gtaun.shoebill.samp.AbstractSampCallbackHandler;
import net.gtaun.shoebill.samp.SampCallbackHandler;
import net.gtaun.util.event.EventManagerImpl;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 
 * 
 * @author MK124
 */
public class SampObjectStoreImpl implements SampObjectStore
{
	public static final int MAX_PLAYERS =				500;
	public static final int MAX_VEHICLES =				2000;
	public static final int MAX_OBJECTS =				1000;
	public static final int MAX_ZONES =					1024;
	public static final int MAX_TEXT_DRAWS =			2048;
	public static final int MAX_PLAYER_TEXT_DRAWS =		256;
	public static final int MAX_MENUS =					128;
	public static final int MAX_GLOBAL_LABELS =			1024;
	public static final int MAX_PLAYER_LABELS =			1024;
	public static final int MAX_PICKUPS =				4096;
	
	
	private static <T> Collection<T> getInstances(Object[] items, Class<T> cls)
	{
		Collection<T> list = new ArrayList<>();
		if (items != null) for (Object item : items)
		{
			if (cls.isInstance(item)) list.add(cls.cast(item));
		}
		return list;
	}
	
	private static <T> void clearUnusedReferences(Collection<Reference<T>> collection)
	{
		Iterator<Reference<T>> iterator = collection.iterator();
		while(iterator.hasNext())
		{
			if(iterator.next().get() == null) iterator.remove();
		}
	}
	
	
	private SampCallbackHandler callbackHandler;
	
	private Server server;
	private World world;
	
	private Player[] players = new Player[MAX_PLAYERS];
	private Vehicle[] vehicles = new Vehicle[MAX_VEHICLES];
	private SampObject[] objects = new SampObject[MAX_OBJECTS];
	private PlayerObject[][] playerObjectsArrays = new PlayerObject[MAX_PLAYERS][];
	private Pickup[] pickups = new Pickup[MAX_PICKUPS];
	private Label[] labels = new Label[MAX_GLOBAL_LABELS];
	private PlayerLabel[][] playerLabelsArrays = new PlayerLabel[MAX_PLAYERS][];
	private Textdraw[] textdraws = new Textdraw[MAX_TEXT_DRAWS];
	private Zone[] zones = new Zone[MAX_ZONES];
	private Menu[] menus = new Menu[MAX_MENUS];
	
	private Collection<Reference<Timer>> timers = new ConcurrentLinkedQueue<>();
	private Map<Integer, Reference<Dialog>> dialogs = new ConcurrentHashMap<>();
	
	
	SampObjectStoreImpl(final EventManagerImpl eventManager)
	{
		callbackHandler = new AbstractSampCallbackHandler()
		{
			@Override
			public int onPlayerConnect(int playerid)
			{
				try
				{
					playerObjectsArrays[playerid] = new PlayerObject[MAX_OBJECTS];
					playerLabelsArrays[playerid] = new PlayerLabel[MAX_PLAYER_LABELS];
					
					Player player = ShoebillImpl.getInstance().getSampObjectFactory().createPlayer(playerid);
					setPlayer(playerid, player);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				
				return 1;
			}
			
			@Override
			public int onPlayerDisconnect(int playerid, int reason)
			{
				return 1;
			}
		};
	}
	
	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}
	
	SampCallbackHandler getCallbackHandler()
	{
		return callbackHandler;
	}
	
	@Override
	public Server getServer()
	{
		return server;
	}
	
	@Override
	public World getWorld()
	{
		return world;
	}
	
	@Override
	public Player getPlayer(int id)
	{
		if (id < 0 || id >= MAX_PLAYERS) return null;
		return players[id];
	}
	
	@Override
	public Vehicle getVehicle(int id)
	{
		if (id < 0 || id >= MAX_VEHICLES) return null;
		return vehicles[id];
	}
	
	@Override
	public SampObject getObject(int id)
	{
		if (id < 0 || id >= MAX_OBJECTS) return null;
		return objects[id];
	}
	
	@Override
	public PlayerObject getPlayerObject(Player player, int id)
	{
		if (player == null) return null;
		
		int playerId = player.getId();
		if (playerId < 0 || playerId >= MAX_PLAYERS) return null;
		
		if (id < 0 || id >= MAX_OBJECTS) return null;
		
		PlayerObject[] playerObjects = playerObjectsArrays[playerId];
		if (playerObjects == null) return null;
		
		return playerObjects[id];
	}
	
	@Override
	public Pickup getPickup(int id)
	{
		if (id < 0 || id >= MAX_PICKUPS) return null;
		return pickups[id];
	}
	
	@Override
	public Label getLabel(int id)
	{
		if (id < 0 || id >= MAX_GLOBAL_LABELS) return null;
		return labels[id];
	}
	
	@Override
	public PlayerLabel getPlayerLabel(Player player, int id)
	{
		if (player == null) return null;
		
		int playerId = player.getId();
		if (playerId < 0 || playerId >= MAX_PLAYERS) return null;
		
		if (id < 0 || id >= MAX_PLAYER_LABELS) return null;
		
		PlayerLabel[] playerLabels = playerLabelsArrays[playerId];
		if (playerLabels == null) return null;
		
		return playerLabels[id];
	}
	
	@Override
	public Textdraw getTextdraw(int id)
	{
		if (id < 0 || id >= MAX_TEXT_DRAWS) return null;
		return textdraws[id];
	}
	
	@Override
	public Zone getZone(int id)
	{
		if (id < 0 || id >= MAX_ZONES) return null;
		return zones[id];
	}
	
	@Override
	public Menu getMenu(int id)
	{
		if (id < 0 || id >= MAX_MENUS) return null;
		return menus[id];
	}
	
	@Override
	public Dialog getDialog(int id)
	{
		return dialogs.get(id).get();
	}
	
	@Override
	public Collection<Player> getPlayers()
	{
		return getInstances(players, Player.class);
	}
	
	@Override
	public Collection<Vehicle> getVehicles()
	{
		return getInstances(players, Vehicle.class);
	}
	
	@Override
	public Collection<SampObject> getObjects()
	{
		return getInstances(objects, SampObject.class);
	}
	
	@Override
	public Collection<PlayerObject> getPlayerObjects(Player player)
	{
		return getPlayerObjects(player, PlayerObject.class);
	}
	
	@Override
	public Collection<Pickup> getPickups()
	{
		return getInstances(pickups, Pickup.class);
	}
	
	@Override
	public Collection<Label> getLabels()
	{
		return getInstances(labels, Label.class);
	}
	
	@Override
	public Collection<PlayerLabel> getPlayerLabels(Player player)
	{
		return getPlayerLabels(player, PlayerLabel.class);
	}
	
	@Override
	public Collection<Textdraw> getTextdraws()
	{
		return getInstances(textdraws, Textdraw.class);
	}
	
	@Override
	public Collection<Zone> getZones()
	{
		return getInstances(zones, Zone.class);
	}
	
	@Override
	public Collection<Menu> getMenus()
	{
		return getInstances(menus, Menu.class);
	}
	
	@Override
	public Collection<Dialog> getDialogs()
	{
		return getDialogs(Dialog.class);
	}
	
	@Override
	public Collection<Timer> getTimers()
	{
		return getTimers(Timer.class);
	}
	
	public <T extends Player> Collection<T> getPlayers(Class<T> cls)
	{
		return getInstances(players, cls);
	}
	
	public <T extends Vehicle> Collection<T> getVehicles(Class<T> cls)
	{
		return getInstances(vehicles, cls);
	}
	
	public <T extends Object> Collection<T> getObjects(Class<T> cls)
	{
		return getInstances(objects, cls);
	}
	
	public <T extends PlayerObject> Collection<T> getPlayerObjects(Player player, Class<T> cls)
	{
		if (player == null) return null;
		
		int playerId = player.getId();
		if (playerId < 0 || playerId >= MAX_PLAYERS) return new ArrayList<T>(0);
		
		PlayerObject[] playerObjects = playerObjectsArrays[playerId];
		return getInstances(playerObjects, cls);
	}
	
	public <T extends Pickup> Collection<T> getPickups(Class<T> cls)
	{
		return getInstances(pickups, cls);
	}
	
	public <T extends Label> Collection<T> getLabels(Class<T> cls)
	{
		return getInstances(labels, cls);
	}
	
	public <T extends PlayerLabel> Collection<T> getPlayerLabels(Player player, Class<T> cls)
	{
		if (player == null) return null;
		
		int playerId = player.getId();
		if (playerId < 0 || playerId >= MAX_PLAYERS) return new ArrayList<T>(0);
		
		PlayerLabel[] playerLabels = playerLabelsArrays[playerId];
		return getInstances(playerLabels, cls);
	}
	
	public <T extends Textdraw> Collection<T> getTextdraws(Class<T> cls)
	{
		return getInstances(textdraws, cls);
	}
	
	public <T extends Zone> Collection<T> getZones(Class<T> cls)
	{
		return getInstances(zones, cls);
	}
	
	public <T extends Menu> Collection<T> getMenus(Class<T> cls)
	{
		return getInstances(menus, cls);
	}
	
	public <T extends Dialog> Collection<T> getDialogs(Class<T> cls)
	{
		Collection<T> items = new ArrayList<T>();
		for (Reference<Dialog> reference : dialogs.values())
		{
			Dialog dialog = reference.get();
			if (!cls.isInstance(dialog)) continue;
			
			items.add(cls.cast(dialog));
		}
		
		return items;
	}
	
	public <T extends Timer> Collection<T> getTimers(Class<T> cls)
	{
		Collection<T> items = new ArrayList<T>();
		for (Reference<Timer> reference : timers)
		{
			Timer timer = reference.get();
			if (!cls.isInstance(timer)) continue;
			
			items.add(cls.cast(timer));
		}
		
		return items;
	}
	
	public void setServer(Server server)
	{
		this.server = server;
	}
	
	public void setWorld(World world)
	{
		this.world = world;
	}
	
	public void setPlayer(int id, Player player)
	{
		if(player == null && players[id] != null)
		{
			removePlayer(id);
			return;
		}
			
		players[id] = player;
	}
	
	private void removePlayer(int id)
	{
		for (PlayerLabel playerLabel : playerLabelsArrays[id])
		{
			if(playerLabel != null) playerLabel.destroy();
		}
		
		for (PlayerObject playerObject : playerObjectsArrays[id])
		{
			if(playerObject != null) playerObject.destroy();
		}

		playerLabelsArrays[id] = null;
		playerObjectsArrays[id] = null;
		players[id] = null;
	}
	
	public void setVehicle(int id, Vehicle vehicle)
	{
		vehicles[id] = vehicle;
	}
	
	public void setObject(int id, SampObject object)
	{
		objects[id] = object;
	}
	
	public void setPlayerObject(Player player, int id, PlayerObject object)
	{
		PlayerObject[] playerObjects = playerObjectsArrays[player.getId()];
		playerObjects[id] = object;
	}
	
	public void setPickup(int id, Pickup pickup)
	{
		pickups[id] = pickup;
	}
	
	public void setLabel(int id, Label label)
	{
		labels[id] = label;
	}
	
	public void setPlayerLabel(Player player, int id, PlayerLabel label)
	{
		PlayerLabel[] playerLabels = playerLabelsArrays[player.getId()];
		playerLabels[id] = label;
	}
	
	public void setTextdraw(int id, Textdraw textdraw)
	{
		textdraws[id] = textdraw;
	}
	
	public void setZone(int id, Zone zone)
	{
		zones[id] = zone;
	}
	
	public void setMenu(int id, Menu menu)
	{
		menus[id] = menu;
	}
	
	public void putTimer(Timer timer)
	{
		clearUnusedReferences(timers);
		timers.add(new WeakReference<Timer>(timer));
	}
	
	public void putDialog(int id, Dialog dialog)
	{
		clearUnusedReferences(dialogs.values());
		dialogs.put(id, new WeakReference<Dialog>(dialog));
	}
}
