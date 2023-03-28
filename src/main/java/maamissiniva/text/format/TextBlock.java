package maamissiniva.text.format;

import java.util.Collections;
import java.util.List;

import maamissiniva.util.Iterables;

/**
 * Rectangular block of text.
 */
public class TextBlock {
        
    public static final TextBlock empty = new TextBlock(Collections.emptyList());

    /**
     * Lines.
     */
    public final List<TextLine> lines;
    
    /**
     * Width.
     */
    public final int width;
    
    /**
     * Height.
     */
    public final int height;
    
    public TextBlock(List<TextLine> lines) {
        this.lines = Collections.unmodifiableList(lines);
        this.width = Iterables.foldL(lines, 0, (x,y) -> Math.max(x, y.width));
        this.height = lines.size();
    }
    
}
