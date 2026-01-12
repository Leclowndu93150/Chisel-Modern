package com.leclowndu93150.chisel.carving;

import com.leclowndu93150.chisel.api.carving.CarvingUtils;
import com.leclowndu93150.chisel.api.carving.IChiselMode;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import net.minecraft.tags.TagKey;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

/**
 * Enum of built-in chisel modes.
 */
public enum ChiselMode implements IChiselMode {

    SINGLE("Chisel a single block.") {
        @Override
        public Iterable<BlockPos> getCandidates(Player player, BlockPos pos, Direction side) {
            return Collections.singleton(pos);
        }

        @Override
        public AABB getBounds(Direction side) {
            return new AABB(0, 0, 0, 1, 1, 1);
        }
    },

    PANEL("Chisel a 3x3 square of blocks.") {
        private final BlockPos ONE = new BlockPos(1, 1, 1);
        private final BlockPos NEG_ONE = new BlockPos(-1, -1, -1);

        @Override
        public Iterable<BlockPos> getCandidates(Player player, BlockPos pos, Direction side) {
            if (side.getAxisDirection() == AxisDirection.NEGATIVE) {
                side = side.getOpposite();
            }
            Vec3i offset = side.getNormal();
            return filteredIterable(
                    BlockPos.betweenClosedStream(NEG_ONE.offset(offset).offset(pos), ONE.subtract(offset).offset(pos)),
                    player.level(),
                    player.level().getBlockState(pos)
            );
        }

        @Override
        public AABB getBounds(Direction side) {
            return switch (side.getAxis()) {
                case X -> new AABB(0, -1, -1, 1, 2, 2);
                case Y -> new AABB(-1, 0, -1, 2, 1, 2);
                case Z -> new AABB(-1, -1, 0, 2, 2, 1);
            };
        }
    },

    COLUMN("Chisel a 3x1 column of blocks.") {
        @Override
        public Iterable<BlockPos> getCandidates(Player player, BlockPos pos, Direction side) {
            int facing = Mth.floor(player.getYRot() * 4.0F / 360.0F + 0.5D) & 3;
            Set<BlockPos> ret = new LinkedHashSet<>();
            for (int i = -1; i <= 1; i++) {
                if (side != Direction.DOWN && side != Direction.UP) {
                    ret.add(pos.above(i));
                } else {
                    if (facing == 0 || facing == 2) {
                        ret.add(pos.south(i));
                    } else {
                        ret.add(pos.east(i));
                    }
                }
            }
            return filteredIterable(ret.stream(), player.level(), player.level().getBlockState(pos));
        }

        @Override
        public AABB getBounds(Direction side) {
            return PANEL.getBounds(side);
        }

        @Override
        public long[] getCacheState(BlockPos origin, Direction side) {
            return new long[]{origin.asLong(), side.ordinal(), getPlayerDirection()};
        }
    },

    ROW("Chisel a 1x3 row of blocks.") {
        @Override
        public Iterable<BlockPos> getCandidates(Player player, BlockPos pos, Direction side) {
            int facing = Mth.floor(player.getYRot() * 4.0F / 360.0F + 0.5D) & 3;
            Set<BlockPos> ret = new LinkedHashSet<>();
            for (int i = -1; i <= 1; i++) {
                if (side != Direction.DOWN && side != Direction.UP) {
                    if (side == Direction.EAST || side == Direction.WEST) {
                        ret.add(pos.south(i));
                    } else {
                        ret.add(pos.east(i));
                    }
                } else {
                    if (facing == 0 || facing == 2) {
                        ret.add(pos.east(i));
                    } else {
                        ret.add(pos.south(i));
                    }
                }
            }
            return filteredIterable(ret.stream(), player.level(), player.level().getBlockState(pos));
        }

        @Override
        public AABB getBounds(Direction side) {
            return PANEL.getBounds(side);
        }

        @Override
        public long[] getCacheState(BlockPos origin, Direction side) {
            return COLUMN.getCacheState(origin, side);
        }
    },

