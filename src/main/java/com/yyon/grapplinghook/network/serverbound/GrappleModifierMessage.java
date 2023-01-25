package com.yyon.grapplinghook.network.serverbound;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.blockentity.GrappleModifierBlockEntity;
import com.yyon.grapplinghook.network.NetworkContext;
import com.yyon.grapplinghook.network.serverbound.BaseMessageServer;
import com.yyon.grapplinghook.util.GrappleCustomization;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/*
    GrappleMod is free software: you can redistribute it and/or modify
    it under the teHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GrappleMod.  If not, see <http://www.gnu.org/licenses/>.
 */

public class GrappleModifierMessage extends BaseMessageServer {
   
	public BlockPos pos;
	public GrappleCustomization custom;

    public GrappleModifierMessage(BlockPos pos, GrappleCustomization custom) {
    	this.pos = pos;
    	this.custom = custom;
    }

	public GrappleModifierMessage(FriendlyByteBuf buf) {
		super(buf);
	}

	@Override
    public void decode(FriendlyByteBuf buf) {
    	this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    	this.custom = new GrappleCustomization();
    	this.custom.readFromBuf(buf);
    }

	@Override
    public void encode(FriendlyByteBuf buf) {
    	buf.writeInt(this.pos.getX());
    	buf.writeInt(this.pos.getY());
    	buf.writeInt(this.pos.getZ());
    	this.custom.writeToBuf(buf);
    }

	@Override
	public ResourceLocation getChannel() {
		return GrappleMod.id("grapple_modifier");
	}

	@Override
    public void processMessage(NetworkContext ctx) {
		Level w = ctx.getSender().level;
		
		BlockEntity ent = w.getBlockEntity(this.pos);

		if (ent != null && ent instanceof GrappleModifierBlockEntity) {
			((GrappleModifierBlockEntity) ent).setCustomizationServer(this.custom);
		}
    }
}
