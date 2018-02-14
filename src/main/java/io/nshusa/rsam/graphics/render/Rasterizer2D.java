package io.nshusa.rsam.graphics.render;

public class Rasterizer2D {
    public static float[] depthBuffer;
    public static int[] pixels;
    public static int width;
    public static int height;
    public static int topY;
    public static int bottomY;
    public static int leftX;
    public static int bottomX;
    public static int lastX;
    public static int viewportCenterX;
    public static int viewportCenterY;

    public Rasterizer2D() {
    }

    public static void initDrawingArea(int height, int width, int[] pixels, float[] depth) {
        depthBuffer = depth;
        pixels = pixels;
        width = width;
        height = height;
        setDrawingArea(height, 0, width, 0);
    }

    public static void drawTransparentGradientBox(int leftX, int topY, int width, int height, int topColour, int bottomColour, int opacity) {
        int gradientProgress = 0;
        int progressPerPixel = 65536 / height;
        if (leftX < leftX) {
            width -= leftX - leftX;
            leftX = leftX;
        }

        if (topY < topY) {
            gradientProgress += (topY - topY) * progressPerPixel;
            height -= topY - topY;
            topY = topY;
        }

        if (leftX + width > bottomX) {
            width = bottomX - leftX;
        }

        if (topY + height > bottomY) {
            height = bottomY - topY;
        }

        int leftOver = width - width;
        int transparency = 256 - opacity;
        int pixelIndex = leftX + topY * width;

        for(int rowIndex = 0; rowIndex < height; ++rowIndex) {
            int gradient = 65536 - gradientProgress >> 8;
            int inverseGradient = gradientProgress >> 8;
            int gradientColour = ((topColour & 16711935) * gradient + (bottomColour & 16711935) * inverseGradient & -16711936) + ((topColour & '\uff00') * gradient + (bottomColour & '\uff00') * inverseGradient & 16711680) >>> 8;
            int transparentPixel = ((gradientColour & 16711935) * opacity >> 8 & 16711935) + ((gradientColour & '\uff00') * opacity >> 8 & '\uff00');

            for(int columnIndex = 0; columnIndex < width; ++columnIndex) {
                int backgroundPixel = pixels[pixelIndex];
                backgroundPixel = ((backgroundPixel & 16711935) * transparency >> 8 & 16711935) + ((backgroundPixel & '\uff00') * transparency >> 8 & '\uff00');
                pixels[pixelIndex++] = transparentPixel + backgroundPixel;
            }

            pixelIndex += leftOver;
            gradientProgress += progressPerPixel;
        }

    }

    public static void defaultDrawingAreaSize() {
        leftX = 0;
        topY = 0;
        bottomX = width;
        bottomY = height;
        lastX = bottomX;
        viewportCenterX = bottomX / 2;
    }

    public static void setDrawingArea(int bottomY, int leftX, int rightX, int topY) {
        if (leftX < 0) {
            leftX = 0;
        }

        if (topY < 0) {
            topY = 0;
        }

        if (rightX > width) {
            rightX = width;
        }

        if (bottomY > height) {
            bottomY = height;
        }

        leftX = leftX;
        topY = topY;
        bottomX = rightX;
        bottomY = bottomY;
        lastX = bottomX;
        viewportCenterX = bottomX / 2;
        viewportCenterY = bottomY / 2;
    }

    public static void clear() {
        int i = width * height;

        for(int j = 0; j < i; ++j) {
            pixels[j] = 0;
            depthBuffer[j] = 3.4028235E38F;
        }

    }

    public static void drawBox(int leftX, int topY, int width, int height, int rgbColour) {
        if (leftX < leftX) {
            width -= leftX - leftX;
            leftX = leftX;
        }

        if (topY < topY) {
            height -= topY - topY;
            topY = topY;
        }

        if (leftX + width > bottomX) {
            width = bottomX - leftX;
        }

        if (topY + height > bottomY) {
            height = bottomY - topY;
        }

        int leftOver = width - width;
        int pixelIndex = leftX + topY * width;

        for(int rowIndex = 0; rowIndex < height; ++rowIndex) {
            for(int columnIndex = 0; columnIndex < width; ++columnIndex) {
                pixels[pixelIndex++] = rgbColour;
            }

            pixelIndex += leftOver;
        }

    }

    public static void drawTransparentBox(int leftX, int topY, int width, int height, int rgbColour, int opacity) {
        if (leftX < leftX) {
            width -= leftX - leftX;
            leftX = leftX;
        }

        if (topY < topY) {
            height -= topY - topY;
            topY = topY;
        }

        if (leftX + width > bottomX) {
            width = bottomX - leftX;
        }

        if (topY + height > bottomY) {
            height = bottomY - topY;
        }

        int transparency = 256 - opacity;
        int red = (rgbColour >> 16 & 255) * opacity;
        int green = (rgbColour >> 8 & 255) * opacity;
        int blue = (rgbColour & 255) * opacity;
        int leftOver = width - width;
        int pixelIndex = leftX + topY * width;

        for(int rowIndex = 0; rowIndex < height; ++rowIndex) {
            for(int columnIndex = 0; columnIndex < width; ++columnIndex) {
                int otherRed = (pixels[pixelIndex] >> 16 & 255) * transparency;
                int otherGreen = (pixels[pixelIndex] >> 8 & 255) * transparency;
                int otherBlue = (pixels[pixelIndex] & 255) * transparency;
                int transparentColour = (red + otherRed >> 8 << 16) + (green + otherGreen >> 8 << 8) + (blue + otherBlue >> 8);
                pixels[pixelIndex++] = transparentColour;
            }

            pixelIndex += leftOver;
        }

    }

