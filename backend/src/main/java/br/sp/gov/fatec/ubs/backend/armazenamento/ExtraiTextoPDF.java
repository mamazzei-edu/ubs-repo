package br.sp.gov.fatec.ubs.backend.armazenamento;

import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;


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

    }
    
}
