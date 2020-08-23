package com.elytradev.davincisvessels.movingworld.client.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.elytradev.davincisvessels.DavincisVesselsMod;
import com.elytradev.davincisvessels.movingworld.common.chunk.mobilechunk.MobileChunk;
import com.elytradev.davincisvessels.movingworld.common.chunk.mobilechunk.MobileChunkClient;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MobileChunkRenderer {
    /**
     * Boolean for whether this renderer needs to be updated or not
     */
    public boolean needsUpdate;
    public boolean isRemoved;
    public LegacyRender legacyRender = new LegacyRender();
    public VBORender vboRender = new VBORender();
    private boolean usingVBOs = useVBO();
    private MobileChunkClient chunk;


    public MobileChunkRenderer(MobileChunk mobilechunk) {
        chunk = (MobileChunkClient) mobilechunk;
        needsUpdate = true;
    }

    public boolean useVBO() {
        return OpenGlHelper.useVbo() && !FMLClientHandler.instance().hasOptifine();
    }

    public void render(float partialTicks) {
        try {
            if (this.usingVBOs != useVBO()) {
                this.usingVBOs = useVBO();
                // Remove the old render.
                if (this.usingVBOs) {
                    this.legacyRender.remove();
                } else {
                    this.vboRender.remove();
                }
                this.needsUpdate = true;
            }

            if (isRemoved) {
                this.vboRender.remove();
                this.legacyRender.remove();

                return;
            }

            if (this.needsUpdate) {
                this.vboRender.compile();
                this.legacyRender.compile();
                this.needsUpdate = false;
            }

            if (this.usingVBOs) {
                this.vboRender.render();
            } else {
                this.legacyRender.render();
            }

            // Tiles always render in the same way.
            renderTiles(partialTicks);
        } catch (Exception e) {
        	DavincisVesselsMod.LOG.error("Exception when rendering a MobileChunk! {}", e.getMessage());
            e.printStackTrace();

            try {
                Tessellator.getInstance().getBuffer().finishDrawing();
            } catch (Exception e2) {
            }
            return;
        }
    }

    private void renderTiles(float partialTicks) {
        GlStateManager.pushMatrix();
        TileEntityRendererDispatcher dispatcher = TileEntityRendererDispatcher.instance;
        World tesrDispatchWorld = dispatcher.world;
        dispatcher.setWorld(chunk.getFakeWorld());
        for (Map.Entry<BlockPos, TileEntity> blockPosTileEntityEntry : chunk.normalTESRS.entrySet()) {
            TileEntity tile = blockPosTileEntityEntry.getValue();
            tile.setWorld(chunk.getFakeWorld());
            TileEntitySpecialRenderer renderer = dispatcher.getRenderer(tile);
            if (renderer != null && tile.shouldRenderInPass(MinecraftForgeClient.getRenderPass())) {
                dispatcher.render(tile, tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), partialTicks);
            }
            tile.setWorld(chunk.world);
        }
        dispatcher.setWorld(tesrDispatchWorld);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        dispatcher.preDrawBatch();
        for (Map.Entry<BlockPos, TileEntity> blockPosTileEntityEntry : chunk.fastTESRS.entrySet()) {
            TileEntity tile = blockPosTileEntityEntry.getValue();
            tile.setWorld(chunk.getFakeWorld());
            TileEntitySpecialRenderer renderer = dispatcher.getRenderer(tile);
            if (renderer != null && tile.shouldRenderInPass(MinecraftForgeClient.getRenderPass())) {
                dispatcher.render(tile, tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), partialTicks);
            }
            tile.setWorld(chunk.world);
        }
        dispatcher.drawBatch(0);
        GlStateManager.popMatrix();
    }

    public void dispatchBlockRender(BlockPos blockPos, IBlockState blockState, BufferBuilder buffer) {
        buffer.color(1.0F, 1.0F, 1.0F, 1.0F);
        BlockRendererDispatcher blockRendererDispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        blockRendererDispatcher.renderBlock(blockState, blockPos, chunk.getFakeWorld(), buffer);
    }

    public void markDirty() {
        needsUpdate = true;
    }

    public void markRemoved() {
        isRemoved = true;
    }

    public class LegacyRender {

        private int displayList = -1;

        public void compile() {
            this.displayList = GLAllocation.generateDisplayLists(1);
            GlStateManager.glNewList(this.displayList, GL11.GL_COMPILE);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();

            RenderHelper.disableStandardItemLighting();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            //GlStateManager.enableBlend();
            GlStateManager.enableCull();

            if (Minecraft.isAmbientOcclusionEnabled()) {
                GlStateManager.shadeModel(GL11.GL_SMOOTH);
            } else {
                GlStateManager.shadeModel(GL11.GL_FLAT);
            }

            HashMap<BlockRenderLayer, List<Tuple<BlockPos, IBlockState>>> blockRenderMap = Maps.newHashMap();
            for (BlockRenderLayer blockRenderLayer : BlockRenderLayer.values()) {
                blockRenderMap.put(blockRenderLayer, new ArrayList<>());
            }
            // Collect block states.
            for (int y = chunk.minY(); y < chunk.maxY(); ++y) {
                for (int z = chunk.minZ(); z < chunk.maxZ(); ++z) {
                    for (int x = chunk.minX(); x < chunk.maxX(); ++x) {
                        BlockPos pos = new BlockPos(x, y, z);
                        IBlockState blockState = chunk.getBlockState(pos);
                        Block block = blockState.getBlock();

                        for (BlockRenderLayer blockRenderLayer : BlockRenderLayer.values()) {
                            if (!block.canRenderInLayer(blockState, blockRenderLayer)
                                    || blockState.getRenderType().equals(EnumBlockRenderType.INVISIBLE)) continue;

                            blockRenderMap.get(blockRenderLayer).add(new Tuple<>(pos, blockState));
                        }
                    }
                }
            }

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
            // Actually render.
            for (BlockRenderLayer blockRenderLayer : BlockRenderLayer.values()) {
            	
            	if(blockRenderLayer == BlockRenderLayer.TRANSLUCENT) {
            		GlStateManager.enableBlend();
            	}
            	
                for (Tuple<BlockPos, IBlockState> blockRenderData : blockRenderMap.get(blockRenderLayer)) {
                    dispatchBlockRender(blockRenderData.getFirst(), blockRenderData.getSecond(), buffer);
                }
                
            	if(blockRenderLayer == BlockRenderLayer.TRANSLUCENT) {
            		GlStateManager.disableBlend();
            	}

            }

            buffer.setTranslation(0.0D, 0.0D, 0.0D);
            tessellator.draw();
            //GlStateManager.disableBlend();
            GlStateManager.disableCull();
            RenderHelper.enableStandardItemLighting();

            GlStateManager.glEndList();
        }

        public void remove() {
            if (displayList >= 0)
                GlStateManager.glDeleteLists(displayList, 1);
        }

        public void render() {
            // Just a sanity check so we don't call nothing and cause bad things to happen.
            if (displayList >= 0)
                GlStateManager.callList(this.displayList);
        }

    }

    public class VBORender {
        private final VertexBuffer[] vertexBuffers = new VertexBuffer[BlockRenderLayer.values().length];

        public void compile() {
            remove();

            HashMap<BlockRenderLayer, List<Tuple<BlockPos, IBlockState>>> blockRenderMap = Maps.newHashMap();
            for (BlockRenderLayer blockRenderLayer : BlockRenderLayer.values()) {
                blockRenderMap.put(blockRenderLayer, new ArrayList<>());
            }
            // Collect block states.
            for (int y = chunk.minY(); y < chunk.maxY(); ++y) {
                for (int z = chunk.minZ(); z < chunk.maxZ(); ++z) {
                    for (int x = chunk.minX(); x < chunk.maxX(); ++x) {
                        BlockPos pos = new BlockPos(x, y, z);
                        IBlockState blockState = chunk.getBlockState(pos);
                        Block block = blockState.getBlock();

                        for (BlockRenderLayer blockRenderLayer : BlockRenderLayer.values()) {
                            if (!block.canRenderInLayer(blockState, blockRenderLayer)
                                    || blockState.getRenderType().equals(EnumBlockRenderType.INVISIBLE)) continue;

                            blockRenderMap.get(blockRenderLayer).add(new Tuple<>(pos, blockState));
                        }
                    }
                }
            }

            for (int i = 0; i < BlockRenderLayer.values().length; ++i) {
                BlockRenderLayer renderLayer = BlockRenderLayer.values()[i];
                this.vertexBuffers[i] = new VertexBuffer(DefaultVertexFormats.BLOCK);

                List<Tuple<BlockPos, IBlockState>> data = blockRenderMap.get(renderLayer);
                BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

                BlockRendererDispatcher blockDispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
                bufferBuilder.begin(7, DefaultVertexFormats.BLOCK);
                for (Tuple<BlockPos, IBlockState> datum : data) {
                    bufferBuilder.color(1.0F, 1.0F, 1.0F, 1.0F);
                    blockDispatcher.renderBlock(datum.getSecond(), datum.getFirst(), chunk.getFakeWorld(), bufferBuilder);
                }
                bufferBuilder.sortVertexData((float) TileEntityRendererDispatcher.staticPlayerX,
                        (float) TileEntityRendererDispatcher.staticPlayerY,
                        (float) TileEntityRendererDispatcher.staticPlayerZ);
                bufferBuilder.finishDrawing();
                vertexBuffers[i].bufferData(bufferBuilder.getByteBuffer());
                bufferBuilder.reset();
            }
        }

        private void renderLayer(BlockRenderLayer layer) {
            VertexBuffer vbo = vertexBuffers[layer.ordinal()];
            if (vbo == null)
                return;

            GlStateManager.pushMatrix();

            GlStateManager.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.glEnableClientState(GL11.GL_COLOR_ARRAY);

            GlStateManager.pushMatrix();
            vbo.bindBuffer();
            setupArrayPointers();
            vbo.drawArrays(GL11.GL_QUADS);
            GlStateManager.popMatrix();
            OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
            GlStateManager.resetColor();

            for (VertexFormatElement vertexformatelement : DefaultVertexFormats.BLOCK.getElements()) {
                VertexFormatElement.EnumUsage enumusage = vertexformatelement.getUsage();
                int i = vertexformatelement.getIndex();

                switch (enumusage) {
                    case POSITION:
                        GlStateManager.glDisableClientState(GL11.GL_VERTEX_ARRAY);
                        break;
                    case UV:
                        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + i);
                        GlStateManager.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                        break;
                    case COLOR:
                        GlStateManager.glDisableClientState(GL11.GL_COLOR_ARRAY);
                        GlStateManager.resetColor();
					default:
						break;
                }
            }
            GlStateManager.popMatrix();
        }

        private void setupArrayPointers() {
            GlStateManager.glVertexPointer(3, GL11.GL_FLOAT, 28, 0);
            GlStateManager.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 28, 12);
            GlStateManager.glTexCoordPointer(2, GL11.GL_FLOAT, 28, 16);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.glTexCoordPointer(2, GL11.GL_SHORT, 28, 24);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
        }

        public void remove() {
            for (int i = 0; i < BlockRenderLayer.values().length; ++i) {
                if (vertexBuffers[i] != null) {
                    vertexBuffers[i].deleteGlBuffers();
                }
            }
        }

        public void render() {
            GlStateManager.pushMatrix();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableBlend();
            GlStateManager.enableCull();

            if (Minecraft.isAmbientOcclusionEnabled()) {
                GlStateManager.shadeModel(GL11.GL_SMOOTH);
            } else {
                GlStateManager.shadeModel(GL11.GL_FLAT);
            }
            for (int i = 0; i < BlockRenderLayer.values().length; ++i) {
                renderLayer(BlockRenderLayer.values()[i]);
            }
            GlStateManager.disableBlend();
            GlStateManager.disableCull();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.popMatrix();
        }
    }

}
