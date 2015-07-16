// Date: 10/07/2015 4:15:59
// Template version 1.1
// Java generated by Techne
// Keep in mind that you still need to fill in some blanks
// - ZeuX






package com.cout970.magneticraft.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelOpticFiber extends ModelBase
{
	//fields
	ModelRenderer base;
	ModelRenderer down;
	ModelRenderer up;
	ModelRenderer front;
	ModelRenderer back;
	ModelRenderer right;
	ModelRenderer left;

	public ModelOpticFiber()
	{
		textureWidth = 36;
		textureHeight = 28;

		base = new ModelRenderer(this, 0, 0);
		base.addBox(3F, 3F, -5F, 2, 2, 2);
		base.setRotationPoint(0F, 16F, 0F);
		base.setTextureSize(36, 28);
		base.mirror = true;
		setRotation(base, 0F, 0F, 0F);
		down = new ModelRenderer(this, 0, 4);
		down.addBox(3F, 5F, -5F, 2, 3, 2);
		down.setRotationPoint(0F, 16F, 0F);
		down.setTextureSize(36, 28);
		down.mirror = true;
		setRotation(down, 0F, 0F, 0F);
		up = new ModelRenderer(this, 0, 9);
		up.addBox(3F, -8F, -5F, 2, 11, 2);
		up.setRotationPoint(0F, 16F, 0F);
		up.setTextureSize(36, 28);
		up.mirror = true;
		setRotation(up, 0F, 0F, 0F);
		front = new ModelRenderer(this, 0, 22);
		front.addBox(3F, 3F, -8F, 2, 2, 3);
		front.setRotationPoint(0F, 16F, 0F);
		front.setTextureSize(36, 28);
		front.mirror = true;
		setRotation(front, 0F, 0F, 0F);
		back = new ModelRenderer(this, 8, 0);
		back.addBox(3F, 3F, -3F, 2, 2, 11);
		back.setRotationPoint(0F, 16F, 0F);
		back.setTextureSize(36, 28);
		back.mirror = true;
		setRotation(back, 0F, 0F, 0F);
		right = new ModelRenderer(this, 8, 13);
		right.addBox(-8F, 3F, -5F, 11, 2, 2);
		right.setRotationPoint(0F, 16F, 0F);
		right.setTextureSize(36, 28);
		right.mirror = true;
		setRotation(right, 0F, 0F, 0F);
		left = new ModelRenderer(this, 8, 17);
		left.addBox(5F, 3F, -5F, 3, 2, 2);
		left.setRotationPoint(0F, 16F, 0F);
		left.setTextureSize(36, 28);
		left.mirror = true;
		setRotation(left, 0F, 0F, 0F);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		super.render(entity, f, f1, f2, f3, f4, f5);
		//    setRotationAngles(f, f1, f2, f3, f4, f5);
		base.render(f5);
		down.render(f5);
		up.render(f5);
		front.render(f5);
		back.render(f5);
		right.render(f5);
		left.render(f5);
	}

	public void renderStatic(float f5){
		base.render(f5);
	}

	public void renderDynamic(float f5, int con){
		if((con & (1 << 0)) > 0) down.render(f5);
		if((con & (1 << 1)) > 0) up.render(f5);
		if((con & (1 << 2)) > 0) back.render(f5);
		if((con & (1 << 3)) > 0) front.render(f5);
		if((con & (1 << 4)) > 0) right.render(f5);
		if((con & (1 << 5)) > 0) left.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
