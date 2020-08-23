package com.elytradev.davincisvessels;

public class ModConstants {

    public static final String MOD_ID = "davincisvessels";
    public static final String MOD_VERSION = "1.12.2-9999.9999.9999z";
    public static final String MOD_NAME = "Davinci's Vessels";
    public static final String RESOURCE_DOMAIN = "davincisvessels:";
    public static final String MOD_GUIFACTORY = "com.elytradev.davincisvessels.client.gui.DavincisVesselsGUIFactory";
	
	public static final String OPTAFTER = "after:";
	public static final String OPTBEFORE = "before:";
	public static final String REQAFTER = "required-after:";
	public static final String REQBEFORE = "required-before:";
	public static final String NEXT = ";";
	public static final String AT = "@[";
	public static final String GREATERTHAN = "@(";
	public static final String ORGREATER = ",)";
    
	//Forge
	private static final String FORGE = "forge";
	public static final String FORGE_VER = FORGE + AT + "14.23.5.2768" + ORGREATER;
	
	public static final String DEPENDENCIES
	= REQAFTER + FORGE_VER
	+ NEXT
	+ REQAFTER + com.ferreusveritas.mcf.ModConstants.MODID
	+ NEXT
	+ REQAFTER + com.ferreusveritas.rfrotors.ModConstants.MODID
	+ NEXT
	+ REQAFTER + cofh.thermalexpansion.ThermalExpansion.MOD_ID
	+ NEXT
	+ REQAFTER + cofh.CoFHCore.MOD_ID
	;
	
}
