package com.leclowndu93150.chisel.integration.jei;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.carving.ICarvingGroup;
import com.leclowndu93150.chisel.carving.CarvingHelper;
import com.leclowndu93150.chisel.init.ChiselItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ChiselRecipeCategory implements IRecipeCategory<ICarvingGroup> {

    public static final RecipeType<ICarvingGroup> RECIPE_TYPE = RecipeType.create(
            Chisel.MODID,
            "chiseling",
            ICarvingGroup.class
    );

    private static final ResourceLocation TEXTURE_LOC = Chisel.id("textures/chiseljei.png");

    private final IDrawable icon;
    private final IDrawable background;
    private final IDrawable arrowUp, arrowDown;

    private @Nullable IRecipeLayoutBuilder layout;
    private @Nullable IFocusGroup focus;

    public ChiselRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ChiselItems.IRON_CHISEL.get()));
        this.background = guiHelper.createDrawable(TEXTURE_LOC, 0, 0, 165, 126);
        this.arrowDown = guiHelper.createDrawable(TEXTURE_LOC, 166, 0, 18, 15);
        this.arrowUp = guiHelper.createDrawable(TEXTURE_LOC, 166, 15, 18, 15);
    }

    @Nonnull
    @Override
    public RecipeType<ICarvingGroup> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.chisel.chiseling");
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(ICarvingGroup recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        if (layout != null) {
            if (focus == null || focus.getFocuses(VanillaTypes.ITEM_STACK, RecipeIngredientRole.OUTPUT).count() == 0) {
                arrowDown.draw(guiGraphics, 73, 21);
            } else {
                arrowUp.draw(guiGraphics, 73, 21);
            }
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, ICarvingGroup group, IFocusGroup focusGroup) {
        this.layout = recipeLayout;
        this.focus = focusGroup;

        // Input slot at top center
        IRecipeSlotBuilder inputSlot = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 74, 4);

        // Get all items in the carving group
        List<ItemStack> groupStacks = CarvingHelper.getItemsInGroup(group.getItemTag()).stream()
                .map(ItemStack::new)
                .toList();

        if (focusGroup.getFocuses(VanillaTypes.ITEM_STACK, RecipeIngredientRole.OUTPUT).count() > 0) {
            inputSlot.addItemStacks(focusGroup.getFocuses(VanillaTypes.ITEM_STACK, RecipeIngredientRole.OUTPUT)
                    .map(f -> f.getTypedValue().getIngredient())
                    .toList());
        } else {
            inputSlot.addItemStacks(groupStacks);
        }

        int rowWidth = 9;
        int xStart = 3;
        int yStart = 37;
        int MAX_SLOTS = 45;

        List<List<ItemStack>> stacks = new ArrayList<>();

        for (int i = 0; i < groupStacks.size(); i++) {
            int slot = i % MAX_SLOTS;
            if (stacks.size() <= slot) {
                stacks.add(new ArrayList<>());
            }
            stacks.get(slot).add(groupStacks.get(i).copy());
        }

        if (groupStacks.size() > MAX_SLOTS) {
            int leftover = groupStacks.size() % MAX_SLOTS;
            for (int i = leftover; i < MAX_SLOTS; i++) {
                stacks.get(i).add(ItemStack.EMPTY);
            }
        }

        for (int i = 0; i < stacks.size(); i++) {
            int x = xStart + (i % rowWidth) * 18;
            int y = yStart + (i / rowWidth) * 18;
            recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, x, y).addItemStacks(stacks.get(i));
        }

        ItemStack craftableBlock = CraftableBlockCache.get(group.getId());
        if (!craftableBlock.isEmpty()) {
            recipeLayout.addSlot(RecipeIngredientRole.RENDER_ONLY, 147, 4).addItemStack(craftableBlock);
        }
    }
}
