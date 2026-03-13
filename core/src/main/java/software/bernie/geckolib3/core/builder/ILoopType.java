package software.bernie.geckolib3.core.builder;

import java.util.Locale;

public interface ILoopType {

	boolean isRepeatingAfterEnd();

	enum EDefaultLoopTypes implements ILoopType {
		LOOP(true),
		PLAY_ONCE,
		HOLD_ON_LAST_FRAME;

		private final boolean looping;

		EDefaultLoopTypes(boolean looping) {
			this.looping = looping;
		}

		EDefaultLoopTypes() {
			this(false);
		}

		@Override
		public boolean isRepeatingAfterEnd() {
			return this.looping;
		}
	}

	static ILoopType fromString(String json) {
		if (json == null) {
			return EDefaultLoopTypes.PLAY_ONCE;
		}

		if (json.equalsIgnoreCase("false")) {
			return EDefaultLoopTypes.PLAY_ONCE;
		}

		if (json.equalsIgnoreCase("true")) {
			return EDefaultLoopTypes.LOOP;
		}

		try {
			return EDefaultLoopTypes.valueOf(json.toUpperCase(Locale.ROOT));
		}
		catch (Exception ex) {
			return EDefaultLoopTypes.PLAY_ONCE;
		}
	}
}