    CONTIGUOUS("Chisel an area of alike blocks, extending 10 blocks in any direction.") {
        @Override
        public Iterable<? extends BlockPos> getCandidates(Player player, BlockPos pos, Direction side) {
            return () -> getContiguousIterator(pos, player.level(), Direction.values());
        }

        @Override
        public AABB getBounds(Direction side) {
            int r = CONTIGUOUS_RANGE;
            return new AABB(-r - 1, -r - 1, -r - 1, r + 2, r + 2, r + 2);
        }
    },

    CONTIGUOUS_2D("Contiguous (2D)", "Chisel an area of alike blocks, extending 10 blocks along the plane of the current side.") {
        @Override
        public Iterable<? extends BlockPos> getCandidates(Player player, BlockPos pos, Direction side) {
            Direction[] directions = Arrays.stream(Direction.values())
                    .filter(d -> d != side && d != side.getOpposite())
                    .toArray(Direction[]::new);
            return () -> getContiguousIterator(pos, player.level(), directions);
        }

        @Override
        public AABB getBounds(Direction side) {
            int r = CONTIGUOUS_RANGE;
            return switch (side.getAxis()) {
                case X -> new AABB(0, -r - 1, -r - 1, 1, r + 2, r + 2);
                case Y -> new AABB(-r - 1, 0, -r - 1, r + 2, 1, r + 2);
                case Z -> new AABB(-r - 1, -r - 1, 0, r + 2, r + 2, 1);
            };
        }
    };

    public static final int CONTIGUOUS_RANGE = 10;

    private final String description;
    @Nullable
    private final String displayName;

    /**
     * Gets candidates for chiseling with optional fuzzy matching.
     * When fuzzy is enabled, blocks are matched based on being in the same carving group
     * rather than requiring exact state equality.
     *
     * @param player The player
     * @param pos    The position of the targeted block
     * @param side   The side of the block being targeted
     * @param fuzzy  If true, match any block in the same carving group; if false, exact state match
     * @return All valid positions to be chiseled
     */
    public Iterable<? extends BlockPos> getCandidates(Player player, BlockPos pos, Direction side, boolean fuzzy) {
        if (!fuzzy) {
            return getCandidates(player, pos, side);
        }

        Level level = player.level();
        BlockState state = level.getBlockState(pos);

        return switch (this) {
            case SINGLE -> Collections.singleton(pos);
            case PANEL -> {
                Direction effectiveSide = side.getAxisDirection() == AxisDirection.NEGATIVE ? side.getOpposite() : side;
                Vec3i offset = effectiveSide.getNormal();
                BlockPos one = new BlockPos(1, 1, 1);
                BlockPos negOne = new BlockPos(-1, -1, -1);
                yield filteredIterable(
                        BlockPos.betweenClosedStream(negOne.offset(offset).offset(pos), one.subtract(offset).offset(pos)),
                        level, state, true
                );
            }
            case COLUMN -> {
                int facing = Mth.floor(player.getYRot() * 4.0F / 360.0F + 0.5D) & 3;
                Set<BlockPos> ret = new LinkedHashSet<>();
                for (int i = -1; i <= 1; i++) {
                    if (side != Direction.DOWN && side != Direction.UP) {
                        ret.add(pos.above(i));
                    } else {
                        if (facing == 0 || facing == 2) {
                            ret.add(pos.south(i));
                        } else {
                            ret.add(pos.east(i));
                        }
                    }
                }
                yield filteredIterable(ret.stream(), level, state, true);
            }
            case ROW -> {
                int facing = Mth.floor(player.getYRot() * 4.0F / 360.0F + 0.5D) & 3;
                Set<BlockPos> ret = new LinkedHashSet<>();
                for (int i = -1; i <= 1; i++) {
                    if (side != Direction.DOWN && side != Direction.UP) {
                        if (side == Direction.EAST || side == Direction.WEST) {
                            ret.add(pos.south(i));
                        } else {
                            ret.add(pos.east(i));
                        }
                    } else {
                        if (facing == 0 || facing == 2) {
                            ret.add(pos.east(i));
                        } else {
                            ret.add(pos.south(i));
                        }
                    }
                }
                yield filteredIterable(ret.stream(), level, state, true);
            }
            case CONTIGUOUS -> () -> getContiguousIterator(pos, level, Direction.values(), true);
            case CONTIGUOUS_2D -> {
                Direction[] directions = Arrays.stream(Direction.values())
                        .filter(d -> d != side && d != side.getOpposite())
                        .toArray(Direction[]::new);
                yield () -> getContiguousIterator(pos, level, directions, true);
            }
        };
    }

