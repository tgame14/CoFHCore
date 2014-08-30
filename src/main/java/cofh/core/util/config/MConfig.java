package cofh.core.util.config;

import net.minecraftforge.common.config.Configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Similar to {@link NConfig}, but instead of having the fields inserted into a config file
 * based on Namespace, this one is {@link cofh.mod.updater.IUpdatableMod} based.
 *
 * @author tgame14
 * @since 30/08/2014
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MConfig
{
	String ownerMod ();

	String category () default Configuration.CATEGORY_GENERAL;

	String key () default "";

	String comment () default "";
}
