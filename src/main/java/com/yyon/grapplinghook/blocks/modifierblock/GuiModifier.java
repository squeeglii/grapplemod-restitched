package com.yyon.grapplinghook.blocks.modifierblock;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.yyon.grapplinghook.client.ClientProxyInterface;
import com.yyon.grapplinghook.utils.GrappleCustomization;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class GuiModifier extends Screen {
	private static final ResourceLocation texture = new ResourceLocation("grapplemod", "textures/gui/guimodifier_bg.png");

	int xSize = 221;
	int ySize = 221;

	protected int guiLeft;
	protected int guiTop;

	int posy;
	int id;
	HashMap<AbstractWidget, String> options;

	TileEntityGrappleModifier tileEnt;
	GrappleCustomization customization;

	GrappleCustomization.upgradeCategories category = null;

	public GuiModifier(TileEntityGrappleModifier tileent) {
		super(Component.translatable("grapplemodifier.title.desc"));

		this.tileEnt = tileent;
		customization = tileent.customization;
	}

	@Override
	public void init() {
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;

		mainScreen();
	}
	
	class PressCategory implements OnPress {
		GrappleCustomization.upgradeCategories category;
		public PressCategory(GrappleCustomization.upgradeCategories category) {
			this.category = category;
		}
		
		public void onPress(Button p_onPress_1_) {
			boolean unlocked = tileEnt.isUnlocked(category) || Minecraft.getInstance().player.isCreative();

			if (unlocked) {
				showCategoryScreen(category);
			} else {
				notAllowedScreen(category);
			}
		}
	}
	
	class PressBack implements OnPress {
		public void onPress(Button p_onPress_1_) {
			mainScreen();
		}
	}
	public void mainScreen() {
		clearScreen();

		this.addRenderableWidget(Button.builder(
								Component.translatable("grapplemodifier.close.desc"),
								onPress -> onClose())
						.bounds(this.guiLeft + 10, this.guiTop + this.ySize - 20 - 10, 50, 20)
						.build()
		);

		this.addRenderableWidget(Button.builder(
						Component.translatable("grapplemodifier.reset.desc"),
						onPress -> {
							customization = new GrappleCustomization();
							mainScreen();
						})
				.bounds(this.guiLeft + this.xSize - 50 - 10, this.guiTop + this.ySize - 20 - 10, 50, 20)
				.build()
		);

		this.addRenderableWidget(Button.builder(
						Component.translatable("grapplemodifier.helpbutton.desc"),
						onPress -> helpScreen())
				.bounds(this.guiLeft + 10 + 75, this.guiTop + this.ySize - 20 - 10, 50, 20)
				.build()
		);

		int y = 0;
		int x = 0;
		for (int i = 0; i < GrappleCustomization.upgradeCategories.size(); i++) {
			GrappleCustomization.upgradeCategories category = GrappleCustomization.upgradeCategories.fromInt(i);
			if (category != GrappleCustomization.upgradeCategories.LIMITS) {
				if (i == GrappleCustomization.upgradeCategories.size()/2) {
					y = 0;
					x += 1;
				}

				this.addRenderableWidget(Button.builder(
									Component.literal(category.getName()),
									new PressCategory(category))
								.bounds(this.guiLeft + 10 + 105*x, this.guiTop + 15 + 30 * y, 95, 20)
								.build()
				);

				y += 1;
			}
		}

		this.addRenderableWidget(new TextWidget(
				Component.translatable("grapplemodifier.apply.desc"),
				this.guiLeft + 10, this.guiTop + this.ySize - 20 - 10 - 10
		));
	}

	static class BackgroundWidget extends AbstractWidget {
		public BackgroundWidget(int p_i232254_1_, int p_i232254_2_, int p_i232254_3_, int p_i232254_4_, Component p_i232254_5_) {
			super(p_i232254_1_, p_i232254_2_, p_i232254_3_, p_i232254_4_, p_i232254_5_);
			this.active = false;
		}
		
		public BackgroundWidget(int x, int y, int w, int h) {
			this(x, y, w, h, Component.literal(""));
		}
		
	    public void renderButton(PoseStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
			RenderSystem.setShaderTexture(0,texture);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			this.blit(p_230431_1_, this.getX(), this.getY(), 0, 0, this.width, this.height);
	    }

		@Override
		protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

		}
	}

	public void clearScreen() {
		this.category = null;
		posy = 10;
		id = 10;
		options = new HashMap<>();
		this.clearWidgets();
		
		this.addRenderableWidget(new BackgroundWidget(this.guiLeft, this.guiTop, this.xSize, this.ySize));
	}
	
	static class TextWidget extends AbstractWidget {
		public TextWidget(int p_i232254_1_, int p_i232254_2_, int p_i232254_3_, int p_i232254_4_, Component p_i232254_5_) {
			super(p_i232254_1_, p_i232254_2_, p_i232254_3_, p_i232254_4_, p_i232254_5_);
		}
		
		public TextWidget(Component text, int x, int y) {
			this(x, y, 50, 15 * text.getString().split("\n").length + 5, text);
		}
		
		public void renderButton(PoseStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
			Minecraft minecraft = Minecraft.getInstance();
			Font fontRenderer = minecraft.font;
			RenderSystem.setShaderTexture(0,WIDGETS_LOCATION);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();
			int j = this.getFGColor();
			int lineno = 0;
			for (String s : this.getMessage().getString().split("\n")) {
				drawString(p_230431_1_, fontRenderer, Component.literal(s), this.getX(), this.getY() + lineno*15, j | Mth.ceil(this.alpha * 255.0F) << 24);
				lineno++;
			}
		}

		@Override
		protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

		}
	}

	public void notAllowedScreen(GrappleCustomization.upgradeCategories category) {
		clearScreen();

		this.addRenderableWidget(
				Button.builder(
							Component.translatable("grapplemodifier.back.desc"),
							new PressBack())
						.bounds(this.guiLeft + 10, this.guiTop + this.ySize - 20 - 10, 50, 20)
						.build()
		);

		this.category = category;
		this.addRenderableWidget(new TextWidget(Component.translatable("grapplemodifier.unlock1.desc"), this.guiLeft + 10, this.guiTop + 10));
		this.addRenderableWidget(new TextWidget(Component.literal(this.category.getName()), this.guiLeft + 10, this.guiTop + 25));
		this.addRenderableWidget(new TextWidget(Component.translatable("grapplemodifier.unlock2.desc"), this.guiLeft + 10, this.guiTop + 40));
		this.addRenderableWidget(new TextWidget(Component.translatable("grapplemodifier.unlock3.desc"), this.guiLeft + 10, this.guiTop + 55));
		this.addRenderableWidget(new TextWidget(new ItemStack(this.category.getItem()).getDisplayName(), this.guiLeft + 10, this.guiTop + 70));
		this.addRenderableWidget(new TextWidget(Component.translatable("grapplemodifier.unlock4.desc"), this.guiLeft + 10, this.guiTop + 85));
	}

	public void helpScreen() {
		clearScreen();

		this.addRenderableWidget(
				Button.builder(
								Component.translatable("grapplemodifier.back.desc"),
								new PressBack())
						.bounds(this.guiLeft + 10, this.guiTop + this.ySize - 20 - 10, 50, 20)
						.build()
		);

		this.addRenderableWidget(new TextWidget(
						Component.translatable("grapplemodifier.help.desc"),
						this.guiLeft + 10, this.guiTop + 10
		));
		
	}
	
	class GuiCheckbox extends Checkbox {
		String option;
		public Component tooltip;

		public GuiCheckbox(int x, int y, int w, int h,
				Component text, boolean val, String option, Component tooltip) {
			super(x, y, w, h, text, val);
			this.option = option;
			this.tooltip = tooltip;
		}
		
		@Override
		public void onPress() {
			super.onPress();
			
			customization.setBoolean(option, this.selected());
			
			updateEnabled();
		}
		
		@Override
		public void renderButton(PoseStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
			super.renderButton(p_230431_1_, p_230431_2_, p_230431_3_, p_230431_4_);
			
			if (this.isHovered) {
				String tooltiptext = tooltip.getString();
				ArrayList<Component> lines = new ArrayList<Component>();
				for (String line : tooltiptext.split("\n")) {
					lines.add(Component.literal(line));
				}
				renderTooltip(p_230431_1_, lines, Optional.empty(), p_230431_2_, p_230431_3_);
			}
		}
	}

	public void addCheckbox(String option) {
		String text = Component.translatable(this.customization.getName(option)).getString();
		String desc = Component.translatable(this.customization.getDescription(option)).getString();
		GuiCheckbox checkbox = new GuiCheckbox(10 + this.guiLeft, posy + this.guiTop, this.xSize - 20, 20, Component.literal(text), customization.getBoolean(option), option, Component.literal(desc));
		posy += 22;
		this.addRenderableWidget(checkbox);
		options.put(checkbox, option);
	}
		
	class GuiSlider extends AbstractSliderButton {
		double min, max, val;
		String text, option;
		public Component tooltip;
		public GuiSlider(int x, int y, int w, int h,
				Component text, double min, double max, double val, String option, Component tooltip) {
			super(x, y, w, h, text, (val - min) / (max - min));
			this.min = min;
			this.max = max;
			this.val = val;
			this.text = text.getString();
			this.option = option;
			this.tooltip = tooltip;
			
			this.updateMessage();
		}

		@Override
		protected void updateMessage() {
			this.setMessage(Component.literal(text + ": " + String.format("%.1f", this.val)));
		}

		@Override
		protected void applyValue() {
			this.val = (this.value * (this.max - this.min)) + this.min;
//			d = Math.floor(d * 10 + 0.5) / 10;
			customization.setDouble(option, this.val);
		}
		
		@Override
		public void renderButton(PoseStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
			super.renderButton(p_230431_1_, p_230431_2_, p_230431_3_, p_230431_4_);
			
			if (this.isHovered) {
				String tooltiptext = tooltip.getString();
				ArrayList<Component> lines = new ArrayList<Component>();
				for (String line : tooltiptext.split("\n")) {
					lines.add(Component.literal(line));
				}
				renderTooltip(p_230431_1_, lines, Optional.empty(), p_230431_2_, p_230431_3_);
			}
		}
	}
	
	public void addSlider(String option) {
		double d = customization.getDouble(option);
		d = Math.floor(d * 10 + 0.5) / 10;
		
		double max = customization.getMax(option, this.getLimits());
		double min = customization.getMin(option, this.getLimits());
		
		String text = Component.translatable(this.customization.getName(option)).getString();
		String desc = Component.translatable(this.customization.getDescription(option)).getString();
		GuiSlider slider = new GuiSlider(10 + this.guiLeft, posy + this.guiTop, this.xSize - 20, 20, Component.literal(text), min, max, d, option, Component.literal(desc));
		
		posy += 22;
		this.addRenderableWidget(slider);
		options.put(slider, option);
	}

	public void showCategoryScreen(GrappleCustomization.upgradeCategories category) {
		clearScreen();

		this.addRenderableWidget(
				Button.builder(
								Component.translatable("grapplemodifier.back.desc"),
								new PressBack())
						.bounds(this.guiLeft + 10, this.guiTop + this.ySize - 20 - 10, 50, 20)
						.build()
		);

		this.category = category;

		if (category == GrappleCustomization.upgradeCategories.ROPE) {
			addSlider("maxlen");
			addCheckbox("phaserope");
			addCheckbox("sticky");
		} else if (category == GrappleCustomization.upgradeCategories.THROW) {
			addSlider("hookgravity");
			addSlider("throwspeed");
			addCheckbox("reelin");
			addSlider("verticalthrowangle");
			addSlider("sneakingverticalthrowangle");
			addCheckbox("detachonkeyrelease");
		} else if (category == GrappleCustomization.upgradeCategories.MOTOR) {
			addCheckbox("motor");
			addSlider("motormaxspeed");
			addSlider("motoracceleration");
			addCheckbox("motorwhencrouching");
			addCheckbox("motorwhennotcrouching");
			addCheckbox("smartmotor");
			addCheckbox("motordampener");
			addCheckbox("pullbackwards");
		} else if (category == GrappleCustomization.upgradeCategories.SWING) {
			addSlider("playermovementmult");
		} else if (category == GrappleCustomization.upgradeCategories.STAFF) {
			addCheckbox("enderstaff");
		} else if (category == GrappleCustomization.upgradeCategories.FORCEFIELD) {
			addCheckbox("repel");
			addSlider("repelforce");
		} else if (category == GrappleCustomization.upgradeCategories.MAGNET) {
			addCheckbox("attract");
			addSlider("attractradius");
		} else if (category == GrappleCustomization.upgradeCategories.DOUBLE) {
			addCheckbox("doublehook");
			addCheckbox("smartdoublemotor");
			addSlider("angle");
			addSlider("sneakingangle");
			addCheckbox("oneropepull");
		} else if (category == GrappleCustomization.upgradeCategories.ROCKET) {
			addCheckbox("rocket");
			addSlider("rocket_force");
			addSlider("rocket_active_time");
			addSlider("rocket_refuel_ratio");
			addSlider("rocket_vertical_angle");
		}
		
		this.updateEnabled();
	}
	
	@Override
	public void onClose() {
//		this.updateOptions();
		this.tileEnt.setCustomizationClient(customization);
		
		super.onClose();
	}
	
	public void updateEnabled() {
		for (AbstractWidget b : this.options.keySet()) {
			String option = this.options.get(b);
			boolean enabled = true;
			
			String desc = Component.translatable(this.customization.getDescription(option)).getString();
			
			if (this.customization.isOptionValid(option)) {
			} else {
				desc = Component.translatable("grapplemodifier.incompatability.desc") + "\n" + desc;
				enabled = false;
			}
			
			int level = this.customization.optionEnabled(option);
			if (this.getLimits() < level) {
				if (level == 1) {
					desc = Component.translatable("grapplemodifier.limits.desc") + "\n" + desc;
				} else {
					desc = Component.translatable("grapplemodifier.locked.desc") + "\n" + desc;
				}
				enabled = false;
			}
			
			b.active = enabled;

			if (b instanceof GuiSlider) {
				((GuiSlider) b).tooltip = Component.literal(desc);
				b.setAlpha(enabled ? 1.0F : 0.5F);
			}
			if (b instanceof GuiCheckbox) {
				((GuiCheckbox) b).tooltip = Component.literal(desc);
				b.setAlpha(enabled ? 1.0F : 0.5F);
			}
		}
	}
	
	public int getLimits() {
		if (this.tileEnt.isUnlocked(GrappleCustomization.upgradeCategories.LIMITS) || Minecraft.getInstance().player.isCreative()) {
			return 1;
		}
		return 0;
	}
}
