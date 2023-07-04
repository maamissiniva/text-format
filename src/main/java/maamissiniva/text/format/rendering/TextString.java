package maamissiniva.text.format.rendering;

/**
 * TextDoc string that may have some padding. 
 */
public class TextString {
    
    /**
     * Text width.
     */
    public final int width;
    
    /**
     * Text value.
     */
    public final String value;
    
    public TextString(int width, String value) {
        this.width = width;
        this.value = value;
    }
    
    public boolean isPadding() {
        for (int i=0; i<value.length(); i++) {
            switch (value.charAt(i)) {
            case ' '  :
            case '\t' :
                continue;
            default : return false;
            }
        }
        return true;
    }
    
    public static TextString pad(int size) {
        return new TextString(size, "");
    }
    
    public static TextString string(String s) {
        return new TextString(s.length(), s);
    }
    
}
