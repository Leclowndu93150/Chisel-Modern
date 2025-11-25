package com.leclowndu93150.chisel.api.carving;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Registry for chisel modes.
 */
public interface IModeRegistry {

    /**
     * Registers a new chisel mode.
     */
    void registerMode(@Nonnull IChiselMode mode);

    /**
     * Gets all registered modes.
     */
    @Nonnull
    Collection<IChiselMode> getAllModes();

    /**
     * Gets a mode by its name.
     */
    @Nullable
    IChiselMode getModeByName(String name);
}
