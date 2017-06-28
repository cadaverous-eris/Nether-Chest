package netherchest.common.tileentity;

import java.rmi.registry.Registry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import netherchest.common.blocks.BlockNetherChest;
import netherchest.common.inventory.ContainerNetherChest;
import netherchest.common.inventory.ExtendedItemStackHandler;

public class TileEntityNetherChest extends TileEntity implements ITickable, IWorldNameable {

	public float lidAngle = 0;
	public float prevLidAngle = 0;
	public int numPlayersUsing = 0;
	private int ticksSinceSync = 0;
	protected String customName;
	public int renderTick = 0;
	
	ExtendedItemStackHandler itemHandler = new ExtendedItemStackHandler(27) {
		@Override
		public void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			TileEntityNetherChest.this.world.addBlockEvent(TileEntityNetherChest.this.pos, TileEntityNetherChest.this.getBlockType(), 1, TileEntityNetherChest.this.numPlayersUsing);
			TileEntityNetherChest.this.markDirty();
		}
	};
	
	public int getRenderTick() {
		return renderTick;
	}

	@Override
	public void update() {
		int i = this.pos.getX();
		int j = this.pos.getY();
		int k = this.pos.getZ();
		++this.ticksSinceSync;
		++this.renderTick;

		if (!this.world.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + i + j + k) % 200 == 0) {

			int recordedUsers = numPlayersUsing;
			this.numPlayersUsing = 0;
			float f = 5.0F;

			for (EntityPlayer entityplayer : this.world.getEntitiesWithinAABB(EntityPlayer.class,
					new AxisAlignedBB((double) ((float) i - 5.0F), (double) ((float) j - 5.0F),
							(double) ((float) k - 5.0F), (double) ((float) (i + 1) + 5.0F),
							(double) ((float) (j + 1) + 5.0F), (double) ((float) (k + 1) + 5.0F)))) {
				if (entityplayer.openContainer instanceof ContainerNetherChest) {
					TileEntityNetherChest tenc = ((ContainerNetherChest) entityplayer.openContainer).getTile();

					if (tenc.equals(this)) {
						++this.numPlayersUsing;
					}
				}
			}
			if (numPlayersUsing != recordedUsers) {
				markDirty();
			}
		}

		this.prevLidAngle = this.lidAngle;
		float f1 = 0.1F;

		if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F) {
			this.world.playSound((EntityPlayer) null, i + 0.5D, j + 0.5D, k + 0.5D, SoundEvents.BLOCK_CHEST_OPEN,
					SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
		}

		if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F) {
			float f2 = this.lidAngle;

			if (this.numPlayersUsing > 0) {
				this.lidAngle += 0.1F;
			} else {
				this.lidAngle -= 0.1F;
			}

			if (this.lidAngle > 1.0F) {
				this.lidAngle = 1.0F;
			}

			float f3 = 0.5F;

			if (this.lidAngle < 0.5F && f2 >= 0.5F) {
				this.world.playSound((EntityPlayer) null, i + 0.5D, j + 0.5D, k + 0.5D, SoundEvents.BLOCK_CHEST_CLOSE,
						SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
			}

			if (this.lidAngle < 0.0F) {
				this.lidAngle = 0.0F;
			}
			
			//this.lidAngle = 1.0F;
		}
	}

	@Override
	public boolean receiveClientEvent(int id, int type) {
		if (id == 1) {
			this.numPlayersUsing = type;
			this.markDirty();
			return true;
		} else {
			return super.receiveClientEvent(id, type);
		}
	}

	public void openInventory(EntityPlayer player) {
		if (!player.isSpectator()) {
			if (this.numPlayersUsing < 0) {
				this.numPlayersUsing = 0;
			}

			++this.numPlayersUsing;
			this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
			this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
			markDirty();
		}
	}

	public void closeInventory(EntityPlayer player) {
		if (!player.isSpectator() && this.getBlockType() instanceof BlockNetherChest) {
			--this.numPlayersUsing;
			this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
			this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
			markDirty();
		}
	}

	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		this.invalidate();
		if (itemHandler != null && !world.isRemote) {
			for (int i = 0; i < itemHandler.getSlots(); i++) {
				for (ItemStack stack : itemHandler.getExtendedStackInSlot(i).getAllStacks()) {
					if (stack != null && !stack.isEmpty()) {
						state.getBlock().spawnAsEntity(world, pos, stack);
					}
				}
			}
		}
		world.setTileEntity(pos, null);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("Items")) {
			itemHandler.deserializeNBT(compound.getCompoundTag("Items"));
		}
		if (compound.hasKey("CustomName", 8)) {
			this.setCustomName(compound.getString("CustomName"));
		}
		if (compound.hasKey("numUsers")) {
			numPlayersUsing = compound.getInteger("numUsers");
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("Items", itemHandler.serializeNBT());
		if (this.hasCustomName()) {
			compound.setString("CustomName", this.customName);
		}
		compound.setTag("numUsers", new NBTTagInt(numPlayersUsing));
		return compound;
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), getBlockMetadata(), getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public void markDirty() {
		super.markDirty();
		if (getWorld() != null) {
			getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos), 3);
			this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
		}
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		return super.getCapability(capability, facing);
	}
	
	public ExtendedItemStackHandler getHandler() {
		return itemHandler;
	}

	@Override
	public String getName() {
		return this.hasCustomName() ? this.customName : "container.netherchest.nether_chest";
	}

	@Override
	public boolean hasCustomName() {
		return this.customName != null && !this.customName.isEmpty();
	}

	public void setCustomName(String p_190575_1_) {
		this.customName = p_190575_1_;
	}

	@Nullable
	@Override
	public ITextComponent getDisplayName() {
		return (ITextComponent)(this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName(), new Object[0]));
	}

}
