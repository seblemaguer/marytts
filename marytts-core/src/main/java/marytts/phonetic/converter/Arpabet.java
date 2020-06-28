package marytts.phonetic.converter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;


/**
 *
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le Maguer</a>
 */
public class Arpabet extends Alphabet
{
    public Arpabet() {
	super();

        // Consonnants
        this.addIpaCorrespondance("b", "b");
        this.addIpaCorrespondance("bj", "bʲ");
        this.addIpaCorrespondance("ch", "tʃ");
        this.addIpaCorrespondance("d", "d");
        this.addIpaCorrespondance("dj", "dʲ");
        this.addIpaCorrespondance("dh", "ð");
        this.addIpaCorrespondance("dx", "ɾ");
        this.addIpaCorrespondance("el", "l̩");
        this.addIpaCorrespondance("em", "m̩");
        this.addIpaCorrespondance("en", "n̩");
        this.addIpaCorrespondance("f", "f");
        this.addIpaCorrespondance("fj", "fʲ");
        this.addIpaCorrespondance("g", "ɡ");
        this.addIpaCorrespondance("gj", "ɡʲ");
        this.addIpaCorrespondance("hh", "h");
        this.addIpaCorrespondance("hhj", "hʲ");
        this.addIpaCorrespondance("hv", "ç");
        this.addIpaCorrespondance("j", "j");
        this.addIpaCorrespondance("k", "k");
        this.addIpaCorrespondance("kj", "kʲ");
        this.addIpaCorrespondance("l", "l");
        this.addIpaCorrespondance("lj", "lʲ");
        this.addIpaCorrespondance("m", "m");
        this.addIpaCorrespondance("mj", "mʲ");
        this.addIpaCorrespondance("n", "n");
        this.addIpaCorrespondance("nj", "nʲ");
        this.addIpaCorrespondance("ng", "ŋ");
        this.addIpaCorrespondance("nx", "ɾ̃");
        this.addIpaCorrespondance("p", "p");
        this.addIpaCorrespondance("pf", "pf");
        this.addIpaCorrespondance("pj", "pʲ");
        this.addIpaCorrespondance("q", "ʔ"); // FIXME: added by force
        this.addIpaCorrespondance("r", "ɹ");
        this.addIpaCorrespondance("rr", "r"); // FIXME: added by force !
        this.addIpaCorrespondance("rrj", "rʲ"); // FIXME: added by force !
        this.addIpaCorrespondance("rrr", "ʁ"); // FIXME: added by force!
        this.addIpaCorrespondance("s", "s");
        this.addIpaCorrespondance("sj", "sʲ");
        this.addIpaCorrespondance("sch", "ɕ");
        this.addIpaCorrespondance("sh", "ʃ");
        this.addIpaCorrespondance("ssh", "ʂ");
        this.addIpaCorrespondance("t", "t");
        this.addIpaCorrespondance("tj", "tʲ");
        this.addIpaCorrespondance("th", "θ");
        this.addIpaCorrespondance("tch", "tɕ");
        this.addIpaCorrespondance("ts", "ts"); // FIXME: added by force
        this.addIpaCorrespondance("v", "v");
        this.addIpaCorrespondance("vj", "vʲ");
        this.addIpaCorrespondance("w", "w");
        this.addIpaCorrespondance("wh", "ʍ");
        this.addIpaCorrespondance("x", "x"); // FIXME: added by force
        this.addIpaCorrespondance("z", "z");
        this.addIpaCorrespondance("zj", "zʲ");
        this.addIpaCorrespondance("zh", "ʒ");
        this.addIpaCorrespondance("zzh", "ʐ");
        this.addIpaCorrespondance("jh", "dʒ");

        // Vowels or ohers
        this.addIpaCorrespondance("a", "a");
        this.addIpaCorrespondance("a:", "aː");
        this.addIpaCorrespondance("aa", "ɑ");
        this.addIpaCorrespondance("ae", "æ");
        this.addIpaCorrespondance("sae", "ɪ̥");
        this.addIpaCorrespondance("ah", "ʌ");
        this.addIpaCorrespondance("ao", "ɔ");
        this.addIpaCorrespondance("aw", "aʊ");
        this.addIpaCorrespondance("ax", "ə");
        this.addIpaCorrespondance("axr", "ɚ");
        this.addIpaCorrespondance("ay", "aɪ");
        this.addIpaCorrespondance("a~", "ã");
        this.addIpaCorrespondance("e", "e");
        this.addIpaCorrespondance("e:", "eː");
        this.addIpaCorrespondance("eh", "ɛ");
        this.addIpaCorrespondance("eh:", "ɛː");
        this.addIpaCorrespondance("er", "œ");
        this.addIpaCorrespondance("er~", "œ̃"); // FIXME: added by force
        this.addIpaCorrespondance("eu", "ø");
        this.addIpaCorrespondance("eu:", "øː");
        this.addIpaCorrespondance("ey", "eɪ");
        this.addIpaCorrespondance("e~", "ẽ");
        this.addIpaCorrespondance("ih", "ɪ");
        this.addIpaCorrespondance("ix", "ɨ");
        this.addIpaCorrespondance("iy", "i");
        this.addIpaCorrespondance("iy:", "iː");
        this.addIpaCorrespondance("o", "o");
        this.addIpaCorrespondance("o:", "oː");
        this.addIpaCorrespondance("oe", "ɐ");
        this.addIpaCorrespondance("oi", "ɔɪ");
        this.addIpaCorrespondance("ow", "oʊ");
        this.addIpaCorrespondance("oy", "ɔʏ");
        this.addIpaCorrespondance("o~", "õ");
        this.addIpaCorrespondance("uh", "ʊ");
        this.addIpaCorrespondance("ur", "ʊ̥");
        this.addIpaCorrespondance("uw", "u");
        this.addIpaCorrespondance("uw:", "uː");
        this.addIpaCorrespondance("ux", "ʉ");
        this.addIpaCorrespondance("y", "yˈ");
        this.addIpaCorrespondance("y:", "yː");
        this.addIpaCorrespondance("yh", "ʏ");


        this.addIpaCorrespondance("as", "aˈ");
        this.addIpaCorrespondance("es", "eˈ");
        this.addIpaCorrespondance("iys", "iˈ");
        this.addIpaCorrespondance("os", "oˈ");
        this.addIpaCorrespondance("uws", "uˈ");
        this.addIpaCorrespondance("ys", "y");
    }
}
