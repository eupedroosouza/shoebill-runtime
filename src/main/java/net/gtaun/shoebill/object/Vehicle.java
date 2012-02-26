/**
 * Copyright (C) 2011 MK124
 * Copyright (C) 2011 JoJLlmAn
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

package net.gtaun.shoebill.object;

import java.util.Collection;

import net.gtaun.shoebill.IShoebillLowLevel;
import net.gtaun.shoebill.SampObjectPool;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.LocationAngular;
import net.gtaun.shoebill.data.Quaternions;
import net.gtaun.shoebill.data.Velocity;
import net.gtaun.shoebill.event.vehicle.VehicleDestroyEvent;
import net.gtaun.shoebill.event.vehicle.VehicleSpawnEvent;
import net.gtaun.shoebill.exception.CreationFailedException;
import net.gtaun.shoebill.samp.SampNativeFunction;

/**
 * @author MK124, JoJLlmAn
 *
 */

public class Vehicle implements IVehicle
{
	static final int INVALID_ID	=				0xFFFF;
	
	
	public static Collection<IVehicle> get()
	{
		return Shoebill.getInstance().getManagedObjectPool().getVehicles();
	}
	
	public static <T extends IVehicle> Collection<T> get( Class<T> cls )
	{
		return Shoebill.getInstance().getManagedObjectPool().getVehicles( cls );
	}
	
	public static IVehicle get( int id )
	{
		return Shoebill.getInstance().getManagedObjectPool().getVehicle( id );
	}
	
	public static <T extends IVehicle> T get( Class<T> cls, int id )
	{
		return cls.cast( Shoebill.getInstance().getManagedObjectPool().getVehicle(id) );
	}
	
	
	public static void manualEngineAndLights()
	{
		SampNativeFunction.manualVehicleEngineAndLights();
	}
	

	void processVehicleMod()
	{
		component.update();
	}
	
	void processVehicleDamageStatusUpdate()
	{
		SampNativeFunction.getVehicleDamageStatus( id, damage );
	}
	
	
	private boolean isStatic = false;
	
	private int id = INVALID_ID;
	private int modelId;
	private int interiorId;
	private int color1, color2;
	private int respawnDelay;

	private VehicleParam param;
	private VehicleComponent component;
	private VehicleDamage damage;

	
	@Override public int getId()									{ return id; }
	
	@Override public boolean isStatic()								{ return isStatic; }
	
	@Override public int getModelId()								{ return modelId; }
	@Override public int getColor1()								{ return color1; }
	@Override public int getColor2()								{ return color2; }
	@Override public int getRespawnDelay()							{ return respawnDelay; }

	@Override public IVehicleParam getState()						{ return param; }
	@Override public IVehicleComponent getComponent()				{ return component; }
	@Override public IVehicleDamage getDamage()						{ return damage; }
	
	
	public Vehicle( int modelId, float x, float y, float z, int interiorId, int worldId, float angle, int color1, int color2, int respawnDelay ) throws CreationFailedException
	{
		this.color1 = color1;
		this.color2 = color2;
		this.respawnDelay = respawnDelay;
		
		initialize( x, y, z, interiorId, worldId, angle );
	}
	
	public Vehicle( int modelId, float x, float y, float z, float angle, int color1, int color2, int respawnDelay ) throws CreationFailedException
	{
		this.modelId = modelId;
		this.color1 = color1;
		this.color2 = color2;
		this.respawnDelay = respawnDelay;

		initialize( x, y, z, 0, 0, angle );
	}
	
	public Vehicle( int modelId, Location location, float angle, int color1, int color2, int respawnDelay ) throws CreationFailedException
	{
		this.modelId = modelId;
		this.color1 = color1;
		this.color2 = color2;
		this.respawnDelay = respawnDelay;

		initialize( location.x, location.y, location.z, location.interiorId, location.worldId, angle );
	}
	
	public Vehicle( int modelId, LocationAngular location, int color1, int color2, int respawnDelay ) throws CreationFailedException
	{
		this.modelId = modelId;
		this.color1 = color1;
		this.color2 = color2;
		this.respawnDelay = respawnDelay;

		initialize( location.x, location.y, location.z, location.interiorId, location.worldId, location.angle );
	}
	
