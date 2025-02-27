package pw.rxj.iron_quarry.interfaces;

import org.joml.Vector2i;

public interface IManipulateTooltipPositioner {
    Vector2i getTooltipPosition(int x, int y, int width, int height);
}
