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

package gregtech.blocks;

import static gregapi.data.CS.*;

import gregapi.block.BlockBaseMeta;
import gregapi.data.LH;
import gregapi.data.MT;
import gregapi.old.Textures;
import gregapi.util.OM;
import gregapi.util.ST;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class BlockSands extends BlockBaseMeta {
	public BlockSands(String aUnlocalised) {
		super(null, aUnlocalised, Material.sand, soundTypeSand, 1, Textures.BlockIcons.SANDS);
		LH.add(getUnlocalizedName()+ ".0.name", "Black Sand");
		
		OM.data(ST.make(this, 1, 0), MT.OREMATS.Magnetite, U);
	}
	
	@Override public boolean useGravity(int aMeta) {return T;}
	@Override public boolean canCreatureSpawn(int aMeta) {return T;}
	@Override public boolean doesPistonPush(short aMeta) {return T;}
	@Override public boolean isSealable(int aMeta, byte aSide) {return F;}
	@Override public String getHarvestTool(int aMeta) {return TOOL_shovel;}
	@Override public int getHarvestLevel(int aMeta) {return 0;}
	@Override public float getBlockHardness(World aWorld, int aX, int aY, int aZ) {return Blocks.sand.getBlockHardness(aWorld, aX, aY, aZ);}
	@Override public float getExplosionResistance(int aMeta) {return Blocks.sand.getExplosionResistance(null);}
}
