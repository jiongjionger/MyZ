/**
 * 
 */
package jordan.sicherman.nms.utilities;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import jordan.sicherman.MyZ;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author Jordan
 * 
 */
public class NMS {

	public static enum Version {
		v1_8_R1("v1_8_R1"), v1_7_R4("v1_7_R4");

		private final String identifier;

		private Version(String identifier) {
			this.identifier = identifier;
		}

		public String getIdentifier() {
			return identifier;
		}

		public static Version fromString(String identifier) {
			for (Version v : Version.values()) {
				if (v.getIdentifier().equals(identifier)) { return v; }
			}
			return null;
		}
	}

	private static final String packageName = Bukkit.getServer().getClass().getPackage().getName();
	public static final String version = packageName.substring(packageName.lastIndexOf(".") + 1);
	public static final Version compatibilityVersion = Version.fromString(version);
	private static Class<?> craftPlayer, packet;
	private static Method getHandle, sendPacket;
	private static Field connection;

	public static Object castToCraft(Player player) {
		if (craftPlayer == null) {
			try {
				craftPlayer = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}
		if (craftPlayer == null) { return null; }
		return craftPlayer.cast(player);
	}

	public static Object castToNMS(Player player) {
		Object craft = castToCraft(player);
		if (craft == null) { return null; }
		if (getHandle == null) {
			try {
				getHandle = craftPlayer.getMethod("getHandle");
			} catch (Exception exc) {
				return null;
			}
		}
		try {
			return getHandle.invoke(castToCraft(player));
		} catch (Exception exc) {
			return null;
		}
	}

	public static void sendPacket(Object inPacket, Player inPlayer) throws Exception {
		if (packet == null) {
			packet = Class.forName("net.minecraft.server." + version + ".Packet");
		}

		Object handle = castToNMS(inPlayer);
		if (handle == null) { return; }
		if (connection == null) {
			connection = handle.getClass().getField("playerConnection");
		}
		Object con = connection.get(handle);
		if (con == null) { return; }
		if (sendPacket == null) {
			sendPacket = con.getClass().getMethod("sendPacket", packet);
		}
		if (sendPacket != null) {
			sendPacket.invoke(con, inPacket);
		}
	}

	private static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
		try {
			return clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			Class<?> superClass = clazz.getSuperclass();
			if (superClass == null) {
				throw e;
			} else {
				return getField(superClass, fieldName);
			}
		}
	}

	public static void setDeclaredField(Object obj, String fieldName, Object value) {
		try {
			Field field = getField(obj.getClass(), fieldName);
			field.setAccessible(true);
			field.set(obj, value);
			field.setAccessible(false);
		} catch (NoSuchFieldException e) {
			MyZ.log(ChatColor.RED + "Did the field '" + fieldName + "' change?");
		} catch (Exception e) {
			MyZ.log(ChatColor.RED + "MyZ does not have sufficient privileges.");
		}
	}

	public static Object getPrivateStatic(Class<?> clazz, String fieldName) {
		Object value = null;
		try {
			Field field = getField(clazz, fieldName);
			field.setAccessible(true);
			value = field.get(null);
		} catch (NoSuchFieldException e) {
			MyZ.log(ChatColor.RED + "Did the field '" + fieldName + "' change?");
		} catch (Exception e) {
			MyZ.log(ChatColor.RED + "MyZ does not have sufficient privileges.");
		}
		return value;
	}

	public static Object getDeclaredField(Object obj, String fieldName) {
		Object value = null;
		try {
			Field field = getField(obj.getClass(), fieldName);
			field.setAccessible(true);
			value = field.get(obj);
		} catch (NoSuchFieldException e) {
			MyZ.log(ChatColor.RED + "Did the field '" + fieldName + "' change?");
		} catch (Exception e) {
			MyZ.log(ChatColor.RED + "MyZ does not have sufficient privileges.");
		}
		return value;
	}
}
