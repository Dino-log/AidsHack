package cc.aidshack.utils;

import cc.aidshack.mixins.FrustramAccessor;
import cc.aidshack.mixins.WorldRendererAccessor;
import cc.aidshack.mixinterface.IItemRenderer;
import cc.aidshack.mixinterface.IMatrix4f;
import cc.aidshack.utils.math.Matrix4x4;
import cc.aidshack.utils.math.QuadColor;
import cc.aidshack.utils.math.Vector3D;
import cc.aidshack.utils.math.Vertexer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static cc.aidshack.AidsHack.MC;
import static cc.aidshack.AidsHack.mc;

public class RenderUtils {


    public static RenderUtils INSTANCE = new RenderUtils();

    public static boolean SetCustomYaw = false;
    public static float CustomYaw = 0;
    public int scaledWidth = 0;
    public int scaledHeight = 0;

    public void onTick() {
        scaledWidth = mc.getWindow().getScaledWidth();
        scaledHeight = mc.getWindow().getScaledHeight();
    }

    public static void drawString(String string, int x, int y, int color, float scale) {
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(0.0D, 0.0D, 0.0D);
        matrixStack.scale(scale, scale, 1);
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        MC.textRenderer.draw(string, (float) x / scale, (float) y / scale, color, true, matrixStack.peek().getPositionMatrix(), immediate, false, 0, 0xF000F0);
        immediate.draw();
    }
    public static MatrixStack matrixFrom(double x, double y, double z) {
        MatrixStack matrices = new MatrixStack();

        Camera camera = mc.gameRenderer.getCamera();
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw() + 180.0F));

        matrices.translate(x - camera.getPos().x, y - camera.getPos().y, z - camera.getPos().z);

        return matrices;
    }
    public static void drawLine(double x1, double y1, double z1, double x2, double y2, double z2, LineColor color, float width) {
        if (!isPointVisible(x1, y1, z1) && !isPointVisible(x2, y2, z2)) {
            return;
        }

        setup3DRender(true);

        MatrixStack matrices = matrixFrom(x1, y1, z1);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        // Line
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesShader);
        RenderSystem.lineWidth(width);

        buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
        Vertexer.vertexLine(matrices, buffer, 0f, 0f, 0f, (float) (x2 - x1), (float) (y2 - y1), (float) (z2 - z1), color);
        tessellator.draw();

        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        end3DRender();
    }

    public static void setup3DRender(boolean disableDepth) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        if (disableDepth)
            RenderSystem.disableDepthTest();
        RenderSystem.depthMask(MinecraftClient.isFabulousGraphicsOrBetter());
        RenderSystem.enableCull();
    }

    public static void end3DRender() {
        RenderSystem.enableTexture();
        RenderSystem.disableCull();
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
    }


    public static void drawBoxFill(Box box, QuadColor color, Direction... excludeDirs) {
        if (!getFrustum().isVisible(box)) {
            return;
        }

        setup3DRender(true);

        MatrixStack matrices = matrixFrom(box.minX, box.minY, box.minZ);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        // Fill
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        Vertexer.vertexBoxQuads(matrices, buffer, Boxes.moveToZero(box), color, excludeDirs);
        tessellator.draw();

        end3DRender();
    }

    public static void drawBoxOutline(Box box, QuadColor color, float lineWidth, Direction... excludeDirs) {
        if (!getFrustum().isVisible(box)) {
            return;
        }

        setup3DRender(true);

        MatrixStack matrices = matrixFrom(box.minX, box.minY, box.minZ);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        // Outline
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesShader);
        RenderSystem.lineWidth(lineWidth);

        buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
        Vertexer.vertexBoxLines(matrices, buffer, Boxes.moveToZero(box), color, excludeDirs);
        tessellator.draw();

        RenderSystem.enableCull();

        end3DRender();
    }

    public static void drawCircle(MatrixStack matrices, Vec3d pos, float partialTicks, double rad, double height, int color) {
        double lastX = 0;
        double lastZ = rad;
        for (int angle = 0; angle <= 360; angle += 6) {
            float cos = MathHelper.cos((float) Math.toRadians(angle));
            float sin = MathHelper.sin((float) Math.toRadians(angle));

            double x = rad * sin;
            double z = rad * cos;
            drawLine(
                    pos.x + lastX, pos.y, pos.z + lastZ,
                    pos.x + x, pos.y, pos.z + z,
                    LineColor.single(color), 2);

            lastX = x;
            lastZ = z;
        }
    }

    public static void drawItem(ItemStack stack, float xPosition, float yPosition) {
        drawItem(stack, xPosition, yPosition, 1);
    }
    private static void renderGuiQuad(BufferBuilder buffer, float x, float y, float width, float height, int red, int green, int blue, int alpha) {
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex((double) (x + 0), (double) (y + 0), 0.0D).color(red, green, blue, alpha).next();
        buffer.vertex((double) (x + 0), (double) (y + height), 0.0D).color(red, green, blue, alpha).next();
        buffer.vertex((double) (x + width), (double) (y + height), 0.0D).color(red, green, blue, alpha).next();
        buffer.vertex((double) (x + width), (double) (y + 0), 0.0D).color(red, green, blue, alpha).next();
        Tessellator.getInstance().draw();
    }
    public static void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, float x, float y, float scale, @Nullable String countLabel) {
        if (!stack.isEmpty()) {
            MatrixStack matrixStack = new MatrixStack();
            if (stack.getCount() != 1 || countLabel != null) {
                String string = countLabel == null ? String.valueOf(stack.getCount()) : countLabel;
                matrixStack.translate(0.0D, 0.0D, (double)(mc.getItemRenderer().zOffset + 200.0F));
                VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
                renderer.draw(string, (float)(x + 19 - 2 - renderer.getWidth(string)), (float)(y + 6 + 3), 16777215, true, matrixStack.peek().getPositionMatrix(), immediate, false, 0, 15728880);
                immediate.draw();
            }

            if (stack.isItemBarVisible()) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.disableBlend();
                int i = stack.getItemBarStep();
                int j = stack.getItemBarColor();
                fill(matrixStack, x + 2, y + 13, x + 2 + 13, y + 13 + 2, 0xff000000);
                fill(matrixStack, x + 2, y + 13, x + 2 + i, y + 13 + 1, new Color(j >> 16 & 255, j >> 8 & 255, j & 255, 255).getRGB());
                RenderSystem.enableBlend();
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }

            ClientPlayerEntity clientPlayerEntity = mc.player;
            float f = clientPlayerEntity == null ? 0.0F : clientPlayerEntity.getItemCooldownManager().getCooldownProgress(stack.getItem(), MinecraftClient.getInstance().getTickDelta());
            if (f > 0.0F) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                Tessellator tessellator2 = Tessellator.getInstance();
                BufferBuilder bufferBuilder2 = tessellator2.getBuffer();
                renderGuiQuad(bufferBuilder2, x, y + MathHelper.floor(16.0F * (1.0F - f)), 16, MathHelper.ceil(16.0F * f), 255, 255, 255, 127);
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }

        }
    }
    public static void drawItem(ItemStack stack, float xPosition, float yPosition, float scale) {
        String amountText = stack.getCount() != 1 ? stack.getCount() + "" : "";
        IItemRenderer iItemRenderer = (IItemRenderer) mc.getItemRenderer();
        iItemRenderer.renderItemIntoGUI(stack, xPosition, yPosition, scale);
        renderGuiItemOverlay(mc.textRenderer, stack, xPosition - 0.5f, yPosition + 1, scale, amountText);
    }
    public static Frustum getFrustum() {
        return ((WorldRendererAccessor) mc.worldRenderer).getFrustum();
    }
    public static boolean isPointVisible(double x, double y, double z) {
        FrustramAccessor frustum = (FrustramAccessor) getFrustum();
        Vector4f[] frustumCoords = frustum.getHomogeneousCoordinates();
        Vector4f pos = new Vector4f((float) (x - frustum.getX()), (float) (y - frustum.getY()), (float) (z - frustum.getZ()), 1f);

        for (int i = 0; i < 6; ++i) {
            if (frustumCoords[i].dotProduct(pos) <= 0f) {
                return false;
            }
        }

        return true;
    }

    public static Vec3d getEntityRenderPosition(Entity entity, double partial) {
        double x = entity.prevX + ((entity.getX() - entity.prevX) * partial) - mc.getEntityRenderDispatcher().camera.getPos().x;
        double y = entity.prevY + ((entity.getY() - entity.prevY) * partial) - mc.getEntityRenderDispatcher().camera.getPos().y;
        double z = entity.prevZ + ((entity.getZ() - entity.prevZ) * partial) - mc.getEntityRenderDispatcher().camera.getPos().z;
        return new Vec3d(x, y, z);
    }
    public static Vec3d getRenderPosition(Vec3d vec3d, MatrixStack matrixStack) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        double minX = vec3d.getX() - mc.getEntityRenderDispatcher().camera.getPos().x;
        double minY = vec3d.getY() - mc.getEntityRenderDispatcher().camera.getPos().y;
        double minZ = vec3d.getZ() - mc.getEntityRenderDispatcher().camera.getPos().z;
        Vector4f vector4f = new Vector4f((float)minX, (float)minY, (float)minZ, 1.f);
        vector4f.transform(matrix);
        return new Vec3d(vector4f.getX(), vector4f.getY(), vector4f.getZ());
    }
    public static Vec3d getPos(Entity entity, float yOffset, float partialTicks, MatrixStack matrixStack) {
        Vec3d bound = getEntityRenderPosition(entity, partialTicks).add(0, yOffset, 0);
        Vector4f vector4f = new Vector4f((float)bound.x, (float)bound.y, (float)bound.z, 1.f);
        vector4f.transform(matrixStack.peek().getPositionMatrix());
        Vec3d twoD = to2D(vector4f.getX(), vector4f.getY(), vector4f.getZ());
        return new Vec3d(twoD.x, twoD.y, twoD.z);
    }

    public static Vec3d to2D(Vec3d worldPos, MatrixStack matrixStack) {
        Vec3d bound = getRenderPosition(worldPos, matrixStack);
        Vec3d twoD = to2D(bound.x, bound.y, bound.z);
        return new Vec3d(twoD.x, twoD.y, twoD.z);
    }

    private static Vec3d to2D(double x, double y, double z) {
        int displayHeight = mc.getWindow().getHeight();
        Vector3D screenCoords = new Vector3D();
        int[] viewport = new int[4];
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
        Matrix4x4 matrix4x4Proj = Matrix4x4.copyFromColumnMajor(RenderSystem.getProjectionMatrix());
        Matrix4x4 matrix4x4Model = Matrix4x4.copyFromColumnMajor(RenderSystem.getModelViewMatrix());
        matrix4x4Proj.mul(matrix4x4Model).project((float) x, (float) y, (float) z, viewport, screenCoords);

        return new Vec3d(screenCoords.x / getScaleFactor(), (displayHeight - screenCoords.y) / getScaleFactor(), screenCoords.z);
    }
    public static void setup2DRender(boolean disableDepth) {
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        if (disableDepth)
            RenderSystem.disableDepthTest();
    }

    public static double slowDownTo(double x1, double x2, float smooth) {
        return (x2 - x1) / smooth;
    }


    public static double getScaleFactor() {
        return mc.getWindow().getScaleFactor();
    }

    public static int getScaledWidth() {
        return mc.getWindow().getScaledWidth();
    }

    public static int getScaledHeight() {
        return mc.getWindow().getScaledHeight();
    }

    public static void end2DRender() {
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
    }

    public static void setup2DProjection() {
        Matrix4x4 ortho = Matrix4x4.ortho2DMatrix(0, getScaledWidth(), getScaledHeight(), 0, -0.1f, 1000.f);
        ShaderUtils.INSTANCE.setProjectionMatrix(ortho);
        ShaderUtils.INSTANCE.setModelViewMatrix(Matrix4x4.copyFromRowMajor(RenderSystem.getModelViewMatrix()));
    }

    public static void bindTexture(Identifier identifier) {
        RenderSystem.setShaderTexture(0, identifier);
    }

    public static void shaderColor(int hex) {
        float alpha = (hex >> 24 & 0xFF) / 255.0F;
        float red = (hex >> 16 & 0xFF) / 255.0F;
        float green = (hex >> 8 & 0xFF) / 255.0F;
        float blue = (hex & 0xFF) / 255.0F;
        RenderSystem.setShaderColor(red, green, blue, alpha);
    }

    public static int getPercentColor(float percent) {
        if (percent <= 15)
            return new Color(255, 0, 0).getRGB();
        else if (percent <= 25)
            return new Color(255, 75, 92).getRGB();
        else if (percent <= 50)
            return new Color(255, 123, 17).getRGB();
        else if (percent <= 75)
            return new Color(255, 234, 0).getRGB();
        return new Color(0, 255, 0).getRGB();
    }

    public static void fill(MatrixStack matrixStack, double x1, double y1, double x2, double y2, int color) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        double j;
        if (x1 < x2) {
            j = x1;
            x1 = x2;
            x2 = j;
        }

        if (y1 < y2) {
            j = y1;
            y1 = y2;
            y2 = j;
        }

        float f = (float)(color >> 24 & 255) / 255.0F;
        float g = (float)(color >> 16 & 255) / 255.0F;
        float h = (float)(color >> 8 & 255) / 255.0F;
        float k = (float)(color & 255) / 255.0F;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, (float)x1, (float)y2, 0.0F).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, (float)x2, (float)y2, 0.0F).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, (float)x2, (float)y1, 0.0F).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, (float)x1, (float)y1, 0.0F).color(g, h, k, f).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void gradientFill(MatrixStack matrixStack, double x1, double y1, double x2, double y2, int color1, int color2) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        double j;
        if (x1 < x2) {
            j = x1;
            x1 = x2;
            x2 = j;
        }

        if (y1 < y2) {
            j = y1;
            y1 = y2;
            y2 = j;
        }

    }

    public static void drawQuad(double x1, double y1, double x2, double y2, MatrixStack matrixStack) {
        drawQuad((float) x1, (float) y1, (float) x2, (float) y2, matrixStack);
    }

    public static void drawRound(double x1, double y1, double x2, double y2, MatrixStack matrixStack) {
        drawRound((float) x1, (float) y1, (float) x2, (float) y2, matrixStack);
    }

    public static void drawOutlinedQuad(float x1, float y1, float x2, float y2, MatrixStack matrixStack) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION);

        bufferBuilder.vertex(matrix, x1, y2, 0).next();
        bufferBuilder.vertex(matrix, x2, y2, 0).next();
        bufferBuilder.vertex(matrix, x2, y1, 0).next();
        bufferBuilder.vertex(matrix, x2, y2, 0).next();
        bufferBuilder.vertex(matrix, x1, y1, 0).next();
        bufferBuilder.vertex(matrix, x1, y2, 0).next();
        bufferBuilder.vertex(matrix, x1, y1, 0).next();
        bufferBuilder.vertex(matrix, x2, y1, 0).next();

        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
    }

    public static void drawOutlinedQuad(double x1, double y1, double x2, double y2, MatrixStack matrixStack) {
        drawOutlinedQuad((float) x1, (float) y1, (float) x2, (float) y2, matrixStack);
    }

    public static boolean isHoveringOver(double mouseX, double mouseY, double x1, double y1, double x2, double y2) {
        return mouseX > Math.min(x1, x2) && mouseX < Math.max(x1, x2) && mouseY > Math.min(y1, y2) && mouseY < Math.max(y1, y2);
    }

    public static Vec3d getCameraPos() {
        return MC.getBlockEntityRenderDispatcher().camera.getPos();
    }

    public static BlockPos getCameraBlockPos() {
        return MC.getBlockEntityRenderDispatcher().camera.getBlockPos();
    }

    public static void applyRegionalRenderOffset(MatrixStack matrixStack) {
        Vec3d camPos = getCameraPos();
        BlockPos blockPos = getCameraBlockPos();

        int regionX = (blockPos.getX() >> 9) * 512;
        int regionZ = (blockPos.getZ() >> 9) * 512;

        matrixStack.translate(regionX - camPos.x, -camPos.y, regionZ - camPos.z);
    }

    public static Vec3d getRenderLookVec(double partialTicks) {
        double f = 0.017453292;
        double pi = Math.PI;

        double yaw = MathHelper.lerp(partialTicks, MC.player.prevYaw, MC.player.getYaw());
        double pitch = MathHelper.lerp(partialTicks, MC.player.prevPitch, MC.player.getPitch());

        double f1 = Math.cos(-yaw * f - pi);
        double f2 = Math.sin(-yaw * f - pi);
        double f3 = -Math.cos(-pitch * f);
        double f4 = Math.sin(-pitch * f);

        return new Vec3d(f2 * f3, f4, f1 * f3).normalize();
    }

    public static void line(Vec3d start, Vec3d end, Color color, MatrixStack matrices) {
        float red = color.getRed() / 255f;
        float green = color.getGreen() / 255f;
        float blue = color.getBlue() / 255f;
        float alpha = color.getAlpha() / 255f;
        Camera c = mc.gameRenderer.getCamera();
        Vec3d camPos = c.getPos();
        start = start.subtract(camPos);
        end = end.subtract(camPos);
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float x1 = (float) start.x;
        float y1 = (float) start.y;
        float z1 = (float) start.z;
        float x2 = (float) end.x;
        float y2 = (float) end.y;
        float z2 = (float) end.z;
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        GL11.glDepthFunc(GL11.GL_ALWAYS);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableBlend();
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES,
                VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).next();
        buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).next();

        buffer.end();

        BufferRenderer.draw(buffer);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        RenderSystem.disableBlend();
    }
    private static void bobView(MatrixStack matrices) {
        Entity cameraEntity = MinecraftClient.getInstance().getCameraEntity();

        if (cameraEntity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)cameraEntity;
            float f = MinecraftClient.getInstance().getTickDelta();
            float g = playerEntity.horizontalSpeed - playerEntity.prevHorizontalSpeed;
            float h = -(playerEntity.horizontalSpeed + g * f);
            float i = MathHelper.lerp(f, playerEntity.prevStrideDistance, playerEntity.strideDistance);

            matrices.translate(-(MathHelper.sin(h * 3.1415927f) * i * 0.5), -(-Math.abs(MathHelper.cos(h * 3.1415927f) * i)), 0);
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.sin(h * 3.1415927f) * i * 3));
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(Math.abs(MathHelper.cos(h * 3.1415927f - 0.2f) * i) * 5));
        }
    }
    public static Vec3d center() {
        Vec3d pos = new Vec3d(0, 0, 1);

        if (mc.options.bobView) {
            MatrixStack bobViewMatrices = new MatrixStack();

            bobView(bobViewMatrices);
            bobViewMatrices.peek().getPositionMatrix().invert();

            pos = ((IMatrix4f) (Object) bobViewMatrices.peek().getPositionMatrix()).mul(pos);
        }
        return new Vec3d(pos.x, -pos.y, pos.z)
                .rotateX(-(float) Math.toRadians(mc.gameRenderer.getCamera().getPitch()))
                .rotateY(-(float) Math.toRadians(mc.gameRenderer.getCamera().getYaw()))
                .add(mc.gameRenderer.getCamera().getPos());
    }

    public static boolean isOnScreen2d(Vec3d pos) {
        return pos != null && (pos.z > -1 && pos.z < 1);
    }

    public static void drawTexturedQuad(Matrix4f matrices, float x0, float x1, float y0, float y1, float z, float u0, float u1, float v0, float v1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrices, (float)x0, (float)y1, (float)z).texture(u0, v1).next();
        bufferBuilder.vertex(matrices, (float)x1, (float)y1, (float)z).texture(u1, v1).next();
        bufferBuilder.vertex(matrices, (float)x1, (float)y0, (float)z).texture(u1, v0).next();
        bufferBuilder.vertex(matrices, (float)x0, (float)y0, (float)z).texture(u0, v0).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
    }
    public static void drawTexture(MatrixStack matrices, float x, float y, float u, float v, float width, float height, int textureWidth, int textureHeight) {
        drawTexture(matrices, x, y, width, height, u, v, width, height, textureWidth, textureHeight);
    }

    public static void drawTexture(MatrixStack matrices, float x, float y, float width, float height, float u, float v, float regionWidth, float regionHeight, float textureWidth, float textureHeight) {
        drawTexture(matrices, x, x + width, y, y + height, 0, regionWidth, regionHeight, u, v, textureWidth, textureHeight);
    }

    public static void drawTexture(MatrixStack matrices, float x0, float y0, float x1, float y1, int z, float regionWidth, float regionHeight, float u, float v, float textureWidth, float textureHeight) {
        drawTexturedQuad(matrices.peek().getPositionMatrix(), x0, y0, x1, y1, z, (u + 0.0F) / (float)textureWidth, (u + (float)regionWidth) / (float)textureWidth, (v + 0.0F) / (float)textureHeight, (v + (float)regionHeight) / (float)textureHeight);
    }

    public static void drawFace(MatrixStack matrixStack, float x, float y, int renderScale, Identifier id) {
        try {
            bindTexture(id);
            drawTexture(matrixStack, x, y, 8 * renderScale, 8 * renderScale, 8 * renderScale, 8 * renderScale, 8 * renderScale, 8 * renderScale, 64 * renderScale, 64 * renderScale);
            drawTexture(matrixStack, x, y, 8 * renderScale, 8 * renderScale, 40 * renderScale, 8 * renderScale, 8 * renderScale, 8 * renderScale, 64 * renderScale, 64 * renderScale);
        }catch (Exception e){}
    }
}
