import io.nshusa.rsam.FileStore;
import io.nshusa.rsam.IndexedFileSystem;
import io.nshusa.rsam.binary.Archive;
import io.nshusa.rsam.binary.sprite.Sprite;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Paths;

public class JavaTest {

    public static void main(String[] args) {

        long start = System.currentTimeMillis();
        try(IndexedFileSystem fs = IndexedFileSystem.init(Paths.get("./cache/"))) {
            fs.load();

            System.out.println("Loaded cache in: " + (System.currentTimeMillis() - start) + "ms");
            start = System.currentTimeMillis();
            FileStore store = fs.getStore(FileStore.ARCHIVE_FILE_STORE);

            System.out.println("Store loaded: " + (System.currentTimeMillis() - start) + "ms");
            start = System.currentTimeMillis();
            Archive archive = Archive.decode(store.readFile(Archive.MEDIA_ARCHIVE));
            System.out.println("Archive: " + (System.currentTimeMillis() - start) + "ms");
            start = System.currentTimeMillis();

            ByteBuffer metaBuf = archive.readFile("index.dat");
            System.out.println("Index: " + (System.currentTimeMillis() - start) + "ms");
            start = System.currentTimeMillis();

            for(Archive.ArchiveEntry entry : archive.getEntries()) {


                int sprites = 0;

                spriteLoop : while(true) {
                    try {
                        Sprite sprite = Sprite.decode(archive, metaBuf, entry.getHash(), sprites);
                        sprites++;
                    } catch (Exception ex) {
                        break spriteLoop;
                    }
                }

//                System.out.println(String.format("There are %d sprites in archive %s", sprites, entry.getHash()));
            }
            System.out.println("Sprites: " + (System.currentTimeMillis() - start) + "ms");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
