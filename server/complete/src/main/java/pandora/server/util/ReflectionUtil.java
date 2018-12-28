package pandora.server.util;

import java.lang.reflect.Type;
import java.util.List;

import org.modelmapper.TypeToken;

public class ReflectionUtil {

	private ReflectionUtil() {}

	public static <T> Type getTypeToken(T type) {
		return new TypeToken<List<T>>() {}.getType();
	}
}
