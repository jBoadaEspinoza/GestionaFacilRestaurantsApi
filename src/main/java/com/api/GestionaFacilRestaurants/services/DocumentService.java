package com.api.GestionaFacilRestaurants.services;

import java.io.OutputStream;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import com.api.GestionaFacilRestaurants.models.DocumentPayIssued;
import com.api.GestionaFacilRestaurants.models.OrderDetail;
import com.api.GestionaFacilRestaurants.models.Person;
import com.api.GestionaFacilRestaurants.models.TableView;
import com.api.GestionaFacilRestaurants.repositories.DocumentPayIssuedRepository;
import com.api.GestionaFacilRestaurants.repositories.OrderDetailRepository;
import com.api.GestionaFacilRestaurants.repositories.TableViewRepository;
import com.api.GestionaFacilRestaurants.utilities.ApiUtil;
import com.api.GestionaFacilRestaurants.utilities.DateTimeUtil;
import com.api.GestionaFacilRestaurants.utilities.KeyUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class DocumentService {
   
    @Autowired
    private DocumentPayIssuedRepository documentPayIssuedRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private TableViewRepository tableViewRepository;

    @SuppressWarnings("null")
    public void getPDF(String id, String fileName, HttpServletResponse response) {
        try {
            Long documentPayIssuedId = Long.parseLong(KeyUtil.decrypt(id)); // Asume que el ID ya está desencriptado
            Optional<DocumentPayIssued> optionalDocumentIssued = documentPayIssuedRepository.findById(documentPayIssuedId);

            if (optionalDocumentIssued.isEmpty()) {
                throw new RuntimeException("No se encontró el documento con el ID proporcionado.");
            }

            DocumentPayIssued documentIssued = optionalDocumentIssued.get();

            // Configuración del documento
            Document document = new Document(new Rectangle(170, 800)); // Ancho de 58mm
            document.setMargins(5, 5, 5, 10); // Márgenes reducidos
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=" + fileName+".pdf");
            OutputStream outputStream = response.getOutputStream();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Separador
            addDashedSeparator2(document, 0f);

            // Datos del negocio
            String businessType = documentIssued.getOrder().getBusiness().getType().getDenominationEs().toUpperCase().toString();
            String businessName = documentIssued.getOrder().getBusiness().getName().toUpperCase();
            String ruc = documentIssued.getOrder().getBusiness().getRuc().toString();
            String address = documentIssued.getOrder().getBusiness().getAddress().toUpperCase();

            // **1. Encabezado**
            addCenteredParagraph(document, businessType+" "+businessName, 9, Font.BOLD);
            addCenteredParagraph(document, "RUC " + ruc, 9, Font.BOLD);
            addCenteredParagraph(document, address, 9, Font.NORMAL, new BaseColor(33, 47, 61));

            // Separador
            addDashedSeparator2(document, 0f);

            addCenteredParagraph(document, documentIssued.getDocumentPaySerie().getDocumentPayType().getDenomination().toUpperCase(), 9, Font.BOLD);
            addCenteredParagraph(document, documentIssued.getDocumentPaySerie().getNumbering()+" - "+ApiUtil.lzeros(documentIssued.getNumbering(), 8), 9, Font.BOLD);

            Person customer = documentIssued.getCustomer();

            PdfPTable mainInfoTable = new PdfPTable(2);
            mainInfoTable.setWidthPercentage(100);
            mainInfoTable.setSpacingBefore(5f);
            mainInfoTable.setWidths(new int[]{40, 60});

            MultiValueMap<String, String> metadata = ApiUtil.parseMetadata(documentIssued.getOrder().getMetadata());
            String tableIdString = metadata.getFirst("mesa_id"); // Obtén el primer valor de "mesa_id"
            Long tableId = tableIdString != null ? Long.parseLong(tableIdString) : null;
            TableView table = tableViewRepository.findById(tableId).orElse(null);

            if(customer!=null){
                mainInfoTable.addCell(createCell("RAZON SOCIAL:", PdfPCell.ALIGN_RIGHT,true));
                mainInfoTable.addCell(createCell(customer!=null ? (customer.getPersonalDocumentType().getId()==2 ? customer.getRazonSocial() : customer.getLastnames()+" "+customer.getSurnames()) : "", PdfPCell.ALIGN_LEFT,false, new BaseColor(33, 47, 61)));
                mainInfoTable.addCell(createCell("DOC:", PdfPCell.ALIGN_RIGHT,true));
                mainInfoTable.addCell(createCell(customer!=null ? customer.getPersonalDocumentType().getDenominationShort().toUpperCase()+customer.getDocumentNumber() : "", PdfPCell.ALIGN_LEFT,false, new BaseColor(33, 47, 61)));
            }
            
            mainInfoTable.addCell(createCell("EMISIÓN:", PdfPCell.ALIGN_RIGHT,true));
            mainInfoTable.addCell(createCell(DateTimeUtil.convertUtcToLima(documentIssued.getIssueDate()), PdfPCell.ALIGN_LEFT,false, new BaseColor(33, 47, 61)));
            mainInfoTable.addCell(createCell("PAGO:", PdfPCell.ALIGN_RIGHT,true));
            mainInfoTable.addCell(createCell(documentIssued.getPaymentMethod().getDenominationEs().toUpperCase(), PdfPCell.ALIGN_LEFT,false, new BaseColor(33, 47, 61)));
            mainInfoTable.addCell(createCell("REF:", PdfPCell.ALIGN_RIGHT,true));
            mainInfoTable.addCell(createCell(table.getDenomination().toUpperCase(), PdfPCell.ALIGN_LEFT,false, new BaseColor(33, 47, 61)));
            document.add(mainInfoTable);

            // Separador
            addDashedSeparator(document, 0f);

            // **3. Detalle del pedido**
            PdfPTable detailTable = new PdfPTable(3);
            detailTable.setWidthPercentage(100);
            detailTable.setSpacingBefore(5f);
            detailTable.setWidths(new int[]{20, 50, 30});

            detailTable.addCell(createCell("CANT", PdfPCell.ALIGN_CENTER, true));
            detailTable.addCell(createCell("DESCRIPCIÓN", PdfPCell.ALIGN_CENTER, true));
            detailTable.addCell(createCell("IMPORTE", PdfPCell.ALIGN_CENTER, true));

            List<OrderDetail> details = orderDetailRepository.findAllByOrderId(documentIssued.getOrder().getId());

            // Añade dinámicamente los detalles del pedido
            AtomicReference<Double> total = new AtomicReference<>(0.00);
            details.forEach(detail -> {
                Double importe = detail.getUnitPricePen()*detail.getQuantity();
                total.updateAndGet(v -> v + importe);
                detailTable.addCell(createCell(String.valueOf(detail.getQuantity()), PdfPCell.ALIGN_CENTER, false,new BaseColor(33, 47, 61)));
                detailTable.addCell(createCell(ApiUtil.capitalizeEachWord(detail.getMenuItem().getDenomination()+" - "+detail.getMenuItem().getPresentation().getDenomination()), PdfPCell.ALIGN_LEFT,false, new BaseColor(33, 47, 61)));
                detailTable.addCell(createCell(String.format("%.2f", importe), PdfPCell.ALIGN_RIGHT,false, new BaseColor(33, 47, 61)));
            });

            document.add(detailTable);

            // Separador
            addDashedSeparator(document, 0f);

            // **4. Totales**
            PdfPTable totalsTable = new PdfPTable(2);
            totalsTable.setWidthPercentage(100);
            totalsTable.setSpacingBefore(5f);
            totalsTable.setWidths(new int[]{50, 50});

            totalsTable.addCell(createCellH1("TOTAL A PAGAR", PdfPCell.ALIGN_RIGHT, true));
            totalsTable.addCell(createCellH1(String.format("S/. %.2f", total.get()), PdfPCell.ALIGN_RIGHT, true));
            document.add(totalsTable);

            // **5. Pie de página**
            detailTable.setSpacingBefore(5f);
            addCenteredParagraph(document, "Agradecemos su preferencia. Ha sido un placer atenderle. ¡Esperamos verle nuevamente!", 9, Font.NORMAL, new BaseColor(33, 47, 61));

            // Cerrar documento
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar el PDF: " + e.getMessage());
        }
    }

    private void addCenteredParagraph(Document document, String text, int fontSize, int fontStyle) throws DocumentException {
        addCenteredParagraph(document, text, fontSize, fontStyle, BaseColor.BLACK);
    }

    private void addCenteredParagraph(Document document, String text, int fontSize, int fontStyle, BaseColor color) throws DocumentException {
        Font font = new Font(Font.FontFamily.HELVETICA, fontSize, fontStyle, color);
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);
    }

    private static PdfPCell createCell(String content, int alignment, boolean bold) {
        Font font = new Font(Font.FontFamily.HELVETICA, 9, bold ? Font.BOLD : Font.NORMAL);
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    private static PdfPCell createCell(String content, int alignment, boolean bold, BaseColor color) {
        Font font = new Font(Font.FontFamily.HELVETICA, 8, bold ? Font.BOLD : Font.NORMAL, color);
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    private static PdfPCell createCellH1(String content, int alignment, boolean bold) {
        Font font = new Font(Font.FontFamily.HELVETICA, 14, bold ? Font.BOLD : Font.NORMAL);
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    private static void addDashedSeparator(Document document, float marginTop) throws DocumentException {
        StringBuilder separator = new StringBuilder();
        for (int i = 0; i < 60; i++) { // Ajustar según el ancho
            separator.append("-");
        }
        Paragraph dashedSeparator = new Paragraph(separator.toString(), new Font(Font.FontFamily.HELVETICA, 8));
        dashedSeparator.setAlignment(Element.ALIGN_CENTER);
        dashedSeparator.setSpacingBefore(marginTop);
        document.add(dashedSeparator);
    }
    private static void addDashedSeparator2(Document document, float marginTop) throws DocumentException {
        StringBuilder separator = new StringBuilder();
        for (int i = 0; i < 34; i++) { // Ajustar según el ancho
            separator.append("=");
        }

        // Crear la fuente con tamaño y estilo
        Font font = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
        font.setColor(0, 0, 0); // Establecer el color de la fuente (gris claro)

        // Crear el párrafo con el separador
        Paragraph dashedSeparator = new Paragraph(separator.toString(), font);
        dashedSeparator.setAlignment(Element.ALIGN_CENTER); // Centrar el texto
        dashedSeparator.setSpacingBefore(marginTop); // Establecer el margen superior
        document.add(dashedSeparator); // Agregar al documento
        
    }
    
}
