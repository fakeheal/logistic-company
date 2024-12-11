package nbu.team11.controllers.forms.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


/**
 * Class that provides utility methods for creating select options
 * from enums and lists of objects.
 */
public class SelectUtil {

    /**
     * Creates a list of select options from an enum.
     *
     * @param enumClass Enum class to be converted to select options
     * @return List of select options
     */
    public static <E extends Enum<E>> List<SelectOption> fromEnum(Class<E> enumClass) {
        List<SelectOption> options = new ArrayList<>();
        for (E e : enumClass.getEnumConstants()) {
            options.add(new SelectOption(e.name(), e.toString()));
        }
        return options;
    }

    /**
     * Creates a list of select options from a list of objects.
     *
     * @param items       List of objects to be converted to select options
     * @param valueMapper Function that maps an object to its value
     * @param labelMapper Function that maps an object to its label
     * @return List of select options
     */
    public static <T> List<SelectOption> fromList(List<T> items, Function<T, String> valueMapper, Function<T, String> labelMapper) {
        List<SelectOption> options = new ArrayList<>();
        for (T item : items) {
            options.add(new SelectOption(valueMapper.apply(item), labelMapper.apply(item)));
        }
        return options;
    }
}
