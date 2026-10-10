package com.coopfrey.reachping;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class ReachpingConfigScreen extends GuiScreen {
    private final GuiScreen parent;
    private GuiTextField soundField;
    private GuiTextField failSoundField;

    private static final int B_TOGGLE = 0, B_PLAYERS = 1, B_FAIL = 2, B_DIST = 3,
            B_VOL = 4, B_RESET = 5, B_DONE = 6;

    public ReachpingConfigScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        int cx = width / 2 - 100;
        int y = height / 2 - 112;

        buttonList.add(new GuiButton(B_TOGGLE, cx, y, 200, 20, ""));
        buttonList.add(new GuiButton(B_PLAYERS, cx, y + 24, 200, 20, ""));
        buttonList.add(new DistanceSlider(B_DIST, cx, y + 48, 200, 20));
        buttonList.add(new VolumeSlider(B_VOL, cx, y + 72, 200, 20));

        soundField = new GuiTextField(10, fontRendererObj, cx, y + 108, 200, 20);
        soundField.setMaxStringLength(100);
        soundField.setText(ReachpingConfig.soundId);

        buttonList.add(new GuiButton(B_FAIL, cx, y + 132, 200, 20, ""));
        failSoundField = new GuiTextField(11, fontRendererObj, cx, y + 168, 200, 20);
        failSoundField.setMaxStringLength(100);
        failSoundField.setText(ReachpingConfig.failSoundId);

        buttonList.add(new GuiButton(B_RESET, cx, y + 196, 98, 20, "Reset to defaults"));
        buttonList.add(new GuiButton(B_DONE, cx + 102, y + 196, 98, 20, "Done"));
        updateLabels();
    }

    private void updateLabels() {
        for (GuiButton b : buttonList) {
            switch (b.id) {
                case B_TOGGLE:
                    b.displayString = "Sound: " + (ReachpingConfig.enabled ? "Enabled" : "Disabled");
                    break;
                case B_PLAYERS:
                    b.displayString = "Only players: " + (ReachpingConfig.onlyPlayers ? "Enabled" : "Disabled");
                    break;
                case B_FAIL:
                    b.displayString = "Fail sound: " + (ReachpingConfig.failSoundEnabled ? "Enabled" : "Disabled");
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case B_TOGGLE:
                ReachpingConfig.enabled = !ReachpingConfig.enabled;
                break;
            case B_PLAYERS:
                ReachpingConfig.onlyPlayers = !ReachpingConfig.onlyPlayers;
                break;
            case B_FAIL:
                ReachpingConfig.failSoundEnabled = !ReachpingConfig.failSoundEnabled;
                break;
            case B_RESET:
                ReachpingConfig.reset();
                initGui();
                return;
            case B_DONE:
                close();
                return;
            default:
                break;
        }
        updateLabels();
    }

    private void close() {
        mc.displayGuiScreen(parent);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        ReachpingConfig.soundId = ReachpingConfig.nonBlank(soundField.getText(), ReachpingConfig.DEFAULT_SOUND);
        ReachpingConfig.failSoundId = ReachpingConfig.nonBlank(failSoundField.getText(), ReachpingConfig.DEFAULT_FAIL_SOUND);
        ReachpingConfig.save();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            close();
            return;
        }
        soundField.textboxKeyTyped(typedChar, keyCode);
        failSoundField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        soundField.mouseClicked(mouseX, mouseY, mouseButton);
        failSoundField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        soundField.updateCursorCounter();
        failSoundField.updateCursorCounter();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int cx = width / 2;
        int y = height / 2 - 112;
        drawCenteredString(fontRendererObj, "ReachPing Settings", cx, y - 16, 0xFFFFFF);
        drawString(fontRendererObj, "Sound (e.g. note.pling, random.orb)", cx - 100, y + 97, 0xA0A0A0);
        drawString(fontRendererObj, "Fail sound (e.g. note.bd)", cx - 100, y + 157, 0xA0A0A0);
        soundField.drawTextBox();
        failSoundField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    // ---- simple slider implementation (1.8.9 has no public generic slider) ----

    private abstract static class Slider extends GuiButton {
        protected double value; // 0..1
        private boolean dragging;

        Slider(int id, int x, int y, int w, int h) {
            super(id, x, y, w, h, "");
        }

        abstract void apply(double v);

        abstract String label();

        @Override
        protected int getHoverState(boolean mouseOver) {
            return 0;
        }

        private void setFromMouse(int mouseX) {
            value = (mouseX - (xPosition + 4)) / (double) (width - 8);
            value = Math.max(0.0, Math.min(1.0, value));
            apply(value);
            displayString = label();
        }

        @Override
        protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
            if (!visible) return;
            if (dragging) setFromMouse(mouseX);
            mc.getTextureManager().bindTexture(buttonTextures);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            int px = xPosition + (int) (value * (width - 8));
            drawTexturedModalRect(px, yPosition, 0, 66, 4, 20);
            drawTexturedModalRect(px + 4, yPosition, 196, 66, 4, 20);
        }

        @Override
        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
            if (super.mousePressed(mc, mouseX, mouseY)) {
                setFromMouse(mouseX);
                dragging = true;
                return true;
            }
            return false;
        }

        @Override
        public void mouseReleased(int mouseX, int mouseY) {
            dragging = false;
        }
    }

    private static class DistanceSlider extends Slider {
        DistanceSlider(int id, int x, int y, int w, int h) {
            super(id, x, y, w, h);
            value = ReachpingConfig.distance / ReachpingConfig.MAX_DISTANCE;
            displayString = label();
        }

        @Override
        void apply(double v) {
            // snap to 0.1 blocks
            ReachpingConfig.distance = Math.round(v * ReachpingConfig.MAX_DISTANCE * 10.0) / 10.0;
        }

        @Override
        String label() {
            return "Play sound past " + String.format("%.1f", ReachpingConfig.distance) + " blocks";
        }
    }

    private static class VolumeSlider extends Slider {
        VolumeSlider(int id, int x, int y, int w, int h) {
            super(id, x, y, w, h);
            value = ReachpingConfig.volume;
            displayString = label();
        }

        @Override
        void apply(double v) {
            ReachpingConfig.volume = Math.round(v * 100.0) / 100.0;
        }

        @Override
        String label() {
            return "Volume " + Math.round(ReachpingConfig.volume * 100) + "%";
        }
    }
}
