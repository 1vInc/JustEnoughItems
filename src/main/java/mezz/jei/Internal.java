package mezz.jei;

import javax.annotation.Nullable;

import mezz.jei.util.StackHelper;
import mezz.jei.util.color.ColorNamer;

/** For JEI internal use only, these are normally accessed from the API. */
public class Internal {
	private static JeiHelpers helpers = new JeiHelpers();
	@Nullable
	private static JeiRuntime runtime;
	@Nullable
	private static IngredientRegistry ingredientRegistry;
	@Nullable
	private static ColorNamer colorNamer;

	private Internal() {

	}

	public static JeiHelpers getHelpers() {
		return helpers;
	}

	public static void setHelpers(JeiHelpers helpers) {
		Internal.helpers = helpers;
	}

	public static StackHelper getStackHelper() {
		return helpers.getStackHelper();
	}

	@Nullable
	public static JeiRuntime getRuntime() {
		return runtime;
	}

	public static void setRuntime(JeiRuntime runtime) {
		JeiRuntime jeiRuntime = Internal.runtime;
		if (jeiRuntime != null) {
			jeiRuntime.close();
		}
		Internal.runtime = runtime;
	}

	@Nullable
	public static IngredientRegistry getIngredientRegistry() {
		return ingredientRegistry;
	}

	public static void setIngredientRegistry(@Nullable IngredientRegistry ingredientRegistry) {
		Internal.ingredientRegistry = ingredientRegistry;
	}

	@Nullable
	public static ColorNamer getColorNamer() {
		return colorNamer;
	}

	public static void setColorNamer(ColorNamer colorNamer) {
		Internal.colorNamer = colorNamer;
	}
}
