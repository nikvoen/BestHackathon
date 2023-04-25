
package org.tartarus.snowball;

class SpecifiedStemmer(lang: String) {
    private val stemmer: SnowballStemmer = Class.forName("org.tartarus.snowball.ext." +
            lang + "Stemmer").newInstance() as SnowballStemmer;

    fun getStem(word: String): String {
        stemmer.setCurrent(word);
        stemmer.stem();
        return stemmer.getCurrent();
    }
}
