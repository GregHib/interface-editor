package io.nshusa.rsam.util;

import io.nshusa.rsam.binary.Font;
import io.nshusa.rsam.binary.Widget;
import io.nshusa.rsam.binary.sprite.Sprite;
import io.nshusa.rsam.graphics.render.Raster;

public class RenderUtils {

    private RenderUtils() {

    }

    public static void renderWidget(Widget widget, int x, int y, int scroll) {
        if (widget.group != 0 || widget.children == null) {
            return;
        }

        int clipLeft = Raster.getClipLeft();
        int clipBottom = Raster.getClipBottom();
        int clipRight = Raster.getClipRight();
        int clipTop = Raster.getClipTop();

        Raster.setBounds(y + widget.height, x, x + widget.width, y);
        int children = widget.children.length;

        for (int childIndex = 0; childIndex < children; childIndex++) {
            int currentX = widget.childX[childIndex] + x;
            int currentY = widget.childY[childIndex] + y - scroll;

            Widget child = Widget.lookup(widget.children[childIndex]);

            if (child == null) {
                continue;
            }

            currentX += child.horizontalDrawOffset;
            currentY += child.verticalDrawOffset;

            if (child.contentType > 0) {
                //method75(child);
            }

            if (child.group == Widget.TYPE_CONTAINER) {
                if (child.scrollPosition > child.scrollLimit - child.height) {
                    child.scrollPosition = child.scrollLimit - child.height;
                }
                if (child.scrollPosition < 0) {
                    child.scrollPosition = 0;
                }

                renderWidget(child, currentX, currentY, child.scrollPosition);
                if (child.scrollLimit > child.height) {
//                    drawScrollbar(child.height, child.scrollPosition, currentY,
//                            currentX + child.width, child.scrollLimit);
                }
            } else if (child.group != Widget.TYPE_MODEL_LIST) {
                if (child.group == Widget.TYPE_INVENTORY) {

                } else if (child.group == Widget.TYPE_RECTANGLE) {
                    int colour = child.defaultColour;

                    if (child.alpha == 0) {
                        if (child.filled) {
                            Raster.fillRectangle(currentX, currentY, child.width,
                                    child.height, colour);
                        } else {
                            Raster.drawRectangle(currentX, currentY, child.width,
                                    child.height, colour);
                        }
                    } else if (child.filled) {
                        Raster.fillRectangle(currentX, currentY, child.width, child.height,
                                colour, 256 - (child.alpha & 0xff));
                    } else {
                        Raster.drawRectangle(currentX, currentY, child.width, child.height,
                                colour, 256 - (child.alpha & 0xff));
                    }
                } else if (child.group == Widget.TYPE_TEXT) {
                    Font font = child.font;
                    String text = child.defaultText;

                    int colour;

                    colour = child.defaultColour;

                    if (child.optionType == Widget.OPTION_CONTINUE) {
                        text = "Please wait...";
                        colour = child.defaultColour;
                    }

                    if (Raster.width == 479) {
                        if (colour == 0xffff00) {
                            colour = 255;
                        } else if (colour == 49152) {
                            colour = 0xffffff;
                        }
                    }

                    for (int drawY = currentY + font.getVerticalSpace(); text
                            .length() > 0; drawY += font.getVerticalSpace()) {

                        int line = text.indexOf("\\n");
                        String drawn;
                        if (line != -1) {
                            drawn = text.substring(0, line);
                            text = text.substring(line + 2);
                        } else {
                            drawn = text;
                            text = "";
                        }

                        if (child.centeredText) {
                            font.shadowCentre(currentX + child.width / 2, drawY, drawn,
                                    child.shadowedText, colour);
                        } else {
                            font.shadow(currentX, drawY, drawn, child.shadowedText, colour);
                        }
                    }
                } else if (child.group == Widget.TYPE_SPRITE) {
                    Sprite sprite = child.defaultSprite;

                    if (sprite != null) {
                        sprite.drawSprite(currentX, currentY);
                    }
                } else if (child.group == Widget.TYPE_MODEL) {

                } else if (child.group == Widget.TYPE_ITEM_LIST) {

                }
            }
        }

        Raster.setBounds(clipTop, clipLeft, clipRight, clipBottom);
    }

    public static void renderRectangle(Widget child, int currentX, int currentY) {
        if (child.group == Widget.TYPE_RECTANGLE) {
            int colour = child.defaultColour;

            if (child.alpha == 0) {
                if (child.filled) {
                    Raster.fillRectangle(currentX, currentY, child.width,
                            child.height, colour);
                } else {
                    Raster.drawRectangle(currentX, currentY, child.width,
                            child.height, colour);
                }
            } else if (child.filled) {
                Raster.fillRectangle(currentX, currentY, child.width, child.height,
                        colour, 256 - (child.alpha & 0xff));
            } else {
                Raster.drawRectangle(currentX, currentY, child.width, child.height,
                        colour, 256 - (child.alpha & 0xff));
            }
        }
    }

    public static void renderText(Widget child, int x, int y) {
        if (child.group == Widget.TYPE_TEXT) {

            Font font = child.font;
            String text = child.defaultText;

            int colour;

            colour = child.defaultColour;

            if (child.optionType == Widget.OPTION_CONTINUE) {
                text = "Please wait...";
                colour = child.defaultColour;
            }

            if (Raster.width == 479) {
                if (colour == 0xffff00) {
                    colour = 255;
                } else if (colour == 49152) {
                    colour = 0xffffff;
                }
            }

            for (int drawY = y + font.getVerticalSpace(); text
                    .length() > 0; drawY += font.getVerticalSpace()) {

                int line = text.indexOf("\\n");
                String drawn;
                if (line != -1) {
                    drawn = text.substring(0, line);
                    text = text.substring(line + 2);
                } else {
                    drawn = text;
                    text = "";
                }

                if (child.centeredText) {
                    font.shadowCentre(x + child.width / 2, drawY, drawn,
                            child.shadowedText, colour);
                } else {
                    font.shadow(x, drawY, drawn, child.shadowedText, colour);
                }
            }
        }
    }

}
