package fr.pentagon.ugeoverflow.model.embed;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;

@Embeddable
public class TestKit {
    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] testFile;
    private String testFileName;

    public TestKit(byte[] testFile, String testResult) {
        this.testFile = testFile;
        this.testResult = testResult;
    }
    private String testResult;
    public TestKit() {}

    public byte[] getTestFile() {
        return testFile;
    }

    public String getTestResult() {
        return testResult;
    }

    public void setTestFile(byte[] testFile) {
        this.testFile = testFile;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }

    public String getTestFileName() {
        return testFileName;
    }

    public void setTestFileName(String testFileName) {
        this.testFileName = testFileName;
    }
}
