package com.voinearadu.generic.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor
@Getter
@Setter
public class Location  {

    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;
    private @Nullable String world;

    public Location(double x, double y, double z, float pitch, float yaw, @Nullable String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
        this.world = world;
    }

    public Location(double x, double y, double z, @Nullable String world) {
        this(x, y, z, 0, 0, world);
    }

    public Location(double x, double y, double z, float pitch, float yaw) {
        this(x, y, z, pitch, yaw, null);
    }

    public Location(double x, double y, double z) {
        this(x, y, z, null);
    }


    public Location offset(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Location negativeOffset(double x, double y, double z) {
        return offset(-x, -y, -z);
    }

    public Location offsetNew(double x, double y, double z, float pitch, float yaw) {
        Location output = clone();

        output.x += x;
        output.y += y;
        output.z += z;
        output.pitch += pitch;
        output.yaw += yaw;

        return output;
    }

    public Location offsetNew(double x, double y, double z) {
        return offsetNew(x, y, z, 0, 0);
    }

    public Location negativeOffsetNew(double x, double y, double z) {
        return offsetNew(-x, -y, -z);
    }

    public Location negativeOffsetNew(Location offset) {
        return negativeOffsetNew(
                offset.x,
                offset.y,
                offset.z
        );
    }

    public Location offset(Location offset) {
        return offset(offset.x, offset.y, offset.z);
    }

    @SuppressWarnings("unused")
    public Location negativeOffset(Location offset) {
        return negativeOffset(offset.x, offset.y, offset.z);
    }

    public Location offsetNew(Location offset) {
        return offsetNew(
                offset.x,
                offset.y,
                offset.z,
                offset.pitch,
                offset.yaw
        );
    }

    @SuppressWarnings("UnusedReturnValue")
    public Location multiply(double factor) {
        this.x *= factor;
        this.y *= factor;
        this.z *= factor;

        return this;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public Location clone() {
        return new Location(x, y, z, pitch, yaw, world);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location location)) return false;

        if (getX() != location.getX()) return false;
        if (getY() != location.getY()) return false;
        if (getZ() != location.getZ()) return false;
        return getWorld() != null ? getWorld().equals(location.getWorld()) : location.getWorld() == null;
    }

    public boolean equalsCoords(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location location)) return false;

        return x == location.x && y == location.y && z == location.z;
    }

    @Override
    public int hashCode() {
        double result = getX();
        result = 31 * result + getY();
        result = 31 * result + getZ();
        result = 31 * result + getPitch();
        result = 31 * result + getYaw();
        result = 31 * result + (getWorld() != null ? getWorld().hashCode() : 0);
        return (int) result;
    }

    public static Location min(Location... locations) {
        double minX = 1000000000;
        double minY = 1000000000;
        double minZ = 1000000000;

        for (Location location : locations) {
            minX = Double.min(minX, location.getX());
            minY = Double.min(minY, location.getY());
            minZ = Double.min(minZ, location.getZ());
        }

        return new Location(minX, minY, minZ);
    }

    public static Location max(Location... locations) {
        double maxX = -1000000000;
        double maxY = -1000000000;
        double maxZ = -1000000000;

        for (Location location : locations) {
            maxX = Double.max(maxX, location.getX());
            maxY = Double.max(maxY, location.getY());
            maxZ = Double.max(maxZ, location.getZ());
        }

        return new Location(maxX, maxY, maxZ);
    }

    public String toCompactString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
