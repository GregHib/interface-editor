package io.nshusa.rsam.binary;

import io.nshusa.rsam.util.ByteBufferUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;

/**
 * @author Tom, modified by Freyr
 */
public final class Model {

    public short[] facesX;
    public short[] facesY;
    public short[] facesZ;
    public short[] textureMapX;
    public short[] textureMapY;
    public short[] textureMapZ;
    public int[] verticesX;
    public int[] verticesY;
    public int[] verticesZ;
    public short[] colors;
    public short[] textures;
    public byte[] vertexSkinTypes;
    public byte[] pointers;
    public byte[] faceFillAttributes;
    public byte[] textureFillAttributes;
    public byte[] alpha;
    public byte[] priorities;
    public byte[] triangleSkinTypes;
    public int vertices;
    public int faces;
    public int texturedFaceCount;
    public int maxDepth;
    public byte priority;

    public int[] particlesXDir;
    public int[] particlesYDir;
    public int[] particlesZDir;

    public byte[] particlesXLifespan;
    public byte[] particlesYLifespan;
    public int[] particlesZLifespan;


    public int[] primaryTextureColor;
    public int[] secondaryTextureColor;

    public Model(byte[] data) {
        decode(data);
        upscale();
    }

    private void decode(byte[] data) {
        boolean canFillFaceAttributes = false;
        boolean textured = false;

        ByteBuffer triangleColorBuffer = ByteBuffer.wrap(data);
        ByteBuffer drawTypeBuffer = ByteBuffer.wrap(data);
        ByteBuffer priorityBuffer = ByteBuffer.wrap(data);
        ByteBuffer alphaBuffer = ByteBuffer.wrap(data);
        ByteBuffer triangleSkinBuffer = ByteBuffer.wrap(data);

        triangleColorBuffer.position(data.length - 18);

        vertices = triangleColorBuffer.getShort() & 0xffff;
        faces = triangleColorBuffer.getShort() & 0xffff;
        texturedFaceCount = triangleColorBuffer.get() & 0xff;

        int useTextures = triangleColorBuffer.get() & 0xff;

        int texturePriority = triangleColorBuffer.get() & 0xff;

        if (texturePriority == 0xff) {
            texturePriority = -1;
        }

        int useTransparency = triangleColorBuffer.get() & 0xff;

        int useTriangleSkins = triangleColorBuffer.get() & 0xff;

        int useVertexSkinning = triangleColorBuffer.get() & 0xff;

        int xDataOffset = triangleColorBuffer.getShort() & 0xffff;

        int yDataOffset = triangleColorBuffer.getShort() & 0xffff;

        int zDataOffset = triangleColorBuffer.getShort() & 0xffff;

        int textureDataLength = triangleColorBuffer.getShort() & 0xffff;

        int offset = 0;

        int triangleColorBufferPos = offset;
        offset += vertices;
        int drawTypeBufferPos2 = offset;
        offset += faces;
        int priorityBufferPos2 = offset;
        if (texturePriority == -1) {
            offset += faces;
        }

        int triangleSkinBufferPos2 = offset;
        if (useTriangleSkins == 1) {
            offset += faces;
        }

        int drawTypeBufferPos = offset;
        if (useTextures == 1) {
            offset += faces;
        }

        int triangleSkinBufferPos = offset;
        if (useVertexSkinning == 1) {
            offset += vertices;
        }

        int alphaBufferPos2 = offset;
        if (useTransparency == 1) {
            offset += faces;
        }

        int triangleColorBufferPos3 = offset;
        offset += textureDataLength;

        int triangleColorBufferPos2 = offset;
        offset += faces * 2;

        int texture_map_buffer_pos = offset;
        offset += texturedFaceCount * 6;

        int xDataOffsetPos = offset;
        offset += xDataOffset;

        int priorityBufferPos = offset;
        offset += yDataOffset;

        int alphaBufferPos = offset;
        offset += zDataOffset;

        texturePriority = (byte) texturePriority;

        if (texturePriority == -1) {
            priorities = new byte[faces];
        }

        if (useTriangleSkins == 1) {
            triangleSkinTypes = new byte[faces];
        }

        if (useVertexSkinning == 1) {
            vertexSkinTypes = new byte[vertices];
        }

        verticesX = new int[vertices];
        verticesY = new int[vertices];
        verticesZ = new int[vertices];
        facesX = new short[faces];
        facesY = new short[faces];
        facesZ = new short[faces];
        colors = new short[faces];

        if (texturedFaceCount > 0) {
            textureMapY = new short[texturedFaceCount];
            textureMapZ = new short[texturedFaceCount];
            textureMapX = new short[texturedFaceCount];
        }
        if (useTextures == 1) {
            faceFillAttributes = new byte[faces];
            textures = new short[faces];
            pointers = new byte[faces];
        }

        if (useTransparency == 1) {
            alpha = new byte[faces];
        }

        triangleColorBuffer.position(triangleColorBufferPos);
        drawTypeBuffer.position(xDataOffsetPos);
        priorityBuffer.position(priorityBufferPos);
        alphaBuffer.position(alphaBufferPos);
        triangleSkinBuffer.position(triangleSkinBufferPos);

        int vertexX = 0;
        int vertexY = 0;
        int vertexZ = 0;

        for (int vertex = 0; vertex != vertices; ++vertex) {
            int vertexFlags = triangleColorBuffer.get() & 0xff;
            int offsetX = (vertexFlags & 0x1) != 0 ? ByteBufferUtils.getSmart(drawTypeBuffer) : 0;
            int offsetY = (vertexFlags & 0x2) != 0 ? ByteBufferUtils.getSmart(priorityBuffer) : 0;
            int offsetZ = (vertexFlags & 0x4) != 0 ? ByteBufferUtils.getSmart(alphaBuffer) : 0;
            vertexX += offsetX;
            vertexY += offsetY;
            vertexZ += offsetZ;
            verticesX[vertex] = vertexX;
            verticesY[vertex] = vertexY;
            verticesZ[vertex] = vertexZ;

            if (useVertexSkinning == 1) {
                vertexSkinTypes[vertex] = triangleSkinBuffer.get();
            }
        }

        triangleColorBuffer.position(triangleColorBufferPos2);
        drawTypeBuffer.position(drawTypeBufferPos);
        priorityBuffer.position(priorityBufferPos2);
        alphaBuffer.position(alphaBufferPos2);
        triangleSkinBuffer.position(triangleSkinBufferPos2);

        for (int tri = 0; tri != faces; ++tri) {
            colors[tri] = (short) (triangleColorBuffer.getShort() & 0xffff);
            if (useTextures == 1) {
                int attr_mask = drawTypeBuffer.get() & 0xff;
                if ((attr_mask & 0x1) == 0) {
                    faceFillAttributes[tri] = 0;
                } else {
                    faceFillAttributes[tri] = (byte) 1;
                    canFillFaceAttributes = true;
                }
                if ((attr_mask & 0x2) != 0) {
                    pointers[tri] = (byte) (attr_mask >> 2);
                    textures[tri] = colors[tri];
                    colors[tri] = (short) 127;
                    if (textures[tri] != -1) {
                        textured = true;
                    }
                } else {
                    pointers[tri] = -1;
                    textures[tri] = -1;
                }
            }

            if (texturePriority == -1) {
                priorities[tri] = priorityBuffer.get();
            }

            if (useTransparency == 1) {
                alpha[tri] = alphaBuffer.get();
            }

            if (useTriangleSkins == 1) {
                triangleSkinTypes[tri] = triangleSkinBuffer.get();
            }

        }

        maxDepth = -1;
        triangleColorBuffer.position(triangleColorBufferPos3);
        drawTypeBuffer.position(drawTypeBufferPos2);

        short triangleX = 0;
        short triangleY = 0;
        short triangleZ = 0;

        int previousZView = 0;

        for (int triangle = 0; triangle != faces; ++triangle) {

            int type = drawTypeBuffer.get() & 0xff;

            switch (type) {

                case 1:
                    triangleX = (short) (ByteBufferUtils.getSmart(triangleColorBuffer) + previousZView);
                    previousZView = triangleX;
                    triangleY = (short) (ByteBufferUtils.getSmart(triangleColorBuffer) + previousZView);
                    previousZView = triangleY;
                    triangleZ = (short) (ByteBufferUtils.getSmart(triangleColorBuffer) + previousZView);
                    previousZView = triangleZ;
                    facesX[triangle] = triangleX;
                    facesY[triangle] = triangleY;
                    facesZ[triangle] = triangleZ;

                    if (maxDepth < triangleX) {
                        maxDepth = triangleX;
                    }

                    if (maxDepth < triangleY) {
                        maxDepth = triangleY;
                    }

                    if (maxDepth < triangleZ) {
                        maxDepth = triangleZ;
                    }
                    break;
                case 2:
                    triangleY = triangleZ;
                    triangleZ = (short) (ByteBufferUtils.getSmart(triangleColorBuffer) + previousZView);
                    previousZView = triangleZ;
                    facesX[triangle] = triangleX;
                    facesY[triangle] = triangleY;
                    facesZ[triangle] = triangleZ;
                    if (maxDepth < triangleZ) {
                        maxDepth = triangleZ;
                    }
                    break;
                case 3:
                    triangleX = triangleZ;
                    triangleZ = (short) (ByteBufferUtils.getSmart(triangleColorBuffer) + previousZView);
                    previousZView = triangleZ;
                    facesX[triangle] = triangleX;
                    facesY[triangle] = triangleY;
                    facesZ[triangle] = triangleZ;
                    if (maxDepth < triangleZ) {
                        maxDepth = triangleZ;
                    }
                    break;
                case 4:
                    short prev_x_view = triangleX;
                    triangleX = triangleY;
                    triangleZ = (short) (ByteBufferUtils.getSmart(triangleColorBuffer) + previousZView);
                    triangleY = prev_x_view;
                    previousZView = triangleZ;
                    facesX[triangle] = triangleX;
                    facesY[triangle] = triangleY;
                    facesZ[triangle] = triangleZ;
                    if (maxDepth < triangleZ) {
                        maxDepth = triangleZ;
                    }
                    break;
            }
        }

        triangleColorBuffer.position(texture_map_buffer_pos);

        ++maxDepth;
        for (int tri = 0; tri != texturedFaceCount; ++tri) {
            textureMapX[tri] = (short) (triangleColorBuffer.getShort() & 0xffff);
            textureMapY[tri] = (short) (triangleColorBuffer.getShort() & 0xffff);
            textureMapZ[tri] = (short) (triangleColorBuffer.getShort() & 0xffff);
        }
        if (pointers != null) {

            boolean hasPointers = false;

            for (int triangle = 0; triangle != faces; ++triangle) {
                int pointerMask = pointers[triangle] & 0xff;
                if (pointerMask != 0xff)
                    if (textureMapX[pointerMask] != facesX[triangle]
                            || facesY[triangle] != textureMapY[pointerMask]
                            || facesZ[triangle] != textureMapZ[pointerMask]) {
                        hasPointers = true;
                    } else {
                        pointers[triangle] = -1;
                    }

            }
            if (!hasPointers) {
                pointers = null;
            }
        }
        if (!canFillFaceAttributes) {
            faceFillAttributes = null;
        }
        if (!textured) {
            textures = null;
        }
    }

