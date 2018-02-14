package io.nshusa.rsam.graphics.render;

import io.nshusa.rsam.binary.Archive;
import io.nshusa.rsam.binary.sprite.IndexedImage;

public final class Rasterizer3D extends Rasterizer2D {
    public static int viewDistance = 9;
    public static boolean lowMem = true;
    public static boolean textureOutOfDrawingBounds;
    private static boolean aBoolean1463;
    public static boolean aBoolean1464 = true;
    public static int alpha;
    public static int originViewX;
    public static int originViewY;
    private static int[] anIntArray1468 = new int[512];
    public static final int[] anIntArray1469 = new int[2048];
    public static int[] anIntArray1470 = new int[2048];
    public static int[] COSINE = new int[2048];
    public static int[] scanOffsets;
    private static int textureCount;
    public static IndexedImage[] textures = new IndexedImage[51];
    private static boolean[] textureIsTransparant = new boolean[51];
    private static int[] averageTextureColours = new int[51];
    private static int textureRequestBufferPointer;
    private static int[][] textureRequestPixelBuffer;
    private static int[][] texturesPixelBuffer = new int[51][];
    public static int[] textureLastUsed = new int[51];
    public static int lastTextureRetrievalCount;
    public static int[] hslToRgb = new int[65536];
    private static int[][] currentPalette = new int[51][];

    public Rasterizer3D() {
    }

    public static void clear() {
        anIntArray1468 = null;
        anIntArray1468 = null;
        anIntArray1470 = null;
        COSINE = null;
        scanOffsets = null;
        textures = null;
        textureIsTransparant = null;
        averageTextureColours = null;
        textureRequestPixelBuffer = (int[][])null;
        texturesPixelBuffer = (int[][])null;
        textureLastUsed = null;
        hslToRgb = null;
        currentPalette = (int[][])null;
    }

    public static void useViewport() {
        scanOffsets = new int[height];

        for(int j = 0; j < height; ++j) {
            scanOffsets[j] = width * j;
        }

        originViewX = width / 2;
        originViewY = height / 2;
    }

    public static void reposition(int width, int length) {
        scanOffsets = new int[length];

        for(int x = 0; x < length; ++x) {
            scanOffsets[x] = width * x;
        }

        originViewX = width / 2;
        originViewY = length / 2;
    }

    public static void clearTextureCache() {
        textureRequestPixelBuffer = (int[][])null;

        for(int i = 0; i < 50; ++i) {
            texturesPixelBuffer[i] = null;
        }

    }

    public static void initiateRequestBuffers() {
        if (textureRequestPixelBuffer == null) {
            textureRequestBufferPointer = 20;
            if (lowMem) {
                textureRequestPixelBuffer = new int[textureRequestBufferPointer][16384];
            } else {
                textureRequestPixelBuffer = new int[textureRequestBufferPointer][65536];
            }

            for(int i = 0; i < 50; ++i) {
                texturesPixelBuffer[i] = null;
            }
        }

    }

