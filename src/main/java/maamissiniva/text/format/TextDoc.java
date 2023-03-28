package maamissiniva.text.format;

import java.util.Arrays;
import java.util.List;

import static maamissiniva.util.Iterables.ar;
import static maamissiniva.util.Iterables.it;

/**
 * Text document description.
 */
public interface TextDoc {
    
    public interface FVisitor<A,B> {
        B visit(A a, Empty            d);
        B visit(A a, HorizontalAlign  d);
        B visit(A a, HorizontalConcat d);
        B visit(A a, Indent           d);
        B visit(A a, Table            d);
        B visit(A a, Text             d);
        B visit(A a, VerticalAlign    d);
    }
    
    public interface PVisitor<A> extends FVisitor<Void, A> {
        @Override default A visit(Void a, Empty            d) { return visit(d); }
        @Override default A visit(Void a, HorizontalAlign  d) { return visit(d); }
        @Override default A visit(Void a, HorizontalConcat d) { return visit(d); }
        @Override default A visit(Void a, Indent           d) { return visit(d); }
        @Override default A visit(Void a, Table            d) { return visit(d); }
        @Override default A visit(Void a, Text             d) { return visit(d); }
        @Override default A visit(Void a, VerticalAlign    d) { return visit(d); }
        A visit(Empty            d);
        A visit(HorizontalAlign  d);
        A visit(HorizontalConcat d);
        A visit(Indent           d);
        A visit(Table            d);
        A visit(Text             d);
        A visit(VerticalAlign    d);
    }
    
    public interface Visitor extends FVisitor<Void, Void> {
        @Override default Void visit(Void a, Empty            d) { visit(d); return null; }
        @Override default Void visit(Void a, HorizontalAlign  d) { visit(d); return null; }
        @Override default Void visit(Void a, HorizontalConcat d) { visit(d); return null; }
        @Override default Void visit(Void a, Indent           d) { visit(d); return null; }
        @Override default Void visit(Void a, Table            d) { visit(d); return null; }
        @Override default Void visit(Void a, Text             d) { visit(d); return null; }
        @Override default Void visit(Void a, VerticalAlign    d) { visit(d); return null; }
        void visit(Empty            d);
        void visit(HorizontalAlign  d);
        void visit(HorizontalConcat d);
        void visit(Indent           d);
        void visit(Table            d);
        void visit(Text             d);
        void visit(VerticalAlign    d);
    }
    
    /**
     * Empty document.
     */
    public class Empty implements TextDoc {

        @Override
        public <A, B> B accept(A a, FVisitor<A, B> v) {
            return v.visit(a,  this);
        }

        @Override
        public boolean isEmpty() {
            return true;
        }
        
    }

    /**
     * Concatenates two blocks.
     * Concatenation of lines:
     * <pre>
     *    XXXXX + YYYYY
     *    = 
     *    XXXXXYYYYY
     * </pre>
     * Concatentation of blocks:
     * <pre>
     *    XXXXX + YYYYY
     *    XXXXX   
     *    =
     *    XXXXXYYYYY
     *    XXXXX
     * </pre>
     */
    public class HorizontalAlign implements TextDoc {
        
        public final TextDoc left;
        public final TextDoc right;
        
        public HorizontalAlign(TextDoc left, TextDoc right) {
            this.left  = left;
            this.right = right;
        }
        
        @Override
        public boolean isEmpty() {
            return false;
        }
        
        @Override
        public <A, B> B accept(A a, FVisitor<A, B> v) {
            return v.visit(a,  this);
        }
        
    }

    /**
     * Concatenates two blocks.
     * Concatenation of lines:
     * <pre>
     *    XXXXX + YYYYY
     *    = 
     *    XXXXXYYYYY
     * </pre>
     * Concatentation of blocks:
     * <pre>
     *    XXXXX + YYYYY
     *    XXXXX   YYYYY
     *    =
     *    XXXXX
     *    XXXXXYYYYY
     *         YYYYY
     * </pre>
     */
    public class HorizontalConcat implements TextDoc {
        
        public final TextDoc left;
        public final TextDoc right;
        
        public HorizontalConcat(TextDoc left, TextDoc right) {
            this.left  = left;
            this.right = right;
        }
        
        @Override
        public boolean isEmpty() {
            return false;
        }
        
        @Override
        public <A, B> B accept(A a, FVisitor<A, B> v) {
            return v.visit(a,  this);
        }
        
    }

    /**
     * Indents a document.
     */
    public class Indent implements TextDoc {
        
        public final int indent;
        public final TextDoc doc;
        
        public Indent(int indent, TextDoc doc) {
            this.indent = indent;
            this.doc    = doc;
        }
        
        @Override
        public boolean isEmpty() {
            return false;
        }
        
        @Override
        public <A, B> B accept(A a, FVisitor<A, B> v) {
            return v.visit(a,  this);
        }
        
    }
    
    public static class Table implements TextDoc {

        public final List<List<TextDoc>> rows;
        
        public Table(List<List<TextDoc>> rows) {
            this.rows = rows;
        }
        
        @Override
        public <A, B> B accept(A a, FVisitor<A, B> v) {
            return v.visit(a, this);
        }

        @Override
        public boolean isEmpty() {
            return rows.isEmpty();
        }
        
    }
    
    /**
     * Text fragment. Expected to not contain newlines.
     */
    public class Text implements TextDoc {
        
        public final String text;
        
        public Text(String text) {
            this.text = text;
        }
        
        @Override
        public <A, B> B accept(A a, FVisitor<A, B> v) {
            return v.visit(a,  this);
        }
        
        @Override
        public boolean isEmpty() {
            return false;
        }

    }

    public class VerticalAlign implements TextDoc {
        
        public final List<TextDoc> docs;
        
        public VerticalAlign(TextDoc...docs) {
            this(Arrays.asList(docs));
        }
        
        public VerticalAlign(List<TextDoc> docs) {
            this.docs = docs;
        }
        
        @Override
        public <A, B> B accept(A a, FVisitor<A, B> v) {
            return v.visit(a,  this);
        }
        
        @Override
        public boolean isEmpty() {
            return false;
        }
        
    }

    abstract <A,B> B accept(A a, FVisitor<A,B> v);
    
    TextDoc sp    = new Text(" ");
    TextDoc empty = new Empty();

    default TextDoc vcat(TextDoc... ds) {
        return TextDocUtils.vcat(ar(ds).prepend(this));
    }
    
    default TextDoc vcat(Iterable<TextDoc> ds) {
        return TextDocUtils.vcat(it(ds).prepend(this));
    }
    
    default TextDoc vcat(String... ss) {
        return new TextDoc.VerticalAlign(this, TextDocUtils.txt(ss));
    }
    
    /**
     * Horizontal concat.
     * @param ss
     * @return
     */
    default TextDoc cat(String... ss) {
        return TextDocUtils.hcat(this, TextDocUtils.txt(ss));
    }
    
    default TextDoc cat(TextDoc d) {
        return TextDocUtils.hcat(this, d);
    }
    
    default String render() {
        return StringRenderer.getText(this);
    }

    default <A> A accept(PVisitor<A> v) {
        return accept(null, v);
    }
    
    default void accept(Visitor v) {
        accept(null, v);
    }

    /**
     * Is this an instance of {@link Empty} ?
     * @return true is this is an instanceof {@link Empty}, false otherwise
     */
    abstract boolean isEmpty();

    /**
     * Println(render(this)).
     */
    default void printRender() {
        System.out.println(render());
    }
   
}
