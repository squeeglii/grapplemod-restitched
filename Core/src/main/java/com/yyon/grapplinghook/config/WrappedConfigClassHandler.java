package com.yyon.grapplinghook.config;

import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.ConfigSerializer;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public record WrappedConfigClassHandler<T>(ConfigClassHandler<T> baseHandler, Set<Runnable> saveHooks, Set<Runnable> loadHooks) implements ConfigClassHandler<T> {

    @Override
    public boolean load() {
        boolean loaded = this.baseHandler.load();
        this.saveHooks.forEach(Runnable::run);

        return loaded;
    }

    @Override
    public void save() {
        this.baseHandler.save();
        this.loadHooks.forEach(Runnable::run);
    }


    @Override
    public T instance() {
        return this.baseHandler.instance();
    }

    @Override
    public T defaults() {
        return this.baseHandler.defaults();
    }

    @Override
    public Class<T> configClass() {
        return this.baseHandler.configClass();
    }

    @Override
    public ConfigField<?>[] fields() {
        return this.baseHandler.fields();
    }

    @Override
    public ResourceLocation id() {
        return this.baseHandler.id();
    }

    @Override
    public YetAnotherConfigLib generateGui() {
        return this.baseHandler.generateGui();
    }

    @Override
    public boolean supportsAutoGen() {
        return this.baseHandler.supportsAutoGen();
    }

    @Deprecated
    @Override
    public ConfigSerializer<T> serializer() {
        return this.baseHandler.serializer();
    }
}
