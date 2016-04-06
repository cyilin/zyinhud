package com.zyin.zyinhud.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * The type Mod compatibility.
 */
public class ModCompatibility
{

	/**
	 * The type T construct.
	 */
	public static class TConstruct	//Tinker's Construct
	{
		/**
		 * The constant isLoaded.
		 */
		public static boolean isLoaded;

		/**
		 * The constant tConstructWeaponClass.
		 */
		public static final String tConstructWeaponClass = "tconstruct.library.tools.Weapon";
		/**
		 * The constant tConstructBowClass.
		 */
		public static final String tConstructBowClass = "tconstruct.items.tools.BowBase";
		/**
		 * The constant tConstructHarvestToolClass.
		 */
		public static final String tConstructHarvestToolClass = "tconstruct.library.tools.HarvestTool";
		/**
		 * The constant tConstructDualHarvestToolClass.
		 */
		public static final String tConstructDualHarvestToolClass = "tconstruct.library.tools.DualHarvestTool";

		/**
		 * Is t construct harvest tool boolean.
		 *
		 * @param item the item
		 * @return the boolean
		 */
		public static boolean IsTConstructHarvestTool(Item item) {
			if(isLoaded)
	    	{
	        	String className = item.getClass().getSuperclass().getName();
	        	return className.equals(tConstructHarvestToolClass) || className.equals(tConstructDualHarvestToolClass);
			}

			return false;
		}

		/**
		 * Is t construct weapon boolean.
		 *
		 * @param item the item
		 * @return the boolean
		 */
		public static boolean IsTConstructWeapon(Item item) {
			if (isLoaded) {
				String className = item.getClass().getSuperclass().getName();
				return className.equals(tConstructWeaponClass);
			}

			return false;
		}

		/**
		 * Is t construct bow boolean.
		 *
		 * @param item the item
		 * @return the boolean
		 */
		public static boolean IsTConstructBow(Item item) {
			if (isLoaded) {
				String className = item.getClass().getSuperclass().getName();
				return className.equals(tConstructBowClass);
			}

			return false;
		}

		/**
		 * Is t construct item boolean.
		 *
		 * @param item the item
		 * @return the boolean
		 */
		public static boolean IsTConstructItem(Item item) {
			return IsTConstructHarvestTool(item)
					|| IsTConstructWeapon(item)
					|| IsTConstructBow(item);
		}

		/**
		 * Is t construct tool without a right click action boolean.
		 *
		 * @param item the item
		 * @return the boolean
		 */
		public static boolean IsTConstructToolWithoutARightClickAction(Item item) {
			if(isLoaded)
	    	{
	        	String className = item.getClass().getSuperclass().getName();
	        	return className.equals(tConstructHarvestToolClass);
	        		//|| className.equals(tConstructDualHarvestToolClass))	//the only DualHarvestTool is the Mattock which also tills dirt on right click
			}

			return false;
		}

		/**
		 * Get damage integer.
		 *
		 * @param itemStack the item stack
		 * @return returns the damage value of the tool, 			returns the energy if it has any, 			or returns -1 if the tool is broken.
		 */
		public static Integer GetDamage(ItemStack itemStack) {
			NBTTagCompound tags = itemStack.getTagCompound();
	        if (tags == null)
	        {
	        	return null;
	        }
	        else if (tags.hasKey("Energy"))
	        {
				return tags.getInteger("Energy");
	        }
	        else {
				if (tags.getCompoundTag("InfiTool").getBoolean("Broken"))
					return -1;
				else
					return tags.getCompoundTag("InfiTool").getInteger("Damage");
			}
		}

		/**
		 * Get max damage int.
		 *
		 * @param itemStack the item stack
		 * @return returns the max durability of the tool. 			returns 400000 if it has energy.
		 */
		public static int GetMaxDamage(ItemStack itemStack) {
			NBTTagCompound tags = itemStack.getTagCompound();
	        if (tags == null)
	        {
	        	return -1;
	        }
	        else if (tags.hasKey("Energy"))
	        {
				return 400000;	//is this right??
	        }
	        else
	        {
				return tags.getCompoundTag("InfiTool").getInteger("TotalDurability");
	        }
	    }
		
	}
	
	
}
