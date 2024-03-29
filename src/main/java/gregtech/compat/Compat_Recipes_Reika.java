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

package gregtech.compat;

import static gregapi.data.CS.*;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import gregapi.api.Abstract_Mod;
import gregapi.code.ModData;
import gregapi.compat.CompatMods;
import gregapi.data.ANY;
import gregapi.data.FL;
import gregapi.data.IL;
import gregapi.data.MD;
import gregapi.data.MT;
import gregapi.data.OD;
import gregapi.data.OP;
import gregapi.data.RM;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.event.IOreDictListenerEvent;
import gregapi.oredict.event.OreDictListenerEvent_Names;
import gregapi.util.CR;
import gregapi.util.OM;
import gregapi.util.ST;
import net.minecraft.init.Blocks;
import net.minecraftforge.fluids.FluidStack;

public class Compat_Recipes_Reika extends CompatMods {
	public Compat_Recipes_Reika(ModData aMod, Abstract_Mod aGTMod) {super(aMod, aGTMod);}
	
	@Override public void onPostLoad(FMLPostInitializationEvent aInitEvent) {
		if (MD.RoC.mLoaded) {
			OUT.println("GT_Mod: Doing RotaryCraft Recipes.");
			new OreDictListenerEvent_Names(OP.seed) {@Override public void addAllListeners() {
			addListener("seedCanola", new IOreDictListenerEvent() {@Override public void onOreRegistration(OreDictRegistrationContainer aEvent) {
			RM.Shredder         .addRecipe1(T, 16,   16, aEvent.mStack, ST.make(MD.RoC, "rotarycraft_item_canola", 1, 2));
			}});
			}};
			RM.Shredder         .addRecipe1(T, 16,  144, ST.make(MD.RoC, "rotarycraft_item_canola", 1, 1), ST.make(MD.RoC, "rotarycraft_item_canola", 9, 2));
			RM.Squeezer         .addRecipe1(T, 16,  144, ST.make(MD.RoC, "rotarycraft_item_canola", 1, 1), NF, FL.make("rc lubricant", 405, "lubricant"), NI);
			RM.Squeezer         .addRecipe1(T, 16,   16, ST.make(MD.RoC, "rotarycraft_item_canola", 1, 2), NF, FL.make("rc lubricant",  90, "lubricant"), NI);
			RM.Juicer           .addRecipe1(T, 16,  144, ST.make(MD.RoC, "rotarycraft_item_canola", 1, 1), NF, FL.make("rc lubricant", 270, "lubricant"), NI);
			RM.Juicer           .addRecipe1(T, 16,   16, ST.make(MD.RoC, "rotarycraft_item_canola", 1, 2), NF, FL.make("rc lubricant",  60, "lubricant"), NI);
			
			RM.Compressor       .addRecipe1(T,512, 1024, OM.dust(MT.Bedrock, U*8), ST.make(Blocks.bedrock, 1, 0));
			
			for (FluidStack tRedstone : FL.array(FL.Redstone.make(L), FL.Redstone_TE.make(100))) if (tRedstone != null) {
			RM.Bath             .addRecipe1(T,  0,   64, IL.Circuit_Board_HSLA_Circuit.get(1), FL.mul(tRedstone, 2, 9, T), NF, ST.make(MD.RoC, "rotarycraft_item_borecraft", 1, 4));
			RM.Bath             .addRecipe1(T,  0,   64, IL.Circuit_Board_Power_Module.get(1), FL.mul(tRedstone, 2, 9, T), NF, ST.make(MD.RoC, "rotarycraft_item_misccraft", 1, 2));
			}
			
			RM.Centrifuge       .addRecipe1(T, 16,   64,  8000, IL.RoC_Comb_Slippery        .get(1), NF, FL.make("rc lubricant",  50, "lubricant",  50), IL.RoC_Propolis_Slippery.get(1));
			RM.Centrifuge       .addRecipe1(T, 16,   64       , IL.RoC_Propolis_Slippery    .get(1), NF, FL.make("rc lubricant", 150, "lubricant", 150), ZL_IS);
			
			CR.delate(MD.RoC, "rotarycraft_item_powders", 7);
			CR.delate(MD.RoC, "rotarycraft_item_powders", 6);
			CR.shapeless(ST.make(MD.RoC, "rotarycraft_item_powders", 1, 6), CR.DEF, new Object[] {OP.dustSmall.dat(MT.Redstone), OP.dustSmall.dat(MT.Coal), OP.dustSmall.dat(ANY.Salt), OP.dustSmall.dat(MT.Gunpowder)});
			CR.shapeless(ST.make(MD.RoC, "rotarycraft_item_powders", 4, 6), CR.DEF, new Object[] {OD.itemRedstone, OP.dust.dat(MT.Coal), OP.dust.dat(ANY.Salt), OP.dust.dat(MT.Gunpowder)});
			RM.Mixer            .addRecipeX(T, 16,   64, ST.array(OM.dust(MT.Redstone, U*1), OM.dust(MT.Coal, U*1), OM.dust(MT.NaCl,U*1), OM.dust(MT.Gunpowder, U*1)), ST.make(MD.RoC, "rotarycraft_item_powders", 4, 6));
			RM.Mixer            .addRecipeX(T, 16,   16, ST.array(OM.dust(MT.Redstone, U4 ), OM.dust(MT.Coal, U4 ), OM.dust(MT.NaCl,U4 ), OM.dust(MT.Gunpowder, U4 )), ST.make(MD.RoC, "rotarycraft_item_powders", 1, 6));
			RM.Mixer            .addRecipeX(T, 16,   64, ST.array(OM.dust(MT.Redstone, U*1), OM.dust(MT.Coal, U*1), OM.dust(MT.KCl, U*1), OM.dust(MT.Gunpowder, U*1)), ST.make(MD.RoC, "rotarycraft_item_powders", 4, 6));
			RM.Mixer            .addRecipeX(T, 16,   16, ST.array(OM.dust(MT.Redstone, U4 ), OM.dust(MT.Coal, U4 ), OM.dust(MT.KCl, U4 ), OM.dust(MT.Gunpowder, U4 )), ST.make(MD.RoC, "rotarycraft_item_powders", 1, 6));
		}
		if (MD.ReC.mLoaded) {
			OUT.println("GT_Mod: Doing ReactorCraft Recipes.");
		}
		if (MD.ElC.mLoaded) {
			OUT.println("GT_Mod: Doing ElectriCraft Recipes.");
			CR.delate(MD.ElC, "electricraft_item_crafting", 3);
			for (OreDictMaterial tMat1 : ANY.Glowstone.mToThis) for (OreDictMaterial tMat2 : ANY.SiO2.mToThis) for (OreDictMaterial tMat3 : ANY.Diamond.mToThis) {
			RM.Mixer            .addRecipeX(T, 16,   32, ST.array(OM.dust(MT.Redstone, U*4), OM.dust(tMat1, U*1), OM.dust(MT.Lapis, U*1), OM.dust(tMat2, U*1), OM.dust(tMat3, U*1)), ST.make(MD.ElC, "electricraft_item_crafting", 2, 3));
			RM.Mixer            .addRecipeX(T, 16,   16, ST.array(OM.dust(MT.Redstone, U*2), OM.dust(tMat1, U2 ), OM.dust(MT.Lapis, U2 ), OM.dust(tMat2, U2 ), OM.dust(tMat3, U2 )), ST.make(MD.ElC, "electricraft_item_crafting", 1, 3));
			}
		}
		if (MD.CrC.mLoaded) {
			OUT.println("GT_Mod: Doing ChromatiCraft Recipes.");
		}
	}
}
