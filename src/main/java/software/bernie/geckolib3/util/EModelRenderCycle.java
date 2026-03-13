package software.bernie.geckolib3.util;

/**
 * Tracks the current render cycle for layer differentiation.
 *
 * @author DerToaster98
 */
public enum EModelRenderCycle implements IRenderCycle {
	/** Initial rendering of the model in this frame. Armor, heads, and items render during this phase. */
	INITIAL,
	/** The model was re-rendered by a layer renderer or something else during this frame. */
	REPEATED,
	/** For special use by the user, unused by default. */
	SPECIAL
}
