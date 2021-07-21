package examples;

import algorithm.Modes;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;

public class PlaySaved {
    public final static Path directory = Path.of("src/main/resources/data");

    public static File getCompFile(final String name) {
        return directory.resolve(name).resolve("long").toFile();
    }

    @Test
    public void test01() {
        Modes.readCompositionAndPlay(getCompFile("1626895060651"), 2);
    }

    @Test
    public void test02_electro1() {
        Modes.readCompositionAndPlay(getCompFile("electro1"), 2);
    }

    @Test
    public void test03_electro2() {
        Modes.readCompositionAndPlay(getCompFile("electro2"), 2);
    }
}
