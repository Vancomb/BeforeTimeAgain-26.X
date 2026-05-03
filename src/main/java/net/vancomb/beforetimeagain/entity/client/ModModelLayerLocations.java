package net.vancomb.beforetimeagain.entity.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;
import net.vancomb.beforetimeagain.BeforeTimeAgain;

public class ModModelLayerLocations {
    public static final ModelLayerLocation DODO =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(BeforeTimeAgain.MOD_ID, "dodo"), "main");
}
