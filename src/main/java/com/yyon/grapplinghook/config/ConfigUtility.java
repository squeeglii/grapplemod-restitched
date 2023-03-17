package com.yyon.grapplinghook.config;

import com.yyon.grapplinghook.content.registry.GrappleModBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment.Rarity;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;

public class ConfigUtility {
	private static boolean anyBlocks = true;
	private static HashSet<Block> grapplingBlocks;
	private static boolean removeBlocks = false;
	private static HashSet<Block> grapplingBreaksBlocks;
	private static boolean anyBreakBlocks = false;

	public static HashSet<Block> stringToBlocks(String s) {
		HashSet<Block> blocks = new HashSet<>();
		
		if (s.equals("") || s.equals("none") || s.equals("any")) {
			return blocks;
		}
		
		String[] blockstr = s.split(",");
		
	    for(String str: blockstr){
	    	str = str.trim();
	    	String modid;
	    	String name;
	    	if (str.contains(":")) {
	    		String[] splitstr = str.split(":");
	    		modid = splitstr[0];
	    		name = splitstr[1];
	    	} else {
	    		modid = "minecraft";
	    		name = str;
	    	}
	    	
	    	Block b = GrappleModBlocks.getBlocks().get(new ResourceLocation(modid, name)).get();
	    	
	    	blocks.add(b);
	    }
	    
	    return blocks;
	}
	
	public static void updateGrapplingBlocks() {
		String s = GrappleModConfig.getConf().grapplinghook.blocks.grapplingBlocks;
		if (s.equals("any") || s.equals("")) {
			s = GrappleModConfig.getConf().grapplinghook.blocks.grapplingNonBlocks;
			if (s.equals("none") || s.equals("")) {
				anyBlocks = true;
			} else {
				anyBlocks = false;
				removeBlocks = true;
			}
		} else {
			anyBlocks = false;
			removeBlocks = false;
		}
	
		if (!anyBlocks) {
			grapplingBlocks = stringToBlocks(s);
		}
		
		grapplingBreaksBlocks = stringToBlocks(GrappleModConfig.getConf().grapplinghook.blocks.grappleBreakBlocks);
		anyBreakBlocks = grapplingBreaksBlocks.size() != 0;
		
	}

	private static String prevGrapplingBlocks = null;
	private static String prevGrapplingNonBlocks = null;
	public static boolean attachesBlock(Block block) {
		if (!GrappleModConfig.getConf().grapplinghook.blocks.grapplingBlocks.equals(prevGrapplingBlocks) || !GrappleModConfig.getConf().grapplinghook.blocks.grapplingNonBlocks.equals(prevGrapplingNonBlocks)) {
			updateGrapplingBlocks();
		}
		
		if (anyBlocks) {
			return true;
		}
		
		boolean inlist = grapplingBlocks.contains(block);
		
		if (removeBlocks) {
			return !inlist;
		} else {
			return inlist;
		}
	}

	private static String prevGrapplingBreakBlocks = null;
	public static boolean breaksBlock(Block block) {
		if (!GrappleModConfig.getConf().grapplinghook.blocks.grappleBreakBlocks.equals(prevGrapplingBreakBlocks)) {
			updateGrapplingBlocks();
		}
		
		if (!anyBreakBlocks) {
			return false;
		}
		
		return grapplingBreaksBlocks.contains(block);
	}

	public static Rarity getRarityFromInt(int rarity_int) {
		Rarity[] rarities = (new Rarity[] {Rarity.VERY_RARE, Rarity.RARE, Rarity.UNCOMMON, Rarity.COMMON});
		if (rarity_int < 0) {rarity_int = 0;}
		if (rarity_int >= rarities.length) {rarity_int = rarities.length-1;}
		return rarities[rarity_int];
	}
}
