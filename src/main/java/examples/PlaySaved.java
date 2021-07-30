package examples;

import algorithm.MusicCGP;
import files.Utils;
import files.formats.Composition;
import files.formats.OneMelody;
import org.junit.Test;

import java.io.IOException;

import static files.Utils.getCompositionFile;
import static files.Utils.readObject;

public class PlaySaved {

    public static void readCompositionAndPlay(final String name, final double secondsPerMelody) throws IOException{
        MusicCGP.playComposition(readObject(getCompositionFile(name), Composition.class), secondsPerMelody);
    }

    @Test
    public void test01() throws IOException {
        readCompositionAndPlay("1627388596932", 4);
    }

    @Test
    public void test02_eliminating_silence() throws IOException {
        readCompositionAndPlay("good_example", 4);
    }

    @Test
    public void test02_play_one() throws IOException {
        MusicCGP.playMelody(Utils.readObject(Utils.dataDir.resolve("good_example").resolve("song").toFile(), OneMelody.class), 25);
    }

    @Test
    public void test03_frying_potato() throws IOException {
        readCompositionAndPlay("frying potato", 4);
    }

    @Test
    public void test04_short_comp() throws IOException {
        readCompositionAndPlay("short_comp", 2);
    }

}