	private void initialize( float x, float y, float z, int interiorId, int worldId, float angle ) throws CreationFailedException
	{
		switch( modelId )
		{
			case 537:
			case 538:
			case 569:
			case 570:
			case 590:
				id = SampNativeFunction.addStaticVehicle( modelId, x, y, z, angle, color1, color2 );
				isStatic = true;
				break;
							
			default:
				id = SampNativeFunction.createVehicle( modelId, x, y, z, angle, color1, color2, respawnDelay );
		}
		
		if( id == INVALID_ID ) throw new CreationFailedException();
		
		SampNativeFunction.linkVehicleToInterior( id, interiorId );
		SampNativeFunction.setVehicleVirtualWorld( id, worldId );

		param = new VehicleParam(this);
		component = new VehicleComponent(this);
		damage = new VehicleDamage(this);
		
		SampObjectPool pool = (SampObjectPool) Shoebill.getInstance().getManagedObjectPool();
		pool.setVehicle( id, this );
		
		VehicleSpawnEvent event = new VehicleSpawnEvent( this );
		IShoebillLowLevel shoebillLowLevel = (IShoebillLowLevel) Shoebill.getInstance();
		shoebillLowLevel.getEventManager().dispatchEvent( event, this );
	}
	
	
	@Override
	public void destroy()
	{
		if( isDestroyed() ) return;
		if( isStatic ) return;
		
		VehicleDestroyEvent event = new VehicleDestroyEvent( this );
		IShoebillLowLevel shoebillLowLevel = (IShoebillLowLevel) Shoebill.getInstance();
		shoebillLowLevel.getEventManager().dispatchEvent( event, this );
		
		SampNativeFunction.destroyVehicle( id );

		SampObjectPool pool = (SampObjectPool) Shoebill.getInstance().getManagedObjectPool();
		pool.setVehicle( id, null );
		
		id = INVALID_ID;
	}
	
	@Override
	public boolean isDestroyed()
	{
		return id == INVALID_ID;
	}
	
	
	@Override
	public IVehicle getTrailer()
	{
		if( isDestroyed() ) return null;
		
		return get(IVehicle.class, SampNativeFunction.getVehicleTrailer(id));
	}
	
	@Override
	public LocationAngular getLocation()
	{
		if( isDestroyed() ) return null;
		
		LocationAngular location = new LocationAngular();

		SampNativeFunction.getVehiclePos( id, location );
		location.angle = SampNativeFunction.getVehicleZAngle(id);
		location.interiorId = interiorId;
		location.worldId = SampNativeFunction.getVehicleVirtualWorld( id );
		
		return location;
	}
	
	@Override
	public float getAngle()
	{
		if( isDestroyed() ) return 0.0f;
		
		return SampNativeFunction.getVehicleZAngle(id);
	}
	
	@Override
	public int getInteriorId()
	{
		return interiorId;
	}
	
	@Override
	public int getWorldId()
	{
		if( isDestroyed() ) return 0;
		
		return SampNativeFunction.getVehicleVirtualWorld(id);
	}
	
	@Override
	public float getHealth()
	{
		if( isDestroyed() ) return 0.0f;
		
		return SampNativeFunction.getVehicleHealth(id);
	}

	@Override
	public Velocity getVelocity()
	{
		if( isDestroyed() ) return null;
		
		Velocity velocity = new Velocity();
		SampNativeFunction.getVehicleVelocity( id, velocity );
		
		return velocity;
	}
	
	@Override
	public Quaternions getRotationQuat()
	{
		if( isDestroyed() ) return null;
		
		Quaternions quaternions = new Quaternions();
		SampNativeFunction.getVehicleRotationQuat( id, quaternions );
		
		return quaternions;
	}
	

	@Override
	public void setLocation( float x, float y, float z )
	{
		if( isDestroyed() ) return;
		
		SampNativeFunction.setVehiclePos( id, x, y, z );
	}
	
	@Override
	public void setLocation( Location location )
	{
		if( isDestroyed() ) return;
		
		SampNativeFunction.setVehiclePos( id, location.x, location.y, location.z );
		SampNativeFunction.linkVehicleToInterior( id, location.interiorId );
		SampNativeFunction.setVehicleVirtualWorld( id, location.worldId );
	}
	
