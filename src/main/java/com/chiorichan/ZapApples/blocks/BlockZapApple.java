package com.chiorichan.ZapApples.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockZapApple extends BlockGrayApple
{
	
	@SideOnly( Side.CLIENT )
	protected IIcon icon;
	public boolean isHarvestDay;
	
	public BlockZapApple()
	{
		super();
		setResistance( 0.0F );
		setHardness( 0.1F );
		setStepSound( Block.soundTypeGrass );
		//setUnlocalizedName( "zapApple" );
	}
	
	public IIcon getIcon( int side, int meta )
	{
		return icon;
	}
	
	@SideOnly( Side.CLIENT )
	public void registerIcons( IIconRegister register )
	{
		icon = register.registerIcon( "zapapples:zapapple_mature" );
	}
	
	public int quantityDropped( Random rand )
	{
		return isHarvestDay ? 1 : 0;
	}
}