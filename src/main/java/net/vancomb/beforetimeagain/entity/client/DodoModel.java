package net.vancomb.beforetimeagain.entity.client;

import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class DodoModel extends EntityModel<DodoRenderState> {
    private final ModelPart dodo;
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart eyes;
    private final ModelPart jaw_u;
    private final ModelPart jaw_l;
    private final ModelPart tongue;
    private final ModelPart chest;
    private final ModelPart wing_l;
    private final ModelPart wing_r;
    private final ModelPart tail1;
    private final ModelPart tail2;
    private final ModelPart leg_l;
    private final ModelPart foot_l;
    private final ModelPart leg_r;
    private final ModelPart foot_r;

    private final KeyframeAnimation walkingAnimation;
    private final KeyframeAnimation idlingAnimation;

    public DodoModel(ModelPart root) {
        super(root);
        this.dodo = root.getChild("dodo");
        this.body = this.dodo.getChild("body");
        this.neck = this.body.getChild("neck");
        this.head = this.neck.getChild("head");
        this.eyes = this.head.getChild("eyes");
        this.jaw_u = this.head.getChild("jaw_u");
        this.jaw_l = this.head.getChild("jaw_l");
        this.tongue = this.jaw_l.getChild("tongue");
        this.chest = this.body.getChild("chest");
        this.wing_l = this.chest.getChild("wing_l");
        this.wing_r = this.chest.getChild("wing_r");
        this.tail1 = this.body.getChild("tail1");
        this.tail2 = this.tail1.getChild("tail2");
        this.leg_l = this.dodo.getChild("leg_l");
        this.foot_l = this.leg_l.getChild("foot_l");
        this.leg_r = this.dodo.getChild("leg_r");
        this.foot_r = this.leg_r.getChild("foot_r");

        walkingAnimation = DodoAnimations.WALK.bake(root);
        idlingAnimation = DodoAnimations.IDLE.bake(root);


    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition dodo = partdefinition.addOrReplaceChild("dodo", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = dodo.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -7.5F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(14, 21).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 19).addBox(-2.0F, -5.0F, -5.0F, 4.0F, 7.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(20, -4).addBox(1.0F, -7.0F, -5.0F, 0.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 19).mirror().addBox(-1.0F, -5.0F, -5.0F, 0.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(20, -4).mirror().addBox(-1.0F, -7.0F, -5.0F, 0.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 19).addBox(1.0F, -5.0F, -5.0F, 0.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.5F, -5.0F, -0.3927F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -1.5F, -5.0F, 5.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.5F, -4.0F, 0.4363F, 0.0F, 0.0F));

        PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(20, 3).mirror().addBox(0.0F, -3.0F, -2.0F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-1.5F, -1.5F, -2.0F, 0.0F, 0.0F, -0.6545F));

        PartDefinition cube_r2 = head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(20, 3).addBox(0.0F, -3.0F, -2.0F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -1.5F, -2.0F, 0.0F, 0.0F, 0.6545F));

        PartDefinition eyes = head.addOrReplaceChild("eyes", CubeListBuilder.create().texOffs(9, 15).addBox(1.5F, -1.0F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.02F))
                .texOffs(9, 15).mirror().addBox(-2.5F, -1.0F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.02F)).mirror(false), PartPose.offset(0.0F, 0.5F, -4.0F));

        PartDefinition jaw_u = head.addOrReplaceChild("jaw_u", CubeListBuilder.create().texOffs(0, 9).addBox(-1.5F, -2.0F, -4.0F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(44, 0).addBox(-2.0F, -2.5F, -6.0F, 4.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.5F, -5.0F));

        PartDefinition jaw_l = head.addOrReplaceChild("jaw_l", CubeListBuilder.create().texOffs(0, 15).addBox(-1.5F, 0.0F, -3.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(44, 7).addBox(-1.5F, 0.0F, -4.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.5F, -5.0F));

        PartDefinition tongue = jaw_l.addOrReplaceChild("tongue", CubeListBuilder.create().texOffs(-4, 0).mirror().addBox(-1.0F, 0.0F, -4.0F, 2.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, -0.1F, 0.0F));

        PartDefinition chest = body.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(16, 0).addBox(-4.0F, -4.5F, -6.0F, 8.0F, 9.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(36, 22).addBox(0.0F, -2.5F, -8.0F, 0.0F, 9.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r3 = chest.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(34, 28).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.5F, 2.0F, 0.6981F, 0.0F, 0.0F));

        PartDefinition cube_r4 = chest.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(34, 28).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.5F, -1.0F, 0.6981F, 0.0F, 0.0F));

        PartDefinition cube_r5 = chest.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(34, 28).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.5F, -4.0F, 0.6981F, 0.0F, 0.0F));

        PartDefinition wing_l = chest.addOrReplaceChild("wing_l", CubeListBuilder.create().texOffs(12, 28).addBox(-0.5F, -1.5F, 0.0F, 1.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(20, 20).addBox(0.0F, -2.5F, 1.0F, 0.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, 0.0F, -3.0F, -0.0873F, 0.0F, 0.0F));

        PartDefinition wing_r = chest.addOrReplaceChild("wing_r", CubeListBuilder.create().texOffs(20, 20).mirror().addBox(0.0F, -2.5F, 1.0F, 0.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(12, 28).mirror().addBox(-0.5F, -1.5F, 0.0F, 1.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.5F, 0.0F, -3.0F, -0.0873F, 0.0F, 0.0F));

        PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(28, 21).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.5F, 6.0F));

        PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, 1.0F));

        PartDefinition cube_r6 = tail2.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 23).mirror().addBox(0.0F, -4.0F, 0.0F, 0.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.0F, 0.0F, 0.0F, -0.0827F, -0.2595F, -0.035F));

        PartDefinition cube_r7 = tail2.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(0, 23).addBox(0.0F, -4.0F, 0.0F, 0.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, -0.0827F, 0.2595F, 0.035F));

        PartDefinition cube_r8 = tail2.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(0, 23).addBox(0.0F, -4.0F, 0.0F, 0.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition leg_l = dodo.addOrReplaceChild("leg_l", CubeListBuilder.create().texOffs(12, 9).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -3.0F, 1.0F, 0.0F, -0.2618F, 0.0F));

        PartDefinition foot_l = leg_l.addOrReplaceChild("foot_l", CubeListBuilder.create().texOffs(38, 21).addBox(-2.5F, 0.0F, -4.0F, 5.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, 0.0F));

        PartDefinition leg_r = dodo.addOrReplaceChild("leg_r", CubeListBuilder.create().texOffs(12, 9).mirror().addBox(-1.0F, 0.0F, 0.0F, 2.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-3.0F, -3.0F, 1.0F, 0.0F, 0.2618F, 0.0F));

        PartDefinition foot_r = leg_r.addOrReplaceChild("foot_r", CubeListBuilder.create().texOffs(38, 21).mirror().addBox(-2.5F, 0.0F, -4.0F, 5.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 3.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(DodoRenderState state) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.walkingAnimation.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 2f, 2.5f);
        this.idlingAnimation.apply(state.idleAnimationState, state.ageInTicks, 1f);

        this.applyHeadRotation(state.yRot, state.xRot);

    }

    private void applyHeadRotation(float headYaw, float headPitch) {
        headYaw = Mth.clamp(headYaw, -30f, 30f);
        headPitch = Mth.clamp(headPitch, -25f, 45);

        this.head.yRot += headYaw * ((float)Math.PI / 180f);
        this.head.xRot += headPitch * ((float)Math.PI / 180f);

    }

}
