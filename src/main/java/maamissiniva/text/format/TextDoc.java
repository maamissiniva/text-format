package maamissiniva.text.format;

import java.util.Arrays;
import java.util.List;

import maamissiniva.text.format.rendering.StringRenderer;

import static maamissiniva.util.Iterables.ar;
import static maamissiniva.util.Iterables.it;

/**
 * Text document. 
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
    
    public interface PVisitor<A> {
        A visit(Empty            d);
        A visit(HorizontalAlign  d);
        A visit(HorizontalConcat d);
        A visit(Indent           d);
        A visit(Table            d);
        A visit(Text             d);
        A visit(VerticalAlign    d);
    }
    
    public interface Visitor {
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
            return v.visit(a, this);
        }

        @Override
        public <A> A accept(PVisitor<A> v) {
            return v.visit(this);
        }
        
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
        
        @Override
        public boolean isEmpty() {
            return true;
        }
        
        @Override
        public String toString() {
            return render();
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
            return v.visit(a, this);
        }

        @Override
        public <A> A accept(PVisitor<A> v) {
            return v.visit(this);
        }
        
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
        
        @Override
        public String toString() {
            return render();
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
            return v.visit(a, this);
        }

        @Override
        public <A> A accept(PVisitor<A> v) {
            return v.visit(this);
        }
        
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
        
        @Override
        public String toString() {
            return render();
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
            return v.visit(a, this);
        }

        @Override
        public <A> A accept(PVisitor<A> v) {
            return v.visit(this);
        }
        
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
        
        @Override
        public String toString() {
            return render();
        }
        
    }
    
    /**
     * List of list of document that are column aligned. 
     */
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
        public <A> A accept(PVisitor<A> v) {
            return v.visit(this);
        }
        
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }

        @Override
        public boolean isEmpty() {
            return rows.isEmpty();
        }
        
        @Override
        public String toString() {
            return render();
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
            return v.visit(a, this);
        }

        @Override
        public <A> A accept(PVisitor<A> v) {
            return v.visit(this);
        }
        
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
        
        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public String toString() {
            return render();
        }
        
    }

    /**
     * Vertically aligned list of documents. 
     */
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
            return v.visit(a, this);
        }

        @Override
        public <A> A accept(PVisitor<A> v) {
            return v.visit(this);
        }
        
        @Override
        public void accept(Visitor v) {
            v.visit(this);
        }
        
        @Override
        public boolean isEmpty() {
            return false;
        }
        
        @Override
        public String toString() {
            return render();
        }
        
    }

    <A,B> B accept(A a, FVisitor<A,B> v);
    
    <A> A accept(PVisitor<A> v);

    void accept(Visitor v);
    
    /**
     * Is this an instance of {@link Empty} ?
     * @return true is this is an instanceof {@link Empty}, false otherwise
     */
    abstract boolean isEmpty();


    default TextDoc vcat(TextDoc... ds) {
        return TextDocShortcuts.vcat(ar(ds).prepend(this));
    }

    default TextDoc vcat(Iterable<TextDoc> ds) {
        return TextDocShortcuts.vcat(it(ds).prepend(this));
    }

    default TextDoc vcat(String... ss) {
        return new TextDoc.VerticalAlign(this, TextDocShortcuts.txt(ss));
    }

    /**
     * Horizontal concat.
     * @param ss
     * @return
     */
    default TextDoc cat(String... ss) {
        return TextDocShortcuts.hcat(this, TextDocShortcuts.txt(ss));
    }

    default TextDoc cat(TextDoc d) {
        return TextDocShortcuts.hcat(this, d);
    }

    default String render() {
        return StringRenderer.getText(this);
    }

    /**
     * Println(render(this)).
     */
    default void printRender() {
        System.out.println(render());
    }
   
}
