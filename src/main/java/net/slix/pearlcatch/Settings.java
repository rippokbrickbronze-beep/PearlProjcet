package net.slix.pearlcatch;

import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Module state + rebindable keys, saved to .minecraft/config/pearlcatch.properties */
public final class Settings {
    public static boolean enabled = false;
    /** Key that toggles PearlCatch. -1 = unbound. Change it in the ClickGUI. */
    public static int moduleBind = GLFW.GLFW_KEY_UNKNOWN;
    /** Key that opens the ClickGUI. Default Right Shift. Change it in the ClickGUI. */
    public static int guiKey = GLFW.GLFW_KEY_RIGHT_SHIFT;

    private Settings() {}

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("pearlcatch.properties");
    }

    public static void load() {
        Path f = file();
        if (!Files.exists(f)) return;
        try (InputStream in = Files.newInputStream(f)) {
            Properties p = new Properties();
            p.load(in);
            moduleBind = Integer.parseInt(p.getProperty("moduleBind", String.valueOf(moduleBind)));
            guiKey = Integer.parseInt(p.getProperty("guiKey", String.valueOf(guiKey)));
        } catch (IOException | NumberFormatException ignored) {
        }
    }

    public static void save() {
        Properties p = new Properties();
        p.setProperty("moduleBind", String.valueOf(moduleBind));
        p.setProperty("guiKey", String.valueOf(guiKey));
        try (OutputStream out = Files.newOutputStream(file())) {
            p.store(out, "PearlCatch");
        } catch (IOException ignored) {
        }
    }
}
