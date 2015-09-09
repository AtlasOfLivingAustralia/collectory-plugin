package au.org.ala.collectory

/**
 * Created by markew
 * Date: Sep 21, 2010
 * Time: 2:29:51 PM
 */
class Classification {

    public static final keywordSynonyms = [
        'birds':['ornithology','bird'],
        'fish': ['ichthyology'],
        'frogs': ['amphibians','herpetology','frog'],
        'mammals': ['mammal'],
        'reptiles': ['reptile','herpetology'],
        'entomology': ['insect','insects'],
        'invertebrates': ['insect','insects','spiders','arachnids','invertebrate'],
        'plants': ['angiosperms','plant','plantae','herbarium','herbaria','fungi'],
        'fungi': ['fungus'],
        'ferns': ['fern'],
        'microbes': ['microbe','microbial','protista']
    ]

    public static boolean matchKeywords(keywords, filterString) {
        if(!filterString){
            return true
        }

        def filters = filterString.tokenize(",")
        for (filter in filters) {
            //println "Checking filter ${filter} against keywords ${keywords}"
            if (keywords =~ filter) {
                return true;
            } else {
                // check synonyms
                List synonyms = keywordSynonyms.get(filter)
                for (synonym in synonyms) {
                    if (keywords =~ synonym) {
                        return true;
                    }
                }
            }
        }
        return false
    }

}