	@Override
	public void setLocation( LocationAngular location )
	{
		if( isDestroyed() ) return;
		
		SampNativeFunction.setVehiclePos( id, location.x, location.y, location.z );
		SampNativeFunction.setVehicleZAngle( id, location.angle );
		SampNativeFunction.linkVehicleToInterior( id, location.interiorId );
		SampNativeFunction.setVehicleVirtualWorld( id, location.worldId );
	}
	
	@Override
	public void setHealth( float health )
	{
		if( isDestroyed() ) return;
		
		SampNativeFunction.setVehicleHealth( id, health );
	}
	
	@Override
	public void setVelocity( Velocity velocity )
	{
		if( isDestroyed() ) return;
		
		SampNativeFunction.setVehicleVelocity( id, velocity.x, velocity.y, velocity.z );
	}
	
	@Override
	public void setAngle( float angle )
	{
		if( isDestroyed() ) return;
		
		SampNativeFunction.setVehicleZAngle( id, angle );
	}
	
	@Override
	public void setInteriorId( int interiorId )
	{
		if( isDestroyed() ) return;
		
		this.interiorId = interiorId;
		SampNativeFunction.linkVehicleToInterior( id, interiorId );
	}
	
	@Override
	public void setWorldId( int worldId )
	{
		if( isDestroyed() ) return;
		
		SampNativeFunction.setVehicleVirtualWorld( id, worldId );
	}

	
	@Override
	public void putPlayer( IPlayer player, int seat )
	{
		if( isDestroyed() ) return;
		if( player.isOnline() == false ) return;
		
		SampNativeFunction.putPlayerInVehicle( player.getId(), id, seat );
	}
	
	@Override
	public boolean isPlayerIn( IPlayer player )
	{
		if( isDestroyed() ) return false;
		if( player.isOnline() == false ) return false;
		
		return SampNativeFunction.isPlayerInVehicle(player.getId(), id);
	}
	
	@Override
	public boolean isStreamedIn( IPlayer forPlayer )
	{
		if( isDestroyed() ) return false;
		if( forPlayer.isOnline() == false ) return false;
		
		return SampNativeFunction.isVehicleStreamedIn(id, forPlayer.getId());
	}
	
	@Override
	public void setParamsForPlayer( IPlayer player, boolean objective, boolean doorslocked )
	{
		if( isDestroyed() ) return;
		if( player.isOnline() == false ) return;
		
		SampNativeFunction.setVehicleParamsForPlayer( id, player.getId(), objective, doorslocked );
	}
	
	@Override
	public void respawn()
	{
		if( isDestroyed() ) return;
		
		SampNativeFunction.setVehicleToRespawn( id );
	}
	
	@Override
	public void setColor( int color1, int color2 )
	{
		if( isDestroyed() ) return;
		
		SampNativeFunction.changeVehicleColor( id, color1, color2 );
	}
	
	@Override
	public void setPaintjob( int paintjobId )
	{
		if( isDestroyed() ) return;
		
		SampNativeFunction.changeVehiclePaintjob( id, paintjobId );
	}
	
	@Override
	public void attachTrailer( IVehicle trailer )
	{
		if( isDestroyed() ) return;
		if( trailer.isDestroyed() ) return;
		
		SampNativeFunction.attachTrailerToVehicle( trailer.getId(), id );
	}
	
	@Override
	public void detachTrailer()
	{
		if( isDestroyed() ) return;
		
		SampNativeFunction.detachTrailerFromVehicle( id );
	}
	
	@Override
	public boolean isTrailerAttached()
	{
		if( isDestroyed() ) return false;
		
		return SampNativeFunction.isTrailerAttachedToVehicle(id);
	}
	
	@Override
	public void setNumberPlate( String numberplate )
	{
		if( isDestroyed() ) return;
		
		if( numberplate == null ) throw new NullPointerException();
		SampNativeFunction.setVehicleNumberPlate( id, numberplate );
	}
	
	@Override
	public void repair()
	{
		if( isDestroyed() ) return;
		
		SampNativeFunction.repairVehicle( id );
	}
	
	@Override
	public void setAngularVelocity( Velocity velocity )
	{
		if( isDestroyed() ) return;
		
		SampNativeFunction.setVehicleAngularVelocity( id, velocity.x, velocity.y, velocity.z );
	}
}
