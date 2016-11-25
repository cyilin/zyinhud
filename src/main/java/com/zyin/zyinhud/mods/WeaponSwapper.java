package com.zyin.zyinhud.mods;

import com.google.common.collect.Multimap;
import com.zyin.zyinhud.ZyinHUDRenderer;
import com.zyin.zyinhud.util.InventoryUtil;
import com.zyin.zyinhud.util.Localization;
import com.zyin.zyinhud.util.ModCompatibility;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.NonNullList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Weapon Swap allows the player to quickly equip their sword and bow.
 */
public class WeaponSwapper extends ZyinHUDModBase
{
    /**
     * Enables/Disables this Mod
     */
    public static boolean Enabled;

    /**
     * Toggles this Mod on or off
     *
     * @return The state the Mod was changed to
     */
    public static boolean ToggleEnabled()
    {
    	return Enabled = !Enabled;
    }
    
    //private static List<Class> meleeWeaponClasses = null;
    private static List<Class> rangedWeaponClasses = null;


    /**
     * Makes the player select their sword. If a sword is already selected, it selects the bow instead.
     */
    public static void SwapWeapons()
    {
        ItemStack currentItemStack = mc.player.getHeldItemMainhand(); //getHeldItem();
        Item currentItem = null;

        if (!currentItemStack.isEmpty())
        {
            currentItem = currentItemStack.getItem();
        }

        InitializeListOfWeaponClasses();
        

        int meleeWeaponSlot = GetMostDamagingWeaponSlotFromHotbar();
        int rangedWeaponSlot = GetBowSlotFromHotbar(rangedWeaponClasses);

        if (meleeWeaponSlot < 0 && rangedWeaponSlot < 0)
        {
            //we dont have a sword or a bow on the hotbar, so check our inventory

            meleeWeaponSlot = GetMostDamagingWeaponSlotFromInventory();
            if(meleeWeaponSlot < 0)
            {
                rangedWeaponSlot = GetItemSlotFromInventory(rangedWeaponClasses);
                if(rangedWeaponSlot < 0)
                	ZyinHUDRenderer.DisplayNotification(Localization.get("weaponswapper.noweapons"));
                else
                {
                	InventoryUtil.Swap(InventoryUtil.GetCurrentlySelectedItemInventoryIndex(), rangedWeaponSlot);
                }
            }
            else
            {
            	InventoryUtil.Swap(InventoryUtil.GetCurrentlySelectedItemInventoryIndex(), meleeWeaponSlot);
            }
        }
        else if (meleeWeaponSlot >= 0 && rangedWeaponSlot < 0)
        {
            //we have a sword, but no bow
            SelectHotbarSlot(meleeWeaponSlot);
        }
        else if (meleeWeaponSlot < 0 && rangedWeaponSlot >= 0)
        {
            //we have a bow, but no sword
            SelectHotbarSlot(rangedWeaponSlot);
        }
        else
        {
        	//we have both a bow and a sword
        	if(mc.player.inventory.currentItem == meleeWeaponSlot)
        	{
        		//we are selected on the best melee weapon, so select the ranged weapon
        		SelectHotbarSlot(rangedWeaponSlot);
        	}
        	else
        	{
                //we are not selecting the best melee weapon, so select the melee weapon
                SelectHotbarSlot(meleeWeaponSlot);
        	}
        }
    }

