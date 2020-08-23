package com.elytradev.davincisvessels.client;

import java.util.Map;

import com.elytradev.davincisvessels.ModConstants;
import com.elytradev.davincisvessels.client.control.ShipKeyHandler;
import com.elytradev.davincisvessels.client.handler.ClientHookContainer;
import com.elytradev.davincisvessels.client.render.RenderParachute;
import com.elytradev.davincisvessels.client.render.RenderSeat;
import com.elytradev.davincisvessels.client.render.TileEntityGaugeRenderer;
import com.elytradev.davincisvessels.client.render.TileEntityHelmRenderer;
import com.elytradev.davincisvessels.common.CommonProxy;
import com.elytradev.davincisvessels.common.DavincisVesselsConfig;
import com.elytradev.davincisvessels.common.entity.EntityParachute;
import com.elytradev.davincisvessels.common.entity.EntitySeat;
import com.elytradev.davincisvessels.common.entity.EntityShip;
import com.elytradev.davincisvessels.common.object.DavincisVesselsObjects;
import com.elytradev.davincisvessels.common.tileentity.TileGauge;
import com.elytradev.davincisvessels.common.tileentity.TileHelm;
import com.elytradev.davincisvessels.movingworld.client.render.RenderMovingWorld;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {

	public ShipKeyHandler shipKeyHandler;
	public DavincisVesselsConfig syncedConfig;

	@Override
	public ClientHookContainer getHookContainer() {
		return new ClientHookContainer();
	}

	@Override
	public void registerKeyHandlers(DavincisVesselsConfig cfg) {
		shipKeyHandler = new ShipKeyHandler(cfg);
		MinecraftForge.EVENT_BUS.register(shipKeyHandler);
	}

	@Override
	public void registerRenderers(LoaderState.ModState state) {
		if (state == LoaderState.ModState.PREINITIALIZED) {
			registerEntityRenderers();
		}

		if (state == LoaderState.ModState.INITIALIZED) {
			registerTileRenderers();
			registerStandardItemRenders();
		}
	}

	public void registerEntityRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityShip.class, RenderMovingWorld::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityParachute.class, RenderParachute::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySeat.class, RenderSeat::new);
	}

	public void registerTileRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileGauge.class, new TileEntityGaugeRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileHelm.class, new TileEntityHelmRenderer());
	}

	public void registerVariantItemRenders() {
		Item itemToRegister;
		ModelResourceLocation modelResourceLocation;

		// Some specific meta registrations for OBJECTS, like for extended gauges.
		itemToRegister = Item.getItemFromBlock(DavincisVesselsObjects.blockGauge);
		modelResourceLocation = new ModelResourceLocation(ModConstants.RESOURCE_DOMAIN + "gauge", "inventory");
		ModelLoader.setCustomModelResourceLocation(itemToRegister, 0, modelResourceLocation);
		modelResourceLocation = new ModelResourceLocation(ModConstants.RESOURCE_DOMAIN + "gauge_ext", "inventory");
		ModelLoader.setCustomModelResourceLocation(itemToRegister, 1, modelResourceLocation);

		itemToRegister = Item.getItemFromBlock(DavincisVesselsObjects.blockBalloon);

		for (EnumDyeColor color : EnumDyeColor.values()) {
			int i = color.getMetadata();
			modelResourceLocation = new ModelResourceLocation(ModConstants.RESOURCE_DOMAIN + "balloon_" + color.getName(), "inventory");
			ModelLoader.setCustomModelResourceLocation(itemToRegister, i, modelResourceLocation);
		}
	}

	public void registerStandardItemRenders() {
		Item itemToRegister = null;
		ModelResourceLocation modelResourceLocation = null;

		// Do some general render registrations for OBJECTS, not considering meta.
		ItemModelMesher modelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
		for (Map.Entry<String, Block> entry : DavincisVesselsObjects.registeredBlocks.entrySet()) {
			if (DavincisVesselsObjects.skipMesh.contains(entry.getKey()))
				continue;

			modelResourceLocation = new ModelResourceLocation(ModConstants.RESOURCE_DOMAIN + entry.getKey(), "inventory");
			itemToRegister = Item.getItemFromBlock(entry.getValue());

			modelMesher.register(itemToRegister, 0, modelResourceLocation);
		}

		for (Map.Entry<String, Item> entry : DavincisVesselsObjects.registeredItems.entrySet()) {
			if (DavincisVesselsObjects.skipMesh.contains(entry.getKey()))
				continue;

			modelResourceLocation = new ModelResourceLocation(ModConstants.RESOURCE_DOMAIN + entry.getKey(), "inventory");
			itemToRegister = entry.getValue();

			modelMesher.register(itemToRegister, 0, modelResourceLocation);
		}
	}

	@SubscribeEvent
	public void onModelRegistryEvent(ModelRegistryEvent e) {
		registerVariantItemRenders();
	}

	@Override
	public World getWorld(int id) {
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			return Minecraft.getMinecraft().world;
		}

		return super.getWorld(id);
	}

}