    private void upscale() {
        for (int index = 0; index != vertices; ++index) {
            verticesX[index] <<= 2;
            verticesY[index] <<= 2;
            verticesZ[index] <<= 2;
        }
    }

    public void scale(int x, int y, int z) {
        for (int vertex = 0; vertex < vertices; vertex++) {
            verticesX[vertex] = verticesX[vertex] * x / 128;
            verticesY[vertex] = verticesY[vertex] * z / 128;
            verticesZ[vertex] = verticesZ[vertex] * y / 128;
        }
    }

    public void translate(int x, int y, int z) {
        for (int vertex = 0; vertex < vertices; vertex++) {
            verticesX[vertex] += x;
            verticesY[vertex] += y;
            verticesZ[vertex] += z;
        }
    }

    public void rotateClockwise() {
        for (int index = 0; index < vertices; index++) {
            int x = verticesX[index];
            verticesX[index] = verticesZ[index];
            verticesZ[index] = -x;
        }
    }

    public void recolour(int oldColour, int newColour) {
        for (int index = 0; index < faces; index++) {
            if (colors[index] == (short) oldColour) {
                colors[index] = (short) newColour;
            }
        }
    }

    public void outputObj(String path) throws IOException {

        try (PrintWriter writer = new PrintWriter(new File(path + ".obj"))) {

            writer.write("# RS2 Model in OBJ format, Thanks Tom - Vult\n");

            for (int i = 0; i < this.vertices; i++) {
                writer.println("v " + this.verticesX[i] + " " + this.verticesY[i] + " " + this.verticesZ[i]);
            }

            for (int i = 0; i < this.faces; i++) {
                writer.println("f " + (this.facesX[i] + 1) + " " + (this.facesY[i] + 1) + " " + (this.facesZ[i] + 1));
            }

        }

    }

}