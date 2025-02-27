package pw.rxj.iron_quarry.render;

import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class SpriteVec2f {
    public final Vector3f from;
    public final Vector3f to;

    private SpriteVec2f(Vector3f from, Vector3f to) {
        this.from = from;
        this.to = to;
    }

    public static SpriteVec2f from(Vector3f from, Vector3f to) {
        return new SpriteVec2f(from, to);
    }
    public static SpriteVec2f from(Vec3d from, Vec3d to) {
        return from(new Vector3f(from.toVector3f()), new Vector3f(to.toVector3f()));
    }

    public Vector3f normalize() {
        Vector3f normalized = new Vector3f(this.to);
        normalized.sub(this.from);
        normalized.normalize();

        return normalized;
    }
    public SpriteVec2f normalizeSeparate() {
        Vector3f normalizedFrom = new Vector3f(this.from);
        normalizedFrom.normalize();
        Vector3f normalizedTo = new Vector3f(this.to);
        normalizedTo.normalize();

        return SpriteVec2f.from(normalizedFrom, normalizedTo);
    }

    public SpriteVec2f swap() {
        return from(to, from);
    }

    public double innerDistance() {
        return Math.sqrt(this.innerSquaredDistance());
    }
    public double innerSquaredDistance() {
        double x = this.from.x - this.to.x;
        double y = this.from.y - this.to.y;
        double z = this.from.z - this.to.z;

        return x * x + y * y + z * z;
    }
    public double distanceTo(Vector3f pos) {
        return Math.sqrt(this.squaredDistanceTo(pos));
    }
    public double squaredDistanceTo(Vector3f pos) {
        Vector3f center = this.center();

        double x = pos.x - center.x;
        double y = pos.y - center.y;
        double z = pos.z - center.z;

        return x * x + y * y + z * z;
    }

    public List<SpriteVec2f> autoSplit(float roughLength) {
        return this.split((int) Math.ceil(this.innerDistance() / roughLength));
    }
    public List<SpriteVec2f> split(int parts) {
        List<SpriteVec2f> list = new ArrayList<>();

        Vec3d from = new Vec3d(this.from);
        Vec3d to = new Vec3d(this.to);

        Vec3d abs = to.subtract(from);
        Vec3d part = new Vec3d(abs.getX() / parts, abs.getY() / parts, abs.getZ() / parts);

        for (int i = 0; i < parts; i++) {
            list.add(SpriteVec2f.from(from.add(part.multiply(i)), from.add(part.multiply(i + 1))));
        }

        return list;
    }
    public Vector3f center() {
        return new Vector3f(
                (this.from.x + this.to.x) / 2,
                (this.from.y + this.to.y) / 2,
                (this.from.z + this.to.z) / 2
        );
    }

    public boolean isOutsideRange(double range) {
        return RenderUtil.isOutsideRange(new Vec3d(this.from), range) &&
               RenderUtil.isOutsideRange(new Vec3d(this.to), range);
    }
    public boolean isOutsideCuboid(Cuboid cuboid) {
         return cuboid.isOutside(new Vec3d(this.from)) &&
                cuboid.isOutside(new Vec3d(this.to));
    }

    @Override
    public String toString() {
        return String.format("SpriteVec{from: %s, to: %s}", this.from, this.to);
    }
}
