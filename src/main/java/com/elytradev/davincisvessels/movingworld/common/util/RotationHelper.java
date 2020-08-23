package com.elytradev.davincisvessels.movingworld.common.util;

import java.util.Objects;

import com.elytradev.davincisvessels.DavincisVesselsMod;
import com.elytradev.davincisvessels.concrete.reflect.accessor.Accessor;
import com.elytradev.davincisvessels.concrete.reflect.accessor.Accessors;
import com.elytradev.davincisvessels.movingworld.api.rotation.IRotationProperty;
import com.elytradev.davincisvessels.movingworld.common.chunk.LocatedBlock;
import com.ferreusveritas.rfrotors.blocks.BlockRotor;
import com.ferreusveritas.rfrotors.tileentities.TileEntityRotorBlock;

import cofh.core.block.TileReconfigurable;
import cofh.thermalexpansion.block.dynamo.TileDynamoBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.Vec3i;

public class RotationHelper {

	public static LocatedBlock rotateBlock(LocatedBlock locatedBlock, boolean ccw) {
		IBlockState blockState = locatedBlock.blockState;
		if (locatedBlock != null && locatedBlock.blockState != null) {

			Block block = blockState.getBlock();

			if (block != null) {
				if(block instanceof BlockSkull) {
					locatedBlock = rotateSkull(locatedBlock, ccw);
				}
				else if(block instanceof BlockRotor) {
					locatedBlock = rotateRotor(locatedBlock, ccw);
				}
				
				if(locatedBlock.tileEntity != null) {
					TileEntity tile = locatedBlock.tileEntity;
					if(tile instanceof TileReconfigurable) {
						TileReconfigurable tileRecon = (TileReconfigurable) tile;
						for(int i = 0; i < (ccw ? 3 : 1); i++) {
							tileRecon.rotateBlock();
						}
					}
					else if(tile instanceof TileDynamoBase) {
						TileDynamoBase tileDynamo = (TileDynamoBase) tile;
						EnumFacing facing = EnumFacing.values()[tileDynamo.facing % 6];
						if(facing != EnumFacing.DOWN && facing != EnumFacing.UP) {
							facing = ccw ? facing.rotateYCCW() : facing.rotateY();
							tileDynamo.facing = (byte) facing.ordinal();
						}
					}
				}
			}
					
			
			for (IProperty prop : blockState.getProperties().keySet()) {
				if(prop instanceof PropertyInteger) {
					blockState = rotatePropertyInteger((PropertyInteger) prop, blockState, ccw);
				}
				else if(prop instanceof PropertyEnum) {
					blockState = rotatePropertyEnum((PropertyEnum) prop, blockState, ccw);
				}
				else if (prop instanceof IRotationProperty) {
					// Custom rotation property found.
					DavincisVesselsMod.LOG.debug("Rotate state in " + blockState.getBlock().getLocalizedName() + " " + blockState.getValue(prop));
					IRotationProperty rotationProperty = (IRotationProperty) prop;
					blockState = rotationProperty.rotate(blockState, ccw);
					DavincisVesselsMod.LOG.debug("Rotate state out " + blockState.getBlock().getLocalizedName() + " " + blockState.getValue(prop));
				}
			}
		}

		return new LocatedBlock(blockState, locatedBlock.tileEntity, locatedBlock.blockPos, locatedBlock.bPosNoOffset);
	}

	public static IBlockState rotatePropertyInteger(PropertyInteger intProp, IBlockState blockState, boolean ccw) {
		int propVal = blockState.getValue(intProp);

		//Seems to only be used by banners
		if (Objects.equals(intProp.getName(), "rotation") && isValidRotationPropertyInteger(intProp)) {
			for (int i = 0; i <= 3; i++) {
				propVal = RotationHelper.rotateInteger(propVal, 0, 15, ccw);
			}
			blockState = blockState.withProperty(intProp, propVal);
		}

		return blockState;
	}

	static boolean isValidRotationPropertyInteger(PropertyInteger intProp) {
		return intProp.getAllowedValues().contains(0) && intProp.getAllowedValues().contains(15)
				&& (!intProp.getAllowedValues().contains(-1) && !intProp.getAllowedValues().contains(16));
	}


