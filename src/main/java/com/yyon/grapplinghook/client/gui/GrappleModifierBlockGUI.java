package com.yyon.grapplinghook.client.gui;

import com.yyon.grapplinghook.client.gui.widget.*;
import com.yyon.grapplinghook.content.blockentity.GrappleModifierBlockEntity;
import com.yyon.grapplinghook.content.registry.GrappleModCustomizationCategories;
import com.yyon.grapplinghook.content.registry.GrappleModMetaRegistry;
import com.yyon.grapplinghook.customization.CustomizationCategory;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.customization.type.CustomizationProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GrappleModifierBlockGUI extends Screen {

	public static final int FULL_SIZE_X = 221;
	public static final int FULL_SIZE_Y = 221;

	public static final int COLUMNS = 2;

	private final OnPress actionGoBack = button -> showMainScreenLayout();

	protected int guiLeft;
	protected int guiTop;
	private int widgetPosYIncrementor;

	private final GrappleModifierBlockEntity blockEntity;

	private HashMap<AbstractWidget, CustomizationProperty<?>> options;
	private CustomizationVolume customization;
	private CustomizationCategory currentActiveCategory;

	public GrappleModifierBlockGUI(GrappleModifierBlockEntity blockEntity) {
		super(Component.translatable("grapplemodifier.title.desc"));

		this.blockEntity = blockEntity;
		this.customization = blockEntity.getCurrentCustomizations();
		this.currentActiveCategory = null;
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

		AtomicInteger counter = new AtomicInteger(0);
		GrappleModMetaRegistry.CUSTOMIZATION_CATEGORIES.stream().forEach(category -> {
			if (!category.shouldRender()) return;

			int i = counter.getAndIncrement();
			int y = Math.floorDiv(i, COLUMNS);
			int x = i % COLUMNS;

			this.addRenderableWidget(
					Button.builder(category.getName(), this.createCategoryActionHandler(category))
							.pos(this.guiLeft + 10 + 105 * x, this.guiTop + 15 + 30 * y)
							.size(95, 20)
							.build()
			);
		});

		this.addRenderableWidget(new TextWidget(Component.translatable("grapplemodifier.apply.desc"), this.guiLeft + 10, this.guiTop + FULL_SIZE_Y - 20 - 10 - 10));
	}

	public void resetScreenLayout() {
		this.currentActiveCategory = null;
		this.widgetPosYIncrementor = 10;
		this.options = new HashMap<>();
		this.clearWidgets();
		
		this.addRenderableWidget(new BackgroundWidget(this.guiLeft, this.guiTop, FULL_SIZE_X, FULL_SIZE_Y));
	}

	public void showNotAllowedScreenLayout(CustomizationCategory category) {
		this.resetScreenLayout();
		this.addRenderableWidget(
				Button.builder(Component.translatable("grapplemodifier.back.desc"), actionGoBack)
						.pos(this.guiLeft + 10, this.guiTop + FULL_SIZE_Y - 20 - 10)
						.size(50, 20)
						.build()
		);

		this.currentActiveCategory = category;

		this.addRenderableWidget(new TextWidget(Component.translatable("grapplemodifier.unlock1.desc"), this.guiLeft + 10, this.guiTop + 10));
		this.addRenderableWidget(new TextWidget(this.currentActiveCategory.getName(), this.guiLeft + 10, this.guiTop + 25));
		this.addRenderableWidget(new TextWidget(Component.translatable("grapplemodifier.unlock2.desc"), this.guiLeft + 10, this.guiTop + 40));
		this.addRenderableWidget(new TextWidget(Component.translatable("grapplemodifier.unlock3.desc"), this.guiLeft + 10, this.guiTop + 55));
		this.addRenderableWidget(new TextWidget(new ItemStack(this.currentActiveCategory.getUpgradeItem()).getDisplayName(), this.guiLeft + 10, this.guiTop + 70));
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

	public void showCategoryScreen(CustomizationCategory category) {
		this.resetScreenLayout();

		this.addRenderableWidget(
				Button.builder(Component.translatable("grapplemodifier.back.desc"), actionGoBack)
						.pos(this.guiLeft + 10, this.guiTop + FULL_SIZE_Y - 20 - 10)
						.size(50, 20)
						.build()
		);

		this.currentActiveCategory = category;

		this.currentActiveCategory.getLinkedProperties().forEach(property -> {
			int x = 10 + this.guiLeft;
			int y = this.getNextYPosition();

			AbstractWidget widget = property.getRenderer().getModifierBlockUI(this, x, y);
			this.addRenderableWidget(widget);
			this.options.put(widget, property);
		});
		
		this.markConfigurationsDirty();
	}

	/**
	 * Forces all the Grappling Hook customizations to be re-evaluated, with
	 * appropriate UI changes being made.
	 */
	public void markConfigurationsDirty() {
		for (AbstractWidget b : this.options.keySet()) {
			CustomizationProperty<?> option = this.options.get(b);
			boolean enabled = true;

			String desc = option.getDescription().getString();
			
			if (!option.getValidityPredicate().shouldPass(this.customization)) {
				desc = Component.translatable("grapplemodifier.incompatability.desc").getString() + "\n" + desc;
				enabled = false;
			}

			switch (option.getAvailability()) {
				case REQUIRES_LIMITS -> {
					desc = Component.translatable("grapplemodifier.limits.desc").getString() + "\n" + desc;
					enabled = false;
				}

				case BLOCKED -> {
					desc = Component.translatable("grapplemodifier.locked.desc").getString() + "\n" + desc;
					enabled = false;
				}
			}
			
			b.active = enabled;

			if(enabled) {
				if(b instanceof CustomTooltipHandler tooltipHandle)
					tooltipHandle.resetTooltipOverride();
				b.setAlpha(1.0f);
				continue;
			}

			if (b instanceof CustomizationSlider slide) {
				slide.setTooltipOverride(Component.literal(desc));
				slide.setAlpha(0.5F);
				continue;
			}

			if (b instanceof CustomizationCheckbox check) {
				check.setTooltipOverride(Component.literal(desc));
				check.setAlpha(0.5F);
			}
		}
	}
	
	public int getLimits() {
		if(Minecraft.getInstance().player == null) return 0;
		return this.blockEntity.isUnlocked(GrappleModCustomizationCategories.LIMITS) || Minecraft.getInstance().player.isCreative()
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

	protected OnPress createCategoryActionHandler(CustomizationCategory category) {
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
