package mezz.jei.gui;

import javax.annotation.Nullable;
import java.util.List;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.gui.ingredients.GuiFluidStackGroup;
import mezz.jei.gui.ingredients.GuiIngredient;
import mezz.jei.gui.ingredients.GuiItemStackGroup;
import mezz.jei.input.IClickedIngredient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class RecipeLayout implements IRecipeLayout {
	private static final int RECIPE_BUTTON_SIZE = 12;
	public static final int recipeTransferButtonIndex = 100;

	private final IRecipeCategory recipeCategory;
	private final GuiItemStackGroup guiItemStackGroup;
	private final GuiFluidStackGroup guiFluidStackGroup;
	private final RecipeTransferButton recipeTransferButton;
	private final IRecipeWrapper recipeWrapper;

	private final int posX;
	private final int posY;

	public <T extends IRecipeWrapper> RecipeLayout(int index, int posX, int posY, IRecipeCategory<T> recipeCategory, T recipeWrapper, MasterFocus focus) {
		this.recipeCategory = recipeCategory;

		ItemStack itemStackFocus = null;
		FluidStack fluidStackFocus = null;
		Object focusValue = focus.getFocus().getValue();
		if (focusValue instanceof ItemStack) {
			itemStackFocus = (ItemStack) focusValue;
		} else if (focusValue instanceof FluidStack) {
			fluidStackFocus = (FluidStack) focusValue;
		}
		this.guiItemStackGroup = new GuiItemStackGroup(new Focus<ItemStack>(focus.getMode(), itemStackFocus));
		this.guiFluidStackGroup = new GuiFluidStackGroup(new Focus<FluidStack>(focus.getMode(), fluidStackFocus));

		int width = recipeCategory.getBackground().getWidth();
		int height = recipeCategory.getBackground().getHeight();
		this.recipeTransferButton = new RecipeTransferButton(recipeTransferButtonIndex + index, posX + width + 2, posY + height - RECIPE_BUTTON_SIZE, RECIPE_BUTTON_SIZE, RECIPE_BUTTON_SIZE, "+");
		this.posX = posX;
		this.posY = posY;

		this.recipeWrapper = recipeWrapper;
		recipeCategory.setRecipe(this, recipeWrapper);
	}

	public void draw(Minecraft minecraft, int mouseX, int mouseY) {
		IDrawable background = recipeCategory.getBackground();

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		GlStateManager.enableAlpha();

		GlStateManager.pushMatrix();
		GlStateManager.translate(posX, posY, 0.0F);
		{
			background.draw(minecraft);
			recipeCategory.drawExtras(minecraft);
			recipeCategory.drawAnimations(minecraft);
			recipeWrapper.drawAnimations(minecraft, background.getWidth(), background.getHeight());
		}
		GlStateManager.popMatrix();

		final int recipeMouseX = mouseX - posX;
		final int recipeMouseY = mouseY - posY;

		GlStateManager.pushMatrix();
		GlStateManager.translate(posX, posY, 0.0F);
		{
			recipeWrapper.drawInfo(minecraft, background.getWidth(), background.getHeight(), recipeMouseX, recipeMouseY);
		}
		GlStateManager.popMatrix();

		RenderHelper.enableGUIStandardItemLighting();
		GuiIngredient hoveredItemStack = guiItemStackGroup.draw(minecraft, posX, posY, mouseX, mouseY);
		RenderHelper.disableStandardItemLighting();
		GuiIngredient hoveredFluidStack = guiFluidStackGroup.draw(minecraft, posX, posY, mouseX, mouseY);

		recipeTransferButton.drawButton(minecraft, mouseX, mouseY);
		GlStateManager.disableBlend();
		GlStateManager.disableLighting();

		if (hoveredItemStack != null) {
			RenderHelper.enableGUIStandardItemLighting();
			hoveredItemStack.drawHovered(minecraft, posX, posY, recipeMouseX, recipeMouseY);
			RenderHelper.disableStandardItemLighting();
		} else if (hoveredFluidStack != null) {
			hoveredFluidStack.drawHovered(minecraft, posX, posY, recipeMouseX, recipeMouseY);
		} else if (isMouseOver(mouseX, mouseY)) {
			List<String> tooltipStrings = recipeWrapper.getTooltipStrings(recipeMouseX, recipeMouseY);
			if (tooltipStrings != null && !tooltipStrings.isEmpty()) {
				TooltipRenderer.drawHoveringText(minecraft, tooltipStrings, mouseX, mouseY);
			}
		}

		GlStateManager.disableAlpha();
	}

	public boolean isMouseOver(int mouseX, int mouseY) {
		final int recipeMouseX = mouseX - posX;
		final int recipeMouseY = mouseY - posY;
		final IDrawable background = recipeCategory.getBackground();
		return recipeMouseX >= 0 && recipeMouseX < background.getWidth() && recipeMouseY >= 0 && recipeMouseY < background.getHeight();
	}

	@Nullable
	public IClickedIngredient<?> getIngredientUnderMouse(int mouseX, int mouseY) {
		IClickedIngredient<?> clicked = guiItemStackGroup.getIngredientUnderMouse(posX, posY, mouseX, mouseY);
		if (clicked == null) {
			clicked = guiFluidStackGroup.getIngredientUnderMouse(posX, posY, mouseX, mouseY);
		}
		return clicked;
	}

	public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
		return recipeWrapper.handleClick(minecraft, mouseX - posX, mouseY - posY, mouseButton);
	}

	@Override
	public GuiItemStackGroup getItemStacks() {
		return guiItemStackGroup;
	}

	@Override
	public IGuiFluidStackGroup getFluidStacks() {
		return guiFluidStackGroup;
	}

	@Override
	public void setRecipeTransferButton(int posX, int posY) {
		recipeTransferButton.xPosition = posX + this.posX;
		recipeTransferButton.yPosition = posY + this.posY;
	}

	public RecipeTransferButton getRecipeTransferButton() {
		return recipeTransferButton;
	}

	public IRecipeWrapper getRecipeWrapper() {
		return recipeWrapper;
	}

	public IRecipeCategory getRecipeCategory() {
		return recipeCategory;
	}

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}
}