	public static IBlockState rotatePropertyEnum(PropertyEnum propertyEnum, IBlockState blockState, boolean ccw) {
		Object propertyValue = blockState.getValue(propertyEnum);

		if (propertyValue instanceof EnumFacing) {
			EnumFacing facing = (EnumFacing) propertyValue;

			if (facing.getHorizontalIndex() != -1) {
				blockState = blockState.withProperty(propertyEnum, ccw ? facing.rotateYCCW() : facing.rotateY());
			}
		} else if (propertyValue instanceof EnumFacing.Axis) {
			EnumFacing.Axis axis = (EnumFacing.Axis) propertyValue;

			axis = axis == EnumFacing.Axis.X ? EnumFacing.Axis.Z : axis == EnumFacing.Axis.Z ? EnumFacing.Axis.X : axis;

			blockState = blockState.withProperty(propertyEnum, axis);
		} else if (propertyValue instanceof BlockLog.EnumAxis) {
			BlockLog.EnumAxis axis = (BlockLog.EnumAxis) blockState.getValue(propertyEnum);

			axis = axis == BlockLog.EnumAxis.X ? BlockLog.EnumAxis.Z : axis == BlockLog.EnumAxis.Z ? BlockLog.EnumAxis.X : axis;

			blockState = blockState.withProperty(propertyEnum, axis);
		} else if (propertyValue instanceof BlockLever.EnumOrientation) {
			BlockLever.EnumOrientation orientation = (BlockLever.EnumOrientation) blockState.getValue(propertyEnum);
			EnumFacing facing = orientation.getFacing();

			if (facing.getHorizontalIndex() != -1) {
				// Not on the vertical axis.

				facing = ccw ? facing.rotateYCCW() : facing.rotateY();

				for (BlockLever.EnumOrientation enumOrientation : BlockLever.EnumOrientation.values()) {
					if (enumOrientation.getFacing() == facing) {
						orientation = enumOrientation;
						break;
					}
				}
			} else {
				// On the vertical axis.
				switch (orientation) {
					case DOWN_X: orientation = BlockLever.EnumOrientation.DOWN_Z; break;
					case DOWN_Z: orientation = BlockLever.EnumOrientation.DOWN_X; break;
					case UP_X: orientation = BlockLever.EnumOrientation.UP_Z; break;
					case UP_Z: orientation = BlockLever.EnumOrientation.UP_X; break;
					default:break;
				}
			}

			blockState = blockState.withProperty(propertyEnum, orientation);
		}

		return blockState;
	}

	public static LocatedBlock rotateSkull(LocatedBlock locatedBlock, boolean ccw) {
		if (locatedBlock.tileEntity instanceof TileEntitySkull) {
			TileEntitySkull tile = (TileEntitySkull) locatedBlock.tileEntity;
			int skullRot = Accessors.<Integer>findField(TileEntitySkull.class, "skullRotation", "field_145910_i").get(tile);
			tile.setSkullRotation((ccw ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_90).rotate(skullRot, 16));
		}
		
		return locatedBlock;
	}

	public static LocatedBlock rotateRotor(LocatedBlock locatedBlock, boolean ccw) {
		if (locatedBlock.tileEntity instanceof TileEntityRotorBlock) {
			TileEntityRotorBlock tile = (TileEntityRotorBlock) locatedBlock.tileEntity;
			Accessor<EnumFacing> facingAccessor = Accessors.findField(TileEntityRotorBlock.class, "rotorDir");
			EnumFacing facing = facingAccessor.get(tile);
			
			if (facing.getHorizontalIndex() != -1) {
				facing = ccw ? facing.rotateYCCW() : facing.rotateY();
			}
			
			facingAccessor.set(tile, facing);
		}
		
		return locatedBlock;
	}
	
	public static int rotateInteger(int integer, int min, int max, boolean ccw) {
		int result = integer;

		if (!ccw) {
			if (result + 1 > max) {
				result = min;
			}
			else {
				result = result + 1;
			}
		} else {
			if (result - 1 < min) {
				result = max;
			}
			else {
				result = result - 1;
			}
		}

		return result;
	}

	public static Vec3i getDirectionVec(EnumFacing facing) {
		switch (facing) {
			case DOWN:
				return new Vec3i(0, -1, 0);
			case UP:
				return new Vec3i(0, 1, 0);
			case NORTH:
				return new Vec3i(0, 0, -1);
			case SOUTH:
				return new Vec3i(0, 0, 1);
			case WEST:
				return new Vec3i(-1, 0, 0);
			case EAST:
				return new Vec3i(1, 0, 0);
		}

		return null;
	}

}
