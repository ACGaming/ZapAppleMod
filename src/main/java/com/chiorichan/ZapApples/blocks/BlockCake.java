package com.chiorichan.ZapApples.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.chiorichan.ZapApples.ZapApples;
import com.chiorichan.ZapApples.tileentity.TileEntityCake;
import com.chiorichan.ZapApples.util.InventoryUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCake extends BlockContainer
{
	public ItemStack cachedItemStack;
	protected Map<String, CakeIngredientMap> baseIngredients = Maps.newLinkedHashMap();
	
	protected Map<String, CakeIngredientMap> frostIngredients = Maps.newLinkedHashMap();
	
	protected boolean postRender = false;
	
	public BlockCake()
	{
		super( Material.cake );
		setCreativeTab( CreativeTabs.tabFood );
		setHardness( 0.5F );
		setStepSound( Block.soundTypeCloth );
		// setUnlocalizedName( "zapplePie" );
	}
	
	public void registerBaseOption( String key, String title, ItemStack activator )
	{
		if ( postRender )
		{
			return;
		}
		baseIngredients.put( key, new CakeIngredientMap( key, title, activator ) );
	}
	
	public void registerFrostOption( String key, String title, ItemStack activator )
	{
		if ( postRender )
		{
			return;
		}
		frostIngredients.put( key, new CakeIngredientMap( key, title, activator ) );
	}
	
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	
	public void setBlockBoundsBasedOnState( IBlockAccess world, int x, int y, int z )
	{
		TileEntityCake tile = (TileEntityCake) world.getTileEntity( x, y, z );
		if ( tile != null )
		{
			float slice = 0.0625F;
			float delta = ( 1 + tile.stage * 2 ) / 16.0F;
			setBlockBounds( delta, 0.0F, slice, 1.0F - slice, 0.5F, 1.0F - slice );
		}
		else
		{
			setBlockBounds( 0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F );
		}
	}
	
	public void setBlockBoundsForItemRender()
	{
		float slice = 0.0625F;
		setBlockBounds( slice, 0.0F, slice, 1.0F - slice, 0.5F, 1.0F - slice );
	}
	
	public boolean onBlockActivated( World world, int x, int y, int z, EntityPlayer player, int side, float par7, float par8, float par9 )
	{
		ItemStack current = player.inventory.getCurrentItem();
		
		if ( !world.isRemote )
		{
			if ( current != null )
			{
				for ( Entry<String, CakeIngredientMap> ent : frostIngredients.entrySet() )
				{
					CakeIngredientMap val = (CakeIngredientMap) ent.getValue();
					
					if ( ( val.activator != null ) && ( current == val.activator ) && ( current.getItemDamage() == val.activator.getItemDamage() ) )
					{
						TileEntityCake cake = (TileEntityCake) world.getTileEntity( x, y, z );
						
						if ( cake != null )
						{
							cake.setData( cake.base, val.key, cake.stage );
							
							if ( !player.capabilities.isCreativeMode )
							{
								player.inventory.setInventorySlotContents( player.inventory.currentItem, InventoryUtil.consumeItem( current ) );
							}
							world.markBlockForUpdate( x, y, z ); // XXX RenderUpdate - Is this right?
							
							break;
						}
					}
				}
			}
			else
			{
				eatSlice( world, x, y, z, player );
				return true;
			}
		}
		return true;
	}
	
	public void onBlockClicked( World world, int x, int y, int z, EntityPlayer player )
	{
		if ( !world.isRemote )
		{
			eatSlice( world, x, y, z, player );
		}
	}
	
	private void eatSlice( World world, int x, int y, int z, EntityPlayer player )
	{
		if ( player.canEat( false ) )
		{
			TileEntityCake tile = (TileEntityCake) world.getTileEntity( x, y, z );
			if ( tile != null )
			{
				tile.eatSlice( player );
			}
		}
	}
	
	public boolean canPlaceBlockAt( World world, int x, int y, int z )
	{
		return !super.canPlaceBlockAt( world, x, y, z ) ? false : canBlockStay( world, x, y, z );
	}
	
	public boolean canBlockStay( World world, int x, int y, int z )
	{
		return world.getBlock( x, y, z ).getMaterial().isSolid();
	}
	
	public void onNeighborBlockChange( World world, int x, int y, int z, int meta )
	{
		if ( !canBlockStay( world, x, y, z ) )
		{
			dropBlockAsItem( world, x, y, z, world.getBlockMetadata( x, y, z ), 0 );
			world.setBlockToAir( x, y, z );
		}
	}
	
	public int idDropped( int par1, Random rand, int par3 )
	{
		return 0;
	}
	
	public int quantityDropped( Random rand )
	{
		return 0;
	}
	
	@Override
	public void getSubBlocks( Item par1, CreativeTabs par2CreativeTabs, List list )
	{
		list.add( new ItemStack( par1, 1, 0 ) );
	}
	
	public int onBlockPlaced( World world, int x, int y, int z, int hitX, float hitY, float hitZ, float block, int meta )
	{
		return meta;
	}
	
	@Override
	public TileEntity createNewTileEntity( World arg0, int arg1 )
	{
		return new TileEntityCake();
	}
	
	public int getRenderType()
	{
		return ZapApples.idRenderCake;
	}
	
	@SideOnly( Side.CLIENT )
	public void registerIcons( IIconRegister register )
	{
		postRender = true;
		
		for ( Entry<String, CakeIngredientMap> ent : baseIngredients.entrySet() )
		{
			IIcon iconTop = register.registerIcon( "zapapples:cakes/base_top_" + (String) ent.getKey() );
			IIcon iconSide = register.registerIcon( "zapapples:cakes/base_side_" + (String) ent.getKey() );
			IIcon iconEaten = register.registerIcon( "zapapples:cakes/base_eaten_" + (String) ent.getKey() );
			
			( (CakeIngredientMap) ent.getValue() ).setIcons( null, iconTop, iconSide, iconEaten );
		}
		
		for ( Entry<String, CakeIngredientMap> ent : frostIngredients.entrySet() )
		{
			IIcon iconTop = register.registerIcon( "zapapples:cakes/topping_top_" + (String) ent.getKey() );
			IIcon iconSide = register.registerIcon( "zapapples:cakes/topping_side_" + (String) ent.getKey() );
			IIcon iconEaten = register.registerIcon( "zapapples:cakes/topping_eaten_" + (String) ent.getKey() );
			
			( (CakeIngredientMap) ent.getValue() ).setIcons( iconTop, null, iconSide, iconEaten );
		}
	}
	
	@SideOnly( Side.CLIENT )
	public IIcon getIcon( int side, int meta )
	{
		return ( (CakeIngredientMap) baseIngredients.get( "plain" ) ).getIcon( side, true );
	}
	
	public CakeIngredientMap getBaseIngredient( String key )
	{
		if ( baseIngredients.containsKey( key ) )
		{
			return (CakeIngredientMap) baseIngredients.get( key );
		}
		return null;
	}
	
	public CakeIngredientMap getFrostIngredient( String key )
	{
		if ( frostIngredients.containsKey( key ) )
		{
			return (CakeIngredientMap) frostIngredients.get( key );
		}
		return null;
	}
	
	public void onBlockPlacedBy( World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack )
	{
		TileEntityCake tile = (TileEntityCake) world.getTileEntity( x, y, z );
		
		if ( ( tile != null ) && ( stack.getTagCompound() != null ) )
		{
			tile.readItemNBT( stack.getTagCompound() );
		}
	}
	
	public ItemStack getPickBlock( MovingObjectPosition target, World world, int x, int y, int z )
	{
		ItemStack result = new ItemStack( this );
		TileEntityCake tile = (TileEntityCake) world.getTileEntity( x, y, z );
		if ( tile != null )
		{
			result.setTagCompound( tile.getItemNBT() );
		}
		return result;
	}
	
	public void breakBlock( World world, int x, int y, int z, Block oldBlock, int newId )
	{
		ItemStack result = new ItemStack( this );
		TileEntityCake tile = (TileEntityCake) world.getTileEntity( x, y, z );
		if ( tile != null )
		{
			result.setTagCompound( tile.getItemNBT() );
		}
		
		cachedItemStack = result;
		
		super.breakBlock( world, x, y, z, oldBlock, newId );
	}
	
	public ArrayList<ItemStack> getBlockDropped( World world, int x, int y, int z, int metadata, int fortune )
	{
		if ( cachedItemStack == null )
		{
			return null;
		}
		ArrayList<ItemStack> result = Lists.newArrayList();
		
		result.add( cachedItemStack );
		cachedItemStack = null;
		
		return result;
	}
	
	public class CakeIngredientMap
	{
		@SideOnly( Side.CLIENT )
		public IIcon top;
		
		@SideOnly( Side.CLIENT )
		public IIcon bottom;
		
		@SideOnly( Side.CLIENT )
		public IIcon side;
		
		@SideOnly( Side.CLIENT )
		public IIcon eaten;
		public String key;
		public String title;
		public ItemStack activator;
		
		public CakeIngredientMap(String _key, String _title, ItemStack _activator)
		{
			key = _key;
			title = _title;
			activator = _activator;
		}
		
		public void setIcons( IIcon textureTop, IIcon textureBottom, IIcon textureSide, IIcon textureEaten )
		{
			top = textureTop;
			bottom = textureBottom;
			side = textureSide;
			eaten = textureEaten;
		}
		
		public IIcon getIcon( int dir, boolean isFull )
		{
			if ( dir == 0 )
			{
				return bottom;
			}
			if ( dir == 1 )
			{
				return top;
			}
			if ( ( !isFull ) && ( dir == 4 ) )
			{
				return eaten;
			}
			
			return side;
		}
	}
}