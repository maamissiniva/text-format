package maamissiniva.text.format;

import static maamissiniva.util.Iterables.singleton;

import maamissiniva.util.MaamIterable;

/**
 * Text line as a tree of TextString. 
 */
public abstract class TextLine {
    
    public static class TLNode extends TextLine {
        
        public final TextLine left;
        public final TextLine right;
    
        public TLNode(TextLine left, TextLine right) {
            super(left.width + right.width);
            this.left  = left;
            this.right = right;
        }       

        @Override
        public MaamIterable<TextString> leaves() {
            return left.leaves().concat(right.leaves());
        }

    }
    
    public static class TLLeaf extends TextLine {
        
        public final TextString string;
        
        public TLLeaf(TextString string) {
            super(string.width);
            this.string = string;
        }
        
        @Override
        public MaamIterable<TextString> leaves() {
            return singleton(string);
        }
        
    }
    
    public final int width;
    
    public TextLine(int width) {
        this.width   = width;
    }
    
    public abstract MaamIterable<TextString> leaves();
    
    public static TextLine tlString(String s) {
        return new TLLeaf(TextString.string(s));
    }
    
    public static TextLine tlPad(int width) {
        return new TLLeaf(TextString.pad(width));
    }
    
    public static TextLine tl(String left, TextLine rigth) {
        return new TLNode(tlString(left), rigth);
    }
    
    public static TextLine tl(TextLine left, TextLine right) {
        return new TLNode(left, right);
    }
    
    public static TextLine tl(TextLine left, String right) {
        return new TLNode(left, tlString(right));
    }
    
    public static TextLine tl(int left, TextLine right) {
        if (left <= 0)
            return right;
        return tl(tlPad(left), right);
    }
    
    /**
     * Line of left content and right padding. A 0 or less right padding
     * produces the original line.
     * @param left  left content
     * @param right padding size
     * @return      new text line
     */
    public static TextLine tl(TextLine left, int right) {
        if (right <= 0)
            return left;
        return tl(left, tlPad(right));
    }

    /**
     * Build a line that is at least of given width
     * @param width expected line width
     * @return      line of given width or the original line if it's longer
     */
    public TextLine padTo(int width) {
        if (this.width >= width)
            return this;
        return tl(this, width - this.width);
    }
    
}
