package me.blutkrone.rpgcore.util.fontmagic;

import me.blutkrone.rpgcore.resourcepack.utils.IndexedTexture;
import me.blutkrone.rpgcore.util.Utility;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/*
 * todo get rid of font hack string builder
 */
public class MagicStringBuilder {

    private static Pattern COLOR_STRIP = Pattern.compile("(?i)§[0-9A-F]");

    // already compiled parts
    private List<BaseComponent> compiled = new ArrayList<>();
    // which character table to utilize
    private String charset = "default";
    // the buffer we have currently
    private StringBuilder internal = new StringBuilder();
    // computed symbol length, assume accuracy
    private int length = 0;
    // multiple offsets which have to be parsed
    private int pending_offset = 0;
    // offset to enforce before compiling
    private int final_length = -1;

    public MagicStringBuilder() {
    }

    /**
     * The "final length" refers to the length we are expected
     * to have, this measurement is applied BEFORE compiling.
     *
     * @param final_length what length to eventually enforce.
     */
    public void setFinalLength(int final_length) {
        this.final_length = final_length;
    }

    /**
     * Change the font we are operating with.
     *
     * @param font updated font we work with
     */
    public MagicStringBuilder font(String font) {
        // do not update with constant font
        if (this.charset.equals(font))
            return this;
        // fracture the message from this point
        split();
        // purge our internal retrieval
        this.internal = new StringBuilder();
        this.charset = font;
        return this;
    }

    /**
     * Append text into the given workspace
     *
     * @param text the text to be rendered
     */
    public MagicStringBuilder append(String text) {
        if (text.isEmpty()) return this;
        int length = Utility.measureWidthExact(text);
        flush();
        this.internal.append(text);
        this.pending_offset -= 1;
        this.length += length;
        return this;
    }

    /**
     * Append text into the given workspace
     *
     * @param text  the text to be rendered
     * @param color dyes the appended component
     */
    public MagicStringBuilder append(String text, ChatColor color) {
        if (text.isEmpty()) return this;
        int length = Utility.measureWidthExact(text);
        split();
        this.internal.append(text);
        this.pending_offset -= 1;
        this.length += length;
        split();
        BaseComponent component = this.compiled.get(this.compiled.size() - 1);
        component.setColor(color);
        return this;
    }

    /**
     * Append text which is shifted to a different page
     * on the character table, do note that the text is
     * going to break if it uses symbols from multiple
     * character tables
     *
     * @param text   the text to be rendered
     * @param offset which page to offset into
     */
    public MagicStringBuilder append(String text, String offset) {
        int length = Utility.measureWidthExact(text);
        // flush the offset that lays on our buffer
        flush();
        // make sure we can retreat to our page
        String current_font = this.charset;
        // insert the text we are awaiting here
        font("generated_text_" + offset);
        this.internal.append(text);
        this.pending_offset -= 1;
        this.length += length;
        // retreat to our preceding font
        if (!current_font.equals(this.charset))
            font(current_font);
        return this;
    }

    /**
     * Append text which is shifted to a different page
     * on the character table, do note that the text is
     * going to break if it uses symbols from multiple
     * character tables
     *
     * @param text   the text to be rendered
     * @param offset which page to offset into
     */
    public MagicStringBuilder shadow(String text, String offset) {
        // we need to shade it twice so track the position
        int position = this.length;
        text = COLOR_STRIP.matcher(text).replaceAll("");
        return append(text, offset + "_shadow", ChatColor.BLACK)
                .shiftToExact(position)
                .append(text, offset, ChatColor.BLACK)
                .shiftToExact(position);
    }

    /**
     * Append text which is shifted to a different page
     * on the character table, do note that the text is
     * going to break if it uses symbols from multiple
     * character tables
     *
     * @param text   the text to be rendered
     * @param offset which page to offset into
     * @param color  dyes the appended component
     */
    public MagicStringBuilder append(String text, String offset, ChatColor color) {
        // measure the length of the text to append
        int length = Utility.measureWidthExact(text);
        // flush the offset that lays on our buffer
        split();
        // make sure we can retreat to our page
        String current_font = this.charset;
        // insert the text we are awaiting here
        font("generated_text_" + offset);
        this.internal.append(text);
        this.pending_offset -= 1;
        this.length += length;
        // compile and dye the component
        split();
        BaseComponent component = this.compiled.get(this.compiled.size() - 1);
        component.setColor(color);
        // retreat to our preceding font
        if (!current_font.equals(this.charset))
            font(current_font);
        return this;
    }

