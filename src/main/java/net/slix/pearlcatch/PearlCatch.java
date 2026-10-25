package net.slix.pearlcatch;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class PearlCatch {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static int delayTicks = 0;
    private static final int CONFIGURABLE_DELAY = 3;

    public static void onTick() {
        if (mc.player == null || mc.world == null) return;

        EnderPearlEntity closestPearl = null;
        for (EnderPearlEntity pearl : mc.world.getEntitiesByClass(EnderPearlEntity.class, mc.player.getBoundingBox().expand(10.0), e -> true)) {
            if (closestPearl == null || pearl.squaredDistanceTo(mc.player) < closestPearl.squaredDistanceTo(mc.player)) {
                closestPearl = pearl;
            }
        }

        if (closestPearl != null) {
            if (delayTicks > 0) {
                delayTicks--;
                return;
            }

            Vec3d pearlPos = new Vec3d(closestPearl.getX(), closestPearl.getY(), closestPearl.getZ());
            Vec3d pearlVel = closestPearl.getVelocity();
            Vec3d predictedPos = pearlPos.add(pearlVel.multiply(2.0)).add(0, -0.2, 0);

            Vec3d eyePos = mc.player.getEyePos();
            double dx = predictedPos.x - eyePos.x;
            double dy = predictedPos.y - eyePos.y;
            double dz = predictedPos.z - eyePos.z;
            double dist = Math.sqrt(dx * dx + dz * dz);

            float targetYaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0f);
            float targetPitch = (float) -Math.toDegrees(Math.atan2(dy, dist));

            mc.player.setYaw(targetYaw);
            mc.player.setPitch(targetPitch);

            if (mc.player.getMainHandStack().getItem() == Items.ENDER_PEARL || mc.player.getOffHandStack().getItem() == Items.ENDER_PEARL) {
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                delayTicks = CONFIGURABLE_DELAY;
            }
        }
    }
}