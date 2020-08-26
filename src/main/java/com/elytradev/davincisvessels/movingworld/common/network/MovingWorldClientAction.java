package com.elytradev.davincisvessels.movingworld.common.network;

public enum MovingWorldClientAction {
    NONE, ALIGN, DISASSEMBLE;

    public static byte toByte(MovingWorldClientAction action) {
        switch (action) {
            case ALIGN:
                return (byte) 1;
            case DISASSEMBLE:
                return (byte) 2;
            default:
                return (byte) 0;
        }
    }

    public static MovingWorldClientAction fromByte(byte actionInt) {
        switch (actionInt) {
            case 1:
                return ALIGN;
            case 2:
                return DISASSEMBLE;
            default:
                return NONE;
        }
    }

    public byte toByte() {
        return MovingWorldClientAction.toByte(this);
    }
}
