/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.pdf.canvas.parser;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Category(IntegrationTest.class)
public class GlyphBboxCalculationTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/canvas/parser/GlyphBboxCalculationTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/canvas/parser/GlyphBboxCalculationTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void checkBboxCalculationForType3FontsWithFontMatrix01() throws IOException {
        String inputPdf = sourceFolder + "checkBboxCalculationForType3FontsWithFontMatrix01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputPdf));
        CharacterPositionEventListener listener = new CharacterPositionEventListener();
        PdfCanvasProcessor processor = new PdfCanvasProcessor(listener);
        processor.processPageContent(pdfDocument.getPage(1));
        // font size (36) * |fontMatrix| (0.001) * glyph width (600) = 21.6
        Assert.assertEquals(21.6, listener.glyphWidth, 1e-5);
    }

    @Test
    public void checkBboxCalculationForType3FontsWithFontMatrix02() throws IOException {
        String inputPdf = sourceFolder + "checkBboxCalculationForType3FontsWithFontMatrix02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputPdf));
        CharacterPositionEventListener listener = new CharacterPositionEventListener();
        PdfCanvasProcessor processor = new PdfCanvasProcessor(listener);
        processor.processPageContent(pdfDocument.getPage(1));
        // font size (36) * |fontMatrix| (1) * glyph width (0.6) = 21.6
        Assert.assertEquals(21.6, listener.glyphWidth, 1e-5);
    }

    @Test
    public void checkAverageBboxCalculationForType3FontsWithFontMatrix01Test() throws IOException {
        String inputPdf = sourceFolder + "checkAverageBboxCalculationForType3FontsWithFontMatrix01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputPdf));
        CharacterPositionEventListener listener = new CharacterPositionEventListener();
        PdfCanvasProcessor processor = new PdfCanvasProcessor(listener);
        processor.processPageContent(pdfDocument.getPage(1));
        Assert.assertEquals(600, listener.firstTextRenderInfo.getFont().getFontProgram().getAvgWidth(), 0.01f);
    }

    @Test
    public void type3FontsWithIdentityFontMatrixAndMultiplier() throws IOException, InterruptedException {
        String inputPdf = sourceFolder + "type3FontsWithIdentityFontMatrixAndMultiplier.pdf";
        String outputPdf = destinationFolder +  "type3FontsWithIdentityFontMatrixAndMultiplier.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputPdf), new PdfWriter(outputPdf));
        CharacterPositionEventListener listener = new CharacterPositionEventListener();
        PdfCanvasProcessor processor = new PdfCanvasProcessor(listener);
        processor.processPageContent(pdfDocument.getPage(1));

        PdfPage page = pdfDocument.getPage(1);
        Rectangle pageSize = page.getPageSize();
        PdfCanvas pdfCanvas = new PdfCanvas(page);

        pdfCanvas.beginText().setFontAndSize(processor.getGraphicsState().getFont(), processor.getGraphicsState().getFontSize())
                .moveText(pageSize.getWidth() / 2 - 24, pageSize.getHeight() / 2)
                .showText("A")
                .endText();

        pdfDocument.close();
        Assert.assertNull(new CompareTool().compareByContent(outputPdf, sourceFolder + "cmp_type3FontsWithIdentityFontMatrixAndMultiplier.pdf", destinationFolder, "diff_"));
    }

    private static class CharacterPositionEventListener implements ITextExtractionStrategy {
        float glyphWidth;
        TextRenderInfo firstTextRenderInfo;

        @Override
        public String getResultantText() {
            return null;
        }

        @Override
        public void eventOccurred(IEventData data, EventType type) {
            if (type.equals(EventType.RENDER_TEXT)) {
                TextRenderInfo renderInfo = (TextRenderInfo) data;
                if (firstTextRenderInfo == null) {
                    firstTextRenderInfo = renderInfo;
                    firstTextRenderInfo.preserveGraphicsState();
                }
                List<TextRenderInfo> subs = renderInfo.getCharacterRenderInfos();
                for (int i = 0; i < subs.size(); i++) {
                    TextRenderInfo charInfo = subs.get(i);
                    glyphWidth = charInfo.getBaseline().getLength();
                }
            }
        }

        @Override
        public Set<EventType> getSupportedEvents() {
            return new LinkedHashSet<>(Collections.singletonList(EventType.RENDER_TEXT));
        }
    }

}
