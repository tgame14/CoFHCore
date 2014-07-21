package cofh.mod;

import cofh.updater.IUpdatableMod;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringTranslate;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.AbstractLogger;

public abstract class BaseMod implements IUpdatableMod {

	protected File _configFolder;
	protected final String _modid;
	protected final Logger _log;

	protected BaseMod(Logger log) {

		String name = getModId();
		_modid = name.toLowerCase();
		_log = log;
	}

	protected BaseMod() {

		String name = getModId();
		_modid = name.toLowerCase();
		_log = LogManager.getLogger(name);
	}

	protected String getConfigBaseFolder() {

		String base = getClass().getPackage().getName();
		int i = base.indexOf('.');
		if (i >= 0) {
			return base.substring(0, base.indexOf('.'));
		}
		return "";
	}

	protected void setConfigFolderBase(File folder) {

		_configFolder = new File(folder, getConfigBaseFolder() + "/" + _modid + "/");
	}

	protected File getConfig(String name) {

		return new File(_configFolder, name + ".cfg");
	}

	protected File getClientConfig() {

		return getConfig("client");
	}

	protected File getCommonConfig() {

		return getConfig("common");
	}

	protected String getAssetDir() {

		return _modid;
	}

	@Override
	public Logger getLogger() {

		return _log;
	}

	protected void loadLang() {

		if (FMLLaunchHandler.side() == Side.CLIENT) {
			try {
				loadClientLang();
				return;
			} catch (Throwable _) {
				_log.error(AbstractLogger.CATCHING_MARKER, "???", _);
			}
		}
		
		try {
			loadLanguageFile("en_US", getClass().getResourceAsStream("assets/" + getAssetDir() + "/language/en_us.lang"));
		} catch (Throwable _) {
			_log.error(AbstractLogger.CATCHING_MARKER, "Error loading en_US from jar on server", _);
		}
	}

	@SuppressWarnings("unchecked")
	private void loadLanguageFile(String lang, InputStream stream) throws Throwable {

		InputStreamReader is = new InputStreamReader(stream, "UTF-8");

		Properties langPack = new Properties();
		langPack.load(is);
		
		StringTranslate.instance.languageList.putAll(langPack);
	}

	@SideOnly(Side.CLIENT)
	private void loadClientLang() {

		IReloadableResourceManager manager = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();
		LangManager m = new LangManager();
		manager.registerReloadListener(m);
		m.onResourceManagerReload(manager);
	}

	@SideOnly(Side.CLIENT)
	private class LangManager implements IResourceManagerReloadListener {

		private final String _path;

		public LangManager() {

			_path = getAssetDir() + ":language/";
		}

		@Override
		public void onResourceManagerReload(IResourceManager manager) {

			String l = null;
			try {
				l = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
			} catch (Throwable _) {
				_log.catching(Level.WARN, _);
			}

			for (String lang : Arrays.asList("en_US", l))
				if (lang != null) try {
					List<IResource> files = manager.getAllResources(new ResourceLocation(_path + lang + ".lang"));
					for (IResource file : files) {
						if (file.getInputStream() == null) {
							_log.warn("A resource pack defines an entry for language '" + lang + "' but the InputStream is null.");
							continue;
						}
						try {
							loadLanguageFile(lang, file.getInputStream());
						} catch (Throwable _) {
							_log.warn(AbstractLogger.CATCHING_MARKER, "A resource pack has a file for language '" + lang + "' but the file is invalid.", _);
						}
					}
				} catch (Throwable _) {
					_log.info(AbstractLogger.CATCHING_MARKER, "No language data for '" + lang + "'", _);
				}
		}
	}

}
