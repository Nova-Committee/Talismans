package nova.committee.talismans.client.camera;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import nova.committee.talismans.MixinStatic;
import nova.committee.talismans.Talismans;
import nova.committee.talismans.init.proxy.ClientProxy;
import nova.committee.talismans.init.registry.ModItems;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/23 23:35
 * Version: 1.0
 */
public class FreeCamera {
    public static final FreeCamera instance = new FreeCamera();

    private final Minecraft mc = Minecraft.getInstance();
    private final Quaternion rotation = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
    private final Vector3f forwards = new Vector3f(0.0F, 0.0F, 1.0F);
    private final Vector3f up = new Vector3f(0.0F, 1.0F, 0.0F);
    private final Vector3f left = new Vector3f(1.0F, 0.0F, 0.0F);
    private boolean active;
    private CameraType oldCameraType;
    private Input oldInput;
    private double x, y, z;
    private float yRot, xRot;
    private double forwardVelocity;
    private double leftVelocity;
    private double upVelocity;
    private long lastTime;
    private boolean insideRenderDebug;

    private FreeCamera() {
    }

    public boolean isActive() {
        return active;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getXRot() {
        return xRot;
    }

    public float getYRot() {
        return yRot;
    }

    public void toggle() {
        if (active) {
            disable();
        } else {
            enable();
        }
    }

    public void enable() {
        if (!active) {
            active = true;
            oldCameraType = mc.options.getCameraType();
            oldInput = mc.player.input;
            mc.player.input = new Input();
            mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
            if (oldCameraType.isFirstPerson() != mc.options.getCameraType().isFirstPerson()) {
                mc.gameRenderer.checkEntityPostEffect(mc.options.getCameraType().isFirstPerson() ? mc.getCameraEntity() : null);
            }

            float frameTime = mc.getFrameTime();
            Entity entity = mc.getCameraEntity();
            x = Mth.lerp(frameTime, entity.xo, entity.getX());
            y = Mth.lerp(frameTime, entity.yo, entity.getY()) + entity.getEyeHeight();
            z = Mth.lerp(frameTime, entity.zo, entity.getZ());
            yRot = entity.getViewYRot(frameTime);
            xRot = entity.getViewXRot(frameTime);

            calculateVectors();

            double distance = -2;
            x += (double)this.forwards.x() * distance;
            y += (double)this.forwards.y() * distance;
            z += (double)this.forwards.z() * distance;

            forwardVelocity = 0;
            leftVelocity = 0;
            upVelocity = 0;
            lastTime = 0;
        }
    }

    public void disable() {
        if (active) {
            active = false;
            CameraType cameraType = mc.options.getCameraType();
            mc.options.setCameraType(oldCameraType);
            mc.player.input = oldInput;
            if (cameraType.isFirstPerson() != mc.options.getCameraType().isFirstPerson()) {
                mc.gameRenderer.checkEntityPostEffect(mc.options.getCameraType().isFirstPerson() ? mc.getCameraEntity() : null);
            }
            oldCameraType = null;
        }
    }

    public void onKeyInput() {
        if (mc.player == null) {
            return;
        }
        if (mc.screen != null) {
            return;
        }
        if (((ClientProxy) Talismans.proxy).toggleFreeCamera.isDown() && mc.player.getInventory().contains(ModItems.sheep_em.getDefaultInstance())) {
            toggle();
        }
    }



    public void onMouseTurn(double yRot, double xRot) {
        this.xRot += (float)xRot * 0.15F;
        this.yRot += (float)yRot * 0.15F;
        this.xRot = Mth.clamp(this.xRot, -90, 90);
        calculateVectors();
    }

    public void onRenderTickStart() {
        if (active) {
            if (lastTime == 0) {
                lastTime = System.nanoTime();
                return;
            }

            long currTime = System.nanoTime();
            float frameTime = (currTime - lastTime) / 1e9f;
            lastTime = currTime;

            Input input = oldInput;
            float forwardImpulse = (input.up ? 1 : 0) + (input.down ? -1 : 0);
            float leftImpulse = (input.left ? 1 : 0) + (input.right ? -1 : 0);
            float upImpulse = ((input.jumping ? 1 : 0) + (input.shiftKeyDown ? -1 : 0));
            double slowdown = Math.pow(0.01, frameTime);
            forwardVelocity = combineMovement(forwardVelocity, forwardImpulse, frameTime, 50, slowdown);
            leftVelocity = combineMovement(leftVelocity, leftImpulse, frameTime, 50, slowdown);
            upVelocity = combineMovement(upVelocity, upImpulse, frameTime, 50, slowdown);

            double dx = (double) this.forwards.x() * forwardVelocity + (double) this.left.x() * leftVelocity;
            double dy = (double) this.forwards.y() * forwardVelocity + upVelocity + (double) this.left.y() * leftVelocity;
            double dz = (double) this.forwards.z() * forwardVelocity + (double) this.left.z() * leftVelocity;
            dx *= frameTime;
            dy *= frameTime;
            dz *= frameTime;
            double speed = new Vec3(dx, dy, dz).length() / frameTime;
            if (speed > 50) {
                double factor = 50 / speed;
                forwardVelocity *= factor;
                leftVelocity *= factor;
                upVelocity *= factor;
                dx *= factor;
                dy *= factor;
                dz *= factor;
            }
            x += dx;
            y += dy;
            z += dz;
        }
    }

    public void onClientTickStart() {
        if (active) {
            while (mc.options.keyTogglePerspective.consumeClick()) {
                // consume clicks
            }
            oldInput.tick(false);
        }
    }

    public void onWorldUnload() {
        disable();
    }

    public boolean shouldOverridePlayerPosition() {
        return MixinStatic.insidePick || insideRenderDebug;
    }

    public void onRenderDebugScreenLeft(List<String> list) {
        if (active) {
            list.add("");
            String coordinates = String.format(Locale.ROOT, "Free Cam XYZ: %.3f / %.5f / %.3f", x, y, z);
            list.add(coordinates);
        }
    }

    public void onRenderDebugScreenRight(List<String> list) {
        if (active) {
            insideRenderDebug = true;
            try {
                HitResult hit = mc.player.pick(20.0D, 0.0F, false);
                if (hit.getType() == HitResult.Type.BLOCK) {
                    BlockPos pos = ((BlockHitResult)hit).getBlockPos();
                    BlockState state = mc.level.getBlockState(pos);
                    list.add("");
                    list.add(ChatFormatting.UNDERLINE + "Free Cam Targeted Block: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
                    list.add(String.valueOf(ForgeRegistries.BLOCKS.getKey(state.getBlock())));

                    for (var entry: state.getValues().entrySet()) {
                        list.add(getPropertyValueString(entry));
                    }

                    state.getTags().map(tag -> "#" + tag.location()).forEach(list::add);
                }
            }
            finally {
                insideRenderDebug = false;
            }
        }
    }

    public boolean shouldRenderHands() {
        return true;
    }

    private void calculateVectors() {
        rotation.set(0.0F, 0.0F, 0.0F, 1.0F);
        rotation.mul(Vector3f.YP.rotationDegrees(-yRot));
        rotation.mul(Vector3f.XP.rotationDegrees(xRot));
        forwards.set(0.0F, 0.0F, 1.0F);
        forwards.transform(rotation);
        up.set(0.0F, 1.0F, 0.0F);
        up.transform(rotation);
        left.set(1.0F, 0.0F, 0.0F);
        left.transform(rotation);
    }

    private double combineMovement(double velocity, double impulse, double frameTime, double acceleration, double slowdown) {
        if (impulse != 0) {
            if (impulse > 0 && velocity < 0) {
                velocity = 0;
            }
            if (impulse < 0 && velocity > 0) {
                velocity = 0;
            }
            velocity += acceleration * impulse * frameTime;
        } else {
            velocity *= slowdown;
        }
        return velocity;
    }

    private String getPropertyValueString(Map.Entry<Property<?>, Comparable<?>> p_94072_) {
        Property<?> property = p_94072_.getKey();
        Comparable<?> comparable = p_94072_.getValue();
        String s = Util.getPropertyName(property, comparable);
        if (Boolean.TRUE.equals(comparable)) {
            s = ChatFormatting.GREEN + s;
        } else if (Boolean.FALSE.equals(comparable)) {
            s = ChatFormatting.RED + s;
        }

        return property.getName() + ": " + s;
    }

}
