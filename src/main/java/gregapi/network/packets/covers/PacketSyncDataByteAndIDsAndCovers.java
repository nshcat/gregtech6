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

package gregapi.network.packets.covers;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import gregapi.block.IBlockSyncData.IBlockSyncDataAndCoversAndIDs;
import gregapi.cover.CoverData;
import gregapi.network.INetworkHandler;
import gregapi.network.packets.PacketCoordinates;
import gregapi.network.packets.ids.PacketSyncDataByteAndIDs;
import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.IBlockAccess;

/**
 * @author Gregorius Techneticies
 * 
 * Transmits the extended Data of a Block.
 */
public class PacketSyncDataByteAndIDsAndCovers extends PacketSyncDataByteAndIDs {
	public short mCoverIDs[], mCoverMetas[], mCoverVisuals[];
	
	public PacketSyncDataByteAndIDsAndCovers(int aDecoderType) {
		super(aDecoderType);
	}
	
	public PacketSyncDataByteAndIDsAndCovers(int aX, int aY, int aZ, short aID1, short aID2, byte aData, CoverData aCoverData) {
		super(aX, aY, aZ, aID1, aID2, aData);
		mCoverIDs = aCoverData.mIDs;
		mCoverMetas = aCoverData.mMetas;
		mCoverVisuals = aCoverData.mVisuals;
	}
	public PacketSyncDataByteAndIDsAndCovers(ChunkCoordinates aCoords, short aID1, short aID2, byte aData, CoverData aCoverData) {
		super(aCoords, aID1, aID2, aData);
		mCoverIDs = aCoverData.mIDs;
		mCoverMetas = aCoverData.mMetas;
		mCoverVisuals = aCoverData.mVisuals;
	}
	
	private PacketSyncDataByteAndIDsAndCovers(int aX, int aY, int aZ, short aID1, short aID2, byte aData, ByteArrayDataInput aCovers) {
		super(aX, aY, aZ, aID1, aID2, aData);
		mCoverIDs       = new short[] {0,0,0,0,0,0};
		mCoverMetas     = new short[] {0,0,0,0,0,0};
		mCoverVisuals   = new short[] {0,0,0,0,0,0};
		for (byte i = 0, j = aCovers.readByte(); i < 6; i++) if ((j & (1 << i)) != 0) {mCoverIDs[i] = aCovers.readShort(); mCoverMetas[i] = aCovers.readShort(); mCoverVisuals[i] = aCovers.readShort();}
		for (byte i = 0; i < 6; i++) if (mCoverIDs[i] != 0) return;
		mCoverIDs = null;
		mCoverMetas = null;
		mCoverVisuals = null;
	}
	
	@Override
	public byte getPacketIDOffset() {
		return -24;
	}
	
	@Override
	public ByteArrayDataOutput encode2(ByteArrayDataOutput aData) {
		aData = super.encode2(aData);
		byte tCoverBits = 0;
		if (mCoverIDs != null) for (byte i = 0; i < 6; i++) if (mCoverIDs[i] > 0) tCoverBits |= (1 << i);
		aData.writeByte(tCoverBits);
		if (mCoverIDs != null) for (byte i = 0; i < 6; i++) if (mCoverIDs[i] > 0) {aData.writeShort(mCoverIDs[i]); aData.writeShort(mCoverMetas[i]); aData.writeShort(mCoverVisuals[i]);}
		return aData;
	}
	
	@Override
	public PacketCoordinates decode2(int aX, int aY, int aZ, ByteArrayDataInput aData) {
		return new PacketSyncDataByteAndIDsAndCovers(aX, aY, aZ, aData.readShort(), aData.readShort(), aData.readByte(), aData);
	}
	
	@Override
	public void process(IBlockAccess aWorld, INetworkHandler aNetworkHandler) {
		if (aWorld != null) {
			Block tBlock = aWorld.getBlock(mX, mY, mZ);
			if (tBlock instanceof IBlockSyncDataAndCoversAndIDs) ((IBlockSyncDataAndCoversAndIDs)tBlock).receiveDataByte(aWorld, mX, mY, mZ, mData, aNetworkHandler, mID1, mID2, mCoverIDs, mCoverMetas, mCoverVisuals);
		}
	}
}