    /**
     * Gets the inventory index of the most damaging melee weapon on the hotbar.
     *
     * @param minInventoryIndex the min inventory index
     * @param maxInventoryIndex the max inventory index
     * @return 0 -9
     */
    protected static int GetMostDamagingWeaponSlot(int minInventoryIndex, int maxInventoryIndex)
    {
        NonNullList<ItemStack> items = mc.player.inventory.mainInventory;
        double highestWeaponDamage = -1;
        double highestAttackSpeed = -1;
        int highestWeaponDamageSlot = -1;
        double highestSwordDamage = -1;
        double highestSwordAttackSpeed = -1;
        int highestSwordDamageSlot = -1;
        
        for (int i = minInventoryIndex; i <= maxInventoryIndex; i++)
        {
            ItemStack itemStack = items.get(i);

            if (!itemStack.isEmpty())
            {
                if (itemStack.getItem() instanceof ItemSword) {
                    double swordDamage = GetItemWeaponDamage(itemStack);
                    double swordAttackSpeed = GetAttackSpeed(itemStack);
                    if ((swordDamage > highestSwordDamage && swordAttackSpeed >= highestSwordAttackSpeed) ||
                            (swordDamage >= highestSwordDamage && swordAttackSpeed > highestSwordAttackSpeed)) {
                        highestSwordDamage = swordDamage;
                        highestSwordAttackSpeed = swordAttackSpeed;
                        highestSwordDamageSlot = i;
                    }
                    continue;
                }
                double weaponDamage = GetItemWeaponDamage(itemStack);
                double weaponAttackSpeed = GetAttackSpeed(itemStack);
                if ((weaponDamage > highestWeaponDamage && weaponAttackSpeed >= highestAttackSpeed) ||
                        (weaponDamage >= highestWeaponDamage && weaponAttackSpeed > highestAttackSpeed)) {
                    highestWeaponDamage = weaponDamage;
                    highestAttackSpeed = weaponAttackSpeed;
                    highestWeaponDamageSlot = i;
                }
            }
        }
        if (highestSwordDamageSlot == -1) {
            return highestWeaponDamageSlot;
        }else if ((highestAttackSpeed > highestSwordDamage && highestAttackSpeed >= highestSwordAttackSpeed) ||
                (highestWeaponDamage >= highestSwordDamage && highestAttackSpeed > highestSwordAttackSpeed)) {
            return highestWeaponDamageSlot;
        } else {
            return highestSwordDamageSlot;
        }
    }

    /**
     * Gets the hotbar index of the most damaging melee weapon on the hotbar.
     *
     * @return 0 -8, -1 if none found
     */
    public static int GetMostDamagingWeaponSlotFromHotbar()
    {
        return GetMostDamagingWeaponSlot(0, 8);
    }

    /**
     * Gets the inventory index of the most damaging melee weapon in the inventory.
     *
     * @return 9 -35, -1 if none found
     */
    public static int GetMostDamagingWeaponSlotFromInventory()
    {
        return GetMostDamagingWeaponSlot(9, 35);
    }

    /**
     * Gets the amount of melee damage delt by the specified item
     *
     * @param itemStack the item stack
     * @return -1 if it doesn't have a damage modifier
     */
    public static double GetItemWeaponDamage(ItemStack itemStack)
    {
        EntityEquipmentSlot EquipmentSlot = EntityEquipmentSlot.MAINHAND;
        Multimap multimap = itemStack.getItem().getAttributeModifiers(EquipmentSlot, itemStack);
        double enchantDamage=GetEnchantDamage(itemStack);
        
        if (multimap.containsKey(SharedMonsterAttributes.ATTACK_DAMAGE.getName())) {
            Collection attributes = multimap.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
            if (attributes.size() > 0) {
				Object attribute = attributes.iterator().next();
				if (attribute instanceof AttributeModifier)
				{
					AttributeModifier weaponModifier = (AttributeModifier)attribute;
                    return weaponModifier.getAmount() + enchantDamage;
                }
			}
		}else {
            if(enchantDamage>0.0D){
                return enchantDamage;
            }
		}
		return -1;
    }
    
	private static void InitializeListOfWeaponClasses()
	{
        if(rangedWeaponClasses == null)
        {
        	rangedWeaponClasses = new ArrayList<Class>();
        	rangedWeaponClasses.add(ItemBow.class);
        	
            if(ModCompatibility.TConstruct.isLoaded)
            {
    			try
    			{
    	        	rangedWeaponClasses.add(Class.forName(ModCompatibility.TConstruct.tConstructBowClass));
    			}
    			catch (ClassNotFoundException e)
    			{
    				e.printStackTrace();
    			}
            }
        }
	}
    
    /**
     * Determines if an item is a melee weapon.
     * @param item
     * @return
     */
    private static boolean IsRangedWeapon(Item item)
    {
    	if(rangedWeaponClasses == null)
    		return false;

        for (Class rangedWeaponClass : rangedWeaponClasses) {
            if (rangedWeaponClass.isInstance(item)) {
                return true;
            }
        }
		return false;
	}

    /**
     * Makes the player select a slot on their hotbar
     *
     * @param slot 0 through 8
     */
    protected static void SelectHotbarSlot(int slot)
    {
        if (slot < 0 || slot > 8)
        {
            return;
        }

        mc.player.inventory.currentItem = slot;
    }


    /**
     * Gets the index of an item that exists in the player's hotbar.
     *
     * @param itemClasses       the type of item to find (i.e. ItemSword.class, ItemBow.class)
     * @param minInventoryIndex the min inventory index
     * @param maxInventoryIndex the max inventory index
     * @return 0 through 8, inclusive. -1 if not found.
     */
    protected static int GetItemSlot(List<Class> itemClasses, int minInventoryIndex, int maxInventoryIndex)
    {
        NonNullList<ItemStack> items = mc.player.inventory.mainInventory;

        for (int i = minInventoryIndex; i <= maxInventoryIndex; i++)
        {
            ItemStack itemStack = items.get(i);

            if (!itemStack.isEmpty())
            {
                Item item = itemStack.getItem();

                for (Class itemClass : itemClasses) {
                    if (itemClass.isInstance(item)) {
                        return i;
                    }
                }
            }
        }
            
        return -1;
    }

    /**
     * Gets the index of an item that exists in the player's hotbar.
     *
     * @param itemClasses the type of item to find (i.e. ItemSword.class, ItemBow.class)
     * @return 0 through 8, inclusive. -1 if not found.
     */
    public static int GetItemSlotFromHotbar(List<Class> itemClasses)
    {
    	return GetItemSlot(itemClasses, 0, 8);
    }

    /**
     * Gets the index of an item that exists in the player's hotbar.
     *
     * @param itemClasses the type of item to find (i.e. ItemSword.class, ItemBow.class)
     * @return 9 through 35, inclusive. -1 if not found.
     */
    public static int GetItemSlotFromInventory(List<Class> itemClasses)
    {
    	return GetItemSlot(itemClasses, 9, 35);
    }

    public static double GetEnchantDamage(ItemStack item) {
        double damage = 0.0D;
        if (item.isItemEnchanted()) {
            damage = EnchantmentHelper.getModifierForCreature(item, EnumCreatureAttribute.UNDEFINED);
        }
        return damage;
    }

    public static double GetAttackSpeed(ItemStack item) {
        EntityEquipmentSlot EquipmentSlot = EntityEquipmentSlot.MAINHAND;
        Multimap multimap = item.getItem().getAttributeModifiers(EquipmentSlot, item);

        if (multimap.containsKey(SharedMonsterAttributes.ATTACK_SPEED.getName())) {
            Collection attributes = multimap.get(SharedMonsterAttributes.ATTACK_SPEED.getName());
            if (attributes.size() > 0) {
                Object attribute = attributes.iterator().next();
                if (attribute instanceof AttributeModifier) {
                    return (4.0D)+(((AttributeModifier) attribute).getAmount());
                }
            }
        }
        return -1;
    }
    
    public static double GetBowDamage(ItemStack item) {
        double damage = 0.0D;
        if (item.isItemEnchanted()) {
            int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, item);
            if (power > 0) {
                damage = 2.0D + (double) power * 0.5D + 0.5D;
            }
        }
        return damage;
    }

    public static int GetBowSlotFromHotbar(List<Class> itemClasses) {
        NonNullList<ItemStack> items = mc.player.inventory.mainInventory;
        double highestDamage = -1;
        int slot = -1;
        for (int i = 0; i <= 8; i++) {
            ItemStack itemStack = items.get(i);

            if (!itemStack.isEmpty()) {
                Item item = itemStack.getItem();

                for (Class itemClass : itemClasses) {
                    if (itemClass.isInstance(item)) {
                        if (GetBowDamage(itemStack) > highestDamage) {
                            highestDamage = GetBowDamage(itemStack);
                            slot = i;
                        }
                    }
                }
            }
        }
        return slot;
    }
}
