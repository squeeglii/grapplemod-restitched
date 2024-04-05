package com.yyon.grapplinghook.config;

import com.yyon.grapplinghook.content.registry.GrappleModBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;

public class ConfigUtility {

	private static boolean anyBlocks = true;
	private static HashSet<Block> grapplingBlocks;
	private static boolean removeBlocks = false;
	private static HashSet<Block> grapplingBreaksBlocks;

	public static HashSet<Block> stringToBlocks(String s) {
		HashSet<Block> blocks = new HashSet<>();
		
		if (s.isEmpty() || s.equals("none") || s.equals("any")) {
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
		String s = GrappleModLegacyConfig.getConf().grapplinghook.blocks.grapplingBlocks;
		if (s.equals("any") || s.isEmpty()) {
			s = GrappleModLegacyConfig.getConf().grapplinghook.blocks.grapplingNonBlocks;
			if (s.equals("none") || s.isEmpty()) {
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
	}

	private static final String prevGrapplingBlocks = null;
	private static final String prevGrapplingNonBlocks = null;
	public static boolean attachesBlock(Block block) {
		if (!GrappleModLegacyConfig.getConf().grapplinghook.blocks.grapplingBlocks.equals(prevGrapplingBlocks) || !GrappleModLegacyConfig.getConf().grapplinghook.blocks.grapplingNonBlocks.equals(prevGrapplingNonBlocks)) {
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

}
