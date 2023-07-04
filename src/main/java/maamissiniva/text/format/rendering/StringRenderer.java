package maamissiniva.text.format.rendering;

import static maamissiniva.text.format.rendering.TextLine.tl;
import static maamissiniva.text.format.rendering.TextLine.tlPad;
import static maamissiniva.text.format.rendering.TextLine.tlString;
import static maamissiniva.util.Iterables.all;
import static maamissiniva.util.Iterables.flatMap;
import static maamissiniva.util.Iterables.foldL;
import static maamissiniva.util.Iterables.it;
import static maamissiniva.util.Iterables.map;
import static maamissiniva.util.Iterables.range;
import static maamissiniva.util.Iterables.repeat;
import static maamissiniva.util.Iterables.take;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import maamissiniva.text.format.TextDoc;
import maamissiniva.text.format.TextDoc.Empty;
import maamissiniva.text.format.TextDoc.HorizontalAlign;
import maamissiniva.text.format.TextDoc.HorizontalConcat;
import maamissiniva.text.format.TextDoc.Indent;
import maamissiniva.text.format.TextDoc.PVisitor;
import maamissiniva.text.format.TextDoc.Table;
import maamissiniva.text.format.TextDoc.Text;
import maamissiniva.text.format.TextDoc.VerticalAlign;
import static maamissiniva.text.format.TextDocShortcuts.empty;

/**
 * Render text document in a string.
 */
public class StringRenderer {

    /**
     * Spaces to render padding.
     */
    public static final String spaces = repeat(" ").take(1024).asString();

    public static TextDoc prepare(TextDoc doc) {
        return doc.accept(new TextDoc.PVisitor<TextDoc>() {
            @Override public TextDoc visit(Empty d) {
                return d;
            }
            @Override public TextDoc visit(HorizontalAlign d) {
                TextDoc left  = prepare(d.left);
                TextDoc right = prepare(d.right);
                if (left.isEmpty())
                    return right;
                else if (right.isEmpty())
                    return left;
                else
                    return new HorizontalAlign(left, right);
            }
            @Override public TextDoc visit(HorizontalConcat d) {
                TextDoc left  = prepare(d.left);
                TextDoc right = prepare(d.right);
                if (left.isEmpty())
                    return right;
                else if (right.isEmpty())
                    return left;
                else
                    return new HorizontalConcat(left, right);
            }
            @Override public TextDoc visit(Indent d) {
                TextDoc p = prepare(d.doc);
                if (p instanceof TextDoc.Empty)
                    return empty;
                return new Indent(d.indent, p);
            }
            @Override public TextDoc visit(Table d) {
                List<List<TextDoc>> rows =
                    map(d.rows, x -> map(x, y -> prepare(y)).asList())
                    .filter(x -> ! all(x, y -> y.isEmpty()))
                    .asList();
                return new Table(rows);
            }
            @Override public TextDoc visit(Text d) {
                return d;
            }
            @Override public TextDoc visit(VerticalAlign d) {
                List<TextDoc> ds =
                    map(d.docs, x -> prepare(x))
                    .filter(x -> ! (x instanceof TextDoc.Empty))
                    .asList();
                if (ds.isEmpty())
                    return empty;
                return new VerticalAlign(ds);
            }
        });
    }
    
