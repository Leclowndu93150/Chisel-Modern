package com.leclowndu93150.chisel.client.render.ctx;

import com.leclowndu93150.chisel.client.render.texture.ChiselTextureAlterR;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import team.chisel.ctm.client.texture.ctx.TextureContextPosition;

import java.util.Random;

public class BlockRenderContextAlterR extends TextureContextPosition {
    private static final Random rand = new Random();

    private final int texture;

    public BlockRenderContextAlterR(BlockPos pos, ChiselTextureAlterR tex) {
        super(pos);

        int num = 0;

        rand.setSeed(Mth.getSeed(pos));
        rand.nextBoolean();

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        num += rand.nextInt(2) * 2;
        boolean type = true;

        // If even, switch boolean
        // If we have a set of multiple coords that are [x, y, z]
        // [5, 8, 9], [0, 0, 1], [9, 8, 8]...

        if (x % 2 == 0) {
            type = !type;
        }
        // Odd Even Odd
        // True False True

        if (y % 2 == 0) {
            type = !type;
        }
        // Even Even Even
        // False True False

        if (z % 2 == 0) {
            type = !type;
        }
        // Odd Odd Even
        // False True True

        num += type ? 0 : 1;

        this.texture = num;
    }

    public int getTexture() {
        return texture;
    }

    @Override
    public long getCompressedData() {
        return texture;
    }
}
