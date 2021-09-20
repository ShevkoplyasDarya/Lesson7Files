package ru.shevkoplyas;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.Test;


import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.zip.ZipInputStream;

import net.lingala.zip4j.core.ZipFile;


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


    @Test
    void ReadTxtTest() throws Exception {
        String contentsTxt = "Utilitatis causa amicitia est quaesita.";
        String fileTxt = "sample1.txt";
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileTxt)) {
            Scanner sc = new Scanner(Objects.requireNonNull(is)).useDelimiter("\\A");
            String text = sc.hasNext() ? sc.next() : "";
            assertThat(text).contains(contentsTxt);
        }
    }

    @Test
    void readPdfTest() throws Exception {
        String pdfFile = "sample2.pdf";
        String contentsPdf = "Lorem ipsum";
        PDF parsed = new PDF(getClass().getClassLoader().getResourceAsStream(pdfFile));
        assertThat(parsed.text).contains(contentsPdf);
        assertThat(parsed.numberOfPages).isEqualTo(4);
    }

    @Test
    void readExcelTest() throws Exception {
        String excelFile = "sample3.xlsx";
        String contentsExcel = "Ivanov";
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(excelFile)) {
            XLS parsed = new XLS(stream);
            assertThat(parsed.excel.getSheetAt(1).getRow(1).getCell(0).getStringCellValue())
                    .isEqualTo(contentsExcel);
        }
    }

    @Test
    void readDocTest() throws Exception {
        String docxFile = "sample4.docx";
        String contentsDocx = "Example text for a test.";
        try (InputStream file = getClass().getClassLoader().getResourceAsStream(docxFile)) {
            XWPFDocument document = new XWPFDocument(file);
            StringBuilder text = new StringBuilder();
            List<XWPFParagraph> paragraphs = document.getParagraphs();

            for (XWPFParagraph para : paragraphs) {
                text.append(para.getText());
            }
            assertThat(text.toString().contains(contentsDocx));
        }
    }

    @Test
    void readDoc2Test() throws Exception {
        String docxFile = "sample4.docx";
        String contentsDocx = "Example text for a test.";
        try (InputStream file = getClass().getClassLoader().getResourceAsStream(docxFile)) {
            XWPFDocument document = new XWPFDocument(file);
            XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(document);
            String docText = xwpfWordExtractor.getText();
            assertThat(docText.contains(contentsDocx));
        }

    }

    @Test
    void zipFileNoPasswordTest() throws Exception {
        String zipArchive = "samplenopass1.zip";
        String zipContents = "Utilitatis causa amicitia est quaesita";
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(zipArchive)) {
            ZipInputStream zis = new ZipInputStream(is);
            zis.getNextEntry();
            Scanner sc = new Scanner(zis);
            while (sc.hasNext()) {
                assertThat(sc.nextLine().contains(zipContents));
            }


        }
    }

    @Test
    void zipFileWithPasswordTest() throws Exception {
        try {
            String password = "1234";
            String zipContents = "Utilitatis causa amicitia est quaesita";
            String zipPath = "src/test/resources/samplepass1.zip";
            String unzippedPath = ".src/test/resources/unzipped/";
            ZipFile zipFile = new ZipFile(zipPath);
            if (zipFile.isEncrypted())
                zipFile.setPassword(password.toCharArray());
            zipFile.extractAll(unzippedPath);
            assertThat(zipFile.getFileHeaders().get(1).toString().contains(zipContents));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}