    ChiselMode(String description) {
        this(null, description);
    }

    ChiselMode(@Nullable String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Nullable
    public String getDisplayName() {
        return displayName;
    }

    private static Iterable<BlockPos> filteredIterable(Stream<BlockPos> source, Level world, BlockState state) {
        return filteredIterable(source, world, state, false);
    }

    /**
     * Filters block positions based on state matching.
     *
     * @param source The source stream of positions
     * @param world  The level
     * @param state  The original block state to match against
     * @param fuzzy  If true, matches any block in the same carving group; if false, exact state match only
     */
    private static Iterable<BlockPos> filteredIterable(Stream<BlockPos> source, Level world, BlockState state, boolean fuzzy) {
        if (fuzzy) {
            TagKey<Block> group = CarvingHelper.getCarvingGroup(state);
            if (group == null) {
                return source.filter(p -> world.getBlockState(p) == state)::iterator;
            }
            return source.filter(p -> {
                BlockState otherState = world.getBlockState(p);
                return CarvingHelper.areInSameGroup(state, otherState);
            })::iterator;
        }
        return source.filter(p -> world.getBlockState(p) == state)::iterator;
    }

    private static long getPlayerDirection() {
        if (Minecraft.getInstance().player != null) {
            return Minecraft.getInstance().player.getDirection().ordinal();
        }
        return 0;
    }

    private record Node(BlockPos pos, int distance) {
    }

    private static Iterator<BlockPos> getContiguousIterator(BlockPos origin, Level world, Direction[] directionsToSearch) {
        return getContiguousIterator(origin, world, directionsToSearch, false);
    }

    /**
     * Gets an iterator for contiguous block positions.
     *
     * @param origin             The starting position
     * @param world              The level
     * @param directionsToSearch Directions to search in
     * @param fuzzy              If true, matches any block in the same carving group; if false, exact state match only
     */
    private static Iterator<BlockPos> getContiguousIterator(BlockPos origin, Level world, Direction[] directionsToSearch, boolean fuzzy) {
        final BlockState state = world.getBlockState(origin);
        final TagKey<Block> group = fuzzy ? CarvingHelper.getCarvingGroup(state) : null;
        return new Iterator<>() {
            private final Set<BlockPos> seen = Sets.newHashSet(origin);
            private final Queue<Node> search = new ArrayDeque<>();

            {
                search.add(new Node(origin, 0));
            }

            @Override
            public boolean hasNext() {
                return !search.isEmpty();
            }

            private boolean matchesTarget(BlockState newState) {
                if (fuzzy && group != null) {
                    return CarvingHelper.areInSameGroup(state, newState);
                }
                return newState == state;
            }

            @Override
            public BlockPos next() {
                Node ret = search.poll();
                if (ret != null && ret.distance() < CONTIGUOUS_RANGE) {
                    for (Direction face : directionsToSearch) {
                        BlockPos bp = ret.pos().relative(face);
                        BlockState newState = world.getBlockState(bp);
                        if (!seen.contains(bp) && matchesTarget(newState)) {
                            for (Direction obscureCheck : Direction.values()) {
                                if (!newState.isFaceSturdy(world, bp, obscureCheck.getOpposite(), SupportType.FULL)) {
                                    search.offer(new Node(bp, ret.distance() + 1));
                                    break;
                                }
                            }
                        }
                        seen.add(bp);
                    }
                }
                return ret != null ? ret.pos() : origin;
            }
        };
    }

    /**
     * Register all modes to the mode registry.
     */
    public static void registerAll() {
        for (ChiselMode mode : values()) {
            CarvingUtils.getModeRegistry().registerMode(mode);
        }
    }
}
