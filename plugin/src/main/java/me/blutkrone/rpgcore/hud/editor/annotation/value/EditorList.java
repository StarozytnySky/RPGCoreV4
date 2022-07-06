package me.blutkrone.rpgcore.hud.editor.annotation.value;

import me.blutkrone.rpgcore.hud.editor.IEditorConstraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Treat as a string reference to an attribute.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EditorList {
    /**
     * Name of the field being modified.
     *
     * @return a displayable name for the field.
     */
    String name();

    /**
     * The constraint of the object input.
     *
     * @return an array of valid inputs.
     */
    Class<? extends IEditorConstraint> constraint();

    /**
     * Only allows one element in the list.
     *
     * @return whether to limit elements to a maximum of 1.
     */
    boolean singleton() default false;
}

