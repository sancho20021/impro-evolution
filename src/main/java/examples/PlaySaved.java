package examples;

import algorithm.Modes;
import com.jsyn.unitgen.FilterHighPass;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.Maximum;
import com.jsyn.unitgen.SineOscillator;
import examples.manual.Common;
import modules.UnitConstant;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

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
    @Test
    public void test04() {
        Modes.readCompositionAndPlay(getCompFile("1626966600266"), 2);
    }
    @Test
    public void test05() {
        Modes.readCompositionAndPlay(getCompFile("firstFeedback"), 6);
    }
    @Test
    public void test06() {
        Modes.readCompositionAndPlay(getCompFile("40 modules"), 3);
    }
    @Test
    public void test07() {
        Modes.readCompositionAndPlay(getCompFile("1626986491562"), 2);
    }
    @Test
    public void test08() {
        Modes.readCompositionAndPlay(getCompFile("4 rows"), 4);
    }

    @Test
    public void test09_one() {
        Modes.playMelody(getCompFile("4 rows").toPath().getParent().resolve("1153180745").toFile(), 8);
    }

    // This has a lot of cycles in the end
    @Test
    public void test10_3rows() {
        Modes.readCompositionAndPlay(getCompFile("3 rows"), 4);
    }
}
