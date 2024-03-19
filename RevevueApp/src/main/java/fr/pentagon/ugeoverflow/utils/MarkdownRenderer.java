package fr.pentagon.ugeoverflow.utils;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import org.springframework.stereotype.Component;

@Component
public final class MarkdownRenderer {
    public String markdownToHtml(String markdownContent) {
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(parser.parse(markdownContent));
    }
}
