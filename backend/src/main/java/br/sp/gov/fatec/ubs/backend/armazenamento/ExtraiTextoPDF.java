package br.sp.gov.fatec.ubs.backend.armazenamento;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.filter.TextRegionEventFilter;
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;

import org.apache.pdfbox.pdmodel.PDPage;


public class ExtraiTextoPDF {
    
    public static String extraiTextoPDF(String caminhoArquivo) {
        String texto = "Não retornou texto";
        // Extrai texto de um pdf utilizando pdfbox 
        try(PDDocument documento = Loader.loadPDF(new RandomAccessReadBufferedFile(caminhoArquivo))) {
                texto = new PDFTextStripper().getText(documento);                   
        } catch (Exception e) {
            e.printStackTrace();
        }
        return texto;
    }


    public static String extraiTextoPDFiText(String caminho) throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(caminho));

        // Create a text extraction renderer
        LocationTextExtractionStrategy strategy = new LocationTextExtractionStrategy();

        // Note: if you want to re-use the PdfCanvasProcessor, you must call PdfCanvasProcessor.reset()
        PdfCanvasProcessor parser = new PdfCanvasProcessor(strategy);
        parser.processPageContent(pdfDoc.getFirstPage());

        byte[] content = strategy.getResultantText().getBytes("UTF-8");
        pdfDoc.close();
        return new String(content, "UTF-8");
//        try (FileOutputStream stream = new FileOutputStream(dest)) {
//            stream.write(content);
//        }

    }

/* 
 protected class CustomFontFilter extends TextRegionEventFilter {
        public CustomFontFilter(Rectangle filterRect) {
            super(filterRect);
        }

        @Override
        public boolean accept(IEventData data, EventType type) {
            if (type.equals(EventType.RENDER_TEXT)) {
                TextRenderInfo renderInfo = (TextRenderInfo) data;
                PdfFont font = renderInfo.getFont();
                if (null != font) {
                    String fontName = font.getFontProgram().getFontNames().getFontName();
                    return fontName.endsWith("Bold") || fontName.endsWith("Oblique");
                }
            }

            return false;
        }
    }
*/
    // Também é possível utilizar um serviço como:
    // https://docs.aws.amazon.com/textract/latest/dg/how-it-works-detecting.html
    
}
