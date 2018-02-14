package io.nshusa.rsam.binary;

import io.nshusa.rsam.binary.sprite.Sprite;
import io.nshusa.rsam.graphics.render.Raster;
import io.nshusa.rsam.util.ByteBufferUtils;
import io.nshusa.rsam.util.HashUtils;
import io.nshusa.rsam.util.RenderUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Widget {

    public static final int OPTION_CLOSE = 3;
    public static final int OPTION_CONTINUE = 6;
    public static final int OPTION_OK = 1;
    public static final int OPTION_RESET_SETTING = 5;
    public static final int OPTION_TOGGLE_SETTING = 4;
    public static final int OPTION_USABLE = 2;

    public static final int TYPE_CONTAINER = 0;
    public static final int TYPE_INVENTORY = 2;
    public static final int TYPE_ITEM_LIST = 7;
    public static final int TYPE_MODEL = 6;
    public static final int TYPE_MODEL_LIST = 1;
    public static final int TYPE_RECTANGLE = 3;
    public static final int TYPE_SPRITE = 5;
    public static final int TYPE_TEXT = 4;

    private static Widget[] widgets;
    private static Map<Integer, Model> models = new HashMap<>();
    private static Map<Long, Sprite> spriteCache = new HashMap<>();

    private static void clearModels(int id, int type, Model model) {
        models.clear();

        if (model != null && type != 4) {
            models.put((type << 16) | id, model);
        }
    }

    public Widget() {
        this.id = -1;
    }

    public Widget(int id) {
        this.id = id;

        if (id < 0 || id >= widgets.length) {
            throw new IllegalArgumentException(String.format("widget=%d must be between 0 and %d", id, widgets.length));
        }

        if (widgets[id] != null) {
            System.out.println(String.format("overriding widget: %d", id));
        }

        widgets[id] = this;
    }

    public static void decode(Archive interfaces, Archive graphics, Font[] fonts) throws IOException {
        ByteBuffer buffer = interfaces.readFile("data");
        widgets = new Widget[buffer.getShort() & 0xFFFF];

        int parent = -1;

        while (buffer.position() < buffer.remaining()) {
            int id = buffer.getShort() & 0xFFFF;
            if (id == 65535) {
                parent = buffer.getShort() & 0xFFFF;
                id = buffer.getShort() & 0xFFFF;
            }

            Widget widget = new Widget(id);
            widget.parent = parent;
            widget.group = buffer.get() & 0xFF;
            widget.optionType = buffer.get() & 0xFF;
            widget.contentType = buffer.getShort() & 0xFFFF;
            widget.width = buffer.getShort() & 0xFFFF;
            widget.height = buffer.getShort() & 0xFFFF;
            widget.alpha = (byte)(buffer.get() & 0xFF);

            int hover = buffer.get() & 0xFF;
            widget.hoverId = (hover != 0) ? (hover - 1 << 8) | buffer.get() & 0xFF : -1;

            int operators = buffer.get() & 0xFF;
            if (operators > 0) {
                widget.scriptOperators = new int[operators];
                widget.scriptDefaults = new int[operators];

                for (int index = 0; index < operators; index++) {
                    widget.scriptOperators[index] = buffer.get() & 0xFF;
                    widget.scriptDefaults[index] = buffer.getShort() & 0xFFFF;
                }
            }

            int scripts = buffer.get() & 0xFF;
            if (scripts > 0) {
                widget.scripts = new int[scripts][];

                for (int script = 0; script < scripts; script++) {
                    int instructions = buffer.getShort() & 0xFFFF;
                    widget.scripts[script] = new int[instructions];

                    for (int instruction = 0; instruction < instructions; instruction++) {
                        widget.scripts[script][instruction] = buffer.getShort() & 0xFFFF;
                    }
                }
            }

            if (widget.group == TYPE_CONTAINER) {
                widget.scrollLimit = buffer.getShort() & 0xFFFF;
                widget.hidden = (buffer.get() & 0xFF) == 1;

                int children = buffer.getShort() & 0xFFFF;
                widget.children = new int[children];
                widget.childX = new int[children];
                widget.childY = new int[children];

                for (int index = 0; index < children; index++) {
                    widget.children[index] = buffer.getShort() & 0xFFFF;
                    widget.childX[index] = buffer.getShort();
                    widget.childY[index] = buffer.getShort();
                }
            }

            if (widget.group == TYPE_MODEL_LIST) {
                buffer.getShort(); // if use, read unsigned
                buffer.get(); // if use, read unsigned
            }

            if (widget.group == TYPE_INVENTORY) {
                widget.inventoryIds = new int[widget.width * widget.height];
                widget.inventoryAmounts = new int[widget.width * widget.height];

                widget.swappableItems = (buffer.get() & 0xFF) == 1;
                widget.hasActions = (buffer.get() & 0xFF) == 1;
                widget.usableItems = (buffer.get() & 0xFF) == 1;
                widget.replaceItems = (buffer.get() & 0xFF) == 1;

                widget.spritePaddingX = buffer.get() & 0xFF;
                widget.spritePaddingY = buffer.get() & 0xFF;

                widget.spriteX = new int[20];
                widget.spriteY = new int[20];
                widget.sprites = new Sprite[20];

                for (int index = 0; index < 20; index++) {
                    int exists = buffer.get() & 0xFF;
                    if (exists == 1) {
                        widget.spriteX[index] = buffer.getShort();
                        widget.spriteY[index] = buffer.getShort();
                        String name = ByteBufferUtils.getString(buffer);

                        if (graphics != null && name.length() > 0) {
                            int position = name.lastIndexOf(",");
                            widget.sprites[index] = getSprite(graphics,
                                    name.substring(0, position),
                                    Integer.parseInt(name.substring(position + 1)));
                        }
                    }
                }

                widget.actions = new String[5];
                for (int index = 0; index < 5; index++) {
                    widget.actions[index] = ByteBufferUtils.getString(buffer);

                    if (widget.actions[index].isEmpty()) {
                        widget.actions[index] = null;
                    }
                }
            }

            if (widget.group == TYPE_RECTANGLE) {
                widget.filled = (buffer.get() & 0xFF) == 1;
            }

            if (widget.group == TYPE_TEXT || widget.group == TYPE_MODEL_LIST) {
                widget.centeredText = (buffer.get() & 0xFF) == 1;
                int font = buffer.get() & 0xFF;

                if (fonts != null) {
                    widget.font = fonts[font];
                }

                widget.shadowedText = (buffer.get() & 0xFF) == 1;
            }

            if (widget.group == TYPE_TEXT) {
                widget.defaultText = ByteBufferUtils.getString(buffer);
                widget.secondaryText = ByteBufferUtils.getString(buffer);
            }

            if (widget.group == TYPE_MODEL_LIST || widget.group == TYPE_RECTANGLE
                    || widget.group == TYPE_TEXT) {
                widget.defaultColour = buffer.getInt();
            }

            if (widget.group == TYPE_RECTANGLE || widget.group == TYPE_TEXT) {
                widget.secondaryColour = buffer.getInt();
                widget.defaultHoverColour = buffer.getInt();
                widget.secondaryHoverColour = buffer.getInt();
            } else if (widget.group == TYPE_SPRITE) {
                String name = ByteBufferUtils.getString(buffer);
                if (graphics != null && name.length() > 0) {
                    int index = name.lastIndexOf(",");
                    widget.defaultSprite = getSprite(graphics, name.substring(0, index),
                            Integer.parseInt(name.substring(index + 1)));
                }

                name = ByteBufferUtils.getString(buffer);
                if (graphics != null && name.length() > 0) {
                    int index = name.lastIndexOf(",");
                    widget.secondarySprite = getSprite(graphics, name.substring(0, index),
                            Integer.parseInt(name.substring(index + 1)));
                }
            } else if (widget.group == TYPE_MODEL) {
                int content = buffer.get() & 0xFF;
                if (content != 0) {
                    widget.defaultMediaType = 1;
                    widget.defaultMedia = (content - 1 << 8) + buffer.get() & 0xFF;
                }

                content = buffer.get() & 0xFF;
                if (content != 0) {
                    widget.secondaryMediaType = 1;
                    widget.secondaryMedia = (content - 1 << 8) + buffer.get() & 0xFF;
                }

                content = buffer.get() & 0xFF;
                widget.defaultAnimationId = (content != 0) ? (content - 1 << 8) + buffer.get() & 0xFF
                        : -1;

                content = buffer.get() & 0xFF;
                widget.secondaryAnimationId = (content != 0) ? (content - 1 << 8) + buffer.get() & 0xFF
                        : -1;

                widget.spriteScale = buffer.getShort() & 0xFFFF;
                widget.spritePitch = buffer.getShort() & 0xFFFF;
                widget.spriteRoll = buffer.getShort() & 0xFFFF;
            } else if (widget.group == TYPE_ITEM_LIST) {
                widget.inventoryIds = new int[widget.width * widget.height];
                widget.inventoryAmounts = new int[widget.width * widget.height];
                widget.centeredText = (buffer.get() & 0xFF) == 1;

                int font = buffer.get() & 0xFF;
                if (fonts != null) {
                    widget.font = fonts[font];
                }

                widget.shadowedText = (buffer.get() & 0xFF) == 1;
                widget.defaultColour = buffer.getInt();
                widget.spritePaddingX = buffer.getShort();
                widget.spritePaddingY = buffer.getShort();
                widget.hasActions = (buffer.get() & 0xFF) == 1;
                widget.actions = new String[5];

                for (int index = 0; index < 5; index++) {
                    widget.actions[index] = ByteBufferUtils.getString(buffer);

                    if (widget.actions[index].isEmpty()) {
                        widget.actions[index] = null;
                    }
                }
            }

            if (widget.optionType == OPTION_USABLE || widget.group == TYPE_INVENTORY) {
                widget.optionCircumfix = ByteBufferUtils.getString(buffer);
                widget.optionText = ByteBufferUtils.getString(buffer);
                widget.optionAttributes = buffer.getShort() & 0xFFFF;
            }

            if (widget.optionType == OPTION_OK || widget.optionType == OPTION_TOGGLE_SETTING
                    || widget.optionType == OPTION_RESET_SETTING
                    || widget.optionType == OPTION_CONTINUE) {
                widget.hover = ByteBufferUtils.getString(buffer);

                if (widget.hover.isEmpty()) {
                    if (widget.optionType == OPTION_OK) {
                        widget.hover = "Ok";
                    } else if (widget.optionType == OPTION_TOGGLE_SETTING) {
                        widget.hover = "Select";
                    } else if (widget.optionType == OPTION_RESET_SETTING) {
                        widget.hover = "Select";
                    } else if (widget.optionType == OPTION_CONTINUE) {
                        widget.hover = "Continue";
                    }
                }
            }
        }
    }

    private static Sprite getSprite(Archive archive, String name, int id) {
        long key = (HashUtils.hashSpriteName(name) << 8) | id;
        Sprite sprite = spriteCache.get(key);
        if (sprite != null) {
            return sprite;
        }

        try {
            sprite = Sprite.decode(archive, name, id);
            spriteCache.put(key, sprite);
        } catch (Exception ex) {
            return null;
        }

        return sprite;
    }

    public static Widget lookup(int id) {
        if (widgets == null) {
            return null;
        }

        return widgets[id];
    }

    public BufferedImage toBufferedImage() {
        if (this.width <= 0 || this.height <= 0) {
            return null;
        }

        Raster.init(this.height, this.width, new int[this.width * this.height]);
        Raster.reset();

        if (group == TYPE_CONTAINER) {
            RenderUtils.renderWidget(this, 0, 0, 0);
        } else if (group == TYPE_SPRITE) {
            if (defaultSprite != null) {
                defaultSprite.drawSprite(0, 0);
            }
        } else if (group == TYPE_TEXT) {
            RenderUtils.renderText(this, 0, 0);
        } else if (group == TYPE_RECTANGLE) {
            RenderUtils.renderText(this, 0, 0);
        }

        final int[] data = Raster.raster;

        BufferedImage bimage = new BufferedImage(Raster.width, Raster.height, BufferedImage.TYPE_INT_RGB);

        final int[] pixels = ((DataBufferInt) bimage.getRaster().getDataBuffer()).getData();

        System.arraycopy(data, 0, pixels, 0, data.length);

        return bimage;
    }

    public String[] actions;
    public byte alpha;
    public boolean centeredText;
    public int[] children;
    public int[] childX;
    public int[] childY;
    public int contentType;
    public int currentFrame;
    public int defaultAnimationId;
    public int defaultColour;
    public int defaultHoverColour;
    public int defaultMedia;
    public int defaultMediaType;
    public Sprite defaultSprite;
    public String defaultText;

    public boolean filled;
    public Font font;
    public int group;
    public boolean hasActions;
    public int height;
    public boolean hidden;
    public int horizontalDrawOffset;
    public String hover;
    public int hoverId;
    public int id;
    public int[] inventoryAmounts;
    public int[] inventoryIds;
    public int lastFrameTime;
    public int optionAttributes;
    public String optionCircumfix;
    public String optionText;
    public int optionType;
    public int parent;
    public boolean replaceItems;
    public int[] scriptDefaults;
    public int[] scriptOperators;
    public int[][] scripts;
    public int scrollLimit;
    public int scrollPosition;
    public int secondaryAnimationId;
    public int secondaryColour;
    public int secondaryHoverColour;
    public int secondaryMedia;
    public int secondaryMediaType;
    public Sprite secondarySprite;
    public String secondaryText;
    public boolean shadowedText;
    public int spritePaddingX;
    public int spritePaddingY;
    public int spritePitch;
    public int spriteRoll;
    public Sprite[] sprites;
    public int spriteScale;
    public int[] spriteX;
    public int[] spriteY;
    public boolean swappableItems;
    public boolean usableItems;
    public int verticalDrawOffset;
    public int width;

    public void swapInventoryItems(int first, int second) {
        int tmp = inventoryIds[first];
        inventoryIds[first] = inventoryIds[second];
        inventoryIds[second] = tmp;

        tmp = inventoryAmounts[first];
        inventoryAmounts[first] = inventoryAmounts[second];
        inventoryAmounts[second] = tmp;
    }

    public static int count() {
        if (widgets == null) {
            return 0;
        }
        return widgets.length;
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }

}
