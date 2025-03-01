package pw.rxj.iron_quarry.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.DrawContext;

public interface InGameHudRenderCallback {
    Event<Start> START = EventFactory.createArrayBacked(Start.class, (listeners) -> (context, tickDelta) -> {
        for (Start event : listeners) {
            event.onStart(context, tickDelta);
        }
    });

    interface Start {
        void onStart(DrawContext context, double tickDelta);
    }
}