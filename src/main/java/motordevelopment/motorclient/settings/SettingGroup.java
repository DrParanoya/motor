/*
 * This file is part of the motor Client distribution (https://github.com/motorDevelopment/motor-client).
 * Copyright (c) motor Development.
 */

package motordevelopment.motorclient.settings;

import motordevelopment.motorclient.utils.misc.ISerializable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SettingGroup implements ISerializable<SettingGroup>, Iterable<Setting<?>> {
    public final String name;
    public boolean sectionExpanded;

    final List<Setting<?>> settings = new ArrayList<>(1);

    SettingGroup(String name, boolean sectionExpanded) {
        this.name = name;
        this.sectionExpanded = sectionExpanded;
    }

    public Setting<?> get(String name) {
        for (Setting<?> setting : this) {
            if (setting.name.equals(name)) return setting;
        }

        return null;
    }

    public <T> Setting<T> add(Setting<T> setting) {
        settings.add(setting);

        return setting;
    }

    public Setting<?> getByIndex(int index) {
        return settings.get(index);
    }

    @Override
    public @NotNull Iterator<Setting<?>> iterator() {
        return settings.iterator();
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();

        tag.putString("name", name);
        tag.putBoolean("sectionExpanded", sectionExpanded);

        NbtList settingsTag = new NbtList();
        for (Setting<?> setting : this) if (setting.wasChanged()) settingsTag.add(setting.toTag());
        tag.put("settings", settingsTag);

        return tag;
    }

    @Override
    public SettingGroup fromTag(NbtCompound tag) {
        sectionExpanded = tag.getBoolean("sectionExpanded");

        NbtList settingsTag = tag.getList("settings", 10);
        for (NbtElement t : settingsTag) {
            NbtCompound settingTag = (NbtCompound) t;

            Setting<?> setting = get(settingTag.getString("name"));
            if (setting != null) setting.fromTag(settingTag);
        }

        return this;
    }
}