    public static void loadTextures(Archive archive) {
        textureCount = 0;

        for(int index = 0; index < 51; ++index) {
            try {
                textures[index] = IndexedImage.decode(archive, String.valueOf(index), 0);
                if (lowMem && textures[index].resizeWidth == 128) {
                    textures[index].downscale();
                } else {
                    textures[index].resize();
                }

                ++textureCount;
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }

    }

    public static int getOverallColour(int textureId) {
        if (averageTextureColours[textureId] != 0) {
            return averageTextureColours[textureId];
        } else {
            int totalRed = 0;
            int totalGreen = 0;
            int totalBlue = 0;
            int colourCount = currentPalette[textureId].length;

            int avgPaletteColour;
            for(avgPaletteColour = 0; avgPaletteColour < colourCount; ++avgPaletteColour) {
                totalRed += currentPalette[textureId][avgPaletteColour] >> 16 & 255;
                totalGreen += currentPalette[textureId][avgPaletteColour] >> 8 & 255;
                totalBlue += currentPalette[textureId][avgPaletteColour] & 255;
            }

            avgPaletteColour = (totalRed / colourCount << 16) + (totalGreen / colourCount << 8) + totalBlue / colourCount;
            avgPaletteColour = adjustBrightness(avgPaletteColour, 1.4D);
            if (avgPaletteColour == 0) {
                avgPaletteColour = 1;
            }

            averageTextureColours[textureId] = avgPaletteColour;
            return avgPaletteColour;
        }
    }

    public static void requestTextureUpdate(int textureId) {
        if (texturesPixelBuffer[textureId] != null) {
            textureRequestPixelBuffer[textureRequestBufferPointer++] = texturesPixelBuffer[textureId];
            texturesPixelBuffer[textureId] = null;
        }
    }

    private static int[] getTexturePixels(int textureId) {
        textureLastUsed[textureId] = lastTextureRetrievalCount++;
        if (texturesPixelBuffer[textureId] != null) {
            return texturesPixelBuffer[textureId];
        } else {
            int[] texturePixels;
            int i;
            if (textureRequestBufferPointer > 0) {
                texturePixels = textureRequestPixelBuffer[--textureRequestBufferPointer];
                textureRequestPixelBuffer[textureRequestBufferPointer] = null;
            } else {
                int lastUsed = 0;
                int target = -1;

                for(i = 0; i < textureCount; ++i) {
                    if (texturesPixelBuffer[i] != null && (textureLastUsed[i] < lastUsed || target == -1)) {
                        lastUsed = textureLastUsed[i];
                        target = i;
                    }
                }

                texturePixels = texturesPixelBuffer[target];
                texturesPixelBuffer[target] = null;
            }

            texturesPixelBuffer[textureId] = texturePixels;
            IndexedImage background = textures[textureId];
            int[] texturePalette = currentPalette[textureId];
            int colour;
            if (lowMem) {
                textureIsTransparant[textureId] = false;

                for(i = 0; i < 4096; ++i) {
                    colour = texturePixels[i] = texturePalette[background.palettePixels[i]] & 16316671;
                    if (colour == 0) {
                        textureIsTransparant[textureId] = true;
                    }

                    texturePixels[4096 + i] = colour - (colour >>> 3) & 16316671;
                    texturePixels[8192 + i] = colour - (colour >>> 2) & 16316671;
                    texturePixels[12288 + i] = colour - (colour >>> 2) - (colour >>> 3) & 16316671;
                }
            } else {
                if (background.width != 64) {
                    for(i = 0; i < 16384; ++i) {
                        texturePixels[i] = texturePalette[background.palettePixels[i]];
                    }
                } else {
                    for(i = 0; i < 128; ++i) {
                        for(colour = 0; colour < 128; ++colour) {
                            texturePixels[colour + (i << 7)] = texturePalette[background.palettePixels[(colour >> 1) + (i >> 1 << 6)]];
                        }
                    }
                }

                textureIsTransparant[textureId] = false;

                for(i = 0; i < 16384; ++i) {
                    texturePixels[i] &= 16316671;
                    colour = texturePixels[i];
                    if (colour == 0) {
                        textureIsTransparant[textureId] = true;
                    }

                    texturePixels[16384 + i] = colour - (colour >>> 3) & 16316671;
                    texturePixels['耀' + i] = colour - (colour >>> 2) & 16316671;
                    texturePixels['쀀' + i] = colour - (colour >>> 2) - (colour >>> 3) & 16316671;
                }
            }

            return texturePixels;
        }
    }

    public static void setBrightness(double brightness) {
        int j = 0;

        int textureId;
        for(textureId = 0; textureId < 512; ++textureId) {
            double d1 = (double)(textureId / 8) / 64.0D + 0.0078125D;
            double d2 = (double)(textureId & 7) / 8.0D + 0.0625D;

            for(int k1 = 0; k1 < 128; ++k1) {
                double d3 = (double)k1 / 128.0D;
                double r = d3;
                double g = d3;
                double b = d3;
                if (d2 != 0.0D) {
                    double d7;
                    if (d3 < 0.5D) {
                        d7 = d3 * (1.0D + d2);
                    } else {
                        d7 = d3 + d2 - d3 * d2;
                    }

                    double d8 = 2.0D * d3 - d7;
                    double d9 = d1 + 0.3333333333333333D;
                    if (d9 > 1.0D) {
                        --d9;
                    }

                    double d11 = d1 - 0.3333333333333333D;
                    if (d11 < 0.0D) {
                        ++d11;
                    }

                    if (6.0D * d9 < 1.0D) {
                        r = d8 + (d7 - d8) * 6.0D * d9;
                    } else if (2.0D * d9 < 1.0D) {
                        r = d7;
                    } else if (3.0D * d9 < 2.0D) {
                        r = d8 + (d7 - d8) * (0.6666666666666666D - d9) * 6.0D;
                    } else {
                        r = d8;
                    }

                    if (6.0D * d1 < 1.0D) {
                        g = d8 + (d7 - d8) * 6.0D * d1;
                    } else if (2.0D * d1 < 1.0D) {
                        g = d7;
                    } else if (3.0D * d1 < 2.0D) {
                        g = d8 + (d7 - d8) * (0.6666666666666666D - d1) * 6.0D;
                    } else {
                        g = d8;
                    }

                    if (6.0D * d11 < 1.0D) {
                        b = d8 + (d7 - d8) * 6.0D * d11;
                    } else if (2.0D * d11 < 1.0D) {
                        b = d7;
                    } else if (3.0D * d11 < 2.0D) {
                        b = d8 + (d7 - d8) * (0.6666666666666666D - d11) * 6.0D;
                    } else {
                        b = d8;
                    }
                }

                int byteR = (int)(r * 256.0D);
                int byteG = (int)(g * 256.0D);
                int byteB = (int)(b * 256.0D);
                int rgb = (byteR << 16) + (byteG << 8) + byteB;
                rgb = adjustBrightness(rgb, brightness);
                if (rgb == 0) {
                    rgb = 1;
                }

                hslToRgb[j++] = rgb;
            }
        }

        for(textureId = 0; textureId < 51; ++textureId) {
            if (textures[textureId] != null) {
                int[] originalPalette = textures[textureId].palette;
                currentPalette[textureId] = new int[originalPalette.length];

                for(int colourId = 0; colourId < originalPalette.length; ++colourId) {
                    currentPalette[textureId][colourId] = adjustBrightness(originalPalette[colourId], brightness);
                    if ((currentPalette[textureId][colourId] & 16316671) == 0 && colourId != 0) {
                        currentPalette[textureId][colourId] = 1;
                    }
                }
            }
        }

        for(textureId = 0; textureId < 51; ++textureId) {
            requestTextureUpdate(textureId);
        }

    }

    private static int adjustBrightness(int rgb, double intensity) {
        double r = (double)(rgb >> 16) / 256.0D;
        double g = (double)(rgb >> 8 & 255) / 256.0D;
        double b = (double)(rgb & 255) / 256.0D;
        r = Math.pow(r, intensity);
        g = Math.pow(g, intensity);
        b = Math.pow(b, intensity);
        int r_byte = (int)(r * 256.0D);
        int g_byte = (int)(g * 256.0D);
        int b_byte = (int)(b * 256.0D);
        return (r_byte << 16) + (g_byte << 8) + b_byte;
    }

    public static void drawShadedTriangle(int y_a, int y_b, int y_c, int x_a, int x_b, int x_c, int hsl1, int hsl2, int hsl3, float z_a, float z_b, float z_c) {
        if (z_a >= 0.0F && z_b >= 0.0F && z_c >= 0.0F) {
            int rgb1 = hslToRgb[hsl1];
            int rgb2 = hslToRgb[hsl2];
            int rgb3 = hslToRgb[hsl3];
            int r1 = rgb1 >> 16 & 255;
            int g1 = rgb1 >> 8 & 255;
            int b1 = rgb1 & 255;
            int r2 = rgb2 >> 16 & 255;
            int g2 = rgb2 >> 8 & 255;
            int b2 = rgb2 & 255;
            int r3 = rgb3 >> 16 & 255;
            int g3 = rgb3 >> 8 & 255;
            int b3 = rgb3 & 255;
            int a_to_b = 0;
            int dr1 = 0;
            int dg1 = 0;
            int db1 = 0;
            if (y_b != y_a) {
                a_to_b = (x_b - x_a << 16) / (y_b - y_a);
                dr1 = (r2 - r1 << 16) / (y_b - y_a);
                dg1 = (g2 - g1 << 16) / (y_b - y_a);
                db1 = (b2 - b1 << 16) / (y_b - y_a);
            }

            int b_to_c = 0;
            int dr2 = 0;
            int dg2 = 0;
            int db2 = 0;
            if (y_c != y_b) {
                b_to_c = (x_c - x_b << 16) / (y_c - y_b);
                dr2 = (r3 - r2 << 16) / (y_c - y_b);
                dg2 = (g3 - g2 << 16) / (y_c - y_b);
                db2 = (b3 - b2 << 16) / (y_c - y_b);
            }

            int c_to_a = 0;
            int dr3 = 0;
            int dg3 = 0;
            int db3 = 0;
            if (y_c != y_a) {
                c_to_a = (x_a - x_c << 16) / (y_a - y_c);
                dr3 = (r1 - r3 << 16) / (y_a - y_c);
                dg3 = (g1 - g3 << 16) / (y_a - y_c);
                db3 = (b1 - b3 << 16) / (y_a - y_c);
            }

            float b_aX = (float)(x_b - x_a);
            float b_aY = (float)(y_b - y_a);
            float c_aX = (float)(x_c - x_a);
            float c_aY = (float)(y_c - y_a);
            float b_aZ = z_b - z_a;
            float c_aZ = z_c - z_a;
            float div = b_aX * c_aY - c_aX * b_aY;
            float depth_slope = (b_aZ * c_aY - c_aZ * b_aY) / div;
            float depth_increment = (c_aZ * b_aX - b_aZ * c_aX) / div;
            if (y_a <= y_b && y_a <= y_c) {
                if (y_a < bottomY) {
                    if (y_b > bottomY) {
                        y_b = bottomY;
                    }

                    if (y_c > bottomY) {
                        y_c = bottomY;
                    }

                    z_a = z_a - depth_slope * (float)x_a + depth_slope;
                    if (y_b < y_c) {
                        x_c = x_a <<= 16;
                        r3 = r1 <<= 16;
                        g3 = g1 <<= 16;
                        b3 = b1 <<= 16;
                        if (y_a < 0) {
                            x_c -= c_to_a * y_a;
                            x_a -= a_to_b * y_a;
                            r3 -= dr3 * y_a;
                            g3 -= dg3 * y_a;
                            b3 -= db3 * y_a;
                            r1 -= dr1 * y_a;
                            g1 -= dg1 * y_a;
                            b1 -= db1 * y_a;
                            z_a -= depth_increment * (float)y_a;
                            y_a = 0;
                        }

                        x_b <<= 16;
                        r2 <<= 16;
                        g2 <<= 16;
                        b2 <<= 16;
                        if (y_b < 0) {
                            x_b -= b_to_c * y_b;
                            r2 -= dr2 * y_b;
                            g2 -= dg2 * y_b;
                            b2 -= db2 * y_b;
                            y_b = 0;
                        }

                        if (y_a != y_b && c_to_a < a_to_b || y_a == y_b && c_to_a > b_to_c) {
                            y_c -= y_b;
                            y_b -= y_a;
                            y_a = scanOffsets[y_a];

                            while(true) {
                                --y_b;
                                if (y_b < 0) {
                                    while(true) {
                                        --y_c;
                                        if (y_c < 0) {
                                            return;
                                        }

                                        drawShadedScanline(pixels, y_a, x_c >> 16, x_b >> 16, r3, g3, b3, r2, g2, b2, z_a, depth_slope);
                                        x_c += c_to_a;
                                        x_b += b_to_c;
                                        r3 += dr3;
                                        g3 += dg3;
                                        b3 += db3;
                                        r2 += dr2;
                                        g2 += dg2;
                                        b2 += db2;
                                        y_a += width;
                                        z_a += depth_increment;
                                    }
                                }

                                drawShadedScanline(pixels, y_a, x_c >> 16, x_a >> 16, r3, g3, b3, r1, g1, b1, z_a, depth_slope);
                                x_c += c_to_a;
                                x_a += a_to_b;
                                r3 += dr3;
                                g3 += dg3;
                                b3 += db3;
                                r1 += dr1;
                                g1 += dg1;
                                b1 += db1;
                                z_a += depth_increment;
                                y_a += width;
                            }
                        } else {
                            y_c -= y_b;
                            y_b -= y_a;
                            y_a = scanOffsets[y_a];

                            while(true) {
                                --y_b;
                                if (y_b < 0) {
                                    while(true) {
                                        --y_c;
                                        if (y_c < 0) {
                                            return;
                                        }

                                        drawShadedScanline(pixels, y_a, x_b >> 16, x_c >> 16, r2, g2, b2, r3, g3, b3, z_a, depth_slope);
                                        x_c += c_to_a;
                                        x_b += b_to_c;
                                        r3 += dr3;
                                        g3 += dg3;
                                        b3 += db3;
                                        r2 += dr2;
                                        g2 += dg2;
                                        b2 += db2;
                                        y_a += width;
                                        z_a += depth_increment;
                                    }
                                }

                                drawShadedScanline(pixels, y_a, x_a >> 16, x_c >> 16, r1, g1, b1, r3, g3, b3, z_a, depth_slope);
                                x_c += c_to_a;
                                x_a += a_to_b;
                                r3 += dr3;
                                g3 += dg3;
                                b3 += db3;
                                r1 += dr1;
                                g1 += dg1;
                                b1 += db1;
                                z_a += depth_increment;
                                y_a += width;
                            }
                        }
                    } else {
                        x_b = x_a <<= 16;
                        r2 = r1 <<= 16;
                        g2 = g1 <<= 16;
                        b2 = b1 <<= 16;
                        if (y_a < 0) {
                            x_b -= c_to_a * y_a;
                            x_a -= a_to_b * y_a;
                            r2 -= dr3 * y_a;
                            g2 -= dg3 * y_a;
                            b2 -= db3 * y_a;
                            r1 -= dr1 * y_a;
                            g1 -= dg1 * y_a;
                            b1 -= db1 * y_a;
                            z_a -= depth_increment * (float)y_a;
                            y_a = 0;
                        }

                        x_c <<= 16;
                        r3 <<= 16;
                        g3 <<= 16;
                        b3 <<= 16;
                        if (y_c < 0) {
                            x_c -= b_to_c * y_c;
                            r3 -= dr2 * y_c;
                            g3 -= dg2 * y_c;
                            b3 -= db2 * y_c;
                            y_c = 0;
                        }

                        if ((y_a == y_c || c_to_a >= a_to_b) && (y_a != y_c || b_to_c <= a_to_b)) {
                            y_b -= y_c;
                            y_c -= y_a;
                            y_a = scanOffsets[y_a];

                            while(true) {
                                --y_c;
                                if (y_c < 0) {
                                    while(true) {
                                        --y_b;
                                        if (y_b < 0) {
                                            return;
                                        }

                                        drawShadedScanline(pixels, y_a, x_a >> 16, x_c >> 16, r1, g1, b1, r3, g3, b3, z_a, depth_slope);
                                        x_c += b_to_c;
                                        x_a += a_to_b;
                                        r3 += dr2;
                                        g3 += dg2;
                                        b3 += db2;
                                        r1 += dr1;
                                        g1 += dg1;
                                        b1 += db1;
                                        y_a += width;
                                        z_a += depth_increment;
                                    }
                                }

                                drawShadedScanline(pixels, y_a, x_a >> 16, x_b >> 16, r1, g1, b1, r2, g2, b2, z_a, depth_slope);
                                x_b += c_to_a;
                                x_a += a_to_b;
                                r2 += dr3;
                                g2 += dg3;
                                b2 += db3;
                                r1 += dr1;
                                g1 += dg1;
                                b1 += db1;
                                z_a += depth_increment;
                                y_a += width;
                            }
                        } else {
                            y_b -= y_c;
                            y_c -= y_a;
                            y_a = scanOffsets[y_a];

                            while(true) {
                                --y_c;
                                if (y_c < 0) {
                                    while(true) {
                                        --y_b;
                                        if (y_b < 0) {
                                            return;
                                        }

                                        drawShadedScanline(pixels, y_a, x_c >> 16, x_a >> 16, r3, g3, b3, r1, g1, b1, z_a, depth_slope);
                                        x_c += b_to_c;
                                        x_a += a_to_b;
                                        r3 += dr2;
                                        g3 += dg2;
                                        b3 += db2;
                                        r1 += dr1;
                                        g1 += dg1;
                                        b1 += db1;
                                        y_a += width;
                                        z_a += depth_increment;
                                    }
                                }

                                drawShadedScanline(pixels, y_a, x_b >> 16, x_a >> 16, r2, g2, b2, r1, g1, b1, z_a, depth_slope);
                                x_b += c_to_a;
                                x_a += a_to_b;
                                r2 += dr3;
                                g2 += dg3;
                                b2 += db3;
                                r1 += dr1;
                                g1 += dg1;
                                b1 += db1;
                                z_a += depth_increment;
                                y_a += width;
                            }
                        }
                    }
                }
            } else if (y_b <= y_c) {
                if (y_b < bottomY) {
                    if (y_c > bottomY) {
                        y_c = bottomY;
                    }

                    if (y_a > bottomY) {
                        y_a = bottomY;
                    }

                    z_b = z_b - depth_slope * (float)x_b + depth_slope;
                    if (y_c < y_a) {
                        x_a = x_b <<= 16;
                        r1 = r2 <<= 16;
                        g1 = g2 <<= 16;
                        b1 = b2 <<= 16;
                        if (y_b < 0) {
                            x_a -= a_to_b * y_b;
                            x_b -= b_to_c * y_b;
                            r1 -= dr1 * y_b;
                            g1 -= dg1 * y_b;
                            b1 -= db1 * y_b;
                            r2 -= dr2 * y_b;
                            g2 -= dg2 * y_b;
                            b2 -= db2 * y_b;
                            z_b -= depth_increment * (float)y_b;
                            y_b = 0;
                        }

                        x_c <<= 16;
                        r3 <<= 16;
                        g3 <<= 16;
                        b3 <<= 16;
                        if (y_c < 0) {
                            x_c -= c_to_a * y_c;
                            r3 -= dr3 * y_c;
                            g3 -= dg3 * y_c;
                            b3 -= db3 * y_c;
                            y_c = 0;
                        }

                        if (y_b != y_c && a_to_b < b_to_c || y_b == y_c && a_to_b > c_to_a) {
                            y_a -= y_c;
                            y_c -= y_b;
                            y_b = scanOffsets[y_b];

                            while(true) {
                                --y_c;
                                if (y_c < 0) {
                                    while(true) {
                                        --y_a;
                                        if (y_a < 0) {
                                            return;
                                        }

                                        drawShadedScanline(pixels, y_b, x_a >> 16, x_c >> 16, r1, g1, b1, r3, g3, b3, z_b, depth_slope);
                                        x_a += a_to_b;
                                        x_c += c_to_a;
                                        r1 += dr1;
                                        g1 += dg1;
                                        b1 += db1;
                                        r3 += dr3;
                                        g3 += dg3;
                                        b3 += db3;
                                        y_b += width;
                                        z_b += depth_increment;
                                    }
                                }

                                drawShadedScanline(pixels, y_b, x_a >> 16, x_b >> 16, r1, g1, b1, r2, g2, b2, z_b, depth_slope);
                                x_a += a_to_b;
                                x_b += b_to_c;
                                r1 += dr1;
                                g1 += dg1;
                                b1 += db1;
                                r2 += dr2;
                                g2 += dg2;
                                b2 += db2;
                                z_b += depth_increment;
                                y_b += width;
                            }
                        } else {
                            y_a -= y_c;
                            y_c -= y_b;
                            y_b = scanOffsets[y_b];

                            while(true) {
                                --y_c;
                                if (y_c < 0) {
                                    while(true) {
                                        --y_a;
                                        if (y_a < 0) {
                                            return;
                                        }

                                        drawShadedScanline(pixels, y_b, x_c >> 16, x_a >> 16, r3, g3, b3, r1, g1, b1, z_b, depth_slope);
                                        x_a += a_to_b;
                                        x_c += c_to_a;
                                        r1 += dr1;
                                        g1 += dg1;
                                        b1 += db1;
                                        r3 += dr3;
                                        g3 += dg3;
                                        b3 += db3;
                                        y_b += width;
                                        z_b += depth_increment;
                                    }
                                }

                                drawShadedScanline(pixels, y_b, x_b >> 16, x_a >> 16, r2, g2, b2, r1, g1, b1, z_b, depth_slope);
                                x_a += a_to_b;
                                x_b += b_to_c;
                                r1 += dr1;
                                g1 += dg1;
                                b1 += db1;
                                r2 += dr2;
                                g2 += dg2;
                                b2 += db2;
                                z_b += depth_increment;
                                y_b += width;
                            }
                        }
                    } else {
                        x_c = x_b <<= 16;
                        r3 = r2 <<= 16;
                        g3 = g2 <<= 16;
                        b3 = b2 <<= 16;
                        if (y_b < 0) {
                            x_c -= a_to_b * y_b;
                            x_b -= b_to_c * y_b;
                            r3 -= dr1 * y_b;
                            g3 -= dg1 * y_b;
                            b3 -= db1 * y_b;
                            r2 -= dr2 * y_b;
                            g2 -= dg2 * y_b;
                            b2 -= db2 * y_b;
                            z_b -= depth_increment * (float)y_b;
                            y_b = 0;
                        }

                        x_a <<= 16;
                        r1 <<= 16;
                        g1 <<= 16;
                        b1 <<= 16;
                        if (y_a < 0) {
                            x_a -= c_to_a * y_a;
                            r1 -= dr3 * y_a;
                            g1 -= dg3 * y_a;
                            b1 -= db3 * y_a;
                            y_a = 0;
                        }

                        if (a_to_b < b_to_c) {
                            y_c -= y_a;
                            y_a -= y_b;
                            y_b = scanOffsets[y_b];

                            while(true) {
                                --y_a;
                                if (y_a < 0) {
                                    while(true) {
                                        --y_c;
                                        if (y_c < 0) {
                                            return;
                                        }

                                        drawShadedScanline(pixels, y_b, x_a >> 16, x_b >> 16, r1, g1, b1, r2, g2, b2, z_b, depth_slope);
                                        x_a += c_to_a;
                                        x_b += b_to_c;
                                        r1 += dr3;
                                        g1 += dg3;
                                        b1 += db3;
                                        r2 += dr2;
                                        g2 += dg2;
                                        b2 += db2;
                                        y_b += width;
                                        z_b += depth_increment;
                                    }
                                }

                                drawShadedScanline(pixels, y_b, x_c >> 16, x_b >> 16, r3, g3, b3, r2, g2, b2, z_b, depth_slope);
                                x_c += a_to_b;
                                x_b += b_to_c;
                                r3 += dr1;
                                g3 += dg1;
                                b3 += db1;
                                r2 += dr2;
                                g2 += dg2;
                                b2 += db2;
                                z_b += depth_increment;
                                y_b += width;
                            }
                        } else {
                            y_c -= y_a;
                            y_a -= y_b;
                            y_b = scanOffsets[y_b];

                            while(true) {
                                --y_a;
                                if (y_a < 0) {
                                    while(true) {
                                        --y_c;
                                        if (y_c < 0) {
                                            return;
                                        }

                                        drawShadedScanline(pixels, y_b, x_b >> 16, x_a >> 16, r2, g2, b2, r1, g1, b1, z_b, depth_slope);
                                        x_a += c_to_a;
                                        x_b += b_to_c;
                                        r1 += dr3;
                                        g1 += dg3;
                                        b1 += db3;
                                        r2 += dr2;
                                        g2 += dg2;
                                        b2 += db2;
                                        y_b += width;
                                        z_b += depth_increment;
                                    }
                                }

                                drawShadedScanline(pixels, y_b, x_b >> 16, x_c >> 16, r2, g2, b2, r3, g3, b3, z_b, depth_slope);
                                x_c += a_to_b;
                                x_b += b_to_c;
                                r3 += dr1;
                                g3 += dg1;
                                b3 += db1;
                                r2 += dr2;
                                g2 += dg2;
                                b2 += db2;
                                z_b += depth_increment;
                                y_b += width;
                            }
                        }
                    }
                }
            } else if (y_c < bottomY) {
                if (y_a > bottomY) {
                    y_a = bottomY;
                }

                if (y_b > bottomY) {
                    y_b = bottomY;
                }

                z_c = z_c - depth_slope * (float)x_c + depth_slope;
                if (y_a < y_b) {
                    x_b = x_c <<= 16;
                    r2 = r3 <<= 16;
                    g2 = g3 <<= 16;
                    b2 = b3 <<= 16;
                    if (y_c < 0) {
                        x_b -= b_to_c * y_c;
                        x_c -= c_to_a * y_c;
                        r2 -= dr2 * y_c;
                        g2 -= dg2 * y_c;
                        b2 -= db2 * y_c;
                        r3 -= dr3 * y_c;
                        g3 -= dg3 * y_c;
                        b3 -= db3 * y_c;
                        z_c -= depth_increment * (float)y_c;
                        y_c = 0;
                    }

                    x_a <<= 16;
                    r1 <<= 16;
                    g1 <<= 16;
                    b1 <<= 16;
                    if (y_a < 0) {
                        x_a -= a_to_b * y_a;
                        r1 -= dr1 * y_a;
                        g1 -= dg1 * y_a;
                        b1 -= db1 * y_a;
                        y_a = 0;
                    }

                    if (b_to_c < c_to_a) {
                        y_b -= y_a;
                        y_a -= y_c;
                        y_c = scanOffsets[y_c];

                        while(true) {
                            --y_a;
                            if (y_a < 0) {
                                while(true) {
                                    --y_b;
                                    if (y_b < 0) {
                                        return;
                                    }

                                    drawShadedScanline(pixels, y_c, x_b >> 16, x_a >> 16, r2, g2, b2, r1, g1, b1, z_c, depth_slope);
                                    x_b += b_to_c;
                                    x_a += a_to_b;
                                    r2 += dr2;
                                    g2 += dg2;
                                    b2 += db2;
                                    r1 += dr1;
                                    g1 += dg1;
                                    b1 += db1;
                                    y_c += width;
                                    z_c += depth_increment;
                                }
                            }

                            drawShadedScanline(pixels, y_c, x_b >> 16, x_c >> 16, r2, g2, b2, r3, g3, b3, z_c, depth_slope);
                            x_b += b_to_c;
                            x_c += c_to_a;
                            r2 += dr2;
                            g2 += dg2;
                            b2 += db2;
                            r3 += dr3;
                            g3 += dg3;
                            b3 += db3;
                            z_c += depth_increment;
                            y_c += width;
                        }
                    } else {
                        y_b -= y_a;
                        y_a -= y_c;
                        y_c = scanOffsets[y_c];

                        while(true) {
                            --y_a;
                            if (y_a < 0) {
                                while(true) {
                                    --y_b;
                                    if (y_b < 0) {
                                        return;
                                    }

                                    drawShadedScanline(pixels, y_c, x_a >> 16, x_b >> 16, r1, g1, b1, r2, g2, b2, z_c, depth_slope);
                                    x_b += b_to_c;
                                    x_a += a_to_b;
                                    r2 += dr2;
                                    g2 += dg2;
                                    b2 += db2;
                                    r1 += dr1;
                                    g1 += dg1;
                                    b1 += db1;
                                    z_c += depth_increment;
                                    y_c += width;
                                }
                            }

                            drawShadedScanline(pixels, y_c, x_c >> 16, x_b >> 16, r3, g3, b3, r2, g2, b2, z_c, depth_slope);
                            x_b += b_to_c;
                            x_c += c_to_a;
                            r2 += dr2;
                            g2 += dg2;
                            b2 += db2;
                            r3 += dr3;
                            g3 += dg3;
                            b3 += db3;
                            z_c += depth_increment;
                            y_c += width;
                        }
                    }
                } else {
                    x_a = x_c <<= 16;
                    r1 = r3 <<= 16;
                    g1 = g3 <<= 16;
                    b1 = b3 <<= 16;
                    if (y_c < 0) {
                        x_a -= b_to_c * y_c;
                        x_c -= c_to_a * y_c;
                        r1 -= dr2 * y_c;
                        g1 -= dg2 * y_c;
                        b1 -= db2 * y_c;
                        r3 -= dr3 * y_c;
                        g3 -= dg3 * y_c;
                        b3 -= db3 * y_c;
                        z_c -= depth_increment * (float)y_c;
                        y_c = 0;
                    }

                    x_b <<= 16;
                    r2 <<= 16;
                    g2 <<= 16;
                    b2 <<= 16;
                    if (y_b < 0) {
                        x_b -= a_to_b * y_b;
                        r2 -= dr1 * y_b;
                        g2 -= dg1 * y_b;
                        b2 -= db1 * y_b;
                        y_b = 0;
                    }

                    if (b_to_c < c_to_a) {
                        y_a -= y_b;
                        y_b -= y_c;
                        y_c = scanOffsets[y_c];

                        while(true) {
                            --y_b;
                            if (y_b < 0) {
                                while(true) {
                                    --y_a;
                                    if (y_a < 0) {
                                        return;
                                    }

                                    drawShadedScanline(pixels, y_c, x_b >> 16, x_c >> 16, r2, g2, b2, r3, g3, b3, z_c, depth_slope);
                                    x_b += a_to_b;
                                    x_c += c_to_a;
                                    r2 += dr1;
                                    g2 += dg1;
                                    b2 += db1;
                                    r3 += dr3;
                                    g3 += dg3;
                                    b3 += db3;
                                    z_c += depth_increment;
                                    y_c += width;
                                }
                            }

                            drawShadedScanline(pixels, y_c, x_a >> 16, x_c >> 16, r1, g1, b1, r3, g3, b3, z_c, depth_slope);
                            x_a += b_to_c;
                            x_c += c_to_a;
                            r1 += dr2;
                            g1 += dg2;
                            b1 += db2;
                            r3 += dr3;
                            g3 += dg3;
                            b3 += db3;
                            z_c += depth_increment;
                            y_c += width;
                        }
                    } else {
                        y_a -= y_b;
                        y_b -= y_c;
                        y_c = scanOffsets[y_c];

                        while(true) {
                            --y_b;
                            if (y_b < 0) {
                                while(true) {
                                    --y_a;
                                    if (y_a < 0) {
                                        return;
                                    }

                                    drawShadedScanline(pixels, y_c, x_c >> 16, x_b >> 16, r3, g3, b3, r2, g2, b2, z_c, depth_slope);
                                    x_b += a_to_b;
                                    x_c += c_to_a;
                                    r2 += dr1;
                                    g2 += dg1;
                                    b2 += db1;
                                    r3 += dr3;
                                    g3 += dg3;
                                    b3 += db3;
                                    y_c += width;
                                    z_c += depth_increment;
                                }
                            }

                            drawShadedScanline(pixels, y_c, x_c >> 16, x_a >> 16, r3, g3, b3, r1, g1, b1, z_c, depth_slope);
                            x_a += b_to_c;
                            x_c += c_to_a;
                            r1 += dr2;
                            g1 += dg2;
                            b1 += db2;
                            r3 += dr3;
                            g3 += dg3;
                            b3 += db3;
                            z_c += depth_increment;
                            y_c += width;
                        }
                    }
                }
            }
        }
    }

    public static void drawShadedScanline(int[] dest, int offset, int x1, int x2, int r1, int g1, int b1, int r2, int g2, int b2, float depth, float depth_slope) {
        int n = x2 - x1;
        if (n > 0) {
            r2 = (r2 - r1) / n;
            g2 = (g2 - g1) / n;
            b2 = (b2 - b1) / n;
            if (textureOutOfDrawingBounds) {
                if (x2 > lastX) {
                    n -= x2 - lastX;
                    x2 = lastX;
                }

                if (x1 < 0) {
                    n = x2;
                    r1 -= x1 * r2;
                    g1 -= x1 * g2;
                    b1 -= x1 * b2;
                    x1 = 0;
                }
            }

            if (x1 < x2) {
                offset += x1;
                depth += depth_slope * (float)x1;
                if (alpha == 0) {
                    while(true) {
                        --n;
                        if (n < 0) {
                            break;
                        }

                        dest[offset] = r1 & 16711680 | g1 >> 8 & '\uff00' | b1 >> 16 & 255;
                        depthBuffer[offset] = depth;
                        depth += depth_slope;
                        r1 += r2;
                        g1 += g2;
                        b1 += b2;
                        ++offset;
                    }
                } else {
                    int a1 = alpha;
                    int a2 = 256 - alpha;

                    while(true) {
                        --n;
                        if (n < 0) {
                            break;
                        }

                        int rgb = r1 & 16711680 | g1 >> 8 & '\uff00' | b1 >> 16 & 255;
                        rgb = ((rgb & 16711935) * a2 >> 8 & 16711935) + ((rgb & '\uff00') * a2 >> 8 & '\uff00');
                        int dst = dest[offset];
                        dest[offset] = rgb + ((dst & 16711935) * a1 >> 8 & 16711935) + ((dst & '\uff00') * a1 >> 8 & '\uff00');
                        depthBuffer[offset] = depth;
                        depth += depth_slope;
                        r1 += r2;
                        g1 += g2;
                        b1 += b2;
                        ++offset;
                    }
                }
            }

        }
    }

    public static void drawFlatTriangle(int y_a, int y_b, int y_c, int x_a, int x_b, int x_c, int k1, float z_a, float z_b, float z_c) {
        if (z_a >= 0.0F && z_b >= 0.0F && z_c >= 0.0F) {
            int a_to_b = 0;
            if (y_b != y_a) {
                a_to_b = (x_b - x_a << 16) / (y_b - y_a);
            }

            int b_to_c = 0;
            if (y_c != y_b) {
                b_to_c = (x_c - x_b << 16) / (y_c - y_b);
            }

            int c_to_a = 0;
            if (y_c != y_a) {
                c_to_a = (x_a - x_c << 16) / (y_a - y_c);
            }

            float b_aX = (float)(x_b - x_a);
            float b_aY = (float)(y_b - y_a);
            float c_aX = (float)(x_c - x_a);
            float c_aY = (float)(y_c - y_a);
            float b_aZ = z_b - z_a;
            float c_aZ = z_c - z_a;
            float div = b_aX * c_aY - c_aX * b_aY;
            float depth_slope = (b_aZ * c_aY - c_aZ * b_aY) / div;
            float depth_increment = (c_aZ * b_aX - b_aZ * c_aX) / div;
            if (y_a <= y_b && y_a <= y_c) {
                if (y_a < bottomY) {
                    if (y_b > bottomY) {
                        y_b = bottomY;
                    }

                    if (y_c > bottomY) {
                        y_c = bottomY;
                    }

                    z_a = z_a - depth_slope * (float)x_a + depth_slope;
                    if (y_b < y_c) {
                        x_c = x_a <<= 16;
                        if (y_a < 0) {
                            x_c -= c_to_a * y_a;
                            x_a -= a_to_b * y_a;
                            z_a -= depth_increment * (float)y_a;
                            y_a = 0;
                        }

                        x_b <<= 16;
                        if (y_b < 0) {
                            x_b -= b_to_c * y_b;
                            y_b = 0;
                        }

                        if (y_a != y_b && c_to_a < a_to_b || y_a == y_b && c_to_a > b_to_c) {
                            y_c -= y_b;
                            y_b -= y_a;
                            y_a = scanOffsets[y_a];

                            while(true) {
                                --y_b;
                                if (y_b < 0) {
                                    while(true) {
                                        --y_c;
                                        if (y_c < 0) {
                                            return;
                                        }

                                        drawFlatTexturedScanline(pixels, y_a, k1, x_c >> 16, x_b >> 16, z_a, depth_slope);
                                        x_c += c_to_a;
                                        x_b += b_to_c;
                                        y_a += width;
                                        z_a += depth_increment;
                                    }
                                }

                                drawFlatTexturedScanline(pixels, y_a, k1, x_c >> 16, x_a >> 16, z_a, depth_slope);
                                x_c += c_to_a;
                                x_a += a_to_b;
                                z_a += depth_increment;
                                y_a += width;
                            }
                        } else {
                            y_c -= y_b;
                            y_b -= y_a;
                            y_a = scanOffsets[y_a];

                            while(true) {
                                --y_b;
                                if (y_b < 0) {
                                    while(true) {
                                        --y_c;
                                        if (y_c < 0) {
                                            return;
                                        }

                                        drawFlatTexturedScanline(pixels, y_a, k1, x_b >> 16, x_c >> 16, z_a, depth_slope);
                                        x_c += c_to_a;
                                        x_b += b_to_c;
                                        y_a += width;
                                        z_a += depth_increment;
                                    }
                                }

                                drawFlatTexturedScanline(pixels, y_a, k1, x_a >> 16, x_c >> 16, z_a, depth_slope);
                                x_c += c_to_a;
                                x_a += a_to_b;
                                z_a += depth_increment;
                                y_a += width;
                            }
                        }
                    } else {
                        x_b = x_a <<= 16;
                        if (y_a < 0) {
                            x_b -= c_to_a * y_a;
                            x_a -= a_to_b * y_a;
                            z_a -= depth_increment * (float)y_a;
                            y_a = 0;
                        }

                        x_c <<= 16;
                        if (y_c < 0) {
                            x_c -= b_to_c * y_c;
                            y_c = 0;
                        }

                        if ((y_a == y_c || c_to_a >= a_to_b) && (y_a != y_c || b_to_c <= a_to_b)) {
                            y_b -= y_c;
                            y_c -= y_a;
                            y_a = scanOffsets[y_a];

                            while(true) {
                                --y_c;
                                if (y_c < 0) {
                                    while(true) {
                                        --y_b;
                                        if (y_b < 0) {
                                            return;
                                        }

                                        drawFlatTexturedScanline(pixels, y_a, k1, x_a >> 16, x_c >> 16, z_a, depth_slope);
                                        z_a += depth_increment;
                                        x_c += b_to_c;
                                        x_a += a_to_b;
                                        y_a += width;
                                    }
                                }

                                drawFlatTexturedScanline(pixels, y_a, k1, x_a >> 16, x_b >> 16, z_a, depth_slope);
                                z_a += depth_increment;
                                x_b += c_to_a;
                                x_a += a_to_b;
                                y_a += width;
                            }
                        } else {
                            y_b -= y_c;
                            y_c -= y_a;
                            y_a = scanOffsets[y_a];

                            while(true) {
                                --y_c;
                                if (y_c < 0) {
                                    while(true) {
                                        --y_b;
                                        if (y_b < 0) {
                                            return;
                                        }

                                        drawFlatTexturedScanline(pixels, y_a, k1, x_c >> 16, x_a >> 16, z_a, depth_slope);
                                        z_a += depth_increment;
                                        x_c += b_to_c;
                                        x_a += a_to_b;
                                        y_a += width;
                                    }
                                }

                                drawFlatTexturedScanline(pixels, y_a, k1, x_b >> 16, x_a >> 16, z_a, depth_slope);
                                z_a += depth_increment;
                                x_b += c_to_a;
                                x_a += a_to_b;
                                y_a += width;
                            }
                        }
                    }
                }
            } else if (y_b <= y_c) {
                if (y_b < bottomY) {
                    if (y_c > bottomY) {
                        y_c = bottomY;
                    }

                    if (y_a > bottomY) {
                        y_a = bottomY;
                    }

                    z_b = z_b - depth_slope * (float)x_b + depth_slope;
                    if (y_c < y_a) {
                        x_a = x_b <<= 16;
                        if (y_b < 0) {
                            x_a -= a_to_b * y_b;
                            x_b -= b_to_c * y_b;
                            z_b -= depth_increment * (float)y_b;
                            y_b = 0;
                        }

                        x_c <<= 16;
                        if (y_c < 0) {
                            x_c -= c_to_a * y_c;
                            y_c = 0;
                        }

                        if (y_b != y_c && a_to_b < b_to_c || y_b == y_c && a_to_b > c_to_a) {
                            y_a -= y_c;
                            y_c -= y_b;
                            y_b = scanOffsets[y_b];

                            while(true) {
                                --y_c;
                                if (y_c < 0) {
                                    while(true) {
                                        --y_a;
                                        if (y_a < 0) {
                                            return;
                                        }

                                        drawFlatTexturedScanline(pixels, y_b, k1, x_a >> 16, x_c >> 16, z_b, depth_slope);
                                        z_b += depth_increment;
                                        x_a += a_to_b;
                                        x_c += c_to_a;
                                        y_b += width;
                                    }
                                }

                                drawFlatTexturedScanline(pixels, y_b, k1, x_a >> 16, x_b >> 16, z_b, depth_slope);
                                z_b += depth_increment;
                                x_a += a_to_b;
                                x_b += b_to_c;
                                y_b += width;
                            }
                        } else {
                            y_a -= y_c;
                            y_c -= y_b;
                            y_b = scanOffsets[y_b];

                            while(true) {
                                --y_c;
                                if (y_c < 0) {
                                    while(true) {
                                        --y_a;
                                        if (y_a < 0) {
                                            return;
                                        }

                                        drawFlatTexturedScanline(pixels, y_b, k1, x_c >> 16, x_a >> 16, z_b, depth_slope);
                                        z_b += depth_increment;
                                        x_a += a_to_b;
                                        x_c += c_to_a;
                                        y_b += width;
                                    }
                                }

                                drawFlatTexturedScanline(pixels, y_b, k1, x_b >> 16, x_a >> 16, z_b, depth_slope);
                                z_b += depth_increment;
                                x_a += a_to_b;
                                x_b += b_to_c;
                                y_b += width;
                            }
                        }
                    } else {
                        x_c = x_b <<= 16;
                        if (y_b < 0) {
                            x_c -= a_to_b * y_b;
                            x_b -= b_to_c * y_b;
                            z_b -= depth_increment * (float)y_b;
                            y_b = 0;
                        }

                        x_a <<= 16;
                        if (y_a < 0) {
                            x_a -= c_to_a * y_a;
                            y_a = 0;
                        }

                        if (a_to_b < b_to_c) {
                            y_c -= y_a;
                            y_a -= y_b;
                            y_b = scanOffsets[y_b];

                            while(true) {
                                --y_a;
                                if (y_a < 0) {
                                    while(true) {
                                        --y_c;
                                        if (y_c < 0) {
                                            return;
                                        }

                                        drawFlatTexturedScanline(pixels, y_b, k1, x_a >> 16, x_b >> 16, z_b, depth_slope);
                                        z_b += depth_increment;
                                        x_a += c_to_a;
                                        x_b += b_to_c;
                                        y_b += width;
                                    }
                                }

                                drawFlatTexturedScanline(pixels, y_b, k1, x_c >> 16, x_b >> 16, z_b, depth_slope);
                                z_b += depth_increment;
                                x_c += a_to_b;
                                x_b += b_to_c;
                                y_b += width;
                            }
                        } else {
                            y_c -= y_a;
                            y_a -= y_b;
                            y_b = scanOffsets[y_b];

                            while(true) {
                                --y_a;
                                if (y_a < 0) {
                                    while(true) {
                                        --y_c;
                                        if (y_c < 0) {
                                            return;
                                        }

                                        drawFlatTexturedScanline(pixels, y_b, k1, x_b >> 16, x_a >> 16, z_b, depth_slope);
                                        z_b += depth_increment;
                                        x_a += c_to_a;
                                        x_b += b_to_c;
                                        y_b += width;
                                    }
                                }

                                drawFlatTexturedScanline(pixels, y_b, k1, x_b >> 16, x_c >> 16, z_b, depth_slope);
                                z_b += depth_increment;
                                x_c += a_to_b;
                                x_b += b_to_c;
                                y_b += width;
                            }
                        }
                    }
                }
            } else if (y_c < bottomY) {
                if (y_a > bottomY) {
                    y_a = bottomY;
                }

                if (y_b > bottomY) {
                    y_b = bottomY;
                }

                z_c = z_c - depth_slope * (float)x_c + depth_slope;
                if (y_a < y_b) {
                    x_b = x_c <<= 16;
                    if (y_c < 0) {
                        x_b -= b_to_c * y_c;
                        x_c -= c_to_a * y_c;
                        z_c -= depth_increment * (float)y_c;
                        y_c = 0;
                    }

                    x_a <<= 16;
                    if (y_a < 0) {
                        x_a -= a_to_b * y_a;
                        y_a = 0;
                    }

                    if (b_to_c < c_to_a) {
                        y_b -= y_a;
                        y_a -= y_c;
                        y_c = scanOffsets[y_c];

                        while(true) {
                            --y_a;
                            if (y_a < 0) {
                                while(true) {
                                    --y_b;
                                    if (y_b < 0) {
                                        return;
                                    }

                                    drawFlatTexturedScanline(pixels, y_c, k1, x_b >> 16, x_a >> 16, z_c, depth_slope);
                                    z_c += depth_increment;
                                    x_b += b_to_c;
                                    x_a += a_to_b;
                                    y_c += width;
                                }
                            }

                            drawFlatTexturedScanline(pixels, y_c, k1, x_b >> 16, x_c >> 16, z_c, depth_slope);
                            z_c += depth_increment;
                            x_b += b_to_c;
                            x_c += c_to_a;
                            y_c += width;
                        }
                    } else {
                        y_b -= y_a;
                        y_a -= y_c;
                        y_c = scanOffsets[y_c];

                        while(true) {
                            --y_a;
                            if (y_a < 0) {
                                while(true) {
                                    --y_b;
                                    if (y_b < 0) {
                                        return;
                                    }

                                    drawFlatTexturedScanline(pixels, y_c, k1, x_a >> 16, x_b >> 16, z_c, depth_slope);
                                    z_c += depth_increment;
                                    x_b += b_to_c;
                                    x_a += a_to_b;
                                    y_c += width;
                                }
                            }

                            drawFlatTexturedScanline(pixels, y_c, k1, x_c >> 16, x_b >> 16, z_c, depth_slope);
                            z_c += depth_increment;
                            x_b += b_to_c;
                            x_c += c_to_a;
                            y_c += width;
                        }
                    }
                } else {
                    x_a = x_c <<= 16;
                    if (y_c < 0) {
                        x_a -= b_to_c * y_c;
                        x_c -= c_to_a * y_c;
                        z_c -= depth_increment * (float)y_c;
                        y_c = 0;
                    }

                    x_b <<= 16;
                    if (y_b < 0) {
                        x_b -= a_to_b * y_b;
                        y_b = 0;
                    }

                    if (b_to_c < c_to_a) {
                        y_a -= y_b;
                        y_b -= y_c;
                        y_c = scanOffsets[y_c];

                        while(true) {
                            --y_b;
                            if (y_b < 0) {
                                while(true) {
                                    --y_a;
                                    if (y_a < 0) {
                                        return;
                                    }

                                    drawFlatTexturedScanline(pixels, y_c, k1, x_b >> 16, x_c >> 16, z_c, depth_slope);
                                    z_c += depth_increment;
                                    x_b += a_to_b;
                                    x_c += c_to_a;
                                    y_c += width;
                                }
                            }

                            drawFlatTexturedScanline(pixels, y_c, k1, x_a >> 16, x_c >> 16, z_c, depth_slope);
                            z_c += depth_increment;
                            x_a += b_to_c;
                            x_c += c_to_a;
                            y_c += width;
                        }
                    } else {
                        y_a -= y_b;
                        y_b -= y_c;
                        y_c = scanOffsets[y_c];

                        while(true) {
                            --y_b;
                            if (y_b < 0) {
                                while(true) {
                                    --y_a;
                                    if (y_a < 0) {
                                        return;
                                    }

                                    drawFlatTexturedScanline(pixels, y_c, k1, x_c >> 16, x_b >> 16, z_c, depth_slope);
                                    z_c += depth_increment;
                                    x_b += a_to_b;
                                    x_c += c_to_a;
                                    y_c += width;
                                }
                            }

                            drawFlatTexturedScanline(pixels, y_c, k1, x_c >> 16, x_a >> 16, z_c, depth_slope);
                            z_c += depth_increment;
                            x_a += b_to_c;
                            x_c += c_to_a;
                            y_c += width;
                        }
                    }
                }
            }
        }
    }

    private static void drawFlatTexturedScanline(int[] dest, int dest_off, int loops, int start_x, int end_x, float depth, float depth_slope) {
        if (textureOutOfDrawingBounds) {
            if (end_x > lastX) {
                end_x = lastX;
            }

            if (start_x < 0) {
                start_x = 0;
            }
        }

        if (start_x < end_x) {
            dest_off += start_x;
            int rgb = end_x - start_x >> 2;
            depth += depth_slope * (float)start_x;
            int dest_alpha;
            if (alpha == 0) {
                while(true) {
                    --rgb;
                    if (rgb < 0) {
                        rgb = end_x - start_x & 3;

                        while(true) {
                            --rgb;
                            if (rgb < 0) {
                                return;
                            }

                            dest[dest_off] = loops;
                            depthBuffer[dest_off] = depth;
                            ++dest_off;
                            depth += depth_slope;
                        }
                    }

                    for(dest_alpha = 0; dest_alpha < 4; ++dest_alpha) {
                        dest[dest_off] = loops;
                        depthBuffer[dest_off] = depth;
                        ++dest_off;
                        depth += depth_slope;
                    }
                }
            } else {
                dest_alpha = alpha;
                int src_alpha = 256 - alpha;
                loops = ((loops & 16711935) * src_alpha >> 8 & 16711935) + ((loops & '\uff00') * src_alpha >> 8 & '\uff00');

                while(true) {
                    --rgb;
                    if (rgb < 0) {
                        rgb = end_x - start_x & 3;

                        while(true) {
                            --rgb;
                            if (rgb < 0) {
                                return;
                            }

                            dest[dest_off] = loops + ((dest[dest_off] & 16711935) * dest_alpha >> 8 & 16711935) + ((dest[dest_off] & '\uff00') * dest_alpha >> 8 & '\uff00');
                            depthBuffer[dest_off] = depth;
                            ++dest_off;
                            depth += depth_slope;
                        }
                    }

                    for(int i = 0; i < 4; ++i) {
                        dest[dest_off] = loops + ((dest[dest_off] & 16711935) * dest_alpha >> 8 & 16711935) + ((dest[dest_off] & '\uff00') * dest_alpha >> 8 & '\uff00');
                        depthBuffer[dest_off] = depth;
                        ++dest_off;
                        depth += depth_slope;
                    }
                }
            }
        }
    }

    public static void drawTexturedTriangle(int y_a, int y_b, int y_c, int x_a, int x_b, int x_c, int k1, int l1, int i2, int Px, int Mx, int Nx, int Pz, int Mz, int Nz, int Py, int My, int Ny, int k4, float z_a, float z_b, float z_c) {
        if (z_a >= 0.0F && z_b >= 0.0F && z_c >= 0.0F) {
            int[] texture = getTexturePixels(k4);
            aBoolean1463 = !textureIsTransparant[k4];
            Mx = Px - Mx;
            Mz = Pz - Mz;
            My = Py - My;
            Nx -= Px;
            Nz -= Pz;
            Ny -= Py;
            int Oa = Nx * Pz - Nz * Px << (viewDistance == 9 ? 14 : 15);
            int Ha = Nz * Py - Ny * Pz << 8;
            int Va = Ny * Px - Nx * Py << 5;
            int Ob = Mx * Pz - Mz * Px << (viewDistance == 9 ? 14 : 15);
            int Hb = Mz * Py - My * Pz << 8;
            int Vb = My * Px - Mx * Py << 5;
            int Oc = Mz * Nx - Mx * Nz << (viewDistance == 9 ? 14 : 15);
            int Hc = My * Nz - Mz * Ny << 8;
            int Vc = Mx * Ny - My * Nx << 5;
            int a_to_b = 0;
            int grad_a_off = 0;
            if (y_b != y_a) {
                a_to_b = (x_b - x_a << 16) / (y_b - y_a);
                grad_a_off = (l1 - k1 << 16) / (y_b - y_a);
            }

            int b_to_c = 0;
            int grad_b_off = 0;
            if (y_c != y_b) {
                b_to_c = (x_c - x_b << 16) / (y_c - y_b);
                grad_b_off = (i2 - l1 << 16) / (y_c - y_b);
            }

            int c_to_a = 0;
            int grad_c_off = 0;
            if (y_c != y_a) {
                c_to_a = (x_a - x_c << 16) / (y_a - y_c);
                grad_c_off = (k1 - i2 << 16) / (y_a - y_c);
            }

            float b_aX = (float)(x_b - x_a);
            float b_aY = (float)(y_b - y_a);
            float c_aX = (float)(x_c - x_a);
            float c_aY = (float)(y_c - y_a);
            float b_aZ = z_b - z_a;
            float c_aZ = z_c - z_a;
            float div = b_aX * c_aY - c_aX * b_aY;
            float depth_slope = (b_aZ * c_aY - c_aZ * b_aY) / div;
            float depth_increment = (c_aZ * b_aX - b_aZ * c_aX) / div;
            int l9;
            if (y_a <= y_b && y_a <= y_c) {
                if (y_a < bottomY) {
                    if (y_b > bottomY) {
                        y_b = bottomY;
                    }

                    if (y_c > bottomY) {
                        y_c = bottomY;
                    }

                    z_a = z_a - depth_slope * (float)x_a + depth_slope;
                    if (y_b < y_c) {
                        x_c = x_a <<= 16;
                        i2 = k1 <<= 16;
                        if (y_a < 0) {
                            x_c -= c_to_a * y_a;
                            x_a -= a_to_b * y_a;
                            z_a -= depth_increment * (float)y_a;
                            i2 -= grad_c_off * y_a;
                            k1 -= grad_a_off * y_a;
                            y_a = 0;
                        }

                        x_b <<= 16;
                        l1 <<= 16;
                        if (y_b < 0) {
                            x_b -= b_to_c * y_b;
                            l1 -= grad_b_off * y_b;
                            y_b = 0;
                        }

                        l9 = y_a - originViewY;
                        Oa += Va * l9;
                        Ob += Vb * l9;
                        Oc += Vc * l9;
                        if ((y_a == y_b || c_to_a >= a_to_b) && (y_a != y_b || c_to_a <= b_to_c)) {
                            y_c -= y_b;
                            y_b -= y_a;
                            y_a = scanOffsets[y_a];

                            while(true) {
                                --y_b;
                                if (y_b < 0) {
                                    while(true) {
                                        --y_c;
                                        if (y_c < 0) {
                                            return;
                                        }

                                        drawTexturedScanline(pixels, texture, y_a, x_b >> 16, x_c >> 16, l1 >> 8, i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
                                        x_c += c_to_a;
                                        x_b += b_to_c;
                                        z_a += depth_increment;
                                        i2 += grad_c_off;
                                        l1 += grad_b_off;
                                        y_a += width;
                                        Oa += Va;
                                        Ob += Vb;
                                        Oc += Vc;
                                    }
                                }

                                drawTexturedScanline(pixels, texture, y_a, x_a >> 16, x_c >> 16, k1 >> 8, i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
                                x_c += c_to_a;
                                x_a += a_to_b;
                                z_a += depth_increment;
                                i2 += grad_c_off;
                                k1 += grad_a_off;
                                y_a += width;
                                Oa += Va;
                                Ob += Vb;
                                Oc += Vc;
                            }
                        } else {
                            y_c -= y_b;
                            y_b -= y_a;
                            y_a = scanOffsets[y_a];

                            while(true) {
                                --y_b;
                                if (y_b < 0) {
                                    while(true) {
                                        --y_c;
                                        if (y_c < 0) {
                                            return;
                                        }

                                        drawTexturedScanline(pixels, texture, y_a, x_c >> 16, x_b >> 16, i2 >> 8, l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
                                        x_c += c_to_a;
                                        x_b += b_to_c;
                                        z_a += depth_increment;
                                        i2 += grad_c_off;
                                        l1 += grad_b_off;
                                        y_a += width;
                                        Oa += Va;
                                        Ob += Vb;
                                        Oc += Vc;
                                    }
                                }

                                drawTexturedScanline(pixels, texture, y_a, x_c >> 16, x_a >> 16, i2 >> 8, k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
                                x_c += c_to_a;
                                x_a += a_to_b;
                                z_a += depth_increment;
                                i2 += grad_c_off;
                                k1 += grad_a_off;
                                y_a += width;
                                Oa += Va;
                                Ob += Vb;
                                Oc += Vc;
                            }
                        }
                    } else {
                        x_b = x_a <<= 16;
                        l1 = k1 <<= 16;
                        if (y_a < 0) {
                            x_b -= c_to_a * y_a;
                            x_a -= a_to_b * y_a;
                            z_a -= depth_increment * (float)y_a;
                            l1 -= grad_c_off * y_a;
                            k1 -= grad_a_off * y_a;
                            y_a = 0;
                        }

                        x_c <<= 16;
                        i2 <<= 16;
                        if (y_c < 0) {
                            x_c -= b_to_c * y_c;
                            i2 -= grad_b_off * y_c;
                            y_c = 0;
                        }

                        l9 = y_a - originViewY;
                        Oa += Va * l9;
                        Ob += Vb * l9;
                        Oc += Vc * l9;
                        if (y_a != y_c && c_to_a < a_to_b || y_a == y_c && b_to_c > a_to_b) {
                            y_b -= y_c;
                            y_c -= y_a;
                            y_a = scanOffsets[y_a];

                            while(true) {
                                --y_c;
                                if (y_c < 0) {
                                    while(true) {
                                        --y_b;
                                        if (y_b < 0) {
                                            return;
                                        }

                                        drawTexturedScanline(pixels, texture, y_a, x_c >> 16, x_a >> 16, i2 >> 8, k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
                                        x_c += b_to_c;
                                        x_a += a_to_b;
                                        i2 += grad_b_off;
                                        k1 += grad_a_off;
                                        z_a += depth_increment;
                                        y_a += width;
                                        Oa += Va;
                                        Ob += Vb;
                                        Oc += Vc;
                                    }
                                }

                                drawTexturedScanline(pixels, texture, y_a, x_b >> 16, x_a >> 16, l1 >> 8, k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
                                x_b += c_to_a;
                                x_a += a_to_b;
                                l1 += grad_c_off;
                                k1 += grad_a_off;
                                z_a += depth_increment;
                                y_a += width;
                                Oa += Va;
                                Ob += Vb;
                                Oc += Vc;
                            }
                        } else {
                            y_b -= y_c;
                            y_c -= y_a;
                            y_a = scanOffsets[y_a];

                            while(true) {
                                --y_c;
                                if (y_c < 0) {
                                    while(true) {
                                        --y_b;
                                        if (y_b < 0) {
                                            return;
                                        }

                                        drawTexturedScanline(pixels, texture, y_a, x_a >> 16, x_c >> 16, k1 >> 8, i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
                                        x_c += b_to_c;
                                        x_a += a_to_b;
                                        i2 += grad_b_off;
                                        k1 += grad_a_off;
                                        z_a += depth_increment;
                                        y_a += width;
                                        Oa += Va;
                                        Ob += Vb;
                                        Oc += Vc;
                                    }
                                }

                                drawTexturedScanline(pixels, texture, y_a, x_a >> 16, x_b >> 16, k1 >> 8, l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
                                x_b += c_to_a;
                                x_a += a_to_b;
                                l1 += grad_c_off;
                                k1 += grad_a_off;
                                z_a += depth_increment;
                                y_a += width;
                                Oa += Va;
                                Ob += Vb;
                                Oc += Vc;
                            }
                        }
                    }
                }
            } else if (y_b <= y_c) {
                if (y_b < bottomY) {
                    if (y_c > bottomY) {
                        y_c = bottomY;
                    }

                    if (y_a > bottomY) {
                        y_a = bottomY;
                    }

                    z_b = z_b - depth_slope * (float)x_b + depth_slope;
                    if (y_c < y_a) {
                        x_a = x_b <<= 16;
                        k1 = l1 <<= 16;
                        if (y_b < 0) {
                            x_a -= a_to_b * y_b;
                            x_b -= b_to_c * y_b;
                            z_b -= depth_increment * (float)y_b;
                            k1 -= grad_a_off * y_b;
                            l1 -= grad_b_off * y_b;
                            y_b = 0;
                        }

                        x_c <<= 16;
                        i2 <<= 16;
                        if (y_c < 0) {
                            x_c -= c_to_a * y_c;
                            i2 -= grad_c_off * y_c;
                            y_c = 0;
                        }

                        l9 = y_b - originViewY;
                        Oa += Va * l9;
                        Ob += Vb * l9;
                        Oc += Vc * l9;
                        if ((y_b == y_c || a_to_b >= b_to_c) && (y_b != y_c || a_to_b <= c_to_a)) {
                            y_a -= y_c;
                            y_c -= y_b;
                            y_b = scanOffsets[y_b];

                            while(true) {
                                --y_c;
                                if (y_c < 0) {
                                    while(true) {
                                        --y_a;
                                        if (y_a < 0) {
                                            return;
                                        }

                                        drawTexturedScanline(pixels, texture, y_b, x_c >> 16, x_a >> 16, i2 >> 8, k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
                                        x_a += a_to_b;
                                        x_c += c_to_a;
                                        k1 += grad_a_off;
                                        i2 += grad_c_off;
                                        z_b += depth_increment;
                                        y_b += width;
                                        Oa += Va;
                                        Ob += Vb;
                                        Oc += Vc;
                                    }
                                }

                                drawTexturedScanline(pixels, texture, y_b, x_b >> 16, x_a >> 16, l1 >> 8, k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
                                x_a += a_to_b;
                                x_b += b_to_c;
                                k1 += grad_a_off;
                                l1 += grad_b_off;
                                z_b += depth_increment;
                                y_b += width;
                                Oa += Va;
                                Ob += Vb;
                                Oc += Vc;
                            }
                        } else {
                            y_a -= y_c;
                            y_c -= y_b;
                            y_b = scanOffsets[y_b];

                            while(true) {
                                --y_c;
                                if (y_c < 0) {
                                    while(true) {
                                        --y_a;
                                        if (y_a < 0) {
                                            return;
                                        }

                                        drawTexturedScanline(pixels, texture, y_b, x_a >> 16, x_c >> 16, k1 >> 8, i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
                                        x_a += a_to_b;
                                        x_c += c_to_a;
                                        k1 += grad_a_off;
                                        i2 += grad_c_off;
                                        z_b += depth_increment;
                                        y_b += width;
                                        Oa += Va;
                                        Ob += Vb;
                                        Oc += Vc;
                                    }
                                }

                                drawTexturedScanline(pixels, texture, y_b, x_a >> 16, x_b >> 16, k1 >> 8, l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
                                x_a += a_to_b;
                                x_b += b_to_c;
                                k1 += grad_a_off;
                                l1 += grad_b_off;
                                z_b += depth_increment;
                                y_b += width;
                                Oa += Va;
                                Ob += Vb;
                                Oc += Vc;
                            }
                        }
                    } else {
                        x_c = x_b <<= 16;
                        i2 = l1 <<= 16;
                        if (y_b < 0) {
                            x_c -= a_to_b * y_b;
                            x_b -= b_to_c * y_b;
                            z_b -= depth_increment * (float)y_b;
                            i2 -= grad_a_off * y_b;
                            l1 -= grad_b_off * y_b;
                            y_b = 0;
                        }

                        x_a <<= 16;
                        k1 <<= 16;
                        if (y_a < 0) {
                            x_a -= c_to_a * y_a;
                            k1 -= grad_c_off * y_a;
                            y_a = 0;
                        }

                        l9 = y_b - originViewY;
                        Oa += Va * l9;
                        Ob += Vb * l9;
                        Oc += Vc * l9;
                        if (a_to_b < b_to_c) {
                            y_c -= y_a;
                            y_a -= y_b;
                            y_b = scanOffsets[y_b];

                            while(true) {
                                --y_a;
                                if (y_a < 0) {
                                    while(true) {
                                        --y_c;
                                        if (y_c < 0) {
                                            return;
                                        }

                                        drawTexturedScanline(pixels, texture, y_b, x_a >> 16, x_b >> 16, k1 >> 8, l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
                                        x_a += c_to_a;
                                        x_b += b_to_c;
                                        k1 += grad_c_off;
                                        l1 += grad_b_off;
                                        z_b += depth_increment;
                                        y_b += width;
                                        Oa += Va;
                                        Ob += Vb;
                                        Oc += Vc;
                                    }
                                }

                                drawTexturedScanline(pixels, texture, y_b, x_c >> 16, x_b >> 16, i2 >> 8, l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
                                x_c += a_to_b;
                                x_b += b_to_c;
                                i2 += grad_a_off;
                                l1 += grad_b_off;
                                z_b += depth_increment;
                                y_b += width;
                                Oa += Va;
                                Ob += Vb;
                                Oc += Vc;
                            }
                        } else {
                            y_c -= y_a;
                            y_a -= y_b;
                            y_b = scanOffsets[y_b];

                            while(true) {
                                --y_a;
                                if (y_a < 0) {
                                    while(true) {
                                        --y_c;
                                        if (y_c < 0) {
                                            return;
                                        }

                                        drawTexturedScanline(pixels, texture, y_b, x_b >> 16, x_a >> 16, l1 >> 8, k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
                                        x_a += c_to_a;
                                        x_b += b_to_c;
                                        k1 += grad_c_off;
                                        l1 += grad_b_off;
                                        z_b += depth_increment;
                                        y_b += width;
                                        Oa += Va;
                                        Ob += Vb;
                                        Oc += Vc;
                                    }
                                }

                                drawTexturedScanline(pixels, texture, y_b, x_b >> 16, x_c >> 16, l1 >> 8, i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
                                x_c += a_to_b;
                                x_b += b_to_c;
                                i2 += grad_a_off;
                                l1 += grad_b_off;
                                z_b += depth_increment;
                                y_b += width;
                                Oa += Va;
                                Ob += Vb;
                                Oc += Vc;
                            }
                        }
                    }
                }
            } else if (y_c < bottomY) {
                if (y_a > bottomY) {
                    y_a = bottomY;
                }

                if (y_b > bottomY) {
                    y_b = bottomY;
                }

                z_c = z_c - depth_slope * (float)x_c + depth_slope;
                if (y_a < y_b) {
                    x_b = x_c <<= 16;
                    l1 = i2 <<= 16;
                    if (y_c < 0) {
                        x_b -= b_to_c * y_c;
                        x_c -= c_to_a * y_c;
                        z_c -= depth_increment * (float)y_c;
                        l1 -= grad_b_off * y_c;
                        i2 -= grad_c_off * y_c;
                        y_c = 0;
                    }

                    x_a <<= 16;
                    k1 <<= 16;
                    if (y_a < 0) {
                        x_a -= a_to_b * y_a;
                        k1 -= grad_a_off * y_a;
                        y_a = 0;
                    }

                    l9 = y_c - originViewY;
                    Oa += Va * l9;
                    Ob += Vb * l9;
                    Oc += Vc * l9;
                    if (b_to_c < c_to_a) {
                        y_b -= y_a;
                        y_a -= y_c;
                        y_c = scanOffsets[y_c];

                        while(true) {
                            --y_a;
                            if (y_a < 0) {
                                while(true) {
                                    --y_b;
                                    if (y_b < 0) {
                                        return;
                                    }

                                    drawTexturedScanline(pixels, texture, y_c, x_b >> 16, x_a >> 16, l1 >> 8, k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
                                    x_b += b_to_c;
                                    x_a += a_to_b;
                                    l1 += grad_b_off;
                                    k1 += grad_a_off;
                                    z_c += depth_increment;
                                    y_c += width;
                                    Oa += Va;
                                    Ob += Vb;
                                    Oc += Vc;
                                }
                            }

                            drawTexturedScanline(pixels, texture, y_c, x_b >> 16, x_c >> 16, l1 >> 8, i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
                            x_b += b_to_c;
                            x_c += c_to_a;
                            l1 += grad_b_off;
                            i2 += grad_c_off;
                            z_c += depth_increment;
                            y_c += width;
                            Oa += Va;
                            Ob += Vb;
                            Oc += Vc;
                        }
                    } else {
                        y_b -= y_a;
                        y_a -= y_c;
                        y_c = scanOffsets[y_c];

                        while(true) {
                            --y_a;
                            if (y_a < 0) {
                                while(true) {
                                    --y_b;
                                    if (y_b < 0) {
                                        return;
                                    }

                                    drawTexturedScanline(pixels, texture, y_c, x_a >> 16, x_b >> 16, k1 >> 8, l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
                                    x_b += b_to_c;
                                    x_a += a_to_b;
                                    l1 += grad_b_off;
                                    k1 += grad_a_off;
                                    z_c += depth_increment;
                                    y_c += width;
                                    Oa += Va;
                                    Ob += Vb;
                                    Oc += Vc;
                                }
                            }

                            drawTexturedScanline(pixels, texture, y_c, x_c >> 16, x_b >> 16, i2 >> 8, l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
                            x_b += b_to_c;
                            x_c += c_to_a;
                            l1 += grad_b_off;
                            i2 += grad_c_off;
                            z_c += depth_increment;
                            y_c += width;
                            Oa += Va;
                            Ob += Vb;
                            Oc += Vc;
                        }
                    }
                } else {
                    x_a = x_c <<= 16;
                    k1 = i2 <<= 16;
                    if (y_c < 0) {
                        x_a -= b_to_c * y_c;
                        x_c -= c_to_a * y_c;
                        z_c -= depth_increment * (float)y_c;
                        k1 -= grad_b_off * y_c;
                        i2 -= grad_c_off * y_c;
                        y_c = 0;
                    }

                    x_b <<= 16;
                    l1 <<= 16;
                    if (y_b < 0) {
                        x_b -= a_to_b * y_b;
                        l1 -= grad_a_off * y_b;
                        y_b = 0;
                    }

                    l9 = y_c - originViewY;
                    Oa += Va * l9;
                    Ob += Vb * l9;
                    Oc += Vc * l9;
                    if (b_to_c < c_to_a) {
                        y_a -= y_b;
                        y_b -= y_c;
                        y_c = scanOffsets[y_c];

                        while(true) {
                            --y_b;
                            if (y_b < 0) {
                                while(true) {
                                    --y_a;
                                    if (y_a < 0) {
                                        return;
                                    }

                                    drawTexturedScanline(pixels, texture, y_c, x_b >> 16, x_c >> 16, l1 >> 8, i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
                                    x_b += a_to_b;
                                    x_c += c_to_a;
                                    l1 += grad_a_off;
                                    i2 += grad_c_off;
                                    z_c += depth_increment;
                                    y_c += width;
                                    Oa += Va;
                                    Ob += Vb;
                                    Oc += Vc;
                                }
                            }

                            drawTexturedScanline(pixels, texture, y_c, x_a >> 16, x_c >> 16, k1 >> 8, i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
                            x_a += b_to_c;
                            x_c += c_to_a;
                            k1 += grad_b_off;
                            i2 += grad_c_off;
                            z_c += depth_increment;
                            y_c += width;
                            Oa += Va;
                            Ob += Vb;
                            Oc += Vc;
                        }
                    } else {
                        y_a -= y_b;
                        y_b -= y_c;
                        y_c = scanOffsets[y_c];

                        while(true) {
                            --y_b;
                            if (y_b < 0) {
                                while(true) {
                                    --y_a;
                                    if (y_a < 0) {
                                        return;
                                    }

                                    drawTexturedScanline(pixels, texture, y_c, x_c >> 16, x_b >> 16, i2 >> 8, l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
                                    x_b += a_to_b;
                                    x_c += c_to_a;
                                    l1 += grad_a_off;
                                    i2 += grad_c_off;
                                    z_c += depth_increment;
                                    y_c += width;
                                    Oa += Va;
                                    Ob += Vb;
                                    Oc += Vc;
                                }
                            }

                            drawTexturedScanline(pixels, texture, y_c, x_c >> 16, x_a >> 16, i2 >> 8, k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
                            x_a += b_to_c;
                            x_c += c_to_a;
                            k1 += grad_b_off;
                            i2 += grad_c_off;
                            z_c += depth_increment;
                            y_c += width;
                            Oa += Va;
                            Ob += Vb;
                            Oc += Vc;
                        }
                    }
                }
            }
        }
    }

    public static void drawTexturedScanline(int[] dest, int[] texture, int dest_off, int start_x, int end_x, int shadeValue, int gradient, int l1, int i2, int j2, int k2, int l2, int i3, float depth, float depth_slope) {
        int rgb = 0;
        int loops = 0;
        if (start_x < end_x) {
            int j3;
            int k3;
            if (textureOutOfDrawingBounds) {
                j3 = (gradient - shadeValue) / (end_x - start_x);
                if (end_x > lastX) {
                    end_x = lastX;
                }

                if (start_x < 0) {
                    shadeValue -= start_x * j3;
                    start_x = 0;
                }

                if (start_x >= end_x) {
                    return;
                }

                k3 = end_x - start_x >> 3;
                j3 <<= 12;
                shadeValue <<= 9;
            } else {
                if (end_x - start_x > 7) {
                    k3 = end_x - start_x >> 3;
                    j3 = (gradient - shadeValue) * anIntArray1468[k3] >> 6;
                } else {
                    k3 = 0;
                    j3 = 0;
                }

                shadeValue <<= 9;
            }

            dest_off += start_x;
            depth += depth_slope * (float)start_x;
            int j4;
            int l4;
            int l6;
            int l5;
            int j7;
            int l7;
            int j8;
            int i9;
            int j9;
            if (lowMem) {
                j4 = 0;
                l4 = 0;
                l6 = start_x - originViewX;
                l1 += (k2 >> 3) * l6;
                i2 += (l2 >> 3) * l6;
                j2 += (i3 >> 3) * l6;
                l5 = j2 >> 12;
                if (l5 != 0) {
                    rgb = l1 / l5;
                    loops = i2 / l5;
                    if (rgb < 0) {
                        rgb = 0;
                    } else if (rgb > 4032) {
                        rgb = 4032;
                    }
                }

                l1 += k2;
                i2 += l2;
                j2 += i3;
                l5 = j2 >> 12;
                if (l5 != 0) {
                    j4 = l1 / l5;
                    l4 = i2 / l5;
                    if (j4 < 7) {
                        j4 = 7;
                    } else if (j4 > 4032) {
                        j4 = 4032;
                    }
                }

                j7 = j4 - rgb >> 3;
                l7 = l4 - loops >> 3;
                rgb += (shadeValue & 6291456) >> 3;
                j8 = shadeValue >> 23;
                if (aBoolean1463) {
                    while(k3-- > 0) {
                        for(i9 = 0; i9 < 8; ++i9) {
                            dest[dest_off] = texture[(loops & 4032) + (rgb >> 6)] >>> j8;
                            depthBuffer[dest_off] = depth;
                            ++dest_off;
                            depth += depth_slope;
                            rgb += j7;
                            loops += l7;
                        }

                        rgb = j4;
                        loops = l4;
                        l1 += k2;
                        i2 += l2;
                        j2 += i3;
                        i9 = j2 >> 12;
                        if (i9 != 0) {
                            j4 = l1 / i9;
                            l4 = i2 / i9;
                            if (j4 < 7) {
                                j4 = 7;
                            } else if (j4 > 4032) {
                                j4 = 4032;
                            }
                        }

                        j7 = j4 - rgb >> 3;
                        l7 = l4 - loops >> 3;
                        shadeValue += j3;
                        rgb += (shadeValue & 6291456) >> 3;
                        j8 = shadeValue >> 23;
                    }

                    for(k3 = end_x - start_x & 7; k3-- > 0; loops += l7) {
                        dest[dest_off] = texture[(loops & 4032) + (rgb >> 6)] >>> j8;
                        depthBuffer[dest_off] = depth;
                        ++dest_off;
                        depth += depth_slope;
                        rgb += j7;
                    }

                } else {
                    while(k3-- > 0) {
                        for(j9 = 0; j9 < 8; ++j9) {
                            if ((i9 = texture[(loops & 4032) + (rgb >> 6)] >>> j8) != 0) {
                                dest[dest_off] = i9;
                                depthBuffer[dest_off] = depth;
                            }

                            ++dest_off;
                            depth += depth_slope;
                            rgb += j7;
                            loops += l7;
                        }

                        rgb = j4;
                        loops = l4;
                        l1 += k2;
                        i2 += l2;
                        j2 += i3;
                        j9 = j2 >> 12;
                        if (j9 != 0) {
                            j4 = l1 / j9;
                            l4 = i2 / j9;
                            if (j4 < 7) {
                                j4 = 7;
                            } else if (j4 > 4032) {
                                j4 = 4032;
                            }
                        }

                        j7 = j4 - rgb >> 3;
                        l7 = l4 - loops >> 3;
                        shadeValue += j3;
                        rgb += (shadeValue & 6291456) >> 3;
                        j8 = shadeValue >> 23;
                    }

                    for(k3 = end_x - start_x & 7; k3-- > 0; loops += l7) {
                        if ((i9 = texture[(loops & 4032) + (rgb >> 6)] >>> j8) != 0) {
                            dest[dest_off] = i9;
                            depthBuffer[dest_off] = depth;
                        }

                        ++dest_off;
                        depth += depth_slope;
                        rgb += j7;
                    }

                }
            } else {
                j4 = 0;
                l4 = 0;
                l6 = start_x - originViewX;
                l1 += (k2 >> 3) * l6;
                i2 += (l2 >> 3) * l6;
                j2 += (i3 >> 3) * l6;
                l5 = j2 >> 14;
                if (l5 != 0) {
                    rgb = l1 / l5;
                    loops = i2 / l5;
                    if (rgb < 0) {
                        rgb = 0;
                    } else if (rgb > 16256) {
                        rgb = 16256;
                    }
                }

                l1 += k2;
                i2 += l2;
                j2 += i3;
                l5 = j2 >> 14;
                if (l5 != 0) {
                    j4 = l1 / l5;
                    l4 = i2 / l5;
                    if (j4 < 7) {
                        j4 = 7;
                    } else if (j4 > 16256) {
                        j4 = 16256;
                    }
                }

                j7 = j4 - rgb >> 3;
                l7 = l4 - loops >> 3;
                rgb += shadeValue & 6291456;
                j8 = shadeValue >> 23;
                if (aBoolean1463) {
                    while(k3-- > 0) {
                        for(i9 = 0; i9 < 8; ++i9) {
                            dest[dest_off] = texture[(loops & 16256) + (rgb >> 7)] >>> j8;
                            depthBuffer[dest_off] = depth;
                            depth += depth_slope;
                            ++dest_off;
                            rgb += j7;
                            loops += l7;
                        }

                        rgb = j4;
                        loops = l4;
                        l1 += k2;
                        i2 += l2;
                        j2 += i3;
                        i9 = j2 >> 14;
                        if (i9 != 0) {
                            j4 = l1 / i9;
                            l4 = i2 / i9;
                            if (j4 < 7) {
                                j4 = 7;
                            } else if (j4 > 16256) {
                                j4 = 16256;
                            }
                        }

                        j7 = j4 - rgb >> 3;
                        l7 = l4 - loops >> 3;
                        shadeValue += j3;
                        rgb += shadeValue & 6291456;
                        j8 = shadeValue >> 23;
                    }

                    for(k3 = end_x - start_x & 7; k3-- > 0; loops += l7) {
                        dest[dest_off] = texture[(loops & 16256) + (rgb >> 7)] >>> j8;
                        depthBuffer[dest_off] = depth;
                        ++dest_off;
                        depth += depth_slope;
                        rgb += j7;
                    }

                } else {
                    while(k3-- > 0) {
                        for(j9 = 0; j9 < 8; ++j9) {
                            if ((i9 = texture[(loops & 16256) + (rgb >> 7)] >>> j8) != 0) {
                                dest[dest_off] = i9;
                                depthBuffer[dest_off] = depth;
                            }

                            ++dest_off;
                            depth += depth_slope;
                            rgb += j7;
                            loops += l7;
                        }

                        rgb = j4;
                        loops = l4;
                        l1 += k2;
                        i2 += l2;
                        j2 += i3;
                        j9 = j2 >> 14;
                        if (j9 != 0) {
                            j4 = l1 / j9;
                            l4 = i2 / j9;
                            if (j4 < 7) {
                                j4 = 7;
                            } else if (j4 > 16256) {
                                j4 = 16256;
                            }
                        }

                        j7 = j4 - rgb >> 3;
                        l7 = l4 - loops >> 3;
                        shadeValue += j3;
                        rgb += shadeValue & 6291456;
                        j8 = shadeValue >> 23;
                    }

                    for(i9 = end_x - start_x & 7; i9-- > 0; loops += l7) {
                        if ((j9 = texture[(loops & 16256) + (rgb >> 7)] >>> j8) != 0) {
                            dest[dest_off] = j9;
                            depthBuffer[dest_off] = depth;
                        }

                        depth += depth_slope;
                        ++dest_off;
                        rgb += j7;
                    }

                }
            }
        }
    }

    public static void drawDepthTriangle(int x_a, int x_b, int x_c, int y_a, int y_b, int y_c, float z_a, float z_b, float z_c) {
        int a_to_b = 0;
        if (y_b != y_a) {
            a_to_b = (x_b - x_a << 16) / (y_b - y_a);
        }

        int b_to_c = 0;
        if (y_c != y_b) {
            b_to_c = (x_c - x_b << 16) / (y_c - y_b);
        }

        int c_to_a = 0;
        if (y_c != y_a) {
            c_to_a = (x_a - x_c << 16) / (y_a - y_c);
        }

        float b_aX = (float)(x_b - x_a);
        float b_aY = (float)(y_b - y_a);
        float c_aX = (float)(x_c - x_a);
        float c_aY = (float)(y_c - y_a);
        float b_aZ = z_b - z_a;
        float c_aZ = z_c - z_a;
        float div = b_aX * c_aY - c_aX * b_aY;
        float depth_slope = (b_aZ * c_aY - c_aZ * b_aY) / div;
        float depth_increment = (c_aZ * b_aX - b_aZ * c_aX) / div;
        if (y_a <= y_b && y_a <= y_c) {
            if (y_a < bottomY) {
                if (y_b > bottomY) {
                    y_b = bottomY;
                }

                if (y_c > bottomY) {
                    y_c = bottomY;
                }

                z_a = z_a - depth_slope * (float)x_a + depth_slope;
                if (y_b < y_c) {
                    x_c = x_a <<= 16;
                    if (y_a < 0) {
                        x_c -= c_to_a * y_a;
                        x_a -= a_to_b * y_a;
                        z_a -= depth_increment * (float)y_a;
                        y_a = 0;
                    }

                    x_b <<= 16;
                    if (y_b < 0) {
                        x_b -= b_to_c * y_b;
                        y_b = 0;
                    }

                    if ((y_a == y_b || c_to_a >= a_to_b) && (y_a != y_b || c_to_a <= b_to_c)) {
                        y_c -= y_b;
                        y_b -= y_a;
                        y_a = scanOffsets[y_a];

                        while(true) {
                            --y_b;
                            if (y_b < 0) {
                                while(true) {
                                    --y_c;
                                    if (y_c < 0) {
                                        return;
                                    }

                                    drawDepthTriangleScanline(y_a, x_b >> 16, x_c >> 16, z_a, depth_slope);
                                    x_c += c_to_a;
                                    x_b += b_to_c;
                                    z_a += depth_increment;
                                    y_a += width;
                                }
                            }

                            drawDepthTriangleScanline(y_a, x_a >> 16, x_c >> 16, z_a, depth_slope);
                            x_c += c_to_a;
                            x_a += a_to_b;
                            z_a += depth_increment;
                            y_a += width;
                        }
                    } else {
                        y_c -= y_b;
                        y_b -= y_a;
                        y_a = scanOffsets[y_a];

                        while(true) {
                            --y_b;
                            if (y_b < 0) {
                                while(true) {
                                    --y_c;
                                    if (y_c < 0) {
                                        return;
                                    }

                                    drawDepthTriangleScanline(y_a, x_c >> 16, x_b >> 16, z_a, depth_slope);
                                    x_c += c_to_a;
                                    x_b += b_to_c;
                                    z_a += depth_increment;
                                    y_a += width;
                                }
                            }

                            drawDepthTriangleScanline(y_a, x_c >> 16, x_a >> 16, z_a, depth_slope);
                            x_c += c_to_a;
                            x_a += a_to_b;
                            z_a += depth_increment;
                            y_a += width;
                        }
                    }
                } else {
                    x_b = x_a <<= 16;
                    if (y_a < 0) {
                        x_b -= c_to_a * y_a;
                        x_a -= a_to_b * y_a;
                        z_a -= depth_increment * (float)y_a;
                        y_a = 0;
                    }

                    x_c <<= 16;
                    if (y_c < 0) {
                        x_c -= b_to_c * y_c;
                        y_c = 0;
                    }

                    if ((y_a == y_c || c_to_a >= a_to_b) && (y_a != y_c || b_to_c <= a_to_b)) {
                        y_b -= y_c;
                        y_c -= y_a;
                        y_a = scanOffsets[y_a];

                        while(true) {
                            --y_c;
                            if (y_c < 0) {
                                while(true) {
                                    --y_b;
                                    if (y_b < 0) {
                                        return;
                                    }

                                    drawDepthTriangleScanline(y_a, x_a >> 16, x_c >> 16, z_a, depth_slope);
                                    x_c += b_to_c;
                                    x_a += a_to_b;
                                    z_a += depth_increment;
                                    y_a += width;
                                }
                            }

                            drawDepthTriangleScanline(y_a, x_a >> 16, x_b >> 16, z_a, depth_slope);
                            x_b += c_to_a;
                            x_a += a_to_b;
                            z_a += depth_increment;
                            y_a += width;
                        }
                    } else {
                        y_b -= y_c;
                        y_c -= y_a;
                        y_a = scanOffsets[y_a];

                        while(true) {
                            --y_c;
                            if (y_c < 0) {
                                while(true) {
                                    --y_b;
                                    if (y_b < 0) {
                                        return;
                                    }

                                    drawDepthTriangleScanline(y_a, x_c >> 16, x_a >> 16, z_a, depth_slope);
                                    x_c += b_to_c;
                                    x_a += a_to_b;
                                    z_a += depth_increment;
                                    y_a += width;
                                }
                            }

                            drawDepthTriangleScanline(y_a, x_b >> 16, x_a >> 16, z_a, depth_slope);
                            x_b += c_to_a;
                            x_a += a_to_b;
                            z_a += depth_increment;
                            y_a += width;
                        }
                    }
                }
            }
        } else if (y_b <= y_c) {
            if (y_b < bottomY) {
                if (y_c > bottomY) {
                    y_c = bottomY;
                }

                if (y_a > bottomY) {
                    y_a = bottomY;
                }

                z_b = z_b - depth_slope * (float)x_b + depth_slope;
                if (y_c < y_a) {
                    x_a = x_b <<= 16;
                    if (y_b < 0) {
                        x_a -= a_to_b * y_b;
                        x_b -= b_to_c * y_b;
                        z_b -= depth_increment * (float)y_b;
                        y_b = 0;
                    }

                    x_c <<= 16;
                    if (y_c < 0) {
                        x_c -= c_to_a * y_c;
                        y_c = 0;
                    }

                    if (y_b != y_c && a_to_b < b_to_c || y_b == y_c && a_to_b > c_to_a) {
                        y_a -= y_c;
                        y_c -= y_b;
                        y_b = scanOffsets[y_b];

                        while(true) {
                            --y_c;
                            if (y_c < 0) {
                                while(true) {
                                    --y_a;
                                    if (y_a < 0) {
                                        return;
                                    }

                                    drawDepthTriangleScanline(y_b, x_a >> 16, x_c >> 16, z_b, depth_slope);
                                    x_a += a_to_b;
                                    x_c += c_to_a;
                                    z_b += depth_increment;
                                    y_b += width;
                                }
                            }

                            drawDepthTriangleScanline(y_b, x_a >> 16, x_b >> 16, z_b, depth_slope);
                            x_a += a_to_b;
                            x_b += b_to_c;
                            z_b += depth_increment;
                            y_b += width;
                        }
                    } else {
                        y_a -= y_c;
                        y_c -= y_b;
                        y_b = scanOffsets[y_b];

                        while(true) {
                            --y_c;
                            if (y_c < 0) {
                                while(true) {
                                    --y_a;
                                    if (y_a < 0) {
                                        return;
                                    }

                                    drawDepthTriangleScanline(y_b, x_c >> 16, x_a >> 16, z_b, depth_slope);
                                    x_a += a_to_b;
                                    x_c += c_to_a;
                                    z_b += depth_increment;
                                    y_b += width;
                                }
                            }

                            drawDepthTriangleScanline(y_b, x_b >> 16, x_a >> 16, z_b, depth_slope);
                            x_a += a_to_b;
                            x_b += b_to_c;
                            z_b += depth_increment;
                            y_b += width;
                        }
                    }
                } else {
                    x_c = x_b <<= 16;
                    if (y_b < 0) {
                        x_c -= a_to_b * y_b;
                        x_b -= b_to_c * y_b;
                        z_b -= depth_increment * (float)y_b;
                        y_b = 0;
                    }

                    x_a <<= 16;
                    if (y_a < 0) {
                        x_a -= c_to_a * y_a;
                        y_a = 0;
                    }

                    if (a_to_b < b_to_c) {
                        y_c -= y_a;
                        y_a -= y_b;
                        y_b = scanOffsets[y_b];

                        while(true) {
                            --y_a;
                            if (y_a < 0) {
                                while(true) {
                                    --y_c;
                                    if (y_c < 0) {
                                        return;
                                    }

                                    drawDepthTriangleScanline(y_b, x_a >> 16, x_b >> 16, z_b, depth_slope);
                                    x_a += c_to_a;
                                    x_b += b_to_c;
                                    z_b += depth_increment;
                                    y_b += width;
                                }
                            }

                            drawDepthTriangleScanline(y_b, x_c >> 16, x_b >> 16, z_b, depth_slope);
                            x_c += a_to_b;
                            x_b += b_to_c;
                            z_b += depth_increment;
                            y_b += width;
                        }
                    } else {
                        y_c -= y_a;
                        y_a -= y_b;
                        y_b = scanOffsets[y_b];

                        while(true) {
                            --y_a;
                            if (y_a < 0) {
                                while(true) {
                                    --y_c;
                                    if (y_c < 0) {
                                        return;
                                    }

                                    drawDepthTriangleScanline(y_b, x_b >> 16, x_a >> 16, z_b, depth_slope);
                                    x_a += c_to_a;
                                    x_b += b_to_c;
                                    z_b += depth_increment;
                                    y_b += width;
                                }
                            }

                            drawDepthTriangleScanline(y_b, x_b >> 16, x_c >> 16, z_b, depth_slope);
                            x_c += a_to_b;
                            x_b += b_to_c;
                            z_b += depth_increment;
                            y_b += width;
                        }
                    }
                }
            }
        } else if (y_c < bottomY) {
            if (y_a > bottomY) {
                y_a = bottomY;
            }

            if (y_b > bottomY) {
                y_b = bottomY;
            }

            z_c = z_c - depth_slope * (float)x_c + depth_slope;
            if (y_a < y_b) {
                x_b = x_c <<= 16;
                if (y_c < 0) {
                    x_b -= b_to_c * y_c;
                    x_c -= c_to_a * y_c;
                    z_c -= depth_increment * (float)y_c;
                    y_c = 0;
                }

                x_a <<= 16;
                if (y_a < 0) {
                    x_a -= a_to_b * y_a;
                    y_a = 0;
                }

                if (b_to_c < c_to_a) {
                    y_b -= y_a;
                    y_a -= y_c;
                    y_c = scanOffsets[y_c];

                    while(true) {
                        --y_a;
                        if (y_a < 0) {
                            while(true) {
                                --y_b;
                                if (y_b < 0) {
                                    return;
                                }

                                drawDepthTriangleScanline(y_c, x_b >> 16, x_a >> 16, z_c, depth_slope);
                                x_b += b_to_c;
                                x_a += a_to_b;
                                z_c += depth_increment;
                                y_c += width;
                            }
                        }

                        drawDepthTriangleScanline(y_c, x_b >> 16, x_c >> 16, z_c, depth_slope);
                        x_b += b_to_c;
                        x_c += c_to_a;
                        z_c += depth_increment;
                        y_c += width;
                    }
                } else {
                    y_b -= y_a;
                    y_a -= y_c;
                    y_c = scanOffsets[y_c];

                    while(true) {
                        --y_a;
                        if (y_a < 0) {
                            while(true) {
                                --y_b;
                                if (y_b < 0) {
                                    return;
                                }

                                drawDepthTriangleScanline(y_c, x_a >> 16, x_b >> 16, z_c, depth_slope);
                                x_b += b_to_c;
                                x_a += a_to_b;
                                z_c += depth_increment;
                                y_c += width;
                            }
                        }

                        drawDepthTriangleScanline(y_c, x_c >> 16, x_b >> 16, z_c, depth_slope);
                        x_b += b_to_c;
                        x_c += c_to_a;
                        z_c += depth_increment;
                        y_c += width;
                    }
                }
            } else {
                x_a = x_c <<= 16;
                if (y_c < 0) {
                    x_a -= b_to_c * y_c;
                    x_c -= c_to_a * y_c;
                    z_c -= depth_increment * (float)y_c;
                    y_c = 0;
                }

                x_b <<= 16;
                if (y_b < 0) {
                    x_b -= a_to_b * y_b;
                    y_b = 0;
                }

                if (b_to_c < c_to_a) {
                    y_a -= y_b;
                    y_b -= y_c;
                    y_c = scanOffsets[y_c];

                    while(true) {
                        --y_b;
                        if (y_b < 0) {
                            while(true) {
                                --y_a;
                                if (y_a < 0) {
                                    return;
                                }

                                drawDepthTriangleScanline(y_c, x_b >> 16, x_c >> 16, z_c, depth_slope);
                                x_b += a_to_b;
                                x_c += c_to_a;
                                z_c += depth_increment;
                                y_c += width;
                            }
                        }

                        drawDepthTriangleScanline(y_c, x_a >> 16, x_c >> 16, z_c, depth_slope);
                        x_a += b_to_c;
                        x_c += c_to_a;
                        z_c += depth_increment;
                        y_c += width;
                    }
                } else {
                    y_a -= y_b;
                    y_b -= y_c;
                    y_c = scanOffsets[y_c];

                    while(true) {
                        --y_b;
                        if (y_b < 0) {
                            while(true) {
                                --y_a;
                                if (y_a < 0) {
                                    return;
                                }

                                drawDepthTriangleScanline(y_c, x_c >> 16, x_b >> 16, z_c, depth_slope);
                                x_b += a_to_b;
                                x_c += c_to_a;
                                z_c += depth_increment;
                                y_c += width;
                            }
                        }

                        drawDepthTriangleScanline(y_c, x_c >> 16, x_a >> 16, z_c, depth_slope);
                        x_a += b_to_c;
                        x_c += c_to_a;
                        z_c += depth_increment;
                        y_c += width;
                    }
                }
            }
        }

    }

    private static void drawDepthTriangleScanline(int dest_off, int start_x, int end_x, float depth, float depth_slope) {
        int dbl = depthBuffer.length;
        if (textureOutOfDrawingBounds) {
            if (end_x > width) {
                end_x = width;
            }

            if (start_x < 0) {
                start_x = 0;
            }
        }

        if (start_x < end_x) {
            dest_off += start_x - 1;
            int loops = end_x - start_x >> 2;
            depth += depth_slope * (float)start_x;
            if (alpha == 0) {
                while(true) {
                    --loops;
                    if (loops < 0) {
                        loops = end_x - start_x & 3;

                        while(true) {
                            --loops;
                            if (loops < 0) {
                                return;
                            }

                            ++dest_off;
                            if (dest_off >= 0 && dest_off < dbl) {
                                depthBuffer[dest_off] = depth;
                            }

                            depth += depth_slope;
                        }
                    }

                    ++dest_off;
                    if (dest_off >= 0 && dest_off < dbl) {
                        depthBuffer[dest_off] = depth;
                    }

                    depth += depth_slope;
                    ++dest_off;
                    if (dest_off >= 0 && dest_off < dbl) {
                        depthBuffer[dest_off] = depth;
                    }

                    depth += depth_slope;
                    ++dest_off;
                    if (dest_off >= 0 && dest_off < dbl) {
                        depthBuffer[dest_off] = depth;
                    }

                    depth += depth_slope;
                    ++dest_off;
                    if (dest_off >= 0 && dest_off < dbl) {
                        depthBuffer[dest_off] = depth;
                    }

                    depth += depth_slope;
                }
            } else {
                while(true) {
                    --loops;
                    if (loops < 0) {
                        loops = end_x - start_x & 3;

                        while(true) {
                            --loops;
                            if (loops < 0) {
                                return;
                            }

                            ++dest_off;
                            if (dest_off >= 0 && dest_off < dbl) {
                                depthBuffer[dest_off] = depth;
                            }

                            depth += depth_slope;
                        }
                    }

                    ++dest_off;
                    if (dest_off >= 0 && dest_off < dbl) {
                        depthBuffer[dest_off] = depth;
                    }

                    depth += depth_slope;
                    ++dest_off;
                    if (dest_off >= 0 && dest_off < dbl) {
                        depthBuffer[dest_off] = depth;
                    }

                    depth += depth_slope;
                    ++dest_off;
                    if (dest_off >= 0 && dest_off < dbl) {
                        depthBuffer[dest_off] = depth;
                    }

                    depth += depth_slope;
                    ++dest_off;
                    if (dest_off >= 0 && dest_off < dbl) {
                        depthBuffer[dest_off] = depth;
                    }

                    depth += depth_slope;
                }
            }
        }
    }

    static {
        int k;
        for(k = 1; k < 512; ++k) {
            anIntArray1468[k] = '耀' / k;
        }

        for(k = 1; k < 2048; ++k) {
            anIntArray1469[k] = 65536 / k;
        }

        for(k = 0; k < 2048; ++k) {
            anIntArray1470[k] = (int)(65536.0D * Math.sin((double)k * 0.0030679615D));
            COSINE[k] = (int)(65536.0D * Math.cos((double)k * 0.0030679615D));
        }

    }
}
