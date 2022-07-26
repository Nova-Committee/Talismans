package nova.committee.talismans.util;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/26 15:14
 * Version: 1.0
 */
public class ProtectedFieldAccess<T, C>
{    private LazyOptional<Field> field;

    /** A way of accessing protected fields.
     * @param classContainingField This is the class the field is a member of.
     * @param fieldName This is the SRG name (unmapped name) of the field. **/
    public ProtectedFieldAccess(Class<C> classContainingField, String fieldName)
    {
        this.field = LazyOptional.of(() ->
        {
            Field innerField = ObfuscationReflectionHelper.findField(classContainingField, fieldName);
            innerField.setAccessible(true);
            return innerField;
        });
    }

    /** Returns the value of the field, or null if there was an error. **/
    @SuppressWarnings("unchecked")
    public T getValue(C instance)
    {
        try
        {
            return (T)field.resolve().get().get(instance);
        }
        catch (IllegalArgumentException | IllegalAccessException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
