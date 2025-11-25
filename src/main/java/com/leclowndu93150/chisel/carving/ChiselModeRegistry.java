package com.leclowndu93150.chisel.carving;

import com.leclowndu93150.chisel.api.carving.IChiselMode;
import com.leclowndu93150.chisel.api.carving.IModeRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry for chisel modes.
 */
public enum ChiselModeRegistry implements IModeRegistry {

    INSTANCE;

    private final Map<String, IChiselMode> modes = new LinkedHashMap<>();

    @Override
    public void registerMode(@Nonnull IChiselMode mode) {
        this.modes.put(mode.name(), mode);
    }

    @Nonnull
    @Override
    public Collection<IChiselMode> getAllModes() {
        return Collections.unmodifiableCollection(modes.values());
    }

    @Override
    @Nullable
    public IChiselMode getModeByName(String name) {
        return modes.get(name);
    }

    /**
     * Gets the next mode after the given mode.
     */
    public IChiselMode getNextMode(IChiselMode current) {
        IChiselMode[] allModes = modes.values().toArray(new IChiselMode[0]);
        for (int i = 0; i < allModes.length; i++) {
            if (allModes[i] == current) {
                return allModes[(i + 1) % allModes.length];
            }
        }
        return allModes.length > 0 ? allModes[0] : current;
    }

    /**
     * Gets the previous mode before the given mode.
     */
    public IChiselMode getPreviousMode(IChiselMode current) {
        IChiselMode[] allModes = modes.values().toArray(new IChiselMode[0]);
        for (int i = 0; i < allModes.length; i++) {
            if (allModes[i] == current) {
                return allModes[(i - 1 + allModes.length) % allModes.length];
            }
        }
        return allModes.length > 0 ? allModes[0] : current;
    }
}
