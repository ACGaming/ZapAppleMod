package com.chiorichan.ZapApples.network.packet.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import com.chiorichan.ZapApples.network.packet.bidirectional.AbstractBiMessageHandler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class SendEffectsPacketHandler extends AbstractBiMessageHandler<SendEffectsPacket>
{
	@Override
	public IMessage handleClientMessage( EntityPlayer player, SendEffectsPacket message, MessageContext ctx )
	{
		if ( message.msgId == 0 )
			Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects( message.x, message.y, message.z, message.block, message.meta );
		
		return null;
	}
	
	@Override
	public IMessage handleServerMessage( EntityPlayer player, SendEffectsPacket message, MessageContext ctx )
	{
		return null;
	}
}
