/**
 * Copyright (c) 2018 Gregorius Techneticies
 *
 * This file is part of GregTech.
 *
 * GregTech is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GregTech is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GregTech. If not, see <http://www.gnu.org/licenses/>.
 */

package gregapi.block;

import static gregapi.data.CS.*;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class MaterialOil extends Material {
	public static MaterialOil instance = new MaterialOil();
	
	private MaterialOil() {
		super(MapColor.blackColor);
		setNoPushMobility();
		setReplaceable();
	}
	
	@Override public boolean isOpaque() {return F;}
	@Override public boolean isLiquid() {return T;}
	@Override public boolean isSolid() {return F;}
	@Override public boolean blocksMovement() {return F;}
}
