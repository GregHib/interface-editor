import io.nshusa.rsam.FileStore;
import io.nshusa.rsam.IndexedFileSystem;
import io.nshusa.rsam.binary.Archive;
import io.nshusa.rsam.binary.sprite.Sprite;

import java.io.IOException;
import java.nio.file.Paths;

public class JavaTest {
    public static void main(String[] args) {

        try(IndexedFileSystem fs = IndexedFileSystem.init(Paths.get("./cache/"))) {
//            fs.load();

            FileStore store = fs.getStore(FileStore.ARCHIVE_FILE_STORE);

            Archive archive = Archive.decode(store.readFile(Archive.MEDIA_ARCHIVE));

            for(Archive.ArchiveEntry entry : archive.getEntries()) {


                int sprites = 0;

                while(true) {
                    try {
                        Sprite sprite = Sprite.decode(archive, entry.getHash(), sprites);
                        sprites++;
                    } catch (Exception ex) {
                        break;
                    }
                }

                System.out.println(String.format("There are %d sprites in archive %s", sprites, entry.getHash()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
