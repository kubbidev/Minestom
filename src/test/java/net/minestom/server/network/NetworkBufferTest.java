package net.minestom.server.network;

import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NetworkBufferTest {

    @Test
    public void readableBytes() {
        var buffer = new NetworkBuffer();
        assertEquals(0, buffer.readableBytes());

        buffer.write(NetworkBuffer.BYTE, (byte) 0);
        assertEquals(1, buffer.readableBytes());

        buffer.write(NetworkBuffer.LONG, 50L);
        assertEquals(9, buffer.readableBytes());

        assertEquals((byte) 0, buffer.read(NetworkBuffer.BYTE));
        assertEquals(8, buffer.readableBytes());

        assertEquals(50L, buffer.read(NetworkBuffer.LONG));
        assertEquals(0, buffer.readableBytes());
    }

    @Test
    public void numbers() {
        assertBufferType(NetworkBuffer.BOOLEAN, false, new byte[]{0x00});
        assertBufferType(NetworkBuffer.BOOLEAN, true, new byte[]{0x01});

        assertBufferType(NetworkBuffer.BYTE, (byte) 0x00, new byte[]{0x00});
        assertBufferType(NetworkBuffer.BYTE, (byte) 0x01, new byte[]{0x01});
        assertBufferType(NetworkBuffer.BYTE, (byte) 0x7F, new byte[]{0x7F});
        assertBufferType(NetworkBuffer.BYTE, (byte) 0x80, new byte[]{(byte) 0x80});
        assertBufferType(NetworkBuffer.BYTE, (byte) 0xFF, new byte[]{(byte) 0xFF});

        assertBufferType(NetworkBuffer.SHORT, (short) 0x0000, new byte[]{0x00, 0x00});
        assertBufferType(NetworkBuffer.SHORT, (short) 0x0001, new byte[]{0x00, 0x01});
        assertBufferType(NetworkBuffer.SHORT, (short) 0x7FFF, new byte[]{0x7F, (byte) 0xFF});
        assertBufferType(NetworkBuffer.SHORT, (short) 0x8000, new byte[]{(byte) 0x80, 0x00});
        assertBufferType(NetworkBuffer.SHORT, (short) 0xFFFF, new byte[]{(byte) 0xFF, (byte) 0xFF});

        assertBufferType(NetworkBuffer.INT, 0, new byte[]{0x00, 0x00, 0x00, 0x00});
        assertBufferType(NetworkBuffer.INT, 1, new byte[]{0x00, 0x00, 0x00, 0x01});
        assertBufferType(NetworkBuffer.INT, 2, new byte[]{0x00, 0x00, 0x00, 0x02});
        assertBufferType(NetworkBuffer.INT, 127, new byte[]{0x00, 0x00, 0x00, 0x7F});
        assertBufferType(NetworkBuffer.INT, 128, new byte[]{0x00, 0x00, 0x00, (byte) 0x80});
        assertBufferType(NetworkBuffer.INT, 255, new byte[]{0x00, 0x00, 0x00, (byte) 0xFF});
        assertBufferType(NetworkBuffer.INT, 256, new byte[]{0x00, 0x00, 0x01, 0x00});
        assertBufferType(NetworkBuffer.INT, 25565, new byte[]{0x00, 0x00, 0x63, (byte) 0xDD});
        assertBufferType(NetworkBuffer.INT, 32767, new byte[]{0x00, 0x00, 0x7F, (byte) 0xFF});
        assertBufferType(NetworkBuffer.INT, 32768, new byte[]{0x00, 0x00, (byte) 0x80, 0x00});
        assertBufferType(NetworkBuffer.INT, 65535, new byte[]{0x00, 0x00, (byte) 0xFF, (byte) 0xFF});
        assertBufferType(NetworkBuffer.INT, 65536, new byte[]{0x00, 0x01, 0x00, 0x00});
        assertBufferType(NetworkBuffer.INT, 2147483647, new byte[]{0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
        assertBufferType(NetworkBuffer.INT, -1, new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
        assertBufferType(NetworkBuffer.INT, -2147483648, new byte[]{(byte) 0x80, 0x00, 0x00, 0x00});

        assertBufferType(NetworkBuffer.LONG, 0L, new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
        assertBufferType(NetworkBuffer.LONG, 1L, new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01});
        assertBufferType(NetworkBuffer.LONG, 2L, new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02});
        assertBufferType(NetworkBuffer.LONG, 127L, new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x7F});
        assertBufferType(NetworkBuffer.LONG, 128L, new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0x80});
        assertBufferType(NetworkBuffer.LONG, 255L, new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xFF});
        assertBufferType(NetworkBuffer.LONG, 256L, new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00});
        assertBufferType(NetworkBuffer.LONG, 25565L, new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x63, (byte) 0xDD});
        assertBufferType(NetworkBuffer.LONG, 32767L, new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x7F, (byte) 0xFF});
        assertBufferType(NetworkBuffer.LONG, 32768L, new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0x80, 0x00});
        assertBufferType(NetworkBuffer.LONG, 65535L, new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF});
        assertBufferType(NetworkBuffer.LONG, 65536L, new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00});
        assertBufferType(NetworkBuffer.LONG, 2147483647L, new byte[]{0x00, 0x00, 0x00, 0x00, 0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
        assertBufferType(NetworkBuffer.LONG, 2147483648L, new byte[]{0x00, 0x00, 0x00, 0x00, (byte) 0x80, 0x00, 0x00, 0x00});
        assertBufferType(NetworkBuffer.LONG, 4294967295L, new byte[]{0x00, 0x00, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
        assertBufferType(NetworkBuffer.LONG, 4294967296L, new byte[]{0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00});
        assertBufferType(NetworkBuffer.LONG, 9223372036854775807L, new byte[]{0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
        assertBufferType(NetworkBuffer.LONG, -1L, new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
        assertBufferType(NetworkBuffer.LONG, -2147483648L, new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x80, 0x00, 0x00, 0x00});
        assertBufferType(NetworkBuffer.LONG, -4294967296L, new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x00, 0x00, 0x00, 0x00});
        assertBufferType(NetworkBuffer.LONG, -9223372036854775808L, new byte[]{(byte) 0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});

        assertBufferType(NetworkBuffer.FLOAT, 0f, new byte[]{0x00, 0x00, 0x00, 0x00});
        assertBufferType(NetworkBuffer.FLOAT, 1f, new byte[]{0x3F, (byte) 0x80, 0x00, 0x00});
        assertBufferType(NetworkBuffer.FLOAT, 1.1f, new byte[]{0x3F, (byte) 0x8C, (byte) 0xCC, (byte) 0xCD});
        assertBufferType(NetworkBuffer.FLOAT, 1.5f, new byte[]{0x3F, (byte) 0xC0, 0x00, 0x00});
        assertBufferType(NetworkBuffer.FLOAT, 1.6f, new byte[]{0x3F, (byte) 0xCC, (byte) 0xCC, (byte) 0xCD});
        assertBufferType(NetworkBuffer.FLOAT, 2f, new byte[]{0x40, 0x00, 0x00, 0x00});
        assertBufferType(NetworkBuffer.FLOAT, 2.5f, new byte[]{0x40, 0x20, 0x00, 0x00});
        assertBufferType(NetworkBuffer.FLOAT, 3f, new byte[]{0x40, 0x40, 0x00, 0x00});
        assertBufferType(NetworkBuffer.FLOAT, 4f, new byte[]{0x40, (byte) 0x80, 0x00, 0x00});
        assertBufferType(NetworkBuffer.FLOAT, 5f, new byte[]{0x40, (byte) 0xA0, 0x00, 0x00});
        assertBufferType(NetworkBuffer.FLOAT, 10f, new byte[]{0x41, 0x20, 0x00, 0x00});
        assertBufferType(NetworkBuffer.FLOAT, 100f, new byte[]{0x42, (byte) 0xC8, 0x00, 0x00});
        assertBufferType(NetworkBuffer.FLOAT, 1000f, new byte[]{0x44, 0x7a, 0x00, 0x00});
        assertBufferType(NetworkBuffer.FLOAT, 10000f, new byte[]{0x46, 0x1C, 0x40, 0x00});
        assertBufferType(NetworkBuffer.FLOAT, 100000f, new byte[]{0x47, (byte) 0xC3, 0x50, 0x00});

        assertBufferType(NetworkBuffer.DOUBLE, 0d, new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
        assertBufferType(NetworkBuffer.DOUBLE, 1d, new byte[]{0x3F, (byte) 0xF0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
        assertBufferType(NetworkBuffer.DOUBLE, 1.1d, new byte[]{0x3F, (byte) 0xF1, (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x9A});
        assertBufferType(NetworkBuffer.DOUBLE, 1.5d, new byte[]{0x3F, (byte) 0xF8, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
        assertBufferType(NetworkBuffer.DOUBLE, 1.6d, new byte[]{0x3F, (byte) 0xF9, (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x9A});
        assertBufferType(NetworkBuffer.DOUBLE, 2d, new byte[]{0x40, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
        assertBufferType(NetworkBuffer.DOUBLE, 2.5d, new byte[]{0x40, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
        assertBufferType(NetworkBuffer.DOUBLE, 3d, new byte[]{0x40, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
        assertBufferType(NetworkBuffer.DOUBLE, 4d, new byte[]{0x40, 0x10, (byte) 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
        assertBufferType(NetworkBuffer.DOUBLE, 5d, new byte[]{0x40, 0x14, (byte) 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
        assertBufferType(NetworkBuffer.DOUBLE, 10d, new byte[]{0x40, 0x24, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
        assertBufferType(NetworkBuffer.DOUBLE, 100d, new byte[]{0x40, 0x59, (byte) 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
        assertBufferType(NetworkBuffer.DOUBLE, 1000d, new byte[]{0x40, (byte) 0x8F, 0x40, 0x00, 0x00, 0x00, 0x00, 0x00});
        assertBufferType(NetworkBuffer.DOUBLE, 10000d, new byte[]{0x40, (byte) 0xC3, (byte) 0x88, 0x00, 0x00, 0x00, 0x00, 0x00});
    }

    @Test
    public void varInt() {
        assertBufferType(NetworkBuffer.VAR_INT, 0, new byte[]{0});
        assertBufferType(NetworkBuffer.VAR_INT, 1, new byte[]{0x01});
        assertBufferType(NetworkBuffer.VAR_INT, 2, new byte[]{0x02});
        assertBufferType(NetworkBuffer.VAR_INT, 11, new byte[]{0x0B});
        assertBufferType(NetworkBuffer.VAR_INT, 127, new byte[]{0x7f});
        assertBufferType(NetworkBuffer.VAR_INT, 128, new byte[]{(byte) 0x80, 0x01});
        assertBufferType(NetworkBuffer.VAR_INT, 255, new byte[]{(byte) 0xff, 0x01});
        assertBufferType(NetworkBuffer.VAR_INT, 25565, new byte[]{(byte) 0xdd, (byte) 0xc7, 0x01});
        assertBufferType(NetworkBuffer.VAR_INT, 2097151, new byte[]{(byte) 0xff, (byte) 0xff, 0x7f});
        assertBufferType(NetworkBuffer.VAR_INT, 2147483647, new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x07});
        assertBufferType(NetworkBuffer.VAR_INT, -1, new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x0f});
        assertBufferType(NetworkBuffer.VAR_INT, -2147483648, new byte[]{(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, 0x08});
    }

    @Test
    public void varLong() {
        assertBufferType(NetworkBuffer.VAR_LONG, 0L, new byte[]{0});
        assertBufferType(NetworkBuffer.VAR_LONG, 1L, new byte[]{0x01});
        assertBufferType(NetworkBuffer.VAR_LONG, 2L, new byte[]{0x02});
        assertBufferType(NetworkBuffer.VAR_LONG, 127L, new byte[]{0x7f});
        assertBufferType(NetworkBuffer.VAR_LONG, 128L, new byte[]{(byte) 0x80, 0x01});
        assertBufferType(NetworkBuffer.VAR_LONG, 255L, new byte[]{(byte) 0xff, 0x01});
        assertBufferType(NetworkBuffer.VAR_LONG, 2147483647L, new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x07});
        assertBufferType(NetworkBuffer.VAR_LONG, 9223372036854775807L, new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x7f});
        assertBufferType(NetworkBuffer.VAR_LONG, -1L, new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x01});
        assertBufferType(NetworkBuffer.VAR_LONG, -2147483648L, new byte[]{(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0xf8, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x01});
        assertBufferType(NetworkBuffer.VAR_LONG, -9223372036854775808L, new byte[]{(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, 0x01});
    }

    @Test
    public void rawBytes() {
        // FIXME: currently break because the array is identity compared
        //assertBufferType(NetworkBuffer.RAW_BYTES, new byte[]{0x0B, 0x48, 0x65, 0x6c, 0x6c, 0x6f, 0x20, 0x57, 0x6f, 0x72, 0x6c, 0x64},
        //      new byte[]{0x0B, 0x48, 0x65, 0x6c, 0x6c, 0x6f, 0x20, 0x57, 0x6f, 0x72, 0x6c, 0x64});
    }

    @Test
    public void string() {
        assertBufferType(NetworkBuffer.STRING, "Hello World", new byte[]{0x0B, 0x48, 0x65, 0x6c, 0x6c, 0x6f, 0x20, 0x57, 0x6f, 0x72, 0x6c, 0x64});
    }

    @Test
    public void nbt() {
        assertBufferType(NetworkBuffer.NBT, NBT.Int(5));
        assertBufferType(NetworkBuffer.NBT, NBT.Compound(Map.of("key", NBT.Int(5))));
    }

    @Test
    public void component() {
        assertBufferType(NetworkBuffer.COMPONENT, Component.text("Hello world"));
    }

    @Test
    public void uuid() {
        assertBufferType(NetworkBuffer.UUID, new UUID(0, 0), new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        assertBufferType(NetworkBuffer.UUID, new UUID(1, 1), new byte[]{0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1});
    }

    @Test
    public void item() {
        assertBufferType(NetworkBuffer.ITEM, ItemStack.AIR);
        assertBufferType(NetworkBuffer.ITEM, ItemStack.of(Material.STONE, 1));
        assertBufferType(NetworkBuffer.ITEM, ItemStack.of(Material.DIAMOND_AXE, 1).withMeta(builder -> builder.damage(1)));
    }

    @Test
    public void optional() {
        assertBufferTypeOptional(NetworkBuffer.BOOLEAN, null, new byte[]{0});
        assertBufferTypeOptional(NetworkBuffer.BOOLEAN, true, new byte[]{1, 1});
    }

    @Test
    public void collection() {
        assertBufferTypeCollection(NetworkBuffer.BOOLEAN, List.of(), new byte[]{0});
        assertBufferTypeCollection(NetworkBuffer.BOOLEAN, List.of(true), new byte[]{0x01, 0x01});
    }

    static <T> void assertBufferType(NetworkBuffer.@NotNull Type<T> type, @UnknownNullability T value, byte[] expected, @NotNull Action<T> action) {
        var buffer = new NetworkBuffer();
        action.write(buffer, type, value);
        assertEquals(0, buffer.readIndex());
        if (expected != null) assertEquals(expected.length, buffer.writeIndex());

        var actual = action.read(buffer, type);

        assertEquals(value, actual);
        if (expected != null) assertEquals(expected.length, buffer.readIndex(), "Invalid read index");
        if (expected != null) assertEquals(expected.length, buffer.writeIndex());

        if (expected != null) {
            var bytes = new byte[expected.length];
            buffer.copyTo(0, bytes, 0, bytes.length);
            assertArrayEquals(expected, bytes, "Invalid bytes: " + Arrays.toString(expected) + " != " + Arrays.toString(bytes));
        }
    }

    static <T> void assertBufferType(NetworkBuffer.@NotNull Type<T> type, @NotNull T value, byte @Nullable [] expected) {
        assertBufferType(type, value, expected, new Action<>() {
            @Override
            public void write(@NotNull NetworkBuffer buffer, @NotNull NetworkBuffer.Type<T> type, @UnknownNullability T value) {
                buffer.write(type, value);
            }

            @Override
            public T read(@NotNull NetworkBuffer buffer, @NotNull NetworkBuffer.Type<T> type) {
                return buffer.read(type);
            }
        });
    }

    static <T> void assertBufferType(NetworkBuffer.@NotNull Type<T> type, @NotNull T value) {
        assertBufferType(type, value, null);
    }

    static <T> void assertBufferTypeOptional(NetworkBuffer.@NotNull Type<T> type, @Nullable T value, byte @Nullable [] expected) {
        assertBufferType(type, value, expected, new Action<T>() {
            @Override
            public void write(@NotNull NetworkBuffer buffer, @NotNull NetworkBuffer.Type<T> type, @UnknownNullability T value) {
                buffer.writeOptional(type, value);
            }

            @Override
            public T read(@NotNull NetworkBuffer buffer, @NotNull NetworkBuffer.Type<T> type) {
                return buffer.readOptional(type);
            }
        });
    }

    static <T> void assertBufferTypeOptional(NetworkBuffer.@NotNull Type<T> type, @Nullable T value) {
        assertBufferTypeOptional(type, value, null);
    }

    static <T> void assertBufferTypeCollection(NetworkBuffer.@NotNull Type<T> type, @NotNull Collection<T> values, byte @Nullable [] expected) {
        var buffer = new NetworkBuffer();
        buffer.writeCollection(type, values);
        assertEquals(0, buffer.readIndex());
        if (expected != null) assertEquals(expected.length, buffer.writeIndex());

        var actual = buffer.readCollection(type);

        assertEquals(values, actual);
        if (expected != null) assertEquals(expected.length, buffer.readIndex());
        if (expected != null) assertEquals(expected.length, buffer.writeIndex());

        if (expected != null) {
            var bytes = new byte[expected.length];
            buffer.copyTo(0, bytes, 0, bytes.length);
            assertArrayEquals(expected, bytes, "Invalid bytes: " + Arrays.toString(expected) + " != " + Arrays.toString(bytes));
        }
    }

    static <T> void assertBufferTypeCollection(NetworkBuffer.@NotNull Type<T> type, @NotNull Collection<T> value) {
        assertBufferTypeCollection(type, value, null);
    }

    interface Action<T> {
        void write(@NotNull NetworkBuffer buffer, NetworkBuffer.@NotNull Type<T> type, @UnknownNullability T value);

        T read(@NotNull NetworkBuffer buffer, NetworkBuffer.@NotNull Type<T> type);
    }
}
