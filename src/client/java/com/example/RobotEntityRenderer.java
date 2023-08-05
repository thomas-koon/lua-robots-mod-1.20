package com.example;

import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class RobotEntityRenderer extends BipedEntityRenderer<RobotEntity, RobotEntityModel> {

    public RobotEntityRenderer(EntityRendererFactory.Context context, RobotEntityModel entityModel, float f) {
        super(context, entityModel, f);
    }

    public RobotEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new RobotEntityModel(context.getPart(LuabotsModClient.MODEL_ROBOT_LAYER)), 0.5f);
    }

    @Override
    public Identifier getTexture(RobotEntity entity) {
        return new Identifier("luabots", "textures/entity/robot.png");
    }
}
