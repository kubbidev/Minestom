package net.minestom.server.entity.metadata.monster;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.MetadataHolder;
import org.jetbrains.annotations.NotNull;

public class CaveSpiderMeta extends SpiderMeta {
    public static final byte OFFSET = SpiderMeta.MAX_OFFSET;
    public static final byte MAX_OFFSET = OFFSET + 0;

    public CaveSpiderMeta(@NotNull Entity entity, @NotNull MetadataHolder metadata) {
        super(entity, metadata);
    }

}
