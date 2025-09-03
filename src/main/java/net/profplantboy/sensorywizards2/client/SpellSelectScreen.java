package net.profplantboy.sensorywizards2.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.profplantboy.sensorywizards2.item.ModItems;
import net.profplantboy.sensorywizards2.net.ModNetworking;

public class SpellSelectScreen extends Screen {
    private static final int COLS = 3;
    public SpellSelectScreen() { super(Text.literal("Select a Spell")); }

    @Override
    protected void init() {
        int x = this.width / 2 - 150;
        int y = this.height / 2 - 90;
        int i = 0;
        for (String id : ModItems.SPELL_IDS) {
            int cx = x + (i % COLS) * 100;
            int cy = y + (i / COLS) * 24;
            this.addDrawableChild(ButtonWidget.builder(Text.literal(id), b -> {
                ClientPlayNetworking.send(new ModNetworking.SelectSpellC2SPayload(id));
                close();
            }).dimensions(cx, cy, 96, 20).build());
            i++;
        }
        // Close button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Close"), b -> close())
                .dimensions(x, y + ((i+COLS-1)/COLS)*24 + 12, 80, 20).build());
    }

    @Override public void render(DrawContext dc, int mouseX, int mouseY, float delta) {
        renderBackground(dc);
        super.render(dc, mouseX, mouseY, delta);
        dc.drawCenteredTextWithShadow(textRenderer, title, width/2, height/2 - 110, 0xFFFFFF);
    }

    private void close() { if (client != null) client.setScreen(null); }
}