    /**
     * Append a pre-generated symbol provided by the resourcepack,
     * this may shift the font we work with. An
     *
     * @param text the symbol to be appended
     */
    public MagicStringBuilder append(IndexedTexture text) {
        // flush the offset that lays on our buffer
        flush();
        // make sure we can retreat to our page
        String current_font = this.charset;
        // insert our requested component
        font(text.table);
        this.internal.append(text.symbol);
        this.pending_offset -= 1;
        this.length += text.width;
        // retreat to our preceding font
        if (!current_font.equals(this.charset))
            font(current_font);
        return this;
    }

    /**
     * Append a pre-generated symbol provided by the resourcepack,
     * this may shift the font we work with. An
     *
     * @param text  the symbol to be appended
     * @param color dyes the appended component
     */
    public MagicStringBuilder append(IndexedTexture text, ChatColor color) {
        // flush the offset that lays on our buffer
        split();
        // make sure we can retreat to our page
        String current_font = this.charset;
        // insert our requested component
        font(text.table);
        this.internal.append(text.symbol);
        this.pending_offset -= 1;
        this.length += text.width;
        // compile and dye the component
        split();
        BaseComponent component = this.compiled.get(this.compiled.size() - 1);
        component.setColor(color);
        // retreat to our preceding font
        if (!current_font.equals(this.charset))
            font(current_font);
        return this;
    }

    /**
     * Retreat the pointer by a given length.
     *
     * @param length the length to retreat by.
     */
    public MagicStringBuilder retreat(int length) {
        if (length == 0) return this;
        this.pending_offset -= length;
        this.length -= length;
        return this;
    }

    /**
     * Advance the pointer by a given length.
     *
     * @param length the length to advance by.
     */
    public MagicStringBuilder advance(int length) {
        if (length == 0) return this;
        this.pending_offset += length;
        this.length += length;
        return this;
    }

    /**
     * Shift the pointer to an exact position.
     *
     * @param position pointer will face exact pixel.
     */
    public MagicStringBuilder shiftToExact(int position) {
        retreat(length);
        advance(position);
        return this;
    }

    /**
     * Shift in a way that if the next element added has the
     * same length as specified it'll center accordingly.
     *
     * @param center where is our centered point
     * @param length how wide the given string is
     */
    public MagicStringBuilder shiftCentered(int center, int length) {
        shiftToExact(center);
        retreat(length / 2);
        return this;
    }

    /**
     * Invoke a flush before creating a copy of the base components which
     * are backed by the string builder.
     *
     * @return array of base components
     */
    public BaseComponent[] compile() {
        // apply the final shift if necessary
        if (this.final_length >= 0)
            shiftToExact(this.final_length);
        // correct the accumulated padding
        split();
        // thorough-put the computed information
        return this.compiled.toArray(new BaseComponent[0]);
    }

    /**
     * Compile this instance, and clear off any data present on
     * this builder.
     *
     * @return the builder that was compiled.
     */
    public BaseComponent[] compileAndClean() {
        // compile the result we are going to hand out
        BaseComponent[] compiled = compile();
        // clear out any data we have
        this.compiled = new ArrayList<>();
        this.charset = "default";
        this.internal = new StringBuilder();
        this.length = 0;
        this.pending_offset = 0;
        this.final_length = -1;
        // offer up the previously compiled message
        return compiled;
    }

    /*
     * Flushes the internal buffer of letters that were built, it
     * collects {@link BaseComponent} instances with relevant font
     * markings.
     */
    private void flush() {
        // correct the padding we have
        if (pending_offset < 0) {
            this.internal.append(FontMagicConstant.retreat((-1) * pending_offset));
            pending_offset = 0;
        } else if (pending_offset > 0) {
            this.internal.append(FontMagicConstant.advance(pending_offset));
            pending_offset = 0;
        }
    }

    /*
     * Force a split, main use is to prevent the color being
     * carried over.
     */
    private MagicStringBuilder split() {
        flush();
        BaseComponent[] components = TextComponent.fromLegacyText(this.internal.toString());
        for (BaseComponent component : components) {
            component.setFont(this.charset);
            this.compiled.add(component);
        }
        this.internal = new StringBuilder();
        return this;
    }
}