    public static void drawBoxOutline(int leftX, int topY, int width, int height, int rgbColour) {
        drawHorizontalLine(leftX, topY, width, rgbColour);
        drawHorizontalLine(leftX, topY + height - 1, width, rgbColour);
        drawVerticalLine(leftX, topY, height, rgbColour);
        drawVerticalLine(leftX + width - 1, topY, height, rgbColour);
    }

    public static void drawHorizontalLine(int xPosition, int yPosition, int width, int rgbColour) {
        if (yPosition >= topY && yPosition < bottomY) {
            if (xPosition < leftX) {
                width -= leftX - xPosition;
                xPosition = leftX;
            }

            if (xPosition + width > bottomX) {
                width = bottomX - xPosition;
            }

            int pixelIndex = xPosition + yPosition * width;

            for(int i = 0; i < width; ++i) {
                pixels[pixelIndex + i] = rgbColour;
            }

        }
    }

    public static void drawVerticalLine(int xPosition, int yPosition, int height, int rgbColour) {
        if (xPosition >= leftX && xPosition < bottomX) {
            if (yPosition < topY) {
                height -= topY - yPosition;
                yPosition = topY;
            }

            if (yPosition + height > bottomY) {
                height = bottomY - yPosition;
            }

            int pixelIndex = xPosition + yPosition * width;

            for(int rowIndex = 0; rowIndex < height; ++rowIndex) {
                pixels[pixelIndex + rowIndex * width] = rgbColour;
            }

        }
    }

    public static void drawTransparentBoxOutline(int leftX, int topY, int width, int height, int rgbColour, int opacity) {
        drawTransparentHorizontalLine(leftX, topY, width, rgbColour, opacity);
        drawTransparentHorizontalLine(leftX, topY + height - 1, width, rgbColour, opacity);
        if (height >= 3) {
            drawTransparentVerticalLine(leftX, topY + 1, height - 2, rgbColour, opacity);
            drawTransparentVerticalLine(leftX + width - 1, topY + 1, height - 2, rgbColour, opacity);
        }

    }

    public static void drawTransparentHorizontalLine(int xPosition, int yPosition, int width, int rgbColour, int opacity) {
        if (yPosition >= topY && yPosition < bottomY) {
            if (xPosition < leftX) {
                width -= leftX - xPosition;
                xPosition = leftX;
            }

            if (xPosition + width > bottomX) {
                width = bottomX - xPosition;
            }

            int transparency = 256 - opacity;
            int red = (rgbColour >> 16 & 255) * opacity;
            int green = (rgbColour >> 8 & 255) * opacity;
            int blue = (rgbColour & 255) * opacity;
            int pixelIndex = xPosition + yPosition * width;

            for(int i = 0; i < width; ++i) {
                int otherRed = (pixels[pixelIndex] >> 16 & 255) * transparency;
                int otherGreen = (pixels[pixelIndex] >> 8 & 255) * transparency;
                int otherBlue = (pixels[pixelIndex] & 255) * transparency;
                int transparentColour = (red + otherRed >> 8 << 16) + (green + otherGreen >> 8 << 8) + (blue + otherBlue >> 8);
                pixels[pixelIndex++] = transparentColour;
            }

        }
    }

    public static void drawTransparentVerticalLine(int xPosition, int yPosition, int height, int rgbColour, int opacity) {
        if (xPosition >= leftX && xPosition < bottomX) {
            if (yPosition < topY) {
                height -= topY - yPosition;
                yPosition = topY;
            }

            if (yPosition + height > bottomY) {
                height = bottomY - yPosition;
            }

            int transparency = 256 - opacity;
            int red = (rgbColour >> 16 & 255) * opacity;
            int green = (rgbColour >> 8 & 255) * opacity;
            int blue = (rgbColour & 255) * opacity;
            int pixelIndex = xPosition + yPosition * width;

            for(int i = 0; i < height; ++i) {
                int otherRed = (pixels[pixelIndex] >> 16 & 255) * transparency;
                int otherGreen = (pixels[pixelIndex] >> 8 & 255) * transparency;
                int otherBlue = (pixels[pixelIndex] & 255) * transparency;
                int transparentColour = (red + otherRed >> 8 << 16) + (green + otherGreen >> 8 << 8) + (blue + otherBlue >> 8);
                pixels[pixelIndex] = transparentColour;
                pixelIndex += width;
            }

        }
    }
}
