package fr.pentagon.ugeoverflow.model.embed;

import jakarta.persistence.Embeddable;

@Embeddable
public class CodePart {
    private int lineStart;
    private int lineEnd;


    public CodePart() {}
    public CodePart(int lineStart, int lineEnd) {
        this.lineStart = lineStart;
        this.lineEnd = lineEnd;
    }

    public int getLineEnd() {
        return lineEnd;
    }

    public int getLineStart() {
        return lineStart;
    }

    public void setLineEnd(int lineEnd) {
        this.lineEnd = lineEnd;
    }

    public void setLineStart(int lineStart) {
        this.lineStart = lineStart;
    }
}
