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

package gregtech.items.tools.electric;

import gregapi.data.MT;
import gregapi.data.OP;
import gregapi.item.multiitem.MultiItemTool;
import gregapi.old.Textures;
import gregapi.render.IIconContainer;
import net.minecraft.item.ItemStack;

public class GT_Tool_Chainsaw_MV extends GT_Tool_Chainsaw_LV {
	@Override
	public int getToolDamagePerBlockBreak() {
		return 200;
	}
	
	@Override
	public int getToolDamagePerDropConversion() {
		return 10;
	}
	
	@Override
	public int getToolDamagePerContainerCraft() {
		return 800;
	}
	
	@Override
	public int getToolDamagePerEntityAttack() {
		return 8000;
	}
	
	@Override
	public int getBaseQuality() {
		return 1;
	}
	
	@Override
	public float getBaseDamage() {
		return 3.5F;
	}
	
	@Override
	public float getSpeedMultiplier() {
		return 3.0F;
	}
	
	@Override
	public float getMaxDurabilityMultiplier() {
		return 2.0F;
	}
	
	@Override
	public IIconContainer getIcon(boolean aIsToolHead, ItemStack aStack) {
		return aIsToolHead ?  Textures.ItemIcons.POWER_UNIT_MV : MultiItemTool.getPrimaryMaterial(aStack, MT.Steel).mTextureSetsItems.get(OP.toolHeadChainsaw.mIconIndexItem);
	}
}
