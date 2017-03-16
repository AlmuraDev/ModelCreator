package com.mrcrayfish.modelcreator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.jtattoo.plaf.fast.FastLookAndFeel;
import org.apache.commons.lang3.SystemUtils;

public class Start
{
	public enum PLATFORM { LINUX, OSX, WINDOWS }
	private static final Map<PLATFORM, String[]> resources = new HashMap<>();

	static {
		resources.put(PLATFORM.LINUX, new String[] { "liblwjgl.so", "liblwjgl64.so", "libopenal.so", "libopenal64.so" });
		resources.put(PLATFORM.OSX, new String[] { "liblwjgl.dylib", "openal.dylib" });
		resources.put(PLATFORM.WINDOWS, new String[] { "lwjgl.dll", "lwjgl64.dll", "OpenAL32.dll", "OpenAL64.dll" });
	}

	public static void main(String[] args)  {
		Double version = Double.parseDouble(System.getProperty("java.specification.version"));
		if (version < 1.8)
		{
			JOptionPane.showMessageDialog(null, "You need Java 1.8 or higher to run this program.");
			return;
		}

		resources.forEach((k, v) -> {
			for (String resource : v) {
				final InputStream ddlStream = Start.class.getClassLoader().getResourceAsStream(resource);
				final Path nativePlatformPath = Paths.get("natives" + File.separator + k.name().toLowerCase() + File.separator + resource);
				if (Files.notExists(nativePlatformPath.getParent())) {
					try {
						Files.createDirectories(nativePlatformPath.getParent());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				try (FileOutputStream fos = new FileOutputStream(nativePlatformPath.toFile().getPath())) {
					final byte[] buf = new byte[2048];
					int r;
					while(-1 != (r = ddlStream.read(buf))) {
						fos.write(buf, 0, r);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		if (SystemUtils.IS_OS_WINDOWS) {
			System.setProperty("org.lwjgl.librarypath", new File("natives/" + PLATFORM.WINDOWS.name().toLowerCase()).getAbsolutePath());
		} else if (SystemUtils.IS_OS_MAC) {
			System.setProperty("org.lwjgl.librarypath", new File("natives/" + PLATFORM.OSX.name().toLowerCase()).getAbsolutePath());
		} else if (SystemUtils.IS_OS_LINUX) {
			System.setProperty("org.lwjgl.librarypath", new File("natives/" + PLATFORM.LINUX.name().toLowerCase()).getAbsolutePath());
		} else {
			throw new UnsupportedOperationException("Unable to determine operating system.");
		}

		try
		{
			Properties props = new Properties();
			props.put("logoString", "");
			props.put("centerWindowTitle", "on");
			props.put("buttonBackgroundColor", "127 132 145");
			props.put("buttonForegroundColor", "255 255 255");
			props.put("windowTitleBackgroundColor", "97 102 115");
			props.put("windowTitleForegroundColor", "255 255 255");
			props.put("backgroundColor", "221 221 228");
			props.put("menuBackgroundColor", "221 221 228");
			props.put("controlForegroundColor", "120 120 120");
			props.put("windowBorderColor", "97 102 110");
			FastLookAndFeel.setTheme(props);
			UIManager.setLookAndFeel("com.jtattoo.plaf.fast.FastLookAndFeel");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		new ModelCreator(Constants.NAME + " - " + Constants.VERSION);
	}
}
