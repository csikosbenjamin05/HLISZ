package base3D.ui.core;

import base3D.ENV;
import processing.core.PFont;
import processing.core.PShape;

public class Text {

    private static final String defaultFont = "Arial";
    //-----------------------------------------
    // Attributes:
    private String text;
    public int textSize;
    public int textColor;
    public int marginXBefore;
    public int marginXAfter;
    public int marginYBefore;
    public int marginYAfter;
    public PFont font;
    private final float[] textArea;



    //-----------------------------------------
    // Constructors:

    public Text() {
        this("", defaultFont, 20, ENV.p.color(255));
    }

    public Text(String text, String fontName, int textSize, int textColor) {
        this.textArea = new float[4];
        this.textSize = textSize;
        this.textColor = textColor;

        marginXBefore = marginXAfter = marginYBefore = marginYAfter = 5;

        this.font = ENV.p.createFont(fontName, textSize, true);
        setText(text);
    }

    public Text(String text, float[] textArea) {
        this();
        setText(text);
    }

    public Text(String text, int textSize, int textColor) {
        this(text, defaultFont, textSize, textColor);
    }

    //copy
    public Text(Text t) {this(); set(t);}
    public Text copy() {return new Text(this);}
    public void set(Text t) {
        this.text = t.text;
        this.textSize = t.textSize;
        this.textColor = t.textColor;
        this.font = t.font;
        calculateTextArea();
    }
    //-----------------------------------------
    // Methods:

    private void calculateTextArea() {


        if (text.equals("")) {
            textArea[0] = 0;
            textArea[1] = 0;
            textArea[2] = 0;
            textArea[3] = 0;

        } else {
            // Get maxHeight and minHeight
            //https://discourse.processing.org/t/a-more-accurate-approach-to-calculating-text-width-text-height/24879
            ENV.p.textFont(font);
            ENV.p.textSize(textSize);
            ENV.p.fill(textColor);

            float minHeight = Float.MAX_VALUE;
            float maxHeight = Float.NEGATIVE_INFINITY;

            for (Character c : text.toCharArray()) {
                PShape character = font.getShape(c); // create character vector
                for (int i = 0; i < character.getVertexCount(); i++) {
                    minHeight = Math.min(character.getVertex(i).y, minHeight);
                    maxHeight = Math.max(character.getVertex(i).y, maxHeight);
                }
            }

            textArea[1] = minHeight + marginYBefore;
            textArea[3] = maxHeight - minHeight + marginYBefore + marginYAfter;

            // Get text width and whitespace
            //https://discourse.processing.org/t/a-more-accurate-approach-to-calculating-text-width-text-height/24879
            float textWidth = ENV.p.textWidth(text); // call Processing method

            float whitespace = (font.width(text.charAt(text.length() - 1)) * font.getSize()
                    - font.getGlyph(text.charAt(text.length() - 1)).width) / 2;
            textWidth -= whitespace; // subtract whitespace of last character

            whitespace = (font.width(text.charAt(0)) * font.getSize() - font.getGlyph(text.charAt(0)).width) / 2;
            textWidth -= whitespace; // subtract whitespace of first character


            textArea[0] = whitespace + marginXBefore;
            textArea[2] = textWidth + marginXBefore + marginYAfter;
        }
    }

    public void render(float x, float y) {
        ENV.p.textFont(font);
        ENV.p.textSize(textSize);
        ENV.p.fill(textColor);
        ENV.p.text(text, x+marginXBefore, y + textArea[3] - marginYAfter);
    }


    //-----------------------------------------
    // Getters & Setters:
    public String getText() {return text;}

    public void setText(String t) {
        this.text = t;
        calculateTextArea();
    }

    public void setTextWithoutUpdate(String t) {
        this.text = t;
    }

    public float[] getTextArea() {return textArea;}


}
