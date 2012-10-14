/**
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

package net.gtaun.shoebill.object.impl;

import net.gtaun.shoebill.object.primitive.VehicleDamage;
import net.gtaun.shoebill.object.primitive.VehiclePrim;
import net.gtaun.shoebill.samp.SampNativeFunction;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author JoJLlmAn
 *
 */

public class VehicleDamageImpl implements VehicleDamage
{
	private VehiclePrim vehicle;
	private int panels, doors, lights, tires;
	
	
	VehicleDamageImpl( VehiclePrim vehicle )
	{
		this.vehicle = vehicle;
	}

	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}
	
	@Override
	public VehiclePrim getVehicle()
	{
		return vehicle;
	}
	
	@Override
	public int getPanels()
	{
		return panels;
	}
	
	@Override
	public void setPanels( int panels )
	{
		if( vehicle.isDestroyed() ) return;
		
		set( panels, doors, lights, tires );
	}

	@Override
	public int getDoors()
	{
		return doors;
	}
	
	@Override
	public void setDoors( int doors )
	{
		if( vehicle.isDestroyed() ) return;
		
		set( panels, doors, lights, tires );
	}

	@Override
	public int getLights()
	{
		return lights;
	}
	
	@Override
	public void setLights( int lights )
	{
		if( vehicle.isDestroyed() ) return;
		
		set( panels, doors, lights, tires );
	}

	@Override
	public int getTires()
	{
		return tires;
	}
	
	@Override
	public void setTires( int tires )
	{
		if( vehicle.isDestroyed() ) return;
		
		set( panels, doors, lights, tires );
	}
	
	@Override
	public void set( int panels, int doors, int lights, int tires )
	{
		if( vehicle.isDestroyed() ) return;
		
		SampNativeFunction.updateVehicleDamageStatus(vehicle.getId(), panels, doors, lights, tires);
		SampNativeFunction.getVehicleDamageStatus(vehicle.getId(), this);
	}
}