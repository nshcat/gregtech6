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

package gregtech.tileentity.energy.reactors;

import static gregapi.data.CS.*;

import java.util.ArrayList;
import java.util.List;

import gregapi.GT_API_Proxy;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_GetCollisionBoundingBoxFromPool;
import gregapi.block.multitileentity.IMultiTileEntity.IMTE_OnEntityCollidedWithBlock;
import gregapi.data.CS.SFX;
import gregapi.data.FL;
import gregapi.data.LH;
import gregapi.data.LH.Chat;
import gregapi.fluid.FluidTankGT;
import gregapi.item.IItemReactorRod;
import gregapi.network.INetworkHandler;
import gregapi.network.IPacket;
import gregapi.old.Textures;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureFluid;
import gregapi.render.BlockTextureMulti;
import gregapi.render.IIconContainer;
import gregapi.render.ITexture;
import gregapi.tileentity.ITileEntityFunnelAccessible;
import gregapi.tileentity.ITileEntityServerTickPost;
import gregapi.tileentity.ITileEntityTapAccessible;
import gregapi.tileentity.base.TileEntityBase10FacingDouble;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.machines.ITileEntityRunningActively;
import gregapi.tileentity.machines.ITileEntitySwitchableMode;
import gregapi.tileentity.machines.ITileEntitySwitchableOnOff;
import gregapi.util.ST;
import gregapi.util.UT;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

/**
 * @author Gregorius Techneticies
 */
public class MultiTileEntityReactorCore extends TileEntityBase10FacingDouble implements ITileEntityServerTickPost, IFluidHandler, ITileEntityTapAccessible, ITileEntityFunnelAccessible, ITileEntityRunningActively, ITileEntitySwitchableOnOff, ITileEntitySwitchableMode, IMTE_GetCollisionBoundingBoxFromPool, IMTE_OnEntityCollidedWithBlock {
	public int mNeutronCounts[] = new int[] {0,0,0,0}, oNeutronCounts[] = new int[] {0,0,0,0};
	public long mEnergy = 0, oEnergy = 0;
	public byte mMode = 0;
	public boolean mRunning = F, mStopped = F;
	public FluidTankGT[] mTanks = {new FluidTankGT(64000), new FluidTankGT(64000)};
	
	@Override
	public void readFromNBT2(NBTTagCompound aNBT) {
		super.readFromNBT2(aNBT);
		mMode = aNBT.getByte(NBT_MODE);
		mEnergy = aNBT.getLong(NBT_ENERGY);
		mRunning = aNBT.getBoolean(NBT_ACTIVE);
		mStopped = aNBT.getBoolean(NBT_STOPPED);
		mTanks[0].readFromNBT(aNBT, NBT_TANK+".0");
		mTanks[1].readFromNBT(aNBT, NBT_TANK+".1");
		for (int i = 0; i < 4; i++) {
			mNeutronCounts[i] = aNBT.getInteger(NBT_VALUE+".m."+i);
			oNeutronCounts[i] = aNBT.getInteger(NBT_VALUE+".o."+i);
		}
		
		if (worldObj != null && isServerSide() && mHasToAddTimer) {
			GT_API_Proxy.SERVER_TICK_POST.add(this);
			GT_API_Proxy.SERVER_TICK_PO2T.add(this);
			mHasToAddTimer = F;
		}
	}
	
	@Override
	public void writeToNBT2(NBTTagCompound aNBT) {
		super.writeToNBT2(aNBT);
		UT.NBT.setNumber(aNBT, NBT_MODE, mMode);
		UT.NBT.setNumber(aNBT, NBT_ENERGY, mEnergy);
		UT.NBT.setBoolean(aNBT, NBT_ACTIVE, mRunning);
		UT.NBT.setBoolean(aNBT, NBT_STOPPED, mStopped);
		mTanks[0].writeToNBT(aNBT, NBT_TANK+".0");
		mTanks[1].writeToNBT(aNBT, NBT_TANK+".1");
		for (int i = 0; i < 4; i++) {
			UT.NBT.setNumber(aNBT, NBT_VALUE+".m."+i, mNeutronCounts[i]);
			UT.NBT.setNumber(aNBT, NBT_VALUE+".o."+i, oNeutronCounts[i]);
		}
	}
	
	static {
		LH.add("gt.tooltip.reactorcore.1", "Primary Facing Emits Hot Coolant.");
		LH.add("gt.tooltip.reactorcore.2", "Secondary Facing Emits Cold Coolant when over half full.");
	}
	
	@Override
	public void addToolTips(List<String> aList, ItemStack aStack, boolean aF3_H) {
		aList.add(Chat.CYAN     + LH.get("gt.tooltip.reactorcore.1"));
		aList.add(Chat.CYAN     + LH.get("gt.tooltip.reactorcore.2"));
		aList.add(Chat.ORANGE   + LH.get(LH.NO_GUI_FUNNEL_TAP_TO_TANK));
		aList.add(Chat.DRED     + LH.get(LH.HAZARD_CONTACT));
		aList.add(Chat.DGRAY    + LH.get(LH.TOOL_TO_TAKE_PINCERS));
		aList.add(Chat.DGRAY    + LH.get(LH.TOOL_TO_MEASURE_GEIGER_COUNTER));
		aList.add(Chat.DGRAY    + LH.get(LH.TOOL_TO_DETAIL_MAGNIFYINGGLASS));
		super.addToolTips(aList, aStack, aF3_H);
	}
	
	private boolean mHasToAddTimer = T;
	
	@Override public void onUnregisterPost() {mHasToAddTimer = T;}
	
	@Override
	public void onTick2(long aTimer, boolean aIsServerSide) {
		super.onTick2(aTimer, aIsServerSide);
		if (aIsServerSide) {
			if (mHasToAddTimer) {
				GT_API_Proxy.SERVER_TICK_POST.add(this);
				GT_API_Proxy.SERVER_TICK_PO2T.add(this);
				mHasToAddTimer = F;
			}
			if (mTanks[0].isHalf()) FL.move(mTanks[0], getAdjacentTank(mSecondFacing), mTanks[0].amount() - mTanks[0].capacity() / 2);
			if (mTanks[1].has()) FL.move(mTanks[1], getAdjacentTank(mFacing));
			
			if (mTanks[0].check()) updateClientData();
		}
	}
	
	@Override
	public void onCoordinateChange() {
		super.onCoordinateChange();
		GT_API_Proxy.SERVER_TICK_POST.remove(this);
		GT_API_Proxy.SERVER_TICK_PO2T.remove(this);
		onUnregisterPost();
	}
	
	private static final int[] S2103 = new int[] {0,0,2,1,0,3,0}, S0312 = new int[] {0,0,0,3,1,2,0};
	
	// 0 and 2 are at SIDE_Z_NEG    1 3      -->X+
	// 1 and 3 are at SIDE_Z_POS  2|0 2|0   |0 2
	// 0 and 1 are at SIDE_X_NEG  3|1 3|1   v1 3
	// 2 and 3 are at SIDE_X_POS    0 2     Z+
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onServerTickPost(boolean aFirst) {
		if (aFirst) {
			if (mStopped) {
				//
			} else {
				DelegatorTileEntity<MultiTileEntityReactorCore> tAdjacents[] = new DelegatorTileEntity[4], tAdjacent;
				DelegatorTileEntity
				tAdjacentTE = getAdjacentTileEntity(SIDE_Z_NEG); if (tAdjacentTE.mTileEntity instanceof MultiTileEntityReactorCore && SIDES_HORIZONTAL[tAdjacentTE.mSideOfTileEntity]) tAdjacents[0] = tAdjacentTE;
				tAdjacentTE = getAdjacentTileEntity(SIDE_Z_POS); if (tAdjacentTE.mTileEntity instanceof MultiTileEntityReactorCore && SIDES_HORIZONTAL[tAdjacentTE.mSideOfTileEntity]) tAdjacents[1] = tAdjacentTE;
				tAdjacentTE = getAdjacentTileEntity(SIDE_X_NEG); if (tAdjacentTE.mTileEntity instanceof MultiTileEntityReactorCore && SIDES_HORIZONTAL[tAdjacentTE.mSideOfTileEntity]) tAdjacents[2] = tAdjacentTE;
				tAdjacentTE = getAdjacentTileEntity(SIDE_X_POS); if (tAdjacentTE.mTileEntity instanceof MultiTileEntityReactorCore && SIDES_HORIZONTAL[tAdjacentTE.mSideOfTileEntity]) tAdjacents[3] = tAdjacentTE;
				
				int
				tNeutronCount = getReactorRodNeutronEmission(0);
				if (tNeutronCount != 0) {
					mNeutronCounts[0] += getReactorRodNeutronReflection(1, tNeutronCount);
					mNeutronCounts[0] += getReactorRodNeutronReflection(2, tNeutronCount);
					tAdjacent = tAdjacents[SIDE_Z_NEG-2]; if (tAdjacent != null) mNeutronCounts[0] += tAdjacent.mTileEntity.getReactorRodNeutronReflection(S2103[tAdjacent.mSideOfTileEntity], tNeutronCount);
					tAdjacent = tAdjacents[SIDE_X_NEG-2]; if (tAdjacent != null) mNeutronCounts[0] += tAdjacent.mTileEntity.getReactorRodNeutronReflection(S0312[tAdjacent.mSideOfTileEntity], tNeutronCount);
				}
				
				tNeutronCount = getReactorRodNeutronEmission(1);
				if (tNeutronCount != 0) {
					mNeutronCounts[1] += getReactorRodNeutronReflection(0, tNeutronCount);
					mNeutronCounts[1] += getReactorRodNeutronReflection(3, tNeutronCount);
					tAdjacent = tAdjacents[SIDE_Z_POS-2]; if (tAdjacent != null) mNeutronCounts[1] += tAdjacent.mTileEntity.getReactorRodNeutronReflection(S0312[tAdjacent.mSideOfTileEntity], tNeutronCount);
					tAdjacent = tAdjacents[SIDE_X_NEG-2]; if (tAdjacent != null) mNeutronCounts[1] += tAdjacent.mTileEntity.getReactorRodNeutronReflection(S2103[tAdjacent.mSideOfTileEntity], tNeutronCount);
				}
				
				tNeutronCount = getReactorRodNeutronEmission(2);
				if (tNeutronCount != 0) {
					mNeutronCounts[2] += getReactorRodNeutronReflection(0, tNeutronCount);
					mNeutronCounts[2] += getReactorRodNeutronReflection(3, tNeutronCount);
					tAdjacent = tAdjacents[SIDE_Z_NEG-2]; if (tAdjacent != null) mNeutronCounts[2] += tAdjacent.mTileEntity.getReactorRodNeutronReflection(S0312[tAdjacent.mSideOfTileEntity], tNeutronCount);
					tAdjacent = tAdjacents[SIDE_X_POS-2]; if (tAdjacent != null) mNeutronCounts[2] += tAdjacent.mTileEntity.getReactorRodNeutronReflection(S2103[tAdjacent.mSideOfTileEntity], tNeutronCount);
				}
				
				tNeutronCount = getReactorRodNeutronEmission(3);
				if (tNeutronCount != 0) {
					mNeutronCounts[3] += getReactorRodNeutronReflection(1, tNeutronCount);
					mNeutronCounts[3] += getReactorRodNeutronReflection(2, tNeutronCount);
					tAdjacent = tAdjacents[SIDE_Z_POS-2]; if (tAdjacent != null) mNeutronCounts[3] += tAdjacent.mTileEntity.getReactorRodNeutronReflection(S2103[tAdjacent.mSideOfTileEntity], tNeutronCount);
					tAdjacent = tAdjacents[SIDE_X_POS-2]; if (tAdjacent != null) mNeutronCounts[3] += tAdjacent.mTileEntity.getReactorRodNeutronReflection(S0312[tAdjacent.mSideOfTileEntity], tNeutronCount);
				}
			}
		} else {
			int tCalc = (int)UT.Code.divup((oNeutronCounts[0] = mNeutronCounts[0]) + (oNeutronCounts[1] = mNeutronCounts[1]) + (oNeutronCounts[2] = mNeutronCounts[2]) + (oNeutronCounts[3] = mNeutronCounts[3]), 256);
			
			if (tCalc > 0) for (EntityLivingBase tEntity : (ArrayList<EntityLivingBase>)worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(xCoord-tCalc, yCoord-tCalc, zCoord-tCalc, xCoord+1+tCalc, yCoord+1+tCalc, zCoord+1+tCalc))) {
				UT.Entities.applyRadioactivity(tEntity, (int)UT.Code.divup(tCalc, 10), tCalc);
			}
			
			mRunning = (tCalc != 0);
			
			long tEnergy = mEnergy;
			
			if (getReactorRodNeutronReaction(0)) mRunning = T;
			if (getReactorRodNeutronReaction(1)) mRunning = T;
			if (getReactorRodNeutronReaction(2)) mRunning = T;
			if (getReactorRodNeutronReaction(3)) mRunning = T;
			
			oEnergy = mEnergy - tEnergy;
			tEnergy = mEnergy/EU_PER_COOLANT;
			
			if (tEnergy > 0) {
				// TODO Heat up different Coolants
				if (FL.Coolant_IC2.is(mTanks[0]) && mTanks[0].has(tEnergy) && mTanks[1].fillAll(FL.Coolant_IC2_Hot.make(tEnergy))) {
					mEnergy -= EU_PER_COOLANT * mTanks[0].remove(tEnergy);
				} else {
					// explode(0.1); // TODO proper explosion.
					UT.Sounds.send(SFX.MC_EXPLODE, this);
					tCalc *= 2;
					for (EntityLivingBase tEntity : (ArrayList<EntityLivingBase>)worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(xCoord-tCalc, yCoord-tCalc, zCoord-tCalc, xCoord+1+tCalc, yCoord+1+tCalc, zCoord+1+tCalc))) {
						UT.Entities.applyRadioactivity(tEntity, (int)UT.Code.divup(tCalc, 10), tCalc);
					}
				}
			}
		}
	}
	
	public int getReactorRodNeutronEmission(int aSlot) {
		if (slotHas(aSlot) && (mMode & B[aSlot]) == 0 && ST.item(slot(aSlot)) instanceof IItemReactorRod) return ((IItemReactorRod)ST.item(slot(aSlot))).getReactorRodNeutronEmission(this, aSlot, slot(aSlot));
		mNeutronCounts[aSlot] = 0;
		return 0;
	}
	
	public boolean getReactorRodNeutronReaction(int aSlot) {
		mNeutronCounts[aSlot] -= oNeutronCounts[aSlot];
		if (slotHas(aSlot) && (mMode & B[aSlot]) == 0 && ST.item(slot(aSlot)) instanceof IItemReactorRod) return ((IItemReactorRod)ST.item(slot(aSlot))).getReactorRodNeutronReaction(this, aSlot, slot(aSlot));
		return F;
	}
	
	public int getReactorRodNeutronReflection(int aSlot, int aNeutrons) {
		if (slotHas(aSlot) && (mMode & B[aSlot]) == 0 && ST.item(slot(aSlot)) instanceof IItemReactorRod) return ((IItemReactorRod)ST.item(slot(aSlot))).getReactorRodNeutronReflection(this, aSlot, slot(aSlot), aNeutrons);
		return 0;
	}
	
	@Override
	public long onToolClick2(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ) {
		long rReturn = super.onToolClick2(aTool, aRemainingDurability, aQuality, aPlayer, aChatReturn, aPlayerInventory, aSneaking, aStack, aSide, aHitX, aHitY, aHitZ);
		if (rReturn > 0) return rReturn;
		
		if (isClientSide()) return 0;
		
		if (aTool.equals(TOOL_pincers) && SIDES_TOP[aSide]) {
			int tSlot = aHitX < 0.5 ? aHitZ < 0.5 ? 0 : 1 : aHitZ < 0.5 ? 2 : 3;
			if (slotHas(tSlot) && UT.Inventories.addStackToPlayerInventoryOrDrop(aPlayer instanceof EntityPlayer ? (EntityPlayer)aPlayer : null, slot(tSlot), T, worldObj, xCoord+0.5, yCoord+1.5, zCoord+0.5)) {
				slotKill(tSlot);
				updateClientData();
				return 10000;
			}
			return 0;
		}
		if (aTool.equals(TOOL_geigercounter)) {
			if (aChatReturn != null) {
				aChatReturn.add("Neutron Levels: " + oNeutronCounts[0] + "n; " + oNeutronCounts[1] + "n; " + oNeutronCounts[2] + "n; " + oNeutronCounts[3] + "n");
			}
			return 10000;
		}
		if (aTool.equals(TOOL_thermometer)) {// TODO Remove Neutron Levels
			if (aChatReturn != null) {
				aChatReturn.add("Heat Levels: " + (oEnergy <= 0 ? "None" : oEnergy + " HU"));
				aChatReturn.add("Neutron Levels: " + oNeutronCounts[0] + "n; " + oNeutronCounts[1] + "n; " + oNeutronCounts[2] + "n; " + oNeutronCounts[3] + "n");
			}
			return 10000;
		}
		if (aTool.equals(TOOL_magnifyingglass)) {
			if (aChatReturn != null) {
				aChatReturn.add("Input: "  + mTanks[0].content());
				aChatReturn.add("Output: " + mTanks[1].content());
			}
			return 1;
		}
		return 0;
	}
	
	@Override
	public boolean onBlockActivated3(EntityPlayer aPlayer, byte aSide, float aHitX, float aHitY, float aHitZ) {
		if (isServerSide() && SIDES_TOP[aSide]) {
			ItemStack aStack = aPlayer.getCurrentEquippedItem();
			if (ST.item(aStack) instanceof IItemReactorRod && ((IItemReactorRod)ST.item_(aStack)).isReactorRod(aStack)) {
				int tSlot = aHitX < 0.5 ? aHitZ < 0.5 ? 0 : 1 : aHitZ < 0.5 ? 2 : 3;
				if (!slotHas(tSlot) && ST.use(aPlayer, aStack)) {
					slot(tSlot, ST.amount(1, aStack));
					if (mTanks[0].isEmpty()) mStopped = T;
					UT.Sounds.send(SFX.MC_CLICK, this);
					updateClientData();
				}
			}
		}
		return T;
	}
	
	protected byte oVisual = 0;
	
	@Override
	public boolean onTickCheck(long aTimer) {
		return oVisual != getVisualData() || super.onTickCheck(aTimer);
	}
	
	@Override
	public void onTickResetChecks(long aTimer, boolean aIsServerSide) {
		super.onTickResetChecks(aTimer, aIsServerSide);
		oVisual = getVisualData();
	}
	
	@Override
	public IPacket getClientDataPacket(boolean aSendAll) {
		if (aSendAll) return getClientDataPacketByteArray(aSendAll, (byte)UT.Code.getR(mRGBa), (byte)UT.Code.getG(mRGBa), (byte)UT.Code.getB(mRGBa), getVisualData(), getDirectionData(), UT.Code.toByteS(FL.id_(mTanks[0]), 0), UT.Code.toByteS(FL.id_(mTanks[0]), 1)
		, UT.Code.toByteS(ST.id(slot(0)), 0), UT.Code.toByteS(ST.id(slot(0)), 1), UT.Code.toByteS(ST.meta(slot(0)), 0), UT.Code.toByteS(ST.meta(slot(0)), 1)
		, UT.Code.toByteS(ST.id(slot(1)), 0), UT.Code.toByteS(ST.id(slot(1)), 1), UT.Code.toByteS(ST.meta(slot(1)), 0), UT.Code.toByteS(ST.meta(slot(1)), 1)
		, UT.Code.toByteS(ST.id(slot(2)), 0), UT.Code.toByteS(ST.id(slot(2)), 1), UT.Code.toByteS(ST.meta(slot(2)), 0), UT.Code.toByteS(ST.meta(slot(2)), 1)
		, UT.Code.toByteS(ST.id(slot(3)), 0), UT.Code.toByteS(ST.id(slot(3)), 1), UT.Code.toByteS(ST.meta(slot(3)), 0), UT.Code.toByteS(ST.meta(slot(3)), 1)
		);
		return getClientDataPacketByte(aSendAll, getVisualData());
	}
	
	@Override
	public boolean receiveDataByteArray(byte[] aData, INetworkHandler aNetworkHandler) {
		super.receiveDataByteArray(aData, aNetworkHandler);
		int i = 5;
		if (aData.length <= i) return T;
		mTanks[0].setFluid(FL.make(UT.Code.combine(aData[i++], aData[i++]), mTanks[0].getCapacity()));
		slot(0, ST.make(UT.Code.combine(aData[i++], aData[i++]), 1, UT.Code.combine(aData[i++], aData[i++])));
		slot(1, ST.make(UT.Code.combine(aData[i++], aData[i++]), 1, UT.Code.combine(aData[i++], aData[i++])));
		slot(2, ST.make(UT.Code.combine(aData[i++], aData[i++]), 1, UT.Code.combine(aData[i++], aData[i++])));
		slot(3, ST.make(UT.Code.combine(aData[i++], aData[i++]), 1, UT.Code.combine(aData[i++], aData[i++])));
		return T;
	}
	
	@Override
	public void setVisualData(byte aData) {
		mStopped = ((aData & 32) != 0);
		mRunning = ((aData & 16) != 0);
		mMode = (byte)(aData & 15);
	}
	
	@Override public byte getVisualData() {return (byte)(mStopped?mRunning?mMode|48:mMode|32:mRunning?mMode|16:mMode);}
	@Override public byte getDefaultSide() {return SIDE_BOTTOM;}
	@Override public boolean[] getValidSides() {return SIDES_VALID;}
	
	@Override
	protected IFluidTank getFluidTankFillable2(byte aSide, FluidStack aFluidToFill) {
		return FL.Coolant_IC2.is(aFluidToFill) ? mTanks[0] : null; // TODO MULTIPLE COOLANT TYPES
	}
	
	@Override
	protected IFluidTank getFluidTankDrainable2(byte aSide, FluidStack aFluidToDrain) {
		return mTanks[1];
	}
	
	@Override
	protected IFluidTank[] getFluidTanks2(byte aSide) {
		return mTanks;
	}
	
	@Override
	public int funnelFill(byte aSide, FluidStack aFluid, boolean aDoFill) {
		if (!FL.Coolant_IC2.is(aFluid)) return 0; // TODO MULTIPLE COOLANT TYPES
		updateInventory();
		return mTanks[0].fill(aFluid, aDoFill);
	}
	
	@Override
	public FluidStack tapDrain(byte aSide, int aMaxDrain, boolean aDoDrain) {
		updateInventory();
		return mTanks[mTanks[1].has() ? 1 : 0].drain(aMaxDrain, aDoDrain);
	}
	
	public ITexture mTextures[] = new ITexture[15];
	
	@Override
	public int getRenderPasses2(Block aBlock, boolean[] aShouldSideBeRendered) {
		mTextures[ 0] = BlockTextureMulti.get(BlockTextureDefault.get(sColoreds[0], mRGBa), BlockTextureDefault.get(sOverlays[0]));
		mTextures[ 1] = BlockTextureMulti.get(BlockTextureDefault.get(sColoreds[1], mRGBa), BlockTextureDefault.get(sOverlays[1]));
		mTextures[ 2] = BlockTextureMulti.get(BlockTextureDefault.get(sColoreds[2], mRGBa), BlockTextureDefault.get(sOverlays[2]));
		mTextures[ 3] = BlockTextureMulti.get(BlockTextureDefault.get(sColoreds[3], mRGBa), BlockTextureDefault.get(sOverlays[3]));
		mTextures[ 4] = BlockTextureMulti.get(BlockTextureDefault.get(sColoreds[4], mRGBa), BlockTextureDefault.get(sOverlays[4]));
		mTextures[ 5] = BlockTextureMulti.get(BlockTextureDefault.get(sColoreds[5], mRGBa), BlockTextureDefault.get(sOverlays[5]));
		mTextures[10] = (mTanks[0].has() ? BlockTextureFluid.get(mTanks[0]) : null);
		ItemStack
		aStack = slot(0);
		if (ST.item(aStack) instanceof IItemReactorRod) {
			mTextures[ 6] = ((IItemReactorRod)ST.item_(aStack)).getReactorRodTextureSides(this, 0, aStack, !mStopped && (mMode & B[0]) == 0);
			mTextures[11] = ((IItemReactorRod)ST.item_(aStack)).getReactorRodTextureTop  (this, 0, aStack, !mStopped && (mMode & B[0]) == 0);
		} else {
			mTextures[ 6] = null;
			mTextures[11] = null;
		}
		aStack = slot(1);
		if (ST.item(aStack) instanceof IItemReactorRod) {
			mTextures[ 7] = ((IItemReactorRod)ST.item_(aStack)).getReactorRodTextureSides(this, 1, aStack, !mStopped && (mMode & B[1]) == 0);
			mTextures[12] = ((IItemReactorRod)ST.item_(aStack)).getReactorRodTextureTop  (this, 1, aStack, !mStopped && (mMode & B[1]) == 0);
		} else {
			mTextures[ 7] = null;
			mTextures[12] = null;
		}
		aStack = slot(2);
		if (ST.item(aStack) instanceof IItemReactorRod) {
			mTextures[ 8] = ((IItemReactorRod)ST.item_(aStack)).getReactorRodTextureSides(this, 2, aStack, !mStopped && (mMode & B[2]) == 0);
			mTextures[13] = ((IItemReactorRod)ST.item_(aStack)).getReactorRodTextureTop  (this, 2, aStack, !mStopped && (mMode & B[2]) == 0);
		} else {
			mTextures[ 8] = null;
			mTextures[13] = null;
		}
		aStack = slot(3);
		if (ST.item(aStack) instanceof IItemReactorRod) {
			mTextures[ 9] = ((IItemReactorRod)ST.item_(aStack)).getReactorRodTextureSides(this, 3, aStack, !mStopped && (mMode & B[3]) == 0);
			mTextures[14] = ((IItemReactorRod)ST.item_(aStack)).getReactorRodTextureTop  (this, 3, aStack, !mStopped && (mMode & B[3]) == 0);
		} else {
			mTextures[ 9] = null;
			mTextures[14] = null;
		}
		return 11;
	}
	
	@Override
	public boolean usesRenderPass2(int aRenderPass, boolean[] aShouldSideBeRendered) {
		return aRenderPass < 6 || (aRenderPass == 6 && slotHas(0)) || (aRenderPass == 7 && slotHas(1)) || (aRenderPass == 8 && slotHas(2)) || (aRenderPass == 9 && slotHas(3)) || (aRenderPass == 10 && mTanks[0].has());
	}
	
	@Override
	public boolean setBlockBounds2(Block aBlock, int aRenderPass, boolean[] aShouldSideBeRendered) {
		switch (aRenderPass) {
		case SIDE_X_NEG: return box(aBlock, PX_P[ 0], PX_P[ 0], PX_P[ 0], PX_N[14], PX_N[ 0], PX_N[ 0]);
		case SIDE_Y_NEG: return box(aBlock, PX_P[ 0], PX_P[ 0], PX_P[ 0], PX_N[ 0], PX_N[14], PX_N[ 0]);
		case SIDE_Z_NEG: return box(aBlock, PX_P[ 0], PX_P[ 0], PX_P[ 0], PX_N[ 0], PX_N[ 0], PX_N[14]);
		case SIDE_X_POS: return box(aBlock, PX_P[14], PX_P[ 0], PX_P[ 0], PX_N[ 0], PX_N[ 0], PX_N[ 0]);
		case SIDE_Y_POS: return box(aBlock, PX_P[ 0], PX_P[14], PX_P[ 0], PX_N[ 0], PX_N[ 0], PX_N[ 0]);
		case SIDE_Z_POS: return box(aBlock, PX_P[ 0], PX_P[ 0], PX_P[14], PX_N[ 0], PX_N[ 0], PX_N[ 0]);
		
		case  6: return box(aBlock, PX_P[ 2], PX_P[ 1], PX_P[ 2], PX_N[10], PX_P[17], PX_N[10]);
		case  7: return box(aBlock, PX_P[ 2], PX_P[ 1], PX_P[10], PX_N[10], PX_P[17], PX_N[ 2]);
		case  8: return box(aBlock, PX_P[10], PX_P[ 1], PX_P[ 2], PX_N[ 2], PX_P[17], PX_N[10]);
		case  9: return box(aBlock, PX_P[10], PX_P[ 1], PX_P[10], PX_N[ 2], PX_P[17], PX_N[ 2]);
		
		case 10: return box(aBlock, PX_P[ 2]+PX_OFFSET, PX_P[ 2], PX_P[ 2]+PX_OFFSET, PX_N[ 2]-PX_OFFSET, PX_N[ 2], PX_N[ 2]-PX_OFFSET);
		}
		return F;
	}
	
	@Override
	public ITexture getTexture2(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {
		return aRenderPass < 6 && !ALONG_AXIS[aRenderPass][aSide] ? null : aRenderPass == mFacing ? mTextures[4] : aRenderPass == mSecondFacing ? mTextures[5] : aRenderPass >= 6 || aRenderPass < 2 ? mTextures[SIDES_VERTICAL[aSide] && aRenderPass != 10 && aRenderPass > 1 ? aRenderPass+5 : aRenderPass] : mTextures[aRenderPass < 6 && isCovered((byte)aRenderPass) ? 3 : 2];
	}
	
	@Override public void onEntityCollidedWithBlock(Entity aEntity) {if (mRunning) {UT.Entities.applyHeatDamage(aEntity, 5); UT.Entities.applyRadioactivity(aEntity, 3, 1);}}
	@Override public AxisAlignedBB getCollisionBoundingBoxFromPool() {return box(PX_P[1], PX_P[1], PX_P[1], PX_N[1], PX_N[1], PX_N[1]);}
	
	@Override public ItemStack[] getDefaultInventory(NBTTagCompound aNBT) {return new ItemStack[4];}
	@Override public boolean canDrop(int aInventorySlot) {return T;}
	
	@Override public int[] getAccessibleSlotsFromSide2(byte aSide) {return ZL_INTEGER;}
	@Override public boolean canInsertItem2 (int aSlot, ItemStack aStack, byte aSide) {return F;}
	@Override public boolean canExtractItem2(int aSlot, ItemStack aStack, byte aSide) {return F;}
	
	@Override public boolean getStateRunningPassively() {return mRunning;}
	@Override public boolean getStateRunningPossible() {return mRunning;}
	@Override public boolean getStateRunningActively() {return mRunning;}
	@Override public boolean setStateOnOff(boolean aOnOff) {return mStopped = !aOnOff;}
	@Override public boolean getStateOnOff() {return !mStopped;}
	@Override public byte setStateMode(byte aMode) {return mMode = aMode;}
	@Override public byte getStateMode() {return mMode;}
	
	public static IIconContainer sColoreds[] = new IIconContainer[] {
		new Textures.BlockIcons.CustomIcon("machines/generators/reactor_core/colored/bottom"),
		new Textures.BlockIcons.CustomIcon("machines/generators/reactor_core/colored/top"),
		new Textures.BlockIcons.CustomIcon("machines/generators/reactor_core/colored/side1"),
		new Textures.BlockIcons.CustomIcon("machines/generators/reactor_core/colored/side2"),
		new Textures.BlockIcons.CustomIcon("machines/generators/reactor_core/colored/face1"),
		new Textures.BlockIcons.CustomIcon("machines/generators/reactor_core/colored/face2")
	}, sOverlays[] = new IIconContainer[] {
		new Textures.BlockIcons.CustomIcon("machines/generators/reactor_core/overlay/bottom"),
		new Textures.BlockIcons.CustomIcon("machines/generators/reactor_core/overlay/top"),
		new Textures.BlockIcons.CustomIcon("machines/generators/reactor_core/overlay/side1"),
		new Textures.BlockIcons.CustomIcon("machines/generators/reactor_core/overlay/side2"),
		new Textures.BlockIcons.CustomIcon("machines/generators/reactor_core/overlay/face1"),
		new Textures.BlockIcons.CustomIcon("machines/generators/reactor_core/overlay/face2")
	};
	
	@Override public String getTileEntityName() {return "gt.multitileentity.generator.reactor.core";}
}
