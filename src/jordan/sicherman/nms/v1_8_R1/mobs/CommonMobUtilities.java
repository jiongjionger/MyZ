/**
 * 
 */
package jordan.sicherman.nms.v1_8_R1.mobs;

import java.lang.reflect.Field;

import net.minecraft.server.v1_8_R1.PathfinderGoalSelector;

/**
 * @author Jordan
 * 
 */
public class CommonMobUtilities {

	protected static Field bField;
	static {
		try {
			bField = PathfinderGoalSelector.class.getDeclaredField("b");
			bField.setAccessible(true);
		} catch (Exception exc) {
			bField = null;
		}
	}
	protected static Field cField;
	static {
		try {
			cField = PathfinderGoalSelector.class.getDeclaredField("c");
			cField.setAccessible(true);
		} catch (Exception exc) {
			cField = null;
		}
	}
}
