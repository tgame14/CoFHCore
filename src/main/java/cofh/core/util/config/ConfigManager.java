package cofh.core.util.config;

import cpw.mods.fml.common.discovery.ASMDataTable;
import gnu.trove.set.hash.THashSet;
import net.minecraftforge.common.config.Configuration;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * @author tgame14
 * @since 30/08/2014
 */
public class ConfigManager
{
	private Set<ASMDataTable.ASMData> nconfigs;
	private Set<ASMDataTable.ASMData> mconfigs;

	protected Set<Class> nclasses;
	protected Set<Class> mclasses;

	protected ConfigManager()
	{
		this.nconfigs = new THashSet<ASMDataTable.ASMData>();
		this.mconfigs = new THashSet<ASMDataTable.ASMData>();

		this.nclasses = new THashSet<Class>();
		this.mclasses = new THashSet<Class>();
	}

	private static ConfigManager INSTANCE;

	public static ConfigManager Instance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new ConfigManager();
		}

		return INSTANCE;
	}

	public void generateSets(ASMDataTable table)
	{
		this.nconfigs = table.getAll("cofh.config.NConfig");
		this.mconfigs = table.getAll("cofh.config.MConfig");

		for (ASMDataTable.ASMData data : this.nconfigs)
		{
			try
			{
				this.nclasses.add(Class.forName(data.getClassName()));
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}

		for (ASMDataTable.ASMData data : this.mconfigs)
		{
			try
			{
				this.mclasses.add(Class.forName(data.getClassName()));
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}

		}
	}

	public void nconfigure (Configuration config, String namespace)
	{
		config.load();

		for (Class clazz : this.nclasses)
		{
			if (clazz.getName().startsWith(namespace))
			{
				for (Field field : clazz.getDeclaredFields())
				{
					NConfig cfg = field.getAnnotation(NConfig.class);
					if (cfg != null)
					{
						handleField(field, cfg, config);
					}
				}
			}
		}
	}

	public void mconfigure ()
	{

	}

	private static void handleField(Field field, NConfig cfg, Configuration config)
	{
		try
		{
			// Set field and annotation data Handled before handing the write of field to config
			field.setAccessible(true);
			String key;

			if (cfg.key().isEmpty())
			{
				key = field.getName();
			}

			else
			{
				key = cfg.key();
			}

			String comment = !cfg.comment().isEmpty() ? cfg.comment() : null;

			// if field is Array, use Config lists, otherwise use default config read and writes
			if (!field.getType().isArray())
			{
				if (field.getType() == Integer.TYPE)
				{
					int value = config.get(cfg.category(), key, field.getInt(null), comment).getInt(field.getInt(null));
					field.setInt(null, value);
				}
				else if (field.getType() == Double.TYPE)
				{
					double value = config.get(cfg.category(), key, field.getDouble(null), comment).getDouble(field.getDouble(null));
					field.setDouble(null, value);
				}

				else if (field.getType() == Float.TYPE)
				{
					float value = (float) config.get(cfg.category(), key, field.getFloat(null), comment).getDouble(field.getDouble(null));
					field.setFloat(null, value);
				}
				else if (field.getType() == String.class)
				{
					String value = config.get(cfg.category(), key, (String) field.get(null), comment).getString();
					field.set(null, value);
				}
				else if (field.getType() == Boolean.TYPE)
				{
					boolean value = config.get(cfg.category(), key, field.getBoolean(null), comment).getBoolean(field.getBoolean(null));
					field.setBoolean(null, value);
				}
				else if (field.getType() == Long.TYPE)
				{
					// TODO: Add support for reading long values
					long value = config.get(cfg.category(), key, field.getLong(null), comment).getInt();
					field.setLong(null, value);
				}
			}

			else
			{
				if (field.getType().getComponentType() == String.class)
				{
					String[] values = config.get(cfg.category(), key, (String[]) field.get(null), comment).getStringList();
					field.set(null, values);
				}
				else if (field.getType().getComponentType() == int.class)
				{
					int[] values = config.get(cfg.category(), key, (int[]) field.get(null), comment).getIntList();
					field.set(null, values);
				}
				else if (field.getType().getComponentType() == boolean.class)
				{
					boolean[] values = config.get(cfg.category(), key, (boolean[]) field.get(null), comment).getBooleanList();
					field.set(null, values);
				}
				// TODO Add support for reading Long[] lists from config
			}
		}
		catch (Exception e)
		{
			System.out.println("Failed to configure: " + field.getName());
			e.printStackTrace();
		}
	}


}
