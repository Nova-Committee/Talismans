package nova.committee.talismans.util;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/26 13:38
 * Version: 1.0
 */
public class ProtectedMethodAccess<C, T>
{
    private LazyOptional<Method> method;

    /** A way of accessing protected fields.
     * @param classContainingField This is the class the field is a member of.
     * @param fieldName This is the SRG name (unmapped name) of the field. **/
    public ProtectedMethodAccess(Class<C> classContainingField, String fieldName, Class<?>... args)
    {
        this.method = LazyOptional.of(() ->
        {
            Method innerField = ObfuscationReflectionHelper.findMethod(classContainingField, fieldName, args);
            innerField.setAccessible(true);
            return innerField;
        });
    }

    /** Returns the value of the field, or null if there was an error. **/
    @SuppressWarnings("unchecked")
    public T getValue(C instance, Object... args)
    {
        try
        {
            if (method.resolve().isPresent())
                return (T) method.resolve().get().invoke(instance, args);
            else
                return (T) method.resolve().orElseThrow();
        }
        catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