    public static String render(TextLine line) {
        List<TextString> strings = line.leaves().asList();
        while (! strings.isEmpty() && strings.get(strings.size() - 1).isPadding())
            strings.remove(strings.size() - 1);
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<strings.size(); i++) {
            TextString s = strings.get(i);
            sb.append(s.value);
            int width = s.width - s.value.length();
            while (width > spaces.length()) {
                width -= spaces.length();
                sb.append(spaces);
            }
            sb.append(spaces, 0, width);
        }
        return sb.toString();
    }
          
    public static String getText(TextDoc doc) {
        TextBlock b = new StringRenderer().render(doc);
        return map(b.lines, x -> render(x))
            .intercalate("\n")
            .asString();
    }
    
    public TextBlock render(TextDoc doc) {
        return doc.accept(new TextDoc.PVisitor<TextBlock>() {
            @Override public TextBlock visit(Empty d) {
                return TextBlock.empty;
            }
            @Override public TextBlock visit(HorizontalAlign d) {
                TextBlock left  = render(d.left);
                TextBlock right = render(d.right);
                if (left.height == 0)
                    return right;
                if (right.height == 0)
                    return left;
                int lWidth = left.width;
                int rWidth = right.width;
                int height = Math.max(left.height, right.height);
                List<TextLine> lines = new ArrayList<>();
                for (int i=0; i<height; i++) {
                    if (i >= left.lines.size())
                        lines.add(tl(lWidth, right.lines.get(i).padTo(rWidth)));
                    else if (i >= right.lines.size())
                        lines.add(tl(left.lines.get(i).padTo(lWidth), rWidth));
                    else
                        lines.add(tl(left.lines.get(i).padTo(lWidth), right.lines.get(i).padTo(rWidth)));
                }
                return new TextBlock(lines);
            }
            @Override public TextBlock visit(HorizontalConcat d) {
                TextBlock left  = render(d.left);
                TextBlock right = render(d.right);
                if (left.height == 0)
                    return right;
                if (right.height == 0)
                    return left;
                int lHeight = left.height;
                int rHeight = right.height;
                int lWidth  = left.width;
                int rWidth  = right.width;
                int width   = lWidth + rWidth;
                List<TextLine> lines = new ArrayList<>();
                for (int i = 0; i < lHeight - 1; i++) 
                    lines.add(left.lines.get(i).padTo(width)); 
                    lines.add(tl(left.lines.get(lHeight - 1).padTo(lWidth), right.lines.get(0).padTo(rWidth)));
                for (int i = 1; i < rHeight; i++) 
                    lines.add(tl(lWidth, right.lines.get(i).padTo(rWidth)));
                return new TextBlock(lines);
            }
            @Override public TextBlock visit(Indent d) {
                TextBlock b = render(d.doc);
                return new TextBlock(map(b.lines, x -> tl(d.indent, x)).asList());
            }
            @Override public TextBlock visit(Table d) {
                int columns = foldL(d.rows, 0, (x,y) -> Math.max(x, y.size()));
                List<List<TextBlock>> blocks = 
                    map(d.rows, 
                        r -> take(it(r).concat(repeat(empty)).map(e -> render(e)), columns).asList()).asList();
                List<Integer> columnSizes = 
                    range(0, columns-1).map(i -> foldL(range(0, blocks.size()-1), 0, (x,j) -> Math.max(x, blocks.get(j).get(i).width))).asList();
                List<TextLine> lines = new ArrayList<>();
                for (List<TextBlock> bs : blocks) {
                    int height = foldL(bs, 0, (x,y) -> Math.max(x, y.height));
                    for (int l = 0; l <height; l++) {
                        TextLine tl = TextLine.tlString("");
                        for (int c = 0; c<columns; c++) {
                            int cWidth = columnSizes.get(c);
                            if (l < bs.get(c).height)
                                tl = tl(tl, bs.get(c).lines.get(l).padTo(cWidth));
                            else
                                tl = tl(tl, tlPad(cWidth));
//                            tl = tl(tl, "/* " + cWidth + " */");
                        }
                        lines.add(tl);
                    }
                }
                return new TextBlock(lines);
            }
            @Override public TextBlock visit(Text d) {
                return new TextBlock(Arrays.asList(tlString(d.text)));
            }
            @Override public TextBlock visit(VerticalAlign d) {
                List<TextBlock> bs = map(d.docs, x -> render(x)).asList();
                int width = foldL(bs, 0, (x,y) -> Math.max(x, y.width));
                List<TextLine> lines = 
                    flatMap(bs, x -> x.lines)
                    .map(x -> tl(x, width - x.width))
                    .asList();
                return new TextBlock(lines);
            }
        });
    }
    
}
