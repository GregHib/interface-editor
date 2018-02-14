package io.nshusa.rsam.binary.sprite;

import io.nshusa.rsam.binary.Archive;
import io.nshusa.rsam.util.HashUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class ImageArchive {

    private int hash;
    private final List<Sprite> sprites = new ArrayList<>();

    public ImageArchive(int hash) {
        this.hash = hash;
    }

    public static ImageArchive decode(Archive archive, int hash) {
        ImageArchive imageArchive = new ImageArchive(hash);

        for (int i = 0; ; i++) {
            try {
                Sprite decoded = Sprite.decode(archive, hash, i);

                imageArchive.sprites.add(decoded);
            } catch (IOException e) {
                break;
            }
        }

        return imageArchive;
    }

    public static ImageArchive decode(Archive archive, String name) {
        return decode(archive, HashUtils.nameToHash(name));
    }

    public int getHash() {
        return hash;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }

    public void setName(String name) {
        this.hash = HashUtils.nameToHash(name);
    }

    public List<Sprite> getSprites() {
        return sprites;
    }

}
