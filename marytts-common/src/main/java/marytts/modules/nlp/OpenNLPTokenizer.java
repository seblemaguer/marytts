/**
 * Copyright 2000-2006 DFKI GmbH.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of MARY TTS.
 *
 * MARY TTS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package marytts.modules.nlp;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.io.InputStream;

import marytts.datatypes.MaryData;
import marytts.datatypes.MaryXML;
import marytts.util.dom.DomUtils;
import marytts.util.dom.MaryDomUtils;
import marytts.server.MaryProperties;

import marytts.data.utils.IntegerPair;
import marytts.data.utils.SequenceTypePair;
import marytts.data.Utterance;
import marytts.data.Sequence;
import marytts.data.Relation;
import marytts.data.SupportedSequenceType;
import marytts.data.item.linguistic.Paragraph;
import marytts.data.item.linguistic.Sentence;
import marytts.data.item.linguistic.Word;
import marytts.io.XMLSerializer;

import marytts.modules.InternalModule;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

/**
 * @author Marc Schr&ouml;der
 *
 *
 */
public class OpenNLPTokenizer extends InternalModule {
	public static final int TOKEN_MAXLENGTH = 100;

	private TokenizerME tokenizer = null;
	private String language_code;

	public OpenNLPTokenizer() {
		this((Locale) null);
	}

	public OpenNLPTokenizer(String locale) {
		super("OpenNLPTokenizer", new Locale(locale));
	}

	public OpenNLPTokenizer(Locale locale) {
		super("OpenNLPTokenizer", locale);

        // if locale == null, use default tokenizer
		if (locale == null) {
			language_code = "default";
		} else {
			language_code = locale.getLanguage();
		}
	}

	/**
	 * Set the tokenizer language to be different from the Locale of the module. This can be useful
	 * when reusing another language's tokenizer data.
	 *
	 * @param languageCode the language-code to use, as a two-character string such as "de" or "en".
	 */
	protected void setTokenizerLanguage(String languageCode) {
		language_code = languageCode;
	}

	public void startup() throws Exception {
		super.startup();
        // Loading module
        InputStream inputStream = MaryProperties.getStream("marytts/modules/nlp/opennlp/" + language_code + "-token.bin");
        TokenizerModel tokenModel = new TokenizerModel(inputStream);
        tokenizer = new TokenizerME(tokenModel);
	}

	public MaryData process(MaryData d)
        throws Exception
    {
        // Initialisation
        Utterance utt = d.getData();

        // Sequence initialisation
        Sequence<Sentence> sentences = new Sequence<Sentence>();
        Sequence<Word> words = new Sequence<Word>();

        // Alignment initialisation
        ArrayList<IntegerPair> alignment_paragraph_sentence = new ArrayList<IntegerPair>();
        ArrayList<IntegerPair> alignment_sentence_word = new ArrayList<IntegerPair>();

        // Indexes and temp variables
        int p_idx = 0, sent_idx=0, tok_idx=0;
        int sent_offset=0, tok_offset=0;
        String sent_text = "";

        // Tokenize everything
        for (Paragraph p : (Sequence<Paragraph>) utt.getSequence(SupportedSequenceType.PARAGRAPH))
        {
            // Tokenize current paragraph
            // Going through the tokens and create the proper MaryTTS representation
            for (String tok : tokenizer.tokenize(p.getText()))
            {
                // Create the new token
                sent_text += tok + " ";
                Word w = new Word(tok);
                words.add(w);
                tok_idx++;

                // Check if the token is ending the current sentence or not
                if (tok.equals(".") ||
                    tok.equals("?") ||
                    tok.equals("!"))
                {
                    // Create and add the new sentence
                    Sentence s = new Sentence(sent_text);
                    sentences.add(s);

                    // Sent <=> Word relation
                    for(int i=tok_offset; i<tok_idx; i++)
                        alignment_sentence_word.add(new IntegerPair(sent_idx, i));
                    tok_offset = tok_idx;

                    // Paragraph <=> relation
                    alignment_paragraph_sentence.add(new IntegerPair(p_idx, sent_idx));

                    // Prepare the next sentence
                    sent_idx++;
                    sent_text = "";
                }
            }

            p_idx++;
        }


        // Maybe something remains after there is no punctuation
        if (tok_offset < tok_idx)
        {
            // Create and add the new sentence
            Sentence s = new Sentence(sent_text);
            sentences.add(s);

            // // Add a punctuation token(FIXME: somehow kind of patchy!)
            // Word w = new Word(".");
            // words.add(w);
            // tok_idx++;

            // Sent <=> Word relation
            for(int i=tok_offset; i<tok_idx; i++)
                alignment_sentence_word.add(new IntegerPair(sent_idx, i));
            tok_offset = tok_idx;

            // Paragraph <=> relation
            alignment_paragraph_sentence.add(new IntegerPair(p_idx-1, sent_idx));
        }

        // Add the sequences to the utterance
        utt.addSequence(SupportedSequenceType.SENTENCE, sentences);
        utt.addSequence(SupportedSequenceType.WORD, words);


        // Create the relations and add them to the utterance
        Relation rel_par_sent = new Relation(utt.getSequence(SupportedSequenceType.PARAGRAPH),
                                             utt.getSequence(SupportedSequenceType.SENTENCE),
                                             alignment_paragraph_sentence);
        utt.setRelation(SupportedSequenceType.PARAGRAPH, SupportedSequenceType.SENTENCE, rel_par_sent);
        Relation rel_sent_wrd = new Relation(utt.getSequence(SupportedSequenceType.SENTENCE),
                                             utt.getSequence(SupportedSequenceType.WORD),
                                             alignment_sentence_word);
        utt.setRelation(SupportedSequenceType.SENTENCE, SupportedSequenceType.WORD, rel_sent_wrd);

        // Generate the result
        MaryData result = new MaryData(d.getLocale(), utt);
        return result;
    }
}
