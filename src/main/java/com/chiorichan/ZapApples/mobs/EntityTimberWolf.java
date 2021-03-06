package com.chiorichan.ZapApples.mobs;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityTimberWolf extends EntityTameable
{
	private float field_70926_e;
	private float field_70924_f;
	
	private boolean isShaking;
	private boolean field_70928_h;
	
	private float timeWolfIsShaking;
	private float prevTimeWolfIsShaking;
	
	public EntityTimberWolf(World p_i1696_1_)
	{
		super( p_i1696_1_ );
		setSize( 0.6F, 0.8F );
		getNavigator().setAvoidsWater( true );
		tasks.addTask( 1, new EntityAISwimming( this ) );
		tasks.addTask( 2, aiSit );
		tasks.addTask( 3, new EntityAILeapAtTarget( this, 0.4F ) );
		tasks.addTask( 4, new EntityAIAttackOnCollide( this, 1.0D, true ) );
		tasks.addTask( 5, new EntityAIFollowOwner( this, 1.0D, 10.0F, 2.0F ) );
		tasks.addTask( 6, new EntityAIMate( this, 1.0D ) );
		tasks.addTask( 7, new EntityAIWander( this, 1.0D ) );
		// tasks.addTask( 8, new EntityAIBeg( this, 8.0F ) );
		tasks.addTask( 9, new EntityAIWatchClosest( this, EntityPlayer.class, 8.0F ) );
		tasks.addTask( 9, new EntityAILookIdle( this ) );
		targetTasks.addTask( 1, new EntityAIOwnerHurtByTarget( this ) );
		targetTasks.addTask( 2, new EntityAIOwnerHurtTarget( this ) );
		targetTasks.addTask( 3, new EntityAIHurtByTarget( this, true ) );
		targetTasks.addTask( 4, new EntityAITargetNonTamed( this, EntitySheep.class, 200, false ) );
		setTamed( false );
	}
	
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		getEntityAttribute( SharedMonsterAttributes.movementSpeed ).setBaseValue( 0.30000001192092896D );
		getEntityAttribute( SharedMonsterAttributes.maxHealth ).setBaseValue( 50.0D );
	}
	
	/**
	 * Returns true if the newer Entity AI code should be run
	 */
	public boolean isAIEnabled()
	{
		return true;
	}
	
	/**
	 * Sets the active target the Task system uses for tracking
	 */
	public void setAttackTarget( EntityLivingBase p_70624_1_ )
	{
		super.setAttackTarget( p_70624_1_ );
		
		if ( p_70624_1_ == null )
		{
			setAngry( false );
		}
		else if ( !isTamed() )
		{
			setAngry( true );
		}
	}
	
	/**
	 * main AI tick function, replaces updateEntityActionState
	 */
	protected void updateAITick()
	{
		dataWatcher.updateObject( 18, Float.valueOf( getHealth() ) );
	}
	
	protected void entityInit()
	{
		super.entityInit();
		dataWatcher.addObject( 18, new Float( getHealth() ) );
		dataWatcher.addObject( 19, new Byte( (byte) 0 ) );
		dataWatcher.addObject( 20, new Byte( (byte) BlockColored.func_150032_b( 1 ) ) );
	}
	
	protected void func_145780_a( int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_ )
	{
		playSound( "mob.wolf.step", 0.15F, 1.0F );
	}
	
	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT( NBTTagCompound p_70014_1_ )
	{
		super.writeEntityToNBT( p_70014_1_ );
		p_70014_1_.setBoolean( "Angry", isAngry() );
		p_70014_1_.setByte( "CollarColor", (byte) getCollarColor() );
	}
	
	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT( NBTTagCompound par1NBTTagCompound )
	{
		super.readEntityFromNBT( par1NBTTagCompound );
		setAngry( par1NBTTagCompound.getBoolean( "Angry" ) );
		
		if ( par1NBTTagCompound.hasKey( "CollarColor" ) )
		{
			setCollarColor( par1NBTTagCompound.getByte( "CollarColor" ) );
		}
	}
	
	/**
	 * Returns the sound this mob makes while it's alive.
	 */
	protected String getLivingSound()
	{
		return isAngry() ? "mob.wolf.growl" : ( rand.nextInt( 3 ) == 0 ? ( isTamed() && dataWatcher.getWatchableObjectFloat( 18 ) < 10.0F ? "mob.wolf.whine" : "mob.wolf.panting" ) : "mob.wolf.bark" );
	}
	
	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	protected String getHurtSound()
	{
		return "mob.wolf.hurt";
	}
	
	/**
	 * Returns the sound this mob makes on death.
	 */
	protected String getDeathSound()
	{
		return "mob.wolf.death";
	}
	
	/**
	 * Returns the volume for the sounds this mob makes.
	 */
	protected float getSoundVolume()
	{
		return 0.4F;
	}
	
	protected Item func_146068_u()
	{
		return Item.getItemById( -1 );
	}
	
	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		
		if ( worldObj.isRemote && isShaking && !field_70928_h && !hasPath() && onGround )
		{
			field_70928_h = true;
			timeWolfIsShaking = 0.0F;
			prevTimeWolfIsShaking = 0.0F;
			worldObj.setEntityState( this, (byte) 8 );
		}
	}
	
	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate()
	{
		super.onUpdate();
		field_70924_f = field_70926_e;
		
		if ( func_70922_bv() )
		{
			field_70926_e += ( 1.0F - field_70926_e ) * 0.4F;
		}
		else
		{
			field_70926_e += ( 0.0F - field_70926_e ) * 0.4F;
		}
		
		if ( func_70922_bv() )
		{
			numTicksToChaseTarget = 10;
		}
		
		if ( isWet() )
		{
			isShaking = true;
			field_70928_h = false;
			timeWolfIsShaking = 0.0F;
			prevTimeWolfIsShaking = 0.0F;
		}
		else if ( ( isShaking || field_70928_h ) && field_70928_h )
		{
			if ( timeWolfIsShaking == 0.0F )
			{
				playSound( "mob.wolf.shake", getSoundVolume(), ( rand.nextFloat() - rand.nextFloat() ) * 0.2F + 1.0F );
			}
			
			prevTimeWolfIsShaking = timeWolfIsShaking;
			timeWolfIsShaking += 0.05F;
			
			if ( prevTimeWolfIsShaking >= 2.0F )
			{
				isShaking = false;
				field_70928_h = false;
				prevTimeWolfIsShaking = 0.0F;
				timeWolfIsShaking = 0.0F;
			}
			
			if ( timeWolfIsShaking > 0.4F )
			{
				float var1 = (float) boundingBox.minY;
				int var2 = (int) ( MathHelper.sin( ( timeWolfIsShaking - 0.4F ) * (float) Math.PI ) * 7.0F );
				
				for ( int var3 = 0; var3 < var2; ++var3 )
				{
					float var4 = ( rand.nextFloat() * 2.0F - 1.0F ) * width * 0.5F;
					float var5 = ( rand.nextFloat() * 2.0F - 1.0F ) * width * 0.5F;
					worldObj.spawnParticle( "splash", posX + (double) var4, (double) ( var1 + 0.8F ), posZ + (double) var5, motionX, motionY, motionZ );
				}
			}
		}
	}
	
	public boolean getWolfShaking()
	{
		return isShaking;
	}
	
	/**
	 * Used when calculating the amount of shading to apply while the wolf is shaking.
	 */
	public float getShadingWhileShaking( float p_70915_1_ )
	{
		return 0.75F + ( prevTimeWolfIsShaking + ( timeWolfIsShaking - prevTimeWolfIsShaking ) * p_70915_1_ ) / 2.0F * 0.25F;
	}
	
	public float getShakeAngle( float p_70923_1_, float p_70923_2_ )
	{
		float var3 = ( prevTimeWolfIsShaking + ( timeWolfIsShaking - prevTimeWolfIsShaking ) * p_70923_1_ + p_70923_2_ ) / 1.8F;
		
		if ( var3 < 0.0F )
		{
			var3 = 0.0F;
		}
		else if ( var3 > 1.0F )
		{
			var3 = 1.0F;
		}
		
		return MathHelper.sin( var3 * (float) Math.PI ) * MathHelper.sin( var3 * (float) Math.PI * 11.0F ) * 0.15F * (float) Math.PI;
	}
	
	public float getInterestedAngle( float p_70917_1_ )
	{
		return ( field_70924_f + ( field_70926_e - field_70924_f ) * p_70917_1_ ) * 0.15F * (float) Math.PI;
	}
	
	public float getEyeHeight()
	{
		return height * 0.8F;
	}
	
	/**
	 * The speed it takes to move the entityliving's rotationPitch through the faceEntity method. This is only currently
	 * use in wolves.
	 */
	public int getVerticalFaceSpeed()
	{
		return isSitting() ? 20 : super.getVerticalFaceSpeed();
	}
	
	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom( DamageSource p_70097_1_, float p_70097_2_ )
	{
		if ( isEntityInvulnerable() )
		{
			return false;
		}
		else
		{
			Entity var3 = p_70097_1_.getEntity();
			aiSit.setSitting( false );
			
			if ( var3 != null && !( var3 instanceof EntityPlayer ) && !( var3 instanceof EntityArrow ) )
			{
				p_70097_2_ = ( p_70097_2_ + 1.0F ) / 2.0F;
			}
			
			return super.attackEntityFrom( p_70097_1_, p_70097_2_ );
		}
	}
	
	public boolean attackEntityAsMob( Entity p_70652_1_ )
	{
		int var2 = isTamed() ? 4 : 2;
		return p_70652_1_.attackEntityFrom( DamageSource.causeMobDamage( this ), (float) var2 );
	}
	
	public void setTamed( boolean p_70903_1_ )
	{
		super.setTamed( p_70903_1_ );
		
		if ( p_70903_1_ )
		{
			getEntityAttribute( SharedMonsterAttributes.maxHealth ).setBaseValue( 20.0D );
		}
		else
		{
			getEntityAttribute( SharedMonsterAttributes.maxHealth ).setBaseValue( 8.0D );
		}
	}
	
	/**
	 * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
	 */
	public boolean interact( EntityPlayer p_70085_1_ )
	{
		ItemStack var2 = p_70085_1_.inventory.getCurrentItem();
		
		if ( isTamed() )
		{
			if ( var2 != null )
			{
				if ( var2.getItem() instanceof ItemFood )
				{
					ItemFood var3 = (ItemFood) var2.getItem();
					
					if ( var3.isWolfsFavoriteMeat() && dataWatcher.getWatchableObjectFloat( 18 ) < 20.0F )
					{
						if ( !p_70085_1_.capabilities.isCreativeMode )
						{
							--var2.stackSize;
						}
						
						heal( (float) var3.func_150905_g( var2 ) );
						
						if ( var2.stackSize <= 0 )
						{
							p_70085_1_.inventory.setInventorySlotContents( p_70085_1_.inventory.currentItem, (ItemStack) null );
						}
						
						return true;
					}
				}
				else if ( var2.getItem() == Items.dye )
				{
					int var4 = BlockColored.func_150032_b( var2.getItemDamage() );
					
					if ( var4 != getCollarColor() )
					{
						setCollarColor( var4 );
						
						if ( !p_70085_1_.capabilities.isCreativeMode && --var2.stackSize <= 0 )
						{
							p_70085_1_.inventory.setInventorySlotContents( p_70085_1_.inventory.currentItem, (ItemStack) null );
						}
						
						return true;
					}
				}
			}
			
			if ( func_152114_e( p_70085_1_ ) && worldObj.isRemote && !isBreedingItem( var2 ) )
			{
				aiSit.setSitting( !isSitting() );
				isJumping = false;
				setPathToEntity( (PathEntity) null );
				setTarget( (Entity) null );
				setAttackTarget( (EntityLivingBase) null );
			}
		}
		else if ( var2 != null && var2.getItem() == Items.bone && !isAngry() )
		{
			if ( !p_70085_1_.capabilities.isCreativeMode )
			{
				--var2.stackSize;
			}
			
			if ( var2.stackSize <= 0 )
			{
				p_70085_1_.inventory.setInventorySlotContents( p_70085_1_.inventory.currentItem, (ItemStack) null );
			}
			
			if ( worldObj.isRemote )
			{
				if ( rand.nextInt( 3 ) == 0 )
				{
					setTamed( true );
					setPathToEntity( (PathEntity) null );
					setAttackTarget( (EntityLivingBase) null );
					aiSit.setSitting( true );
					setHealth( 20.0F );
					func_152115_b( p_70085_1_.getUniqueID().toString() );
					playTameEffect( true );
					worldObj.setEntityState( this, (byte) 7 );
				}
				else
				{
					playTameEffect( false );
					worldObj.setEntityState( this, (byte) 6 );
				}
			}
			
			return true;
		}
		
		return super.interact( p_70085_1_ );
	}
	
	public void handleHealthUpdate( byte p_70103_1_ )
	{
		if ( p_70103_1_ == 8 )
		{
			field_70928_h = true;
			timeWolfIsShaking = 0.0F;
			prevTimeWolfIsShaking = 0.0F;
		}
		else
		{
			super.handleHealthUpdate( p_70103_1_ );
		}
	}
	
	public float getTailRotation()
	{
		return isAngry() ? 1.5393804F : ( isTamed() ? ( 0.55F - ( 20.0F - dataWatcher.getWatchableObjectFloat( 18 ) ) * 0.02F ) * (float) Math.PI : ( (float) Math.PI / 5F ) );
	}
	
	/**
	 * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
	 * the animal type)
	 */
	public boolean isBreedingItem( ItemStack p_70877_1_ )
	{
		return p_70877_1_ == null ? false : ( !( p_70877_1_.getItem() instanceof ItemFood ) ? false : ( (ItemFood) p_70877_1_.getItem() ).isWolfsFavoriteMeat() );
	}
	
	/**
	 * Will return how many at most can spawn in a chunk at once.
	 */
	public int getMaxSpawnedInChunk()
	{
		return 8;
	}
	
	/**
	 * Determines whether this wolf is angry or not.
	 */
	public boolean isAngry()
	{
		return ( dataWatcher.getWatchableObjectByte( 16 ) & 2 ) != 0;
	}
	
	/**
	 * Sets whether this wolf is angry or not.
	 */
	public void setAngry( boolean p_70916_1_ )
	{
		byte var2 = dataWatcher.getWatchableObjectByte( 16 );
		
		if ( p_70916_1_ )
		{
			dataWatcher.updateObject( 16, Byte.valueOf( (byte) ( var2 | 2 ) ) );
		}
		else
		{
			dataWatcher.updateObject( 16, Byte.valueOf( (byte) ( var2 & -3 ) ) );
		}
	}
	
	/**
	 * Return this wolf's collar color.
	 */
	public int getCollarColor()
	{
		return dataWatcher.getWatchableObjectByte( 20 ) & 15;
	}
	
	/**
	 * Set this wolf's collar color.
	 */
	public void setCollarColor( int p_82185_1_ )
	{
		dataWatcher.updateObject( 20, Byte.valueOf( (byte) ( p_82185_1_ & 15 ) ) );
	}
	
	public EntityTimberWolf createChild( EntityAgeable p_90011_1_ )
	{
		EntityTimberWolf var2 = new EntityTimberWolf( worldObj );
		String var3 = func_152113_b();
		
		if ( var3 != null && var3.trim().length() > 0 )
		{
			var2.func_152115_b( var3 );
			var2.setTamed( true );
		}
		
		return var2;
	}
	
	public void func_70918_i( boolean p_70918_1_ )
	{
		if ( p_70918_1_ )
		{
			dataWatcher.updateObject( 19, Byte.valueOf( (byte) 1 ) );
		}
		else
		{
			dataWatcher.updateObject( 19, Byte.valueOf( (byte) 0 ) );
		}
	}
	
	/**
	 * Returns true if the mob is currently able to mate with the specified mob.
	 */
	public boolean canMateWith( EntityAnimal p_70878_1_ )
	{
		if ( p_70878_1_ == this )
		{
			return false;
		}
		else if ( !isTamed() )
		{
			return false;
		}
		else if ( !( p_70878_1_ instanceof EntityTimberWolf ) )
		{
			return false;
		}
		else
		{
			EntityTimberWolf var2 = (EntityTimberWolf) p_70878_1_;
			return !var2.isTamed() ? false : ( var2.isSitting() ? false : isInLove() && var2.isInLove() );
		}
	}
	
	public boolean func_70922_bv()
	{
		return dataWatcher.getWatchableObjectByte( 19 ) == 1;
	}
	
	/**
	 * Determines if an entity can be despawned, used on idle far away entities
	 */
	protected boolean canDespawn()
	{
		return !isTamed() && ticksExisted > 2400;
	}
	
	public boolean func_142018_a( EntityLivingBase p_142018_1_, EntityLivingBase p_142018_2_ )
	{
		if ( !( p_142018_1_ instanceof EntityCreeper ) && !( p_142018_1_ instanceof EntityGhast ) )
		{
			if ( p_142018_1_ instanceof EntityTimberWolf )
			{
				EntityTimberWolf var3 = (EntityTimberWolf) p_142018_1_;
				
				if ( var3.isTamed() && var3.getOwner() == p_142018_2_ )
				{
					return false;
				}
			}
			
			return p_142018_1_ instanceof EntityPlayer && p_142018_2_ instanceof EntityPlayer && !( (EntityPlayer) p_142018_2_ ).canAttackPlayer( (EntityPlayer) p_142018_1_ ) ? false : !( p_142018_1_ instanceof EntityHorse ) || !( (EntityHorse) p_142018_1_ ).isTame();
		}
		else
		{
			return false;
		}
	}
}
