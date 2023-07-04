package maamissiniva.text.format;

import static maamissiniva.util.Iterables.ar;
import static maamissiniva.util.Iterables.asList;
import static maamissiniva.util.Iterables.it;
import static maamissiniva.util.Iterables.singleton;
import static maamissiniva.util.Iterables.take;

import java.util.List;

import maamissiniva.util.MaamIterable;

/**
 * Factory methods aimed at simplifying the construction of {@link TextDoc}
 * instances. 
 */
public class TextDocShortcuts {

    public static final TextDoc empty      = new TextDoc.Empty();
    public static final TextDoc space      = txt(" ");
    public static final TextDoc comma      = txt(",");
    public static final TextDoc emptySpace = txt("");
    
    /**
     * Horizontal concatenation of documents.
     * @param docs documents to concatenate
     * @return     document
     */
//    public static TextDoc cat(TextDoc... docs) {
//        return hcat(docs);
//    }
//    
//    /**
//     * Horizontal concatenation of strings that are converted to documents.
//     * @param docs documents to concatenate
//     * @return     document
//     */
//    public static TextDoc cat(String... ss) {
//        return hcat(ss);
//    }
//    
//    /**
//     * Horizontal concatenation of document and strings that are converted to documents.
//     * @param docs documents to concatenate
//     * @return     document
//     */   
//    public static TextDoc cat(TextDoc doc, String...ss) {
//       return hcat(doc, ss);
//    }
    
    /**
     * Horizontal concatenation of documents.
     * @param ds documents to concatenate
     * @return   document
     */
    public static TextDoc hcat(MaamIterable<TextDoc> ds) {
        return ds.foldL(empty, (x,y) -> new TextDoc.HorizontalConcat(x, y));
    }

    public static TextDoc hcat(TextDoc doc, String... ss) {
        return hcat(singleton(doc).concat(ar(ss).map(x -> txt(x))));
    }

    public static TextDoc hcat(Iterable<TextDoc> ds) {
        return hcat(it(ds));
    }

    public static TextDoc hcat(List<TextDoc> ds) {
        return hcat(it(ds));
    }

    public static TextDoc hcat(String... ds) {
        return hcat(ar(ds).map(x -> txt(x)));
    }

    public static TextDoc hcat(TextDoc... ds) {
        return hcat(ar(ds));
    }
    
    public static TextDoc indent(int indent, TextDoc doc) {
        return new TextDoc.Indent(indent, doc);
    }
    
    
    public static TextDoc hali(MaamIterable<TextDoc> ds) {
        return ds.foldL(empty, (x,y) -> new TextDoc.HorizontalAlign(x, y));
    }
    
    public static TextDoc hali(TextDoc... ds) {
        return hali(ar(ds));
    }
    
    
    
    
    /**
     * Break string into lines if needed and add them as a vcat.
     * @param s string
     * @return  document
     */
    public static TextDoc txt(String s) {
        return vcat(ar(s.split("\\R")).map(l -> new TextDoc.Text(l)));
    }

    public static TextDoc txt(String... ss) {
        return hcat(ar(ss).map(s -> txt(s)));
    }

    
    public static TextDoc vcat(List<TextDoc> ds) {
        return new TextDoc.VerticalAlign(ds);
    }
    
    public static TextDoc vcat(Iterable<TextDoc> ds) {
        return new TextDoc.VerticalAlign(asList(ds));
    }
   
    public static TextDoc vcat(TextDoc... ds) {
        return new TextDoc.VerticalAlign(ar(ds).asList());
    }   
    
    public static TextDoc vcatIntercalate(List<TextDoc> docs, TextDoc inter) {
        return take(docs, docs.size() - 1)
            .foldR(docs.get(docs.size()-1), (x,y) -> vcat(hcat(x, inter), y));
    }
    
}
