package net.slix.pearlcatch;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

/** Tiny GLFW helpers so we don't depend on Minecraft's changing input classes. */
public final class Keys {
    private Keys() {}

    public static boolean isDown(int key) {
        if (key < 0) return false;
        long handle = MinecraftClient.getInstance().getWindow().getHandle();
        return GLFW.glfwGetKey(handle, key) == GLFW.GLFW_PRESS;
    }

    public static boolean mouseDown(int button) {
        long handle = MinecraftClient.getInstance().getWindow().getHandle();
        return GLFW.glfwGetMouseButton(handle, button) == GLFW.GLFW_PRESS;
    }

    public static String name(int key) {
        if (key < 0) return "NONE";
        if (key >= GLFW.GLFW_KEY_F1 && key <= GLFW.GLFW_KEY_F12) return "F" + (key - GLFW.GLFW_KEY_F1 + 1);
        switch (key) {
            case GLFW.GLFW_KEY_RIGHT_SHIFT: return "RSHIFT";
            case GLFW.GLFW_KEY_LEFT_SHIFT: return "LSHIFT";
            case GLFW.GLFW_KEY_RIGHT_CONTROL: return "RCTRL";
            case GLFW.GLFW_KEY_LEFT_CONTROL: return "LCTRL";
            case GLFW.GLFW_KEY_RIGHT_ALT: return "RALT";
            case GLFW.GLFW_KEY_LEFT_ALT: return "LALT";
            case GLFW.GLFW_KEY_TAB: return "TAB";
            case GLFW.GLFW_KEY_SPACE: return "SPACE";
            case GLFW.GLFW_KEY_ENTER: return "ENTER";
            case GLFW.GLFW_KEY_INSERT: return "INSERT";
            case GLFW.GLFW_KEY_DELETE: return "DELETE";
            case GLFW.GLFW_KEY_HOME: return "HOME";
            case GLFW.GLFW_KEY_END: return "END";
            case GLFW.GLFW_KEY_PAGE_UP: return "PGUP";
            case GLFW.GLFW_KEY_PAGE_DOWN: return "PGDN";
            case GLFW.GLFW_KEY_UP: return "UP";
            case GLFW.GLFW_KEY_DOWN: return "DOWN";
            case GLFW.GLFW_KEY_LEFT: return "LEFT";
            case GLFW.GLFW_KEY_RIGHT: return "RIGHT";
            default: break;
        }
        String n = GLFW.glfwGetKeyName(key, 0);
        return n != null ? n.toUpperCase() : "KEY " + key;
    }
}
