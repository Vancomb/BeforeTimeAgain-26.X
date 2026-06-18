package net.vancomb.beforetimeagain.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import net.vancomb.beforetimeagain.BeforeTimeAgain;
import net.vancomb.beforetimeagain.sound.ModSounds;

public class ModSoundsProvider extends SoundDefinitionsProvider {
    public ModSoundsProvider(PackOutput output) {
        super(output, BeforeTimeAgain.MOD_ID);
    }

    @Override
    public void registerSounds() {
        add(ModSounds.DODO_SQUAWK.get(), definition().subtitle("sounds.beforetimeagain.dodo_squawk")
                .with(sound(Identifier.fromNamespaceAndPath(BeforeTimeAgain.MOD_ID, "dodo_squawk"))));

        add(ModSounds.DODO_TRILLING.get(), definition().subtitle("sounds.beforetimeagain.dodo_trilling")
                .with(sound(Identifier.fromNamespaceAndPath(BeforeTimeAgain.MOD_ID, "dodo_trilling_1")),
                        sound(Identifier.fromNamespaceAndPath(BeforeTimeAgain.MOD_ID, "dodo_trilling_2")),
                        sound(Identifier.fromNamespaceAndPath(BeforeTimeAgain.MOD_ID, "dodo_trilling_3")),
                        sound(Identifier.fromNamespaceAndPath(BeforeTimeAgain.MOD_ID, "dodo_trilling_4")),
                        sound(Identifier.fromNamespaceAndPath(BeforeTimeAgain.MOD_ID, "dodo_trilling_5"))));

        add(ModSounds.DODO_STARTLED.get(), definition().subtitle("sounds.beforetimeagain.dodo_startled")
                .with(sound(Identifier.fromNamespaceAndPath(BeforeTimeAgain.MOD_ID, "dodo_startled"))));

    }
}
