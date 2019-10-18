package marytts.language.ru.phonetic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import marytts.phonetic.converter.Alphabet;


/**
 *
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le Maguer</a>
 */
public class RussianAlphabet extends Alphabet
{
    public RussianAlphabet() {
	super();

        // Consonnants
        this.addIpaCorrespondance("p", "p");
        this.addIpaCorrespondance("pp", "pʲ");
        this.addIpaCorrespondance("b", "b");
        this.addIpaCorrespondance("bb", "bʲ");
        this.addIpaCorrespondance("t", "t");
        this.addIpaCorrespondance("tt", "tʲ");
        this.addIpaCorrespondance("d", "d");
        this.addIpaCorrespondance("dd", "dʲ");
        this.addIpaCorrespondance("k", "k");
        this.addIpaCorrespondance("kk", "kʲ");
        this.addIpaCorrespondance("g", "g");
        this.addIpaCorrespondance("gg", "gʲ");
        this.addIpaCorrespondance("c", "c");
        this.addIpaCorrespondance("ch", "");    // FIXME: double check
        this.addIpaCorrespondance("f", "f");
        this.addIpaCorrespondance("ff", "fʲ");
        this.addIpaCorrespondance("v", "v");
        this.addIpaCorrespondance("vv", "vʲ");
        this.addIpaCorrespondance("s", "s");
        this.addIpaCorrespondance("ss", "sʲ");
        this.addIpaCorrespondance("z", "z");
        this.addIpaCorrespondance("zz", "zʲ");
        this.addIpaCorrespondance("sh", "");    // FIXME: double check
        this.addIpaCorrespondance("sch", "");   // FIXME: double check
        this.addIpaCorrespondance("zh", "");    // FIXME: double check
        this.addIpaCorrespondance("h", "h");
        this.addIpaCorrespondance("hh", "hʲ");
        this.addIpaCorrespondance("m", "m");
        this.addIpaCorrespondance("mm", "mʲ");
        this.addIpaCorrespondance("n", "n");
        this.addIpaCorrespondance("nn", "nʲ");
        this.addIpaCorrespondance("l", "l");
        this.addIpaCorrespondance("ll", "lʲ");
        this.addIpaCorrespondance("r", "r");
        this.addIpaCorrespondance("rr", "rʲ");
        this.addIpaCorrespondance("j", "j");



        // Stressed vowels
        this.addIpaCorrespondance("ii", "iˈ");
        this.addIpaCorrespondance("yy", "yˈ");
        this.addIpaCorrespondance("uu", "uˈ");
        this.addIpaCorrespondance("ee", "eˈ");
        this.addIpaCorrespondance("oo", "oˈ");
        this.addIpaCorrespondance("aa", "aˈ");

        // First level of reduction
        this.addIpaCorrespondance("a", "a");
        this.addIpaCorrespondance("e", "e");
        this.addIpaCorrespondance("i", "i");
        this.addIpaCorrespondance("y", "y");
        this.addIpaCorrespondance("u", "u");

        // Second level of reduction
        this.addIpaCorrespondance("ae", "ɛ"); // FIXME: double check
        this.addIpaCorrespondance("ay", "ə"); // FIXME: double check
        this.addIpaCorrespondance("ur", "ʉ"); // FIXME: double check

    }
}
