package com.api.GestionaFacilRestaurants.services;

import java.io.OutputStream;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import com.api.GestionaFacilRestaurants.models.Business;
import com.api.GestionaFacilRestaurants.models.CashRegister;
import com.api.GestionaFacilRestaurants.models.CashRegisterOpening;
import com.api.GestionaFacilRestaurants.models.CashRegisterOpeningItems;
import com.api.GestionaFacilRestaurants.models.DocumentPayIssued;
import com.api.GestionaFacilRestaurants.repositories.BusinessRepository;
import com.api.GestionaFacilRestaurants.repositories.CashRegisterOpeningItemsRepository;
import com.api.GestionaFacilRestaurants.repositories.CashRegisterOpeningRepository;
import com.api.GestionaFacilRestaurants.repositories.CashRegisterRepository;
import com.api.GestionaFacilRestaurants.repositories.DocumentPayIssuedRepository;
import com.api.GestionaFacilRestaurants.utilities.ApiUtil;
import com.api.GestionaFacilRestaurants.utilities.DateTimeUtil;
import com.api.GestionaFacilRestaurants.utilities.KeyUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class ReportService {
   
   @Value("${api.host}")
    private String host;

    @Value("${api.base.path}")
    private String apiBasePath;

    @Autowired
    private CashRegisterOpeningRepository cashRegisterOpeningRepository;

    @Autowired 
    private CashRegisterOpeningItemsRepository cashRegisterOpeningItemsRepository;

    @Autowired 
    private DocumentPayIssuedRepository documentPayIssuedRepository;

    @Autowired  
    private CashRegisterRepository cashRegisterRepository;

    @Autowired
    private BusinessRepository businessRepository;

    @SuppressWarnings("null")
    public void getCashOpening(String id,String fileName,HttpServletResponse response){
        try{
            Long cashOpeningId = Long.parseLong(KeyUtil.decrypt(id));
            CashRegisterOpening cashRegisterOpeningFinded = cashRegisterOpeningRepository.findById(cashOpeningId).orElse(null);
            if(cashRegisterOpeningFinded==null){
                throw new RuntimeException("Caja no encontrada");
            }
            
            CashRegister cashRegisterFinded = cashRegisterRepository.findById(cashRegisterOpeningFinded.getCashRegisterId()).orElse(null);
            if(cashRegisterFinded==null){
                throw new RuntimeException("Caja no encontrada");
            }

            Business businessFinded = businessRepository.findById(cashRegisterFinded.getBusinessId()).orElse(null);
            if(businessFinded==null){
                throw new RuntimeException("Establecimiento no encontrada");
            }

            // Configuración del documento
            Document document = new Document(new Rectangle(170, 800)); // Ancho de 58mm
            document.setMargins(5, 5, 5, 10); // Márgenes reducidos
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=" + fileName+".pdf");
            OutputStream outputStream = response.getOutputStream();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            createHeaderWithStyledTitle(document, "CIERRE DE LOTE", 18, 30f);
        
            // Datos del negocio
            String businessType = businessFinded.getType().getDenominationEs().toUpperCase().toString();
            String businessName = businessFinded.getName().toUpperCase();
            String ruc = businessFinded.getRuc().toString();
            String address = businessFinded.getAddress().toUpperCase();

            // **Encabezado**
            addCenteredParagraph(document, businessType+" "+businessName, 11, Font.BOLD);
            addCenteredParagraph(document, "RUC " + ruc, 7, Font.BOLD);
            addCenteredParagraph(document, address, 6, Font.NORMAL, new BaseColor(33, 47, 61));

            PdfPTable mainInfoTable = new PdfPTable(2);
            mainInfoTable.setWidthPercentage(100);
            mainInfoTable.setSpacingBefore(5f);
            mainInfoTable.setWidths(new int[]{50, 50});

            mainInfoTable.addCell(createCell("LOTE:", PdfPCell.ALIGN_LEFT,true,8));
            mainInfoTable.addCell(createCell(ApiUtil.lzeros(cashRegisterOpeningFinded.getId(), 6), PdfPCell.ALIGN_LEFT,true, new BaseColor(33, 47, 61),8));
            mainInfoTable.addCell(createCell("F. CIERRE:", PdfPCell.ALIGN_LEFT,true,8));
            mainInfoTable.addCell(createCell(DateTimeUtil.convertUtcToLima(cashRegisterOpeningFinded.getClosingDate().toString()), PdfPCell.ALIGN_LEFT,false, new BaseColor(33, 47, 61),8));
            mainInfoTable.addCell(createCell("F. IMPRESION:", PdfPCell.ALIGN_LEFT,true,8));
            mainInfoTable.addCell(createCell(DateTimeUtil.getDateTimeTodayInLima(), PdfPCell.ALIGN_LEFT,false, new BaseColor(33, 47, 61),8));
            document.add(mainInfoTable);

            // Separador
            addWhiteSpace(document, 2f);

            List<CashRegisterOpeningItems> items = cashRegisterOpeningItemsRepository.findAllByCashRegisterOpeningId(cashOpeningId);
             // Detalle
            Double totalIn = 0.0;
            if(items.size()>0){
                
                PdfPTable table = new PdfPTable(4);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{25, 25, 25, 25});

                // Encabezados
                table.addCell(createStyledTitleCell("RESUMEN - ENTRADAS", 11, 4,22f));
                
                table.addCell(createCell("REF", Element.ALIGN_CENTER, true,7));
                table.addCell(createCell("DETALLE", Element.ALIGN_CENTER, true,7));
                table.addCell(createCell("TIPO", Element.ALIGN_CENTER, true,7));
                table.addCell(createCell("MONTO", Element.ALIGN_CENTER, true,7));
                for (CashRegisterOpeningItems item : items) {
                    if(item.getType().equals("input")){
                        totalIn += item.getAmount();
                        String ref = "#";
                        String type ="Efectivo";
                        if(item.getMetadata()!=null){
                            MultiValueMap<String, String> metadata = ApiUtil.parseMetadata(item.getMetadata());
                            String referenciaIdString = metadata.getFirst("referencia_id"); // Obtén el primer valor de "referencia_id"

                            Long referenciaId = referenciaIdString != null ? Long.parseLong(referenciaIdString) : null;
                            ref = ApiUtil.lzeros(referenciaId, 6);

                            String referenciaModeloString = "";
                            List<String> referenciaModeloValues = metadata.get("referencia_modelo");
                            // Si la lista no es nula y no está vacía, selecciona un valor
                            if (referenciaModeloValues != null && !referenciaModeloValues.isEmpty()) {
                                // Selecciona el primer valor no nulo (o aplica otra lógica)
                                for (String value : referenciaModeloValues) {
                                    if (value != null) {
                                        referenciaModeloString = value;
                                    }
                                }
                            }
                            
                            if(referenciaModeloString.equals("DocumentPayIssued")){
                                DocumentPayIssued documentPayIssuedFinded = documentPayIssuedRepository.findById(referenciaId).orElse(null);
                                if(documentPayIssuedFinded!=null){
                                    type = ApiUtil.capitalizeEachWord(documentPayIssuedFinded.getPaymentMethod().getDenominationEs());
                                }
                            }
                            
                        }
                        table.addCell(createCell(ref, Element.ALIGN_CENTER, true,8));
                        table.addCell(createCell(ApiUtil.capitalizeEachWord(item.getItemDescription()), Element.ALIGN_LEFT, false,8));
                        table.addCell(createCell(type, Element.ALIGN_LEFT, true,8));
                        table.addCell(createCell("S/"+ApiUtil.formatToTwoDecimals(item.getAmount()).toString(), Element.ALIGN_RIGHT, false,7));
                    }
                }
                document.add(table);
            }
            
            // Separador
            addDashedSeparator(document, 0f);

            // **Totales**
            PdfPTable totalsTable = new PdfPTable(2);
            totalsTable.setWidthPercentage(100);
            totalsTable.setSpacingBefore(5f);
            totalsTable.setWidths(new int[]{50, 50});

            totalsTable.addCell(createCellH1("TOTAL", PdfPCell.ALIGN_RIGHT, true));
            totalsTable.addCell(createCellH1(String.format("S/. %.2f", totalIn), PdfPCell.ALIGN_RIGHT, true));
            document.add(totalsTable);


            // Cerrar documento
            document.close();
            
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar el PDF: " + e.getMessage());
        }
    }

    private void addCenteredParagraph(Document document, String text, int fontSize, int fontStyle) throws DocumentException {
        addCenteredParagraph(document, text, fontSize, fontStyle, BaseColor.BLACK);
    }

    private static PdfPCell createStyledTitleCell(String title, int fontSize, int colspan, float cellHeight) {
        // Crear la fuente con color blanco y negrita
        Font font = new Font(Font.FontFamily.HELVETICA, fontSize, Font.BOLD, BaseColor.WHITE);
        
        // Crear la celda con el título
        PdfPCell titleCell = new PdfPCell(new Phrase(title, font));
        
        // Centrar el texto horizontalmente
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        // Establecer el fondo negro
        titleCell.setBackgroundColor(BaseColor.BLACK);
        
        // Agregar bordes alrededor de la celda
        titleCell.setBorder(Rectangle.BOX);
        
        // Establecer el colspan (número de columnas que ocupará la celda)
        titleCell.setColspan(colspan);
        
        // Establecer la altura de la celda
        titleCell.setFixedHeight(cellHeight);
        
        return titleCell;
    }

    private void addCenteredParagraph(Document document, String text, int fontSize, int fontStyle, BaseColor color) throws DocumentException {
        Font font = new Font(Font.FontFamily.HELVETICA, fontSize, fontStyle, color);
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);
    }

    private static PdfPCell createCell(String content, int alignment, boolean bold,int size) {
        Font font = new Font(Font.FontFamily.HELVETICA, size, bold ? Font.BOLD : Font.NORMAL);
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    private static PdfPCell createCell(String content, int alignment, boolean bold) {
        return createCell(content, alignment, bold, 9); // Llama al método principal con size = 9
    }

    private static PdfPCell createCell(String content, int alignment, boolean bold, BaseColor color,int size) {
        Font font = new Font(Font.FontFamily.HELVETICA, size, bold ? Font.BOLD : Font.NORMAL, color);
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

    private static void addWhiteSpace(Document document, float marginTop) throws DocumentException {
        // Crear un párrafo vacío
        Paragraph whiteSpace = new Paragraph(" "); // Espacio en blanco
        whiteSpace.setSpacingBefore(marginTop); // Establecer el margen superior
        document.add(whiteSpace); // Agregar al documento
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
    private void createHeaderWithStyledTitle(Document document, String title, int fontSize, float cellHeight) throws DocumentException {
        // Crear una tabla con una sola celda
        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100); // Ancho relativo al documento
        header.setWidths(new int[]{1}); // Una sola columna
    
        // Agregar la celda con el título estilizado
        header.addCell(createStyledTitleCell(title, fontSize, 1, cellHeight));
    
        // Agregar la tabla al documento
        document.add(header);
    }
}
