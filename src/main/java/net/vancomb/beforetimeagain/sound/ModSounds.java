package net.vancomb.beforetimeagain.sound;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.vancomb.beforetimeagain.BeforeTimeAgain;

import java.util.function.Supplier;


public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, BeforeTimeAgain.MOD_ID);

    public static final Supplier<SoundEvent> DODO_SQUAWK = registerSoundEvent("dodo_squawk");

    public static final Supplier<SoundEvent> DODO_TRILLING = registerSoundEvent("dodo_trilling");

    public static final Supplier<SoundEvent> DODO_STARTLED = registerSoundEvent("dodo_startled");

    private static Supplier<SoundEvent> registerSoundEvent(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(BeforeTimeAgain.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));

    }

    public static void register(IEventBus eventBus) {SOUND_EVENTS.register(eventBus);
    }

}
