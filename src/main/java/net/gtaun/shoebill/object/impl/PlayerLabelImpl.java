/**
 * Copyright (C) 2011 MK124
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

package net.gtaun.shoebill.object.impl;

import net.gtaun.shoebill.SampObjectPoolImpl;
import net.gtaun.shoebill.ShoebillImpl;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Point3D;
import net.gtaun.shoebill.exception.CreationFailedException;
import net.gtaun.shoebill.object.primitive.PlayerPrim;
import net.gtaun.shoebill.object.primitive.PlayerLabelPrim;
import net.gtaun.shoebill.object.primitive.VehiclePrim;
import net.gtaun.shoebill.samp.SampNativeFunction;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author MK124
 *
 */

public class PlayerLabelImpl implements PlayerLabelPrim
{
	private int id = INVALID_ID;
	private PlayerPrim player;
	private String text;
	private Color color;
	private float drawDistance;
	private Location location;
	private boolean testLOS;
	
	private Point3D offset;
	private PlayerPrim attachedPlayer;
	private VehiclePrim attachedVehicle;
	

	public PlayerLabelImpl( PlayerPrim player, String text, Color color, float x, float y, float z, float drawDistance, boolean testLOS ) throws CreationFailedException
	{
		initialize( player, text, color, new Location(x, y, z), drawDistance, testLOS, null, null );
	}
	
	public PlayerLabelImpl( PlayerPrim player, String text, Color color, float x, float y, float z, int worldId, float drawDistance, boolean testLOS ) throws CreationFailedException
	{
		initialize( player, text, color, new Location(x, y, z, worldId), drawDistance, testLOS, null, null );
	}
	
	public PlayerLabelImpl( PlayerPrim player, String text, Color color, Location loc, float drawDistance, boolean testLOS ) throws CreationFailedException
	{
		initialize( player, text, color, new Location(loc), drawDistance, testLOS, null, null );
	}

	public PlayerLabelImpl( PlayerPrim player, String text, Color color, Location loc, float drawDistance, boolean testLOS, PlayerPrim attachedPlayer ) throws CreationFailedException
	{
		initialize( player, text, color, new Location(loc), drawDistance, testLOS, attachedPlayer, null );
	}
	
	public PlayerLabelImpl( PlayerPrim player, String text, Color color, Location loc, float drawDistance, boolean testLOS, VehiclePrim attachedVehicle ) throws CreationFailedException
	{
		initialize( player, text, color, new Location(loc), drawDistance, testLOS, null, attachedVehicle );
	}
	
	private void initialize( PlayerPrim player, String text, Color color, Location loc, float drawDistance, boolean testLOS, PlayerPrim attachedPlayer, VehiclePrim attachedVehicle ) throws CreationFailedException
	{
		if( StringUtils.isEmpty(text) ) text = " ";
		
		this.player = player;
		this.text = text;
		this.color = new Color( color );
		this.drawDistance = drawDistance;
		this.location = new Location( loc );
		this.testLOS = testLOS;
		
		int playerId = PlayerPrim.INVALID_ID, vehicleId = VehiclePrim.INVALID_ID;
		
		if( attachedPlayer != null )	playerId = attachedPlayer.getId();
		if( attachedVehicle != null )	vehicleId = attachedVehicle.getId();
		
		if( playerId == PlayerPrim.INVALID_ID ) attachedPlayer = null;
		if( vehicleId == VehiclePrim.INVALID_ID ) attachedVehicle = null;
		
		if( attachedPlayer != null || attachedVehicle != null )
		{
			offset = new Point3D( location.getX(), location.getY(), location.getZ() );
		}
		
		if( player.isOnline() == false ) throw new CreationFailedException();
		
		id = SampNativeFunction.createPlayer3DTextLabel( player.getId(), text, color.getValue(), location.getX(), location.getY(), location.getZ(), drawDistance, playerId, vehicleId, testLOS );
		if( id == INVALID_ID ) throw new CreationFailedException();
		
		SampObjectPoolImpl pool = (SampObjectPoolImpl) ShoebillImpl.getInstance().getManagedObjectPool();
		// pool.setPlayerLabel( player, id, this );
	}

	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

	@Override public int getId()
	{
		return id;
	}

	@Override public PlayerPrim getPlayer()
	{
		return player;
	}
	
	@Override
	public void destroy()
	{
		if( isDestroyed() ) return;
		
		if( player.isOnline() )
		{
			SampNativeFunction.deletePlayer3DTextLabel( player.getId(), id );

			SampObjectPoolImpl pool = (SampObjectPoolImpl) ShoebillImpl.getInstance().getManagedObjectPool();
			// pool.setPlayerLabel( player, id, null );
		}
		
		id = INVALID_ID;
	}
	
	@Override
	public boolean isDestroyed()
	{
		return id == INVALID_ID;
	}

	@Override public String getText()
	{
		return text;
	}
	
	@Override public Color getColor()
	{
		return color.clone();
	}
	
	@Override public float getDrawDistance()
	{
		return drawDistance;
	}
	
	@Override public PlayerPrim getAttachedPlayer()
	{
		return attachedPlayer;
	}
	
	@Override public VehiclePrim getAttachedVehicle()
	{
		return attachedVehicle;
	}
	
	@Override
	public Location getLocation()
	{
		if( isDestroyed() ) return null;
		if( player.isOnline() == false ) return null;
		
		Location pos = null;
		
		if( attachedPlayer != null )	pos = attachedPlayer.getLocation();
		if( attachedVehicle != null )	pos = attachedVehicle.getLocation();
		
		if( pos != null )
		{
			location.set( pos.getX() + offset.getX(), pos.getY(), pos.getZ() + offset.getZ(), pos.getInteriorId(), pos.getWorldId() );
		}
		
		return location.clone();
	}

	@Override
	public void attach( PlayerPrim target, float x, float y, float z )
	{
		if( isDestroyed() ) return;
		if( player.isOnline() == false ) return;
		if( target.isOnline() == false ) return;
		
		int playerId = player.getId();
		
		SampNativeFunction.deletePlayer3DTextLabel( playerId, id );
		id = SampNativeFunction.createPlayer3DTextLabel( playerId, text, color.getValue(), x, y, z, drawDistance, target.getId(), VehiclePrim.INVALID_ID, testLOS );
		
		attachedPlayer = target;
		attachedVehicle = null;
	}
	
	@Override
	public void attach( PlayerPrim target, Point3D offset )
	{
		attach( target, offset.getX(), offset.getY(), offset.getZ() );
	}

	@Override
	public void attach( VehiclePrim vehicle, float x, float y, float z )
	{
		if( isDestroyed() ) return;
		if( player.isOnline() == false ) return;
		if( vehicle.isDestroyed() ) return;
		
		int playerId = player.getId();
		
		SampNativeFunction.deletePlayer3DTextLabel( playerId, id );
		id = SampNativeFunction.createPlayer3DTextLabel( playerId, text, color.getValue(), x, y, z, drawDistance, PlayerPrim.INVALID_ID, vehicle.getId(), testLOS );
	
		attachedPlayer = null;
		attachedVehicle = vehicle;
	}
	
	@Override
	public void attach( VehiclePrim vehicle, Point3D offset )
	{
		attach( vehicle, offset.getX(), offset.getY(), offset.getZ() );
	}
	
	@Override
	public void update( Color color, String text )
	{
		if( isDestroyed() ) return;
		if( player.isOnline() == false ) return;
		
		this.color.set( color );
		this.text = text;
		
		SampNativeFunction.updatePlayer3DTextLabelText( player.getId(), id, color.getValue(), text );
	}
}