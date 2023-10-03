package com.yyon.grapplinghook.content.entity.grapplinghook;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.network.NetworkManager;
import com.yyon.grapplinghook.network.clientbound.SegmentMessage;
import com.yyon.grapplinghook.util.GrappleModUtils;
import com.yyon.grapplinghook.util.Vec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RopeSegmentHandler {

	private static final double BEND_OFFSET = 0.05d;
	private static final double INTO_BLOCK = 0.05d;

	public GrapplinghookEntity hookEntity;
	private final Level world;

	public LinkedList<Vec> segments;
	public LinkedList<Direction> segmentBottomSides;
	public LinkedList<Direction> segmentTopSides;

	private Vec prevHookPos;
	private Vec prevPlayerPos;

	private double ropeLen;

	public RopeSegmentHandler(Level w, GrapplinghookEntity hookEntity, Vec hookpos, Vec playerpos) {
		segments = new LinkedList<>();
		segments.add(hookpos);
		segments.add(playerpos);
		segmentBottomSides = new LinkedList<>();
		segmentBottomSides.add(null);
		segmentBottomSides.add(null);
		segmentTopSides = new LinkedList<>();
		segmentTopSides.add(null);
		segmentTopSides.add(null);
		this.world = w;
		this.hookEntity = hookEntity;
		this.prevHookPos = new Vec(hookpos);
		this.prevPlayerPos = new Vec(playerpos);
	}
	
	public void forceSetPos(Vec hookpos, Vec playerpos) {
		this.prevHookPos = new Vec(hookpos);
		this.prevPlayerPos = new Vec(playerpos);
    	this.segments.set(0, new Vec(hookpos));
    	this.segments.set(this.segments.size() - 1, new Vec(playerpos));
	}

	public void updatePos(Vec hookpos, Vec playerpos, double ropelen) {
		this.segments.set(0, hookpos);
		this.segments.set(this.segments.size() - 1, playerpos);
		this.ropeLen = ropelen;
	}
	
	public void update(Vec hookpos, Vec playerpos, double ropelen, boolean movinghook) {
		if (this.prevHookPos == null) {
			this.prevHookPos = hookpos;
			this.prevPlayerPos = playerpos;
		}

		this.segments.set(0, hookpos);
		this.segments.set(this.segments.size() - 1, playerpos);
		this.ropeLen = ropelen;
		
		Vec closest = this.segments.get(this.segments.size() - 2);
		
		while (true) {
			if (this.segments.size() == 2)
				break;

			int index = this.segments.size() - 2;
			closest = this.segments.get(index);
			Direction bottomside = this.segmentBottomSides.get(index);
			Direction topside = this.segmentTopSides.get(index);
			Vec ropevec = playerpos.sub(closest);
			
			Vec beforepoint = this.segments.get(index-1);
			
			Vec edgevec = this.getNormal(bottomside).cross(this.getNormal(topside));
			Vec planenormal = beforepoint.sub(closest).cross(edgevec);
			
			if (ropevec.dot(planenormal) > 0) {
				this.removeSegment(index);

			} else break;
		}
		
		Vec farthest;
		
		if (movinghook) {
			while (true) {
				if (this.segments.size() == 2) break;
				
				int index = 1;
				farthest = this.segments.get(index);
				Direction bottomside = this.segmentBottomSides.get(index);
				Direction topside = this.segmentTopSides.get(index);
				Vec ropevec = farthest.sub(hookpos);
				
				Vec beforepoint = this.segments.get(index+1);
				
				Vec edgevec = this.getNormal(bottomside).cross(this.getNormal(topside));
				Vec planenormal = beforepoint.sub(farthest).cross(edgevec);
				
				if (ropevec.dot(planenormal) > 0 || ropevec.length() < 0.1) {
					this.removeSegment(index);

				} else break;

			}
			
			while (true) {
				if (this.getDistToFarthest() > ropelen) {
					this.removeSegment(1);

				} else break;
			}
		}
		
		if (movinghook) {
			farthest = this.segments.get(1);
			Vec prevfarthest = farthest;

			if (this.segments.size() == 2) {
				prevfarthest = this.prevPlayerPos;
			}

			this.updateSegment(hookpos, this.prevHookPos, farthest, prevfarthest, 1, 0);
		}

		Vec prevclosest = closest;

		if (this.segments.size() == 2)
			prevclosest = this.prevHookPos;


		this.updateSegment(closest, prevclosest, playerpos, this.prevPlayerPos, this.segments.size() - 1, 0);


		this.prevHookPos = hookpos;
		this.prevPlayerPos = playerpos;
	}
	
	public void removeSegment(int index) {
		segments.remove(index);
		segmentBottomSides.remove(index);
		segmentTopSides.remove(index);

		if (!this.world.isClientSide) {
			SegmentMessage addmessage = new SegmentMessage(this.hookEntity.getId(), false, index, new Vec(0, 0, 0), Direction.DOWN, Direction.DOWN);
			Vec playerpoint = Vec.positionVec(this.hookEntity.shootingEntity);

			NetworkManager.packetToClient(addmessage, GrappleModUtils.getPlayersThatCanSeeChunkAt((ServerLevel) world, playerpoint));

		}
	}
	
	public void updateSegment(Vec top, Vec prevtop, Vec bottom, Vec prevbottom, int index, int numberrecursions) {
		BlockHitResult bottomraytraceresult = GrappleModUtils.rayTraceBlocks(this.hookEntity, this.world, bottom, top);
        
        // if rope hit block
        if (bottomraytraceresult != null) {
        	if (GrappleModUtils.rayTraceBlocks(this.hookEntity, this.world, prevbottom, prevtop) != null) {
        		return;
        	}
        	
            Vec bottomhitvec = new Vec(bottomraytraceresult.getLocation());

            Direction bottomside = bottomraytraceresult.getDirection();
            Vec bottomnormal = this.getNormal(bottomside);
            
            // calculate where bottomhitvec was along the rope in the previous tick
            double prevropelen = prevtop.sub(prevbottom).length();
            
            Vec cornerbound1 = bottomhitvec.add(bottomnormal.withMagnitude(-INTO_BLOCK));
            
            Vec bound_option1 = linePlaneIntersection(prevtop, prevbottom, cornerbound1, bottomnormal);
            Vec bound_option2 = linePlaneIntersection(top, prevtop, cornerbound1, bottomnormal);
            Vec bound_option3 = linePlaneIntersection(prevbottom, bottom, cornerbound1, bottomnormal);
            
            for (Vec cornerbound2 : new Vec[] {bound_option1, bound_option2, bound_option3}) {
            	if (cornerbound2 == null) {
            		continue;
            	}
            	
            	// the corner must be in the line (cornerbound2, cornerbound1)
            	BlockHitResult cornerraytraceresult = GrappleModUtils.rayTraceBlocks(this.hookEntity, this.world, cornerbound2, cornerbound1);
                if (cornerraytraceresult != null) {
                	Vec cornerhitpos = new Vec(cornerraytraceresult.getLocation());
                	Direction cornerside = cornerraytraceresult.getDirection();
                	
                	if (!(cornerside == bottomside || cornerside.getOpposite() == bottomside)) {
                		// add a bend around the corner
                		Vec actualcorner = cornerhitpos.add(bottomnormal.withMagnitude(INTO_BLOCK));
                		Vec bend = actualcorner.add(bottomnormal.withMagnitude(BEND_OFFSET)).add(getNormal(cornerside).withMagnitude(BEND_OFFSET));
                		Vec topropevec = bend.sub(top);
                		Vec bottomropevec = bend.sub(bottom);
                		
                		// ignore bends that are too close to another bend
                		if (topropevec.length() < 0.05) {
                			if (this.segmentBottomSides.get(index - 1) == bottomside && this.segmentTopSides.get(index - 1) == cornerside) {
                    			continue;
                			}
                		}
                		if (bottomropevec.length() < 0.05) {
                			if (this.segmentBottomSides.get(index) == bottomside && this.segmentTopSides.get(index) == cornerside) {
                    			continue;
                			}
                		}
                		
                		this.actuallyAddSegment(index, bend, bottomside, cornerside);
                		
                		// if not enough rope length left, undo
                		if(this.getDistToAnchor() + .2 > this.ropeLen) {
                			this.removeSegment(index);
                			continue;
                		}
                		
                		// now to recurse on top section of rope
                		double newropelen = topropevec.length() + bottomropevec.length();
                		
                		double prevtoptobend = topropevec.length() * prevropelen / newropelen;
                		Vec prevbend = prevtop.add(prevbottom.sub(prevtop).withMagnitude(prevtoptobend));
                		
                		if (numberrecursions < 10) {
                    		updateSegment(top, prevtop, bend, prevbend, index, numberrecursions+1);
                		} else {
                			GrappleMod.LOGGER.warn("Warning: number recursions exceeded");
                		}
                		break;
                	}
                }
            }
        }
	}
	
	public Vec linePlaneIntersection(Vec linepoint1, Vec linepoint2, Vec planepoint, Vec planenormal) {
		// calculate the intersection of a line and a plane
		// formula: https://en.wikipedia.org/wiki/Line%E2%80%93plane_intersection#Algebraic_form
		
		Vec linevec = linepoint2.sub(linepoint1);
		
		if (linevec.dot(planenormal) == 0) {
			return null;
		}
		
		double d = planepoint.sub(linepoint1).dot(planenormal) / linevec.dot(planenormal);
		return linepoint1.add(linevec.scale(d));
	}
	
	public Vec getNormal(Direction facing) {
		Vec3i facingvec = facing.getNormal();
		return new Vec(facingvec.getX(), facingvec.getY(), facingvec.getZ());
	}
	
	public boolean hookPastBend(double ropelen) {
		return (this.getDistToFarthest() > ropelen);
	}
	
	public BlockPos getBendBlock(int index) {
		Vec bendpos = this.segments.get(index);
		bendpos.mutableAdd(this.getNormal(this.segmentBottomSides.get(index)).withMagnitude(-this.INTO_BLOCK * 2));
		bendpos.mutableAdd(this.getNormal(this.segmentTopSides.get(index)).withMagnitude(-this.INTO_BLOCK * 2));
		return BlockPos.containing(bendpos.toVec3d());
	}
	
	public void actuallyAddSegment(int index, Vec bendpoint, Direction bottomside, Direction topside) {
        segments.add(index, bendpoint);
        segmentBottomSides.add(index, bottomside);
        segmentTopSides.add(index, topside);
        
		if (!this.world.isClientSide) {
			SegmentMessage addmessage = new SegmentMessage(this.hookEntity.getId(), true, index, bendpoint, topside, bottomside);
			Vec playerpoint = Vec.positionVec(this.hookEntity.shootingEntity);

			NetworkManager.packetToClient(addmessage, GrappleModUtils.getPlayersThatCanSeeChunkAt((ServerLevel) world, playerpoint));
		}
	}
	
	public void print() {
		for (int i = 1; i < segments.size() - 1; i++) {
			System.out.print(i);
			System.out.print(" ");
			System.out.print(segmentTopSides.get(i).toString());
			System.out.print(" ");
			System.out.print(segmentBottomSides.get(i).toString());
			System.out.print(" ");
			segments.get(i).print();
		}
	}
	
	public Vec getClosest(Vec hookpos) {
		segments.set(0, hookpos);
		
		return segments.get(segments.size() - 2);
	}
	
	public double getDistToAnchor() {
		double dist = 0;
		for (int i = 0; i < segments.size() - 2; i++) {
			dist += segments.get(i).sub(segments.get(i+1)).length();
		}
		
		return dist;
	}
	
	public Vec getFarthest() {
		return segments.get(1);
	}
	
	public double getDistToFarthest() {
		double dist = 0;
		for (int i = 1; i < segments.size() - 1; i++) {
			dist += segments.get(i).sub(segments.get(i+1)).length();
		}
		
		return dist;
	}
	
	public double getDist(Vec hookpos, Vec playerpos) {
		segments.set(0, hookpos);
		segments.set(segments.size() - 1, playerpos);
		double dist = 0;
		for (int i = 0; i < segments.size() - 1; i++) {
			dist += segments.get(i).sub(segments.get(i+1)).length();
		}
		
		return dist;
	}
	
	public AABB getBoundingBox(Vec hookpos, Vec playerpos) {
		this.updatePos(hookpos, playerpos, this.ropeLen);
		Vec minvec = new Vec(hookpos);
		Vec maxvec = new Vec(hookpos);
		for (int i = 1; i < segments.size(); i++) {
			Vec segpos = segments.get(i);
			if (segpos.x < minvec.x) {
				minvec.x = segpos.x;
			} else if (segpos.x > maxvec.x) {
				maxvec.x = segpos.x;
			}
			if (segpos.y < minvec.y) {
				minvec.y = segpos.y;
			} else if (segpos.y > maxvec.y) {
				maxvec.y = segpos.y;
			}
			if (segpos.z < minvec.z) {
				minvec.z = segpos.z;
			} else if (segpos.z > maxvec.z) {
				maxvec.z = segpos.z;
			}
		}

		return new AABB(minvec.x, minvec.y, minvec.z, maxvec.x, maxvec.y, maxvec.z);
	}


	public List<Vec> getSegments() {
		return Collections.unmodifiableList(this.segments);
	}
}
