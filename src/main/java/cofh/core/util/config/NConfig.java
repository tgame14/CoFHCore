package cofh.core.util.config;

import net.minecraftforge.common.config.Configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The NConfig annotation is an annotation used to automatically insert values
 * into a configuration file, this is done by namespace based behaviour managed per mod.
 * as in, all classes within the package of cofh and all its sub-packages which are marked with @NConfig
 * will be inserted into a file of the choosing of CoFHCore after it is handled properly by it.
 *
 * @author tgame14
 * @since 30/08/2014
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NConfig
{
	String category () default Configuration.CATEGORY_GENERAL;

	String key () default "";

	String comment () default "";

}
