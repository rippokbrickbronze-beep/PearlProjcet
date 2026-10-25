package net.slix.pearlcatch;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/**
 * Simple click GUI. Opens with the GUI key (default Right Shift).
 *  - Left click "PearlCatch"  -> toggle the module on/off
 *  - Right click "PearlCatch" -> show/hide its settings
 *  - Click "Bind" / "GUI key" -> then press a key to rebind (Delete/Backspace clears the module bind)
 * Input is read straight from GLFW so it doesn't depend on Minecraft's screen input methods.
 */
public class ClickGui extends Screen {
    private static final int PW = 150, HEAD = 20, ROW = 18;
    private static final int LISTEN_NONE = 0, LISTEN_BIND = 1, LISTEN_GUI = 2;

    private final boolean[] prevKeys = new boolean[GLFW.GLFW_KEY_LAST + 1];
    private boolean prevLeft, prevRight, prevGuiKey;
    private boolean expanded = true;
    private int listening = LISTEN_NONE;

    public ClickGui() {
        super(Text.literal("PearlCatch"));
        prevLeft = Keys.mouseDown(GLFW.GLFW_MOUSE_BUTTON_LEFT);
        prevRight = Keys.mouseDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT);
        prevGuiKey = Keys.isDown(Settings.guiKey);
        for (int k = GLFW.GLFW_KEY_SPACE; k <= GLFW.GLFW_KEY_LAST; k++) prevKeys[k] = Keys.isDown(k);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        PearlCatchMod.prevGui = Keys.isDown(Settings.guiKey);
        PearlCatchMod.prevBind = Keys.isDown(Settings.moduleBind);
        Settings.save();
        super.close();
    }

    private static boolean inside(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta);

        int x = this.width / 2 - PW / 2;
        int y = this.height / 2 - 40;
        int moduleY = y + HEAD;
        int bindY = moduleY + ROW;
        int guiY = bindY + ROW;

        // ---- input (polled) ----
        boolean wasListening = listening != LISTEN_NONE;

        if (wasListening) {
            for (int k = GLFW.GLFW_KEY_SPACE; k <= GLFW.GLFW_KEY_LAST; k++) {
                boolean down = Keys.isDown(k);
                if (down && !prevKeys[k]) {
                    if (k == GLFW.GLFW_KEY_ESCAPE) {
                        listening = LISTEN_NONE;
                    } else if (listening == LISTEN_BIND) {
                        Settings.moduleBind = (k == GLFW.GLFW_KEY_DELETE || k == GLFW.GLFW_KEY_BACKSPACE) ? -1 : k;
                        listening = LISTEN_NONE;
                    } else if (listening == LISTEN_GUI) {
                        if (k != GLFW.GLFW_KEY_DELETE && k != GLFW.GLFW_KEY_BACKSPACE) {
                            Settings.guiKey = k;
                            listening = LISTEN_NONE;
                        }
                    }
                    Settings.save();
                }
            }
        }
        for (int k = GLFW.GLFW_KEY_SPACE; k <= GLFW.GLFW_KEY_LAST; k++) prevKeys[k] = Keys.isDown(k);

        boolean guiDown = Keys.isDown(Settings.guiKey);
        if (!wasListening && listening == LISTEN_NONE && guiDown && !prevGuiKey) {
            prevGuiKey = guiDown;
            this.close();
            return;
        }
        prevGuiKey = guiDown;

        boolean left = Keys.mouseDown(GLFW.GLFW_MOUSE_BUTTON_LEFT);
        boolean right = Keys.mouseDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT);
        if (left && !prevLeft && listening == LISTEN_NONE) {
            if (inside(mx, my, x, moduleY, PW, ROW)) {
                Settings.enabled = !Settings.enabled;
            } else if (expanded && inside(mx, my, x, bindY, PW, ROW)) {
                listening = LISTEN_BIND;
            } else if (expanded && inside(mx, my, x, guiY, PW, ROW)) {
                listening = LISTEN_GUI;
            }
        }
        if (right && !prevRight && inside(mx, my, x, moduleY, PW, ROW)) {
            expanded = !expanded;
            listening = LISTEN_NONE;
        }
        prevLeft = left;
        prevRight = right;

        // ---- drawing ----
        ctx.fill(x, y, x + PW, y + HEAD, 0xFF2B2D42);
        ctx.drawText(this.textRenderer, "Pearl Catch", x + 6, y + 6, 0xFFFFFFFF, true);

        boolean hoverModule = inside(mx, my, x, moduleY, PW, ROW);
        int moduleBg = Settings.enabled ? (hoverModule ? 0xFF2E8B57 : 0xFF267347) : (hoverModule ? 0xFF3A3A48 : 0xFF2A2A36);
        ctx.fill(x, moduleY, x + PW, moduleY + ROW, moduleBg);
        ctx.drawText(this.textRenderer, "PearlCatch", x + 6, moduleY + 5, 0xFFFFFFFF, true);
        String state = Settings.enabled ? "ON" : "OFF";
        ctx.drawText(this.textRenderer, state, x + PW - 6 - this.textRenderer.getWidth(state), moduleY + 5,
                Settings.enabled ? 0xFFB6FFCB : 0xFFAAAAAA, true);

        if (expanded) {
            boolean hoverBind = inside(mx, my, x, bindY, PW, ROW);
            ctx.fill(x, bindY, x + PW, bindY + ROW, hoverBind ? 0xFF343446 : 0xFF22222E);
            String bindText = listening == LISTEN_BIND ? "Press a key..." : "[" + Keys.name(Settings.moduleBind) + "]";
            ctx.drawText(this.textRenderer, "  Bind", x + 6, bindY + 5, 0xFFDDDDDD, true);
            ctx.drawText(this.textRenderer, bindText, x + PW - 6 - this.textRenderer.getWidth(bindText), bindY + 5, 0xFFFFD166, true);

            boolean hoverGui = inside(mx, my, x, guiY, PW, ROW);
            ctx.fill(x, guiY, x + PW, guiY + ROW, hoverGui ? 0xFF343446 : 0xFF22222E);
            String guiText = listening == LISTEN_GUI ? "Press a key..." : "[" + Keys.name(Settings.guiKey) + "]";
            ctx.drawText(this.textRenderer, "  GUI key", x + 6, guiY + 5, 0xFFDDDDDD, true);
            ctx.drawText(this.textRenderer, guiText, x + PW - 6 - this.textRenderer.getWidth(guiText), guiY + 5, 0xFFFFD166, true);
        }

        String hint = "L-click: toggle   R-click: settings";
        ctx.drawText(this.textRenderer, hint, this.width / 2 - this.textRenderer.getWidth(hint) / 2,
                (expanded ? guiY + ROW : moduleY + ROW) + 6, 0xFF999999, true);
    }
}
