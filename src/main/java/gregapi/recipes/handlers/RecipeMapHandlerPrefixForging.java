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

package gregapi.recipes.handlers;

import gregapi.code.ICondition;
import gregapi.data.CS;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictPrefix;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author Gregorius Techneticies
 */
@SuppressWarnings("rawtypes")
public class RecipeMapHandlerPrefixForging extends RecipeMapHandlerPrefix {
	public RecipeMapHandlerPrefixForging(OreDictPrefix aInputPrefix, long aInputAmount, FluidStack aFluidInputPerUnit, long aEUt, long aDuration, long aMultiplier, FluidStack aFluidOutputPerUnit, OreDictPrefix aOutputPrefix, long aOutputAmount, ItemStack aAdditionalInput, ItemStack aAdditionalOutput, boolean aAllowToGenerateAllRecipesAtOnce, boolean aOutputPulverizedRemains, boolean aFlatFluidCosts, ICondition aCondition) {
		super(aInputPrefix, aInputAmount, aFluidInputPerUnit, aEUt, aDuration, aMultiplier, aFluidOutputPerUnit, aOutputPrefix, aOutputAmount, aAdditionalInput, aAdditionalOutput, aAllowToGenerateAllRecipesAtOnce, aOutputPulverizedRemains, aFlatFluidCosts, aCondition);
	}
	
	public RecipeMapHandlerPrefixForging(OreDictPrefix aInputPrefix1, long aInputAmount1, OreDictPrefix aInputPrefix2, long aInputAmount2, FluidStack aFluidInputPerUnit, long aEUt, long aDuration, long aMultiplier, FluidStack aFluidOutputPerUnit, OreDictPrefix aOutputPrefix1, long aOutputAmount1, OreDictPrefix aOutputPrefix2, long aOutputAmount2, ItemStack aAdditionalInput, ItemStack aAdditionalOutput, boolean aAllowToGenerateAllRecipesAtOnce, boolean aOutputPulverizedRemains, boolean aFlatFluidCosts, ICondition aCondition) {
		super(aInputPrefix1, aInputAmount1, aInputPrefix2, aInputAmount2, aFluidInputPerUnit, aEUt, aDuration, aMultiplier, aFluidOutputPerUnit, aOutputPrefix1, aOutputAmount1, aOutputPrefix2, aOutputAmount2, aAdditionalInput, aAdditionalOutput, aAllowToGenerateAllRecipesAtOnce, aOutputPulverizedRemains, aFlatFluidCosts, aCondition);
	}
	
	public RecipeMapHandlerPrefixForging(OreDictPrefix[] aInputPrefixes, long[] aInputAmount, FluidStack aFluidInputPerUnit, long aEUt, long aDuration, long aMultiplier, FluidStack aFluidOutputPerUnit, OreDictPrefix[] aOutputPrefixes, long[] aOutputAmount, ItemStack aAdditionalInput, ItemStack aAdditionalOutput, boolean aAllowToGenerateAllRecipesAtOnce, boolean aOutputPulverizedRemains, boolean aFlatFluidCosts, ICondition aCondition) {
		super(aInputPrefixes, aInputAmount, aFluidInputPerUnit, aEUt, aDuration, aMultiplier, aFluidOutputPerUnit, aOutputPrefixes, aOutputAmount, aAdditionalInput, aAdditionalOutput, aAllowToGenerateAllRecipesAtOnce, aOutputPulverizedRemains, aFlatFluidCosts, aCondition);
	}
	
	@Override
	public OreDictMaterial getOutputMaterial(OreDictMaterial aMaterial) {
		return aMaterial.mTargetForging.mMaterial;
	}
	
	@Override
	public long getCosts(OreDictMaterial aMaterial) {
		return Math.max(16, 1+(long)Math.abs(((getOutputMaterial(aMaterial).mMeltingPoint - CS.DEF_ENV_TEMP) * aMaterial.getWeight(mUnitsInputted)) / (75*mEUt)));
	}
}
