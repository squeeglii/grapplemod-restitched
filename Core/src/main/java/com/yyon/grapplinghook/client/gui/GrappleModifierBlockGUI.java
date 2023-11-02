package com.yyon.grapplinghook.client.gui;

import com.yyon.grapplinghook.client.gui.widget.*;
import com.yyon.grapplinghook.content.blockentity.GrappleModifierBlockEntity;
import com.yyon.grapplinghook.content.registry.GrappleModMetaRegistry;
import com.yyon.grapplinghook.customization.CustomizationCategory;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.customization.type.CustomizationProperty;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GrappleModifierBlockGUI extends Screen {

	private ModifierGUILayoutView currentView;

	public static final int FULL_SIZE_X = 260;
	public static final int FULL_SIZE_Y = 221;
	public static final int OUTER_PADDING_X = 16;
	public static final int OUTER_PADDING_Y = 16;

	// As there isn't scrolling, we just add more columns every time
	// a visibility limit is reached for 1 column.
	public static final int MAX_ROWS = 5;

	private static final int CONTROL_BUTTON_WIDTH = 50;
	private static final int CONTROL_BUTTON_HEIGHT = 20;
	private static final int COLUMN_PADDING = 4;


	private final OnPress actionGoBack = button -> showMainScreenLayout();
	private final GrappleModifierBlockEntity blockEntity;

	protected int guiLeft;
	protected int guiTop;

	private int widgetPosYIncrementor;

	private Component noticeMessage;

	private HashMap<AbstractWidget, CustomizationProperty<?>> options;
	private CustomizationVolume customization;
	private CustomizationCategory currentActiveCategory;


	public GrappleModifierBlockGUI(GrappleModifierBlockEntity blockEntity) {
		super(Component.translatable("grapple_modifier.title"));

		this.noticeMessage = null;

		this.blockEntity = blockEntity;
		this.customization = blockEntity.getCurrentCustomizations();
		this.currentActiveCategory = null;

		this.currentView = ModifierGUILayoutView.UNKNOWN;
	}


	@Override
	public void init() {
		this.guiLeft = (this.width - FULL_SIZE_X) / 2;
		this.guiTop = (this.height - FULL_SIZE_Y) / 2;

		// Init is called whenever the game window is resized.
		// This preserves the open menu of the screen.
		switch (currentView) {
			case HELP -> this.showHelpScreenLayout();
			case CATEGORY_LOCKED -> this.showCategoryLockedScreen(currentActiveCategory);
			case CATEGORY_PROPERTIES -> this.showCategoryScreen(currentActiveCategory);

			// Main, Unknown, and anything unimplemented.
			default -> this.showMainScreenLayout();
		}
	}

	@Override
	public void onClose() {
		this.blockEntity.setCustomizationClient(customization);
		super.onClose();
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return this.currentView == ModifierGUILayoutView.MAIN ||
			   this.currentView == ModifierGUILayoutView.UNKNOWN;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256 && !this.shouldCloseOnEsc()) {

			Component notice = switch (this.currentView) {
				case CATEGORY_PROPERTIES -> Component.translatable("grapple_modifier.notice.saved_customizations");
				default -> null;
			};

			this.showMainScreenLayout(notice);
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}


	// -- GUI Screens
	public void showMainScreenLayout() {
		this.showMainScreenLayout(null);
	}

	public void showMainScreenLayout(Component noticeMessage) {
		this.resetScreenLayout();
		this.currentView = ModifierGUILayoutView.MAIN;

		this.noticeMessage = noticeMessage;

		this.addRenderableWidget(Button.builder(
						Component.translatable("grapple_modifier.reset_button"),
						button -> {
							this.customization = new CustomizationVolume();
							this.showMainScreenLayout(Component.translatable("grapple_modifier.notice.reset_customizations"));
						})
				.pos(
						this.guiLeft + OUTER_PADDING_X,
						this.guiTop + FULL_SIZE_Y - CONTROL_BUTTON_HEIGHT - OUTER_PADDING_Y
				)
				.size(CONTROL_BUTTON_WIDTH, CONTROL_BUTTON_HEIGHT)
				.build()
		);

		this.addRenderableWidget(Button.builder(
						Component.translatable("grapple_modifier.help_button"),
						button -> this.showHelpScreenLayout())
				.pos(
						this.guiLeft + (FULL_SIZE_X / 2) - (CONTROL_BUTTON_WIDTH / 2),
						this.guiTop + FULL_SIZE_Y - CONTROL_BUTTON_HEIGHT - OUTER_PADDING_Y
				)
				.size(CONTROL_BUTTON_WIDTH, CONTROL_BUTTON_HEIGHT)
				.build()
		);

		this.addRenderableWidget(Button.builder(
				Component.translatable("grapple_modifier.close_button"),
				button -> this.onClose())
				.pos(
						this.guiLeft + FULL_SIZE_X - CONTROL_BUTTON_WIDTH - OUTER_PADDING_X,
						this.guiTop + FULL_SIZE_Y - CONTROL_BUTTON_HEIGHT - OUTER_PADDING_Y
				)
				.size(CONTROL_BUTTON_WIDTH, CONTROL_BUTTON_HEIGHT)
				.build()
		);

		if(this.noticeMessage != null) {
			this.addRenderableWidget(new FadingMultiLineTextWidget(
					this.guiLeft + OUTER_PADDING_X,
					this.guiTop + FULL_SIZE_Y - CONTROL_BUTTON_HEIGHT - 15 - OUTER_PADDING_Y,
					Component.empty()
							.withStyle(ChatFormatting.GREEN, ChatFormatting.ITALIC)
							.append(this.noticeMessage),
					40,
					this.font
			));

			this.noticeMessage = null;
		}

		int columnCount = Mth.positiveCeilDiv(GrappleModMetaRegistry.CUSTOMIZATION_CATEGORIES.size(), MAX_ROWS);

		// get full size, remove left+right padding.
		// + attempt to remove padding from the outermost columns.
		int xColumnViewMax = (FULL_SIZE_X - (2 * OUTER_PADDING_X)) + (2 * COLUMN_PADDING);
		int unpaddedColumnWidth = xColumnViewMax / columnCount;
		int columnWidth = unpaddedColumnWidth - (COLUMN_PADDING * 2);

		AtomicInteger counter = new AtomicInteger(0);
		GrappleModMetaRegistry.CUSTOMIZATION_CATEGORIES.stream().forEach(category -> {
			if (!category.shouldRender()) return;

			int i = counter.getAndIncrement();
			int y = Math.floorDiv(i, columnCount);
			int x = i % columnCount;

			int xColumnAligner = (x * unpaddedColumnWidth);

			this.addRenderableWidget(
					Button.builder(category.getName(), this.createCategoryActionHandler(category))
							.pos(
									this.guiLeft + xColumnAligner + OUTER_PADDING_X,
									this.guiTop + OUTER_PADDING_Y + 30 * y
							)
							.tooltip(Tooltip.create(category.getEmbedContent()))
							.size(columnWidth, 20)
							.build()
			);
		});
	}


	public void showCategoryLockedScreen(CustomizationCategory category) {
		this.resetScreenLayout();
		this.currentView = ModifierGUILayoutView.CATEGORY_LOCKED;

		this.addRenderableWidget(
				Button.builder(Component.translatable("grapple_modifier.back_button"), actionGoBack)
						.pos(this.guiLeft + OUTER_PADDING_X, this.guiTop + FULL_SIZE_Y - 20 - OUTER_PADDING_Y)
						.size(50, 20)
						.build()
		);

		this.currentActiveCategory = category;

		int posX = this.guiLeft + OUTER_PADDING_X;
		int posY = this.guiTop + OUTER_PADDING_Y;

		String categoryName = "\"%s\"".formatted(this.currentActiveCategory.getName().getString());

		MultiLineTextWidget title = new MultiLineTextWidget(
				posX, posY,
				Component.translatable("grapple_modifier.category_unlock.title", categoryName)
						.withStyle(ChatFormatting.BOLD, ChatFormatting.UNDERLINE),
				this.font
		).setMaxWidth(FULL_SIZE_X - (3 * OUTER_PADDING_X));

		MultiLineTextWidget description = new MultiLineTextWidget(
				posX,
				posY + title.getHeight() + 10,
				Component.translatable("grapple_modifier.category_unlock.desc"),
				this.font
		).setMaxWidth(FULL_SIZE_X - (3 * OUTER_PADDING_X));

		this.addRenderableWidget(title);
		this.addRenderableWidget(description);
	}


	public void showHelpScreenLayout() {
		this.resetScreenLayout();
		this.currentView = ModifierGUILayoutView.HELP;

		this.addRenderableWidget(
				Button.builder(Component.translatable("grapple_modifier.back_button"), actionGoBack)
						.pos(this.guiLeft + OUTER_PADDING_X, this.guiTop + FULL_SIZE_Y - 20 - OUTER_PADDING_Y)
						.size(50, 20)
						.build()
		);

		MultiLineTextWidget title = new MultiLineTextWidget(
				this.guiLeft + OUTER_PADDING_X,
				this.guiTop + OUTER_PADDING_Y,
				Component.translatable("grapple_modifier.help.title")
						.withStyle(ChatFormatting.BOLD, ChatFormatting.UNDERLINE),
				this.font
		).setMaxWidth(FULL_SIZE_X - (3 * OUTER_PADDING_X));

        MultiLineTextWidget guide = new MultiLineTextWidget(
				this.guiLeft + OUTER_PADDING_X,
				this.guiTop + OUTER_PADDING_Y + title.getHeight() + 10,
				Component.translatable("grapple_modifier.help.desc"),
				this.font
		).setMaxWidth(FULL_SIZE_X - (3 * OUTER_PADDING_X));

        this.addRenderableWidget(title);
		this.addRenderableWidget(guide);
	}

	public void showCategoryScreen(CustomizationCategory category) {
		this.resetScreenLayout();
		this.currentView = ModifierGUILayoutView.CATEGORY_PROPERTIES;

		this.addRenderableWidget(
				Button.builder(Component.translatable("grapple_modifier.back_button"), action ->
								this.showMainScreenLayout(Component.translatable("grapple_modifier.notice.saved_customizations"))
						)
						.pos(this.guiLeft + OUTER_PADDING_X, this.guiTop + FULL_SIZE_Y - 20 - OUTER_PADDING_Y)
						.size(50, 20)
						.build()
		);

		this.currentActiveCategory = category;

		this.currentActiveCategory.getLinkedProperties().forEach(property -> {
			int x = this.guiLeft + OUTER_PADDING_X;
			int y = this.getNextYPosition() + OUTER_PADDING_Y;

			AbstractWidget widget = property.getDisplay().getModifierBlockUI(this, x, y);
			this.addRenderableWidget(widget);
			this.options.put(widget, property);
		});
		
		this.markConfigurationsDirty();
	}


	// -- UI Helpers

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
				desc = Component.translatable("grapple_property.incompatible").getString() + "\n" + desc;
				enabled = false;
			}

			switch (option.getAvailability()) {
				case REQUIRES_LIMITS -> {
					desc = Component.translatable("grapple_property.limits_restricted").getString() + "\n" + desc;
					enabled = false;
				}

				case BLOCKED -> {
					desc = Component.translatable("grapple_property.blocked").getString() + "\n" + desc;
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

			if (b instanceof PreciseCustomizationSlider slide) {
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

	public void resetScreenLayout() {
		this.currentView = ModifierGUILayoutView.UNKNOWN;
		this.currentActiveCategory = null;
		this.widgetPosYIncrementor = 0;
		this.options = new HashMap<>();
		this.clearWidgets();

		this.addRenderableWidget(new BackgroundWidget(this.guiLeft, this.guiTop, FULL_SIZE_X, FULL_SIZE_Y));
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

			this.showCategoryLockedScreen(category);
		};
	}

	public CustomizationVolume getCurrentCustomizations() {
		return this.customization;
	}
}
