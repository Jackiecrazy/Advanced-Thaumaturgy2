package net.ixios.advancedthaumaturgy.blocks;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.items.TCItems;
import net.ixios.advancedthaumaturgy.misc.ATResearchItem;
import net.ixios.advancedthaumaturgy.tileentities.TileEtherealJar;
import net.ixios.advancedthaumaturgy.tileentities.TileNodeModifier;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.blocks.BlockJar;
import thaumcraft.common.blocks.ItemJarFilled;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigResearch;
import thaumcraft.common.items.ItemEssence;
import thaumcraft.common.items.ItemResource;


public class BlockEtherealJar extends BlockJar
{
    public BlockEtherealJar()
    {
        super();
        this.setCreativeTab(null);
        this.setBlockName("at.etherealjar");
    }

	@Override
	public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		par3List.add(new ItemStack(par1, 1, 0));
	}

	@Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        return new TileEtherealJar();
    }
    
	@Override
	public int getRenderType()
	{
		return -1;
	}
	
    public void register()
    {
    	GameRegistry.registerBlock(this, getUnlocalizedName());
    	GameRegistry.registerTileEntity(TileEtherealJar.class, "tileEtherealJar");
    	GameRegistry.registerItem(AdvThaum.itemEtherealJar, AdvThaum.itemEtherealJar.getUnlocalizedName());
    	
    	// do research
    	ItemStack crystal = new ItemStack(ConfigBlocks.blockCrystal, 1, 32767);
    	ItemStack silverwood = new ItemStack(ConfigBlocks.blockMagicalLog, 1 , 1);
    	ItemStack quicksilver = TCItems.quicksilver;
    	
   	 	ShapedArcaneRecipe recipe = ThaumcraftApi.addArcaneCraftingRecipe("ETHEREALJAR", new ItemStack(AdvThaum.itemEtherealJar, 1, 0),
             (new AspectList()).add(Aspect.AIR, 25).add(Aspect.ENTROPY, 25),
             new Object[] { "SSS",
   	 		  	            "JQJ",
   	 			 		    "JCJ", 'C', crystal, 'J', new ItemStack(ConfigBlocks.blockJar, 1, 0), 'S', silverwood,
   	 			 		    		'Q', quicksilver });
     
	     ConfigResearch.recipes.put("EtherealJar", recipe);
	 
	     // add research
	      ATResearchItem ri = new ATResearchItem("ETHEREALJAR", "ALCHEMY",
	    		  (new AspectList()).add(Aspect.AIR, 4).add(Aspect.ENTROPY, 4).add(Aspect.VOID, 8).add(Aspect.MAGIC, 8),
	             7, -7, 3,
	             new ItemStack(AdvThaum.itemEtherealJar, 1, 0));
	     ri.setTitle("at.research.etherealjar.title");
	     ri.setInfo("at.research.etherealjar.desc");
	     ri.setParents("JARVOID");
	     ri.setPages(new ResearchPage("at.research.etherealjar.pg1"), new ResearchPage("at.research.etherealjar.pg2"),
	             new ResearchPage(recipe));
	     
	     ri.setConcealed();
	     ri.setSecondary();
	     
	     ri.registerResearchItem();
	     
    }
    
    @Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
	        float hitY, float hitZ)
	{
    	TileEntity te = world.getTileEntity(x, y, z);
        ItemStack helditem = player.getHeldItem();
        
        if (!(te instanceof TileEtherealJar))
    		return true;

    	TileEtherealJar jar = (TileEtherealJar)te;;
    		
    	boolean noitem = (helditem == null);
    	boolean isLabel = !noitem && helditem.getItem() == ConfigItems.itemResource&& helditem.getItemDamage() == 13;
    	boolean isPhial = !noitem && helditem.getItem() instanceof IEssentiaContainerItem; //itemID == ConfigItems.itemResource.itemID && helditem.getItemDamage() == 13;

    	if (noitem)
    		handleEmptyHandClick(jar, player, side);
    	else if (isLabel)
    		handleLabelClick(jar, player, helditem);
    	else if (isPhial)
    		handlePhial(jar, player, helditem);
    	
    	world.markBlockForUpdate(x, y, z);
    	return true;
	}
   
    private void handleEmptyHandClick(TileEtherealJar jar, EntityPlayer player, int side)
    {
    	if (player.isSneaking() && jar.aspectFilter != null && side == jar.facing)
    	{ // shift right clicking a label, remove it
    		jar.aspectFilter = null;
          	if (jar.getWorldObj().isRemote)
          	{
          		jar.getWorldObj().playSound(jar.xCoord + 0.5F, jar.yCoord + 0.5F, jar.zCoord + 0.5F, "thaumcraft:page", 1.0F, 1.0F, false);
          	}
          	else
          	{
          		ForgeDirection fd = ForgeDirection.getOrientation(side);
          		jar.getWorldObj().spawnEntityInWorld(new EntityItem(jar.getWorldObj(), jar.xCoord + 0.5F + fd.offsetX/ 3.0F, jar.yCoord + 0.5F, jar.zCoord + 0.5F + fd.offsetZ / 3.0F, new ItemStack(ConfigItems.itemResource, 1, 13)));
          	}
          
    	}
    	else if (player.isSneaking())
    	{// shift right clickign a jar, empty it
    		jar.amount = 0;
    		jar.aspect = null;
    		if (jar.getWorldObj().isRemote)
    		{
    			jar.getWorldObj().playSound(jar.xCoord + 0.5F, jar.yCoord + 0.5F, jar.zCoord + 0.5F, "thaumcraft:jar", 0.4F, 1.0F, false);
    			jar.getWorldObj().playSound(jar.xCoord + 0.5F, jar.yCoord + 0.5F, jar.zCoord + 0.5F, "game.neutral.swim", 0.5F, 1.0F + (jar.getWorldObj().rand.nextFloat() - jar.getWorldObj().rand.nextFloat()) * 0.3F, false);
    		}
    	}
    }
        
    private void handleLabelClick(TileEtherealJar jar, EntityPlayer player, ItemStack stack)
    {
    	World world = jar.getWorldObj();
    	ItemResource label = (ItemResource)stack.getItem();
    	
    	if (jar.amount == 0 && label.getAspects(stack) != null && jar.aspectFilter == null)
    	{ // empty jar, marked label
         	jar.aspectFilter = label.getAspects(stack).getAspects()[0];
              
            --stack.stackSize;
            this.onBlockPlacedBy(world, jar.xCoord, jar.yCoord, jar.zCoord, player, (ItemStack)null);
              
            if (world.isRemote)
            	world.playSound(jar.xCoord + 0.5F, jar.yCoord + 0.5F, jar.zCoord + 0.5F, "thaumcraft:page", 0.4F, 1.0F, false);
    	}
        else if (jar.aspectFilter == null && jar.amount != 0 && jar.aspect != null) 
        {  // applying a blank label
        	jar.aspectFilter = jar.aspect;
             
            --stack.stackSize;
            this.onBlockPlacedBy(world, jar.xCoord, jar.yCoord, jar.zCoord, player, (ItemStack)null);
             
            if (world.isRemote)
            	world.playSound(jar.xCoord + 0.5F, jar.yCoord + 0.5F, jar.zCoord + 0.5F, "thaumcraft:jar", 0.4F, 1.0F, false);
          }
    }
    
    private void handlePhial(TileEtherealJar jar, EntityPlayer player, ItemStack stack)
    {
    	IEssentiaContainerItem container = (IEssentiaContainerItem)stack.getItem();
    	AspectList aspects = container.getAspects(stack);
    	World world = jar.getWorldObj();
    	Aspect aspect = aspects == null ? null : container.getAspects(stack).getAspects()[0];
    	
        if (jar.amount >= 8 && jar.aspect != null && container.getAspects(stack) == null && aspects == null)
       	{ // empty phial, semi full jar
        	stack.stackSize--;
       		ItemStack phialstack = new ItemStack(ConfigItems.itemEssence, 1, 1);
       		((ItemEssence)phialstack.getItem()).setAspects(phialstack, new AspectList().add(jar.aspect, 8));
       		
       		jar.takeFromContainer(jar.aspect, 8);
       		world.markBlockForUpdate(jar.xCoord, jar.yCoord, jar.zCoord);
       		
       		if (!player.inventory.addItemStackToInventory(phialstack))
       			world.spawnEntityInWorld(new EntityItem(world, jar.xCoord + 0.5f, jar.yCoord + 0.5f, jar.zCoord + 0.5f, phialstack));
       		
       		if (world.isRemote)
       			world.playSound(jar.xCoord + 0.5F, jar.yCoord + 0.5F, jar.zCoord + 0.5F, "game.neutral.swim", 0.2F, 1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3F, false);

       	}
       	else if (aspects != null && ((jar.amount <= jar.maxAmount - 8 && jar.aspect != null && jar.aspect == aspect) || (jar.amount == 0 && jar.aspect == null)))
       	{ // empty jar, full phial
       		
       		if (jar.aspectFilter != null && jar.aspectFilter != aspect)
       			return;
       		
       		stack.stackSize--;
       		ItemStack emptyphial = new ItemStack(ConfigItems.itemEssence, 1, 0);
       		
       		jar.addToContainer(aspect, 8);
       		jar.aspect = aspect;
       		
       		if (!player.inventory.addItemStackToInventory(emptyphial))
       			world.spawnEntityInWorld(new EntityItem(world, jar.xCoord + 0.5f, jar.yCoord + 0.5f, jar.zCoord + 0.5f, emptyphial));
       		
       		if (world.isRemote)
       			world.playSound(jar.xCoord + 0.5F, jar.yCoord + 0.5F, jar.zCoord + 0.5F, "game.neutral.swim", 0.2F, 1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3F, false);

       	}
   	    
    }

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		return new ArrayList<ItemStack>();
	}


	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int side) {
		TileEntity te = world.getTileEntity(x, y, z);
		TileEtherealJar ej = (TileEtherealJar)te;

		ItemStack drop = new ItemStack(AdvThaum.itemEtherealJar);

		if (ej.amount > 0)
			((ItemJarFilled)drop.getItem()).setAspects(drop, (new AspectList()).add(ej.aspect, ej.amount));
		if (ej.aspectFilter != null)
		{
			if (!drop.hasTagCompound())
				drop.setTagCompound(new NBTTagCompound());
			drop.stackTagCompound.setString("AspectFilter", ej.aspectFilter.getTag());
		}

		//dropBlockAsItem_do(world, x, y, z, drop);
		dropBlockAsItem(world,x,y,z,drop);
		world.removeTileEntity(x, y, z);
	}

   
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack)
    {
    	TileEntity te = world.getTileEntity(x, y, z);
    	
    	TileEtherealJar ej = (TileEtherealJar)te; 
    	
    	int facing = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		
		if (facing == 0)
			ej.facing = 2;
		else if (facing == 1)
            ej.facing = 5;
		else if (facing == 2)
            ej.facing = 3;
		else if (facing == 3)
            ej.facing = 4;
		    	
    }
}
