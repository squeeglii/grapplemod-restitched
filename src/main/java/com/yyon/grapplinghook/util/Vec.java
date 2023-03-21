package com.yyon.grapplinghook.util;

import com.yyon.grapplinghook.GrappleMod;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class Vec {
	public double x;
	public double y;
	public double z;
	
	public Vec(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.checkNaN();
	}

	public Vec(Vector3f vec) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;

		this.checkNaN();
	}
	
	public void checkNaN() {
		if (Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z)) {
			GrappleMod.LOGGER.error("Error: vector contains NaN");
			this.x = 0; this.y = 0; this.z = 0;
		}
	}
	
	public Vec(Vec3 vec3d) {
		this.x = vec3d.x;
		this.y = vec3d.y;
		this.z = vec3d.z;
		
		this.checkNaN();
	}
	
	public Vec(Vec vec) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
	}

	public Vec3 toVec3d() {
		return new Vec3(this.x, this.y, this.z);
	}

	public static Vec positionVec(Entity e) {
		return new Vec(e.position());
	}
	
	public static Vec partialPositionVec(Entity e, double partialTicks) {
		return new Vec(lerp(partialTicks, e.xo, e.getX()), lerp(partialTicks, e.yo, e.getY()), lerp(partialTicks, e.zo, e.getZ()));
	}
	
	public static double lerp(double frac, double from, double to) {
		return (from * (1-frac)) + (to * frac);
	}
	
	public static Vec motionVec(Entity e) {
		return new Vec(e.getDeltaMovement());
	}
	
	public Vec add(Vec v2) {
		return new Vec(this.x + v2.x, this.y + v2.y, this.z + v2.z);
	}
	
	public Vec mutableAdd(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}
	
	public Vec mutableAdd(Vec v2) {
		return this.mutableAdd(v2.x, v2.y, v2.z);
	}
	
	public Vec sub(Vec v2) {
		return new Vec(this.x - v2.x, this.y - v2.y, this.z - v2.z);
	}

	public Vec mutableSub(Vec v2) {
		this.x -= v2.x;
		this.y -= v2.y;
		this.z -= v2.z;
		return this;
	}

	public Vec multiply(double x, double y, double z) {
		return new Vec(this.x * x, this.y * y, this.z * z);
	}

	public Vec mutableMultiply(double x, double y, double z) {
		this.x *= x;
		this.y *= y;
		this.z *= z;
		return this;
	}
	
	public Vec rotateYaw(double a) {
		return new Vec(this.x * Math.cos(a) - this.z * Math.sin(a), this.y, this.x * Math.sin(a) + this.z * Math.cos(a));
	}
	
    public Vec rotatePitch(double pitch) {
        return new Vec(this.x, this.y * Math.cos(pitch) + this.z * Math.sin(pitch), this.z * Math.cos(pitch) - this.y * Math.sin(pitch));
    }
    
    public static Vec fromAngles(double yaw, double pitch) {
    	return new Vec(Math.tan(-yaw), Math.tan(pitch), 1).normalize();
    }
	
	public Vec scale(double factor) {
		return this.multiply(factor, factor, factor);
	}
	
	public Vec mutableScale(double factor) {
		return this.mutableMultiply(factor, factor, factor);
	}
	
	public double length() {
		return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2) + Math.pow(this.z, 2));
	}
	
	public Vec normalize() {
		if (this.length() == 0) {
			GrappleMod.LOGGER.warn("normalizing vector with no length");
			return new Vec(this);
		}
		return this.scale(1.0 / this.length());
	}
	
	public void mutableNormalize() {
		this.mutableScale(1.0 / this.length());
	}
	
	public double dot(Vec v2) {
		return this.x * v2.x + this.y * v2.y + this.z * v2.z;
	}
	
	public Vec withMagnitude(double l) {
		double oldLength = this.length();
		if (oldLength != 0) {
			double factor = l / oldLength;
			return this.scale(factor);
		}

		return this;
	}
	
	public void mutableSetMagnitude(double l) {
		double oldLength = this.length();
		if (oldLength != 0) {
			double changeFactor = l / oldLength;
			this.mutableScale(changeFactor);
		}
	}
	
	public Vec proj(Vec v2) {
		Vec v3 = v2.normalize();
		double dot = this.dot(v3);
		return v3.withMagnitude(dot);
	}
	
	public double distanceAlong(Vec v2) {
		Vec v3 = v2.normalize();
		return this.dot(v3);
	}
	
	public Vec removeAlong(Vec v2) {
		return this.sub(this.proj(v2));
	}
	
	public void print(){
		GrappleMod.LOGGER.warn(this.toString());
	}
	
	public String toString() {
		return "<%s,%s,%s>".formatted(this.x, this.y, this.z);
	}

	public Vec add(double x, double y, double z) {
		return new Vec(this.x + x, this.y + y, this.z + z);
	}
	
	public double getYaw() {
		Vec norm = this.normalize();
		return Math.toDegrees(-Math.atan2(norm.x, norm.z));
	}
	
	public double getPitch() {
		Vec norm = this.normalize();
		return Math.toDegrees(-Math.asin(norm.y));
	}
	
	public Vec cross(Vec b) {
		return new Vec(this.y * b.z - this.z * b.y, this.z * b.x - this.x * b.z, this.x * b.y - this.y * b.x);
	}
	
	public double angle(Vec b) {
		double la = this.length();
		double lb = b.length();
		if (la == 0 || lb == 0) { return 0; }
		return Math.acos(this.dot(b) / (la*lb));
	}
	
	public void applyAsPositionTo(Entity e) {
		this.checkNaN();
		e.setPos(this.x, this.y, this.z);
	}
	
	public void applyAsMotionTo(Entity e) {
		this.checkNaN();
		e.setDeltaMovement(this.toVec3d());
	}

	public static Vec lookVec(Entity entity) {
		return new Vec(entity.getLookAngle());
	}
}
