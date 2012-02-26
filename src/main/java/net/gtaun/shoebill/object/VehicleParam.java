/**
 * Copyright (C) 2011 JoJLlmAn
 * Copyright (C) 2012 MK124
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

import net.gtaun.shoebill.samp.SampNativeFunction;

/**
 * @author JoJLlmAn, MK124
 *
 */

public class VehicleParam implements IVehicleParam
{
	public static final int PARAM_UNSET =			-1;
	public static final int PARAM_OFF =				0;
	public static final int PARAM_ON =				1;
	
	
	private IVehicle vehicle;
	private int engine, lights, alarm, doors, bonnet, boot, objective;
	
	
	VehicleParam( IVehicle vehicle )
	{
		this.vehicle = vehicle;
	}
	
	
	@Override
	public IVehicle getVehicle()
	{
		return vehicle;
	}

	@Override
	public int getEngine()
	{
		if( vehicle.isDestroyed() == false ) SampNativeFunction.getVehicleParamsEx( vehicle.getId(), this );
		return engine;
	}
	
	@Override
	public int getLights()
	{
		if( vehicle.isDestroyed() == false ) SampNativeFunction.getVehicleParamsEx( vehicle.getId(), this );
		return lights;
	}
	
	@Override
	public int getAlarm()
	{
		if( vehicle.isDestroyed() == false ) SampNativeFunction.getVehicleParamsEx( vehicle.getId(), this );
		return alarm;
	}
	
	@Override
	public int getDoors()
	{
		if( vehicle.isDestroyed() == false ) SampNativeFunction.getVehicleParamsEx( vehicle.getId(), this );
		return doors;
	}
	
	@Override
	public int getBonnet()
	{
		if( vehicle.isDestroyed() == false ) SampNativeFunction.getVehicleParamsEx( vehicle.getId(), this );
		return bonnet;
	}
	
	@Override
	public int getBoot()
	{
		if( vehicle.isDestroyed() == false ) SampNativeFunction.getVehicleParamsEx( vehicle.getId(), this );
		return boot;
	}
	
	@Override
	public int getObjective()
	{
		if( vehicle.isDestroyed() == false ) SampNativeFunction.getVehicleParamsEx( vehicle.getId(), this );
		return objective;
	}
	

	@Override
	public void setEngine( int engine )
	{
		if( vehicle.isDestroyed() ) return;
		
		SampNativeFunction.getVehicleParamsEx( vehicle.getId(), this );
		this.engine = engine;
		
		set( engine, lights, alarm, doors, bonnet, boot, objective );
	}
	
	@Override
	public void setLights( int lights )
	{
		if( vehicle.isDestroyed() ) return;
		
		SampNativeFunction.getVehicleParamsEx( vehicle.getId(), this );
		set( engine, lights, alarm, doors, bonnet, boot, objective );
	}
	
	@Override
	public void setAlarm( int alarm )
	{
		if( vehicle.isDestroyed() ) return;
		
		SampNativeFunction.getVehicleParamsEx( vehicle.getId(), this );
		set( engine, lights, alarm, doors, bonnet, boot, objective );
	}
	
	@Override
	public void setDoors( int doors )
	{
		if( vehicle.isDestroyed() ) return;
		
		SampNativeFunction.getVehicleParamsEx( vehicle.getId(), this );
		set( engine, lights, alarm, doors, bonnet, boot, objective );
	}
	
	@Override
	public void setBonnet( int bonnet )
	{
		if( vehicle.isDestroyed() ) return;
		
		SampNativeFunction.getVehicleParamsEx( vehicle.getId(), this );
		set( engine, lights, alarm, doors, bonnet, boot, objective );
	}
	
	@Override
	public void setBoot( int boot )
	{
		if( vehicle.isDestroyed() ) return;
		
		SampNativeFunction.getVehicleParamsEx( vehicle.getId(), this );
		set( engine, lights, alarm, doors, bonnet, boot, objective );
	}
	
	@Override
	public void setObjective( int objective )
	{
		if( vehicle.isDestroyed() ) return;
		
		SampNativeFunction.getVehicleParamsEx( vehicle.getId(), this );
		set( engine, lights, alarm, doors, bonnet, boot, objective );
	}
	
	
	@Override
	public void set( int engine, int lights, int alarm, int doors, int bonnet, int boot, int objective )
	{
		if( vehicle.isDestroyed() ) return;
		
		SampNativeFunction.setVehicleParamsEx( vehicle.getId(), engine, lights, alarm, doors, bonnet, boot, objective );
		SampNativeFunction.getVehicleParamsEx( vehicle.getId(), this );
	}
}
