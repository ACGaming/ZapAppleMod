package com.chiorichan.ZapApples.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;

public class ItemZapApple extends ItemGrayApple
{
	public ItemZapApple(Block block)
	{
		super( block );
		healAmount = 10;
		saturationModifier = 0.8F;
		
		setAlwaysEdible();
		setPotionEffect( Potion.field_76444_x.id, 60, 1, 1.0F );
	}
	
	public boolean hasEffect( ItemStack stack )
	{
		return true;
	}
	
	public void addInformation( ItemStack itemstack, EntityPlayer player, List list, boolean par )
	{
		list.add( EnumChatFormatting.YELLOW + "Give 1 Minute Absorption and 5 Full Meat Pops!" );
	}
}