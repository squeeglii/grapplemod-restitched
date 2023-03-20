package com.yyon.grapplinghook.client.gui;

import com.yyon.grapplinghook.client.gui.widget.*;
import com.yyon.grapplinghook.content.blockentity.GrappleModifierBlockEntity;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;

public class GrappleModifierBlockGUI extends Screen {

	public static final int FULL_SIZE_X = 221;
	public static final int FULL_SIZE_Y = 221;

	private final OnPress actionGoBack = button -> showMainScreenLayout();

	protected int guiLeft;
	protected int guiTop;
	private int widgetPosYIncrementor;

	private final GrappleModifierBlockEntity blockEntity;

	private HashMap<AbstractWidget, String> options;
	private CustomizationVolume customization;
	private CustomizationVolume.UpgradeCategory currentActiveCategory = null;

	public GrappleModifierBlockGUI(GrappleModifierBlockEntity tileent) {
		super(Component.translatable("grapplemodifier.title.desc"));

		this.blockEntity = tileent;
		this.customization = tileent.customization;
	}

	@Override
	public void init() {
		this.guiLeft = (this.width - FULL_SIZE_X) / 2;
		this.guiTop = (this.height - FULL_SIZE_Y) / 2;

		this.showMainScreenLayout();
	}

	@Override
	public void onClose() {
		this.blockEntity.setCustomizationClient(customization);
		super.onClose();
	}


	public void addCheckbox(String option) {
		String text = Component.translatable(this.customization.getName(option)).getString();
		String desc = Component.translatable(this.customization.getDescription(option)).getString();
		CustomizationCheckbox checkbox = new CustomizationCheckbox(this, this::getCurrentCustomizations, 10 + this.guiLeft, this.getNextYPosition(), FULL_SIZE_X - 20, 20, Component.literal(text), customization.getBoolean(option), option, Component.literal(desc), this::markConfigurationsDirty);
		this.addRenderableWidget(checkbox);
		this.options.put(checkbox, option);
	}

	public void addSlider(String option) {
		double d = this.customization.getDouble(option);
		d = Math.floor(d * 10 + 0.5) / 10;

		double max = CustomizationVolume.getMaxFromConfig(option, this.getLimits());
		double min = CustomizationVolume.getMinFromConfig(option, this.getLimits());

		String text = Component.translatable(this.customization.getName(option)).getString();
		String desc = Component.translatable(this.customization.getDescription(option)).getString();
		CustomizationSlider slider = new CustomizationSlider(this, this::getCurrentCustomizations, 10 + this.guiLeft, this.getNextYPosition(), FULL_SIZE_X - 20, 20, Component.literal(text), min, max, d, option, Component.literal(desc), this::markConfigurationsDirty);
		this.addRenderableWidget(slider);
		this.options.put(slider, option);
	}

	// -- GUI Screens

	public void showMainScreenLayout() {
		this.resetScreenLayout();

		this.addRenderableWidget(Button.builder(
				Component.translatable("grapplemodifier.close.desc"),
				button -> this.onClose())
				.pos(this.guiLeft + 10, this.guiTop + FULL_SIZE_Y - 20 - 10)
				.size(50, 20)
				.build()
		);

		this.addRenderableWidget(Button.builder(
				Component.translatable("grapplemodifier.reset.desc"),
				button -> {
					this.customization = new CustomizationVolume();
					this.showMainScreenLayout();
				})
				.pos(this.guiLeft + FULL_SIZE_X - 50 - 10, this.guiTop + FULL_SIZE_Y - 20 - 10)
				.size(50, 20)
				.build()
		);

		this.addRenderableWidget(Button.builder(
						Component.translatable("grapplemodifier.helpbutton.desc"),
						button -> this.showHelpScreenLayout())
				.pos(this.guiLeft + 10 + 75, this.guiTop + FULL_SIZE_Y - 20 - 10)
				.size(50, 20)
				.build()
		);

		int y = 0;
		int x = 0;
		for (int i = 0; i < CustomizationVolume.UpgradeCategory.size(); i++) {
			CustomizationVolume.UpgradeCategory category = CustomizationVolume.UpgradeCategory.fromInt(i);
			if (category == CustomizationVolume.UpgradeCategory.LIMITS) continue;

			if (i == CustomizationVolume.UpgradeCategory.size() / 2) {
				y = 0;
				x += 1;
			}

			this.addRenderableWidget(
					Button.builder(Component.literal(category.getName()), this.createCategoryActionHandler(category))
							.pos(this.guiLeft + 10 + 105 * x, this.guiTop + 15 + 30 * y)
							.size(95, 20)
							.build()
			);
			y += 1;
		}

		this.addRenderableWidget(new TextWidget(Component.translatable("grapplemodifier.apply.desc"), this.guiLeft + 10, this.guiTop + FULL_SIZE_Y - 20 - 10 - 10));
	}

	public void resetScreenLayout() {
		this.currentActiveCategory = null;
		this.widgetPosYIncrementor = 10;
		this.options = new HashMap<>();
		this.clearWidgets();
		
		this.addRenderableWidget(new BackgroundWidget(this.guiLeft, this.guiTop, FULL_SIZE_X, FULL_SIZE_Y));
	}

	public void showNotAllowedScreenLayout(CustomizationVolume.UpgradeCategory category) {
		this.resetScreenLayout();
		this.addRenderableWidget(
				Button.builder(Component.translatable("grapplemodifier.back.desc"), actionGoBack)
						.pos(this.guiLeft + 10, this.guiTop + FULL_SIZE_Y - 20 - 10)
						.size(50, 20)
						.build()
		);

		this.currentActiveCategory = category;

		this.addRenderableWidget(new TextWidget(Component.translatable("grapplemodifier.unlock1.desc"), this.guiLeft + 10, this.guiTop + 10));
		this.addRenderableWidget(new TextWidget(Component.literal(this.currentActiveCategory.getName()), this.guiLeft + 10, this.guiTop + 25));
		this.addRenderableWidget(new TextWidget(Component.translatable("grapplemodifier.unlock2.desc"), this.guiLeft + 10, this.guiTop + 40));
		this.addRenderableWidget(new TextWidget(Component.translatable("grapplemodifier.unlock3.desc"), this.guiLeft + 10, this.guiTop + 55));
		this.addRenderableWidget(new TextWidget(new ItemStack(this.currentActiveCategory.getItem()).getDisplayName(), this.guiLeft + 10, this.guiTop + 70));
		this.addRenderableWidget(new TextWidget(Component.translatable("grapplemodifier.unlock4.desc"), this.guiLeft + 10, this.guiTop + 85));
	}

	public void showHelpScreenLayout() {
		resetScreenLayout();

		this.addRenderableWidget(
				Button.builder(Component.translatable("grapplemodifier.back.desc"), actionGoBack)
						.pos(this.guiLeft + 10, this.guiTop + FULL_SIZE_Y - 20 - 10)
						.size(50, 20)
						.build()
		);

		this.addRenderableWidget(new TextWidget(Component.translatable("grapplemodifier.help.desc"), this.guiLeft + 10, this.guiTop + 10));
	}

	public void showCategoryScreen(CustomizationVolume.UpgradeCategory category) {
		this.resetScreenLayout();

		this.addRenderableWidget(
				Button.builder(Component.translatable("grapplemodifier.back.desc"), actionGoBack)
						.pos(this.guiLeft + 10, this.guiTop + FULL_SIZE_Y - 20 - 10)
						.size(50, 20)
						.build()
		);

		this.currentActiveCategory = category;

		switch (this.currentActiveCategory) {
			case ROPE -> {
				this.addSlider("maxlen");
				this.addCheckbox("phaserope");
				this.addCheckbox("sticky");
			}

			case THROW -> {
				this.addSlider("hookgravity");
				this.addSlider("throwspeed");
				this.addCheckbox("reelin");
				this.addSlider("verticalthrowangle");
				this.addSlider("sneakingverticalthrowangle");
				this.addCheckbox("detachonkeyrelease");
			}

			case MOTOR -> {
				this.addCheckbox("motor");
				this.addSlider("motormaxspeed");
				this.addSlider("motoracceleration");
				this.addCheckbox("motorwhencrouching");
				this.addCheckbox("motorwhennotcrouching");
				this.addCheckbox("smartmotor");
				this.addCheckbox("motordampener");
				this.addCheckbox("pullbackwards");
			}

			case FORCEFIELD -> {
				this.addCheckbox("repel");
				this.addSlider("repelforce");
			}

			case MAGNET -> {
				this.addCheckbox("attract");
				this.addSlider("attractradius");
			}

			case DOUBLE -> {
				this.addCheckbox("doublehook");
				this.addCheckbox("smartdoublemotor");
				this.addSlider("angle");
				this.addSlider("sneakingangle");
				this.addCheckbox("oneropepull");
			}

			case ROCKET -> {
				this.addCheckbox("rocket");
				this.addSlider("rocket_force");
				this.addSlider("rocket_active_time");
				this.addSlider("rocket_refuel_ratio");
				this.addSlider("rocket_vertical_angle");
			}

			case SWING -> this.addSlider("playermovementmult");
			case STAFF -> this.addCheckbox("enderstaff");
		}
		
		this.markConfigurationsDirty();
	}

	/**
	 * Forces all the Grappling Hook customizations to be re-evaluated, with
	 * appropriate UI changes being made.
	 */
	public void markConfigurationsDirty() {
		for (AbstractWidget b : this.options.keySet()) {
			String option = this.options.get(b);
			boolean enabled = true;
			
			String desc = Component.translatable(this.customization.getDescription(option)).getString();
			
			if (!this.customization.isOptionValid(option)) {
				desc = Component.translatable("grapplemodifier.incompatability.desc").getString() + "\n" + desc;
				enabled = false;
			}
			
			int level = CustomizationVolume.optionEnabledInConfig(option);
			if (this.getLimits() < level) {
				desc = level == 1
						? Component.translatable("grapplemodifier.limits.desc").getString() + "\n" + desc
						: Component.translatable("grapplemodifier.locked.desc").getString() + "\n" + desc;
				enabled = false;
			}
			
			b.active = enabled;

			if (b instanceof CustomizationSlider slide) {
				slide.setTooltip(Component.literal(desc));
				slide.setAlpha(enabled ? 1.0F : 0.5F);
			}

			if (b instanceof CustomizationCheckbox check) {
				check.setTooltip(Component.literal(desc));
				check.setAlpha(enabled ? 1.0F : 0.5F);
			}
		}
	}
	
	public int getLimits() {
		if(Minecraft.getInstance().player == null) return 0;
		return this.blockEntity.isUnlocked(CustomizationVolume.UpgradeCategory.LIMITS) || Minecraft.getInstance().player.isCreative()
				? 1
				: 0;
	}

	public CustomizationVolume getCurrentCustomizations() {
		return this.customization;
	}

	protected int getNextYPosition() {
		this.widgetPosYIncrementor += 22;
		return this.guiTop + this.widgetPosYIncrementor - 22;
	}

	protected OnPress createCategoryActionHandler(CustomizationVolume.UpgradeCategory category) {
		return button -> {
			if(Minecraft.getInstance().player == null) return;

			boolean unlocked = this.blockEntity.isUnlocked(category) || Minecraft.getInstance().player.isCreative();
			if (unlocked) {
				this.showCategoryScreen(category);
				return;
			}

			this.showNotAllowedScreenLayout(category);
		};
	}
}
