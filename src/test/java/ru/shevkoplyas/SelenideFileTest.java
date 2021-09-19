package ru.shevkoplyas;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.pdftest.assertj.Assertions.assertThat;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class SelenideFileTest {

    @Test
    void uploadTxtTest() {
        open("https://the-internet.herokuapp.com/upload");
        $("input[type='file']").uploadFromClasspath("sample1.txt");
        $("#file-submit").click();
        $("#uploaded-files")
                .shouldHave(text("sample1.txt"));

    }

    /*
            @Test
            void ReadTxtTest() throws Exception {
                String result;
                try (InputStream is = getClass().getClassLoader().getResourceAsStream("sample1.txt")) {
                    result = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                }
                assertThat(result).contains("Utilitatis causa amicitia est quaesita");
            }
    */
    @Test
    void readPdfTest() throws Exception {
        PDF parsed = new PDF(getClass().getClassLoader().getResourceAsStream("sample2.pdf"));
        assertThat(parsed.text).contains("Lorem ipsum");
        assertThat(parsed.numberOfPages).isEqualTo(4);
    }

    @Test
    void readExcelTest() throws Exception {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("sample3.xlsx")) {
            XLS parsed = new XLS(stream);
            assertThat(parsed.excel.getSheetAt(1).getRow(1).getCell(0).getStringCellValue())
                    .isEqualTo("Ivanov");
        }
    }

    @Test
    void readDocTest() throws Exception {
        try (InputStream file = getClass().getClassLoader().getResourceAsStream("sample4.docx")) {
            XWPFDocument document = new XWPFDocument(file);
            StringBuilder text = new StringBuilder();
            List<XWPFParagraph> paragraphs = document.getParagraphs();

            for (XWPFParagraph para : paragraphs) {
                text.append(para.getText());
            }
            assertThat(text.toString().contains("Example text for a test."));
        }
    }

    @Test
    void readDoc2Test() throws Exception {
        try (InputStream file = getClass().getClassLoader().getResourceAsStream("sample4.docx")) {
            XWPFDocument document = new XWPFDocument(file);
            XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(document);
            String docText = xwpfWordExtractor.getText();
            assertThat(docText.contains("Example text for a test."));
        }

    }

    @Test
    void zipFileTest() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("sample1.zip")) {
            ZipInputStream zis = new ZipInputStream(is);
            ZipEntry entry;
            Scanner sc = new Scanner(zis);
            while (sc.hasNext()) {
                System.out.println(sc.nextLine());
            }

//        String zipFilePath = "src/test/resources/sample1.zip";
//        String unzipFilePath = "src/test/resources/unzipped";
//        String zipPassword = "1234";
        }

    }
}




