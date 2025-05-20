package com.api.GestionaFacilRestaurants.services;

import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import com.api.GestionaFacilRestaurants.models.Authorization;
import com.api.GestionaFacilRestaurants.models.Business;
import com.api.GestionaFacilRestaurants.models.Order;
import com.api.GestionaFacilRestaurants.models.OrderDetail;
import com.api.GestionaFacilRestaurants.models.TableView;
import com.api.GestionaFacilRestaurants.repositories.AuthRepository;
import com.api.GestionaFacilRestaurants.repositories.BusinessRepository;
import com.api.GestionaFacilRestaurants.repositories.OrderDetailRepository;
import com.api.GestionaFacilRestaurants.repositories.OrderRepository;
import com.api.GestionaFacilRestaurants.repositories.TableViewRepository;
import com.api.GestionaFacilRestaurants.responses.SuccessResponse;
import com.api.GestionaFacilRestaurants.utilities.ApiUtil;
import com.api.GestionaFacilRestaurants.utilities.DateTimeUtil;
import com.api.GestionaFacilRestaurants.utilities.JwtUtil;
import com.api.GestionaFacilRestaurants.utilities.KeyUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class ComandaService {
    @Value("${api.host}")
    private String host;

    @Value("${api.base.path}")
    private String apiBasePath;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private TableViewRepository tableViewRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JwtUtil jwt;

    public Object generate(String token,Long orderId){
        String tokenUpdate = jwt.extendTokenExpiration(token);
        Business businessFinded = businessRepository.findByRuc(jwt.extractBusinessRuc(token)).orElse(null);
        Order orderFinded = orderRepository.findByIdAndBusinessId(orderId, businessFinded.getId()).orElse(null);
        String fileName =  businessFinded.getRuc().toString()+"-"+ApiUtil.lzeros(orderFinded.getNumbering(), 8);
        Map<String,Object> data = new LinkedHashMap<>();
        data.put("fileName",fileName);
        data.put("url",getFullPath()+"/comandas/"+KeyUtil.encrypt(tokenUpdate)+"/getPdf/ticket58mm/"+fileName+".pdf");
        return new SuccessResponse(data,tokenUpdate);
    } 
    public void getTicket58mm(String token,String ruc,String numbering, HttpServletResponse response){
        try{
        Long number = Long.parseLong(numbering);
        String tokenUpdated = jwt.extendTokenExpiration(token);
        Authorization user = authRepository.findById(jwt.extractUid(tokenUpdated)).orElse(null);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Usuario no encontrado\"}");
            return;
        }
        Long rucFromToken = jwt.extractBusinessRuc(tokenUpdated);
        Business businessFinded = businessRepository.findByRuc(rucFromToken).orElse(null);
        if(businessFinded==null){
            throw new RuntimeException("Establecimiento no encontrado");
        }
        if(rucFromToken!=Long.parseLong(ruc)){
            throw new RuntimeException("No tienes acceso al recurso");
        }
        Order orderFinded = orderRepository.findByBusinessIdAndNumbering(businessFinded.getId(), number).orElse(null);
        if(orderFinded==null){
            throw new RuntimeException("Orden no encontrada");
        }
        String fileName = businessFinded.getRuc().toString()+"-"+ApiUtil.lzeros(number, 8);
        // Configuración del documento
            Document document = new Document(new Rectangle(170, 800)); // Ancho de 58mm
            document.setMargins(5, 5, 5, 10); // Márgenes reducidos
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=" + fileName+".pdf");
            OutputStream outputStream = response.getOutputStream();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            String businessType = businessFinded.getType().getDenominationEs().toUpperCase().toString();
            String businessName = businessFinded.getName().toUpperCase();

            // Separador
            addDashedSeparator2(document, 0f);

            // **1. Encabezado**
            addCenteredParagraph(document, "COMANDA", 25, Font.BOLD);
            addCenteredParagraph(document, businessType+" "+businessName, 8, Font.BOLD);
            addCenteredParagraph(document, fileName, 8, Font.BOLD);
            
            // Separador
            addDashedSeparator2(document, 0f);

            PdfPTable mainInfoTable = new PdfPTable(2);
            mainInfoTable.setWidthPercentage(100);
            mainInfoTable.setSpacingBefore(5f);
            mainInfoTable.setWidths(new int[]{40, 60});

            MultiValueMap<String, String> metadata = ApiUtil.parseMetadata(orderFinded.getMetadata());
            String tableIdString = metadata.getFirst("mesa_id"); // Obtén el primer valor de "mesa_id"
            Long tableId = tableIdString != null ? Long.parseLong(tableIdString) : null;
            TableView table = tableViewRepository.findById(tableId).orElse(null);
            
            String[] surnameArray = user.getUserOwnerFirstName().split(" ");
            String nickname = surnameArray[0];

            mainInfoTable.addCell(createCell("IMPRESION:", PdfPCell.ALIGN_RIGHT,true));
            mainInfoTable.addCell(createCell(DateTimeUtil.getDateTimeTodayInLima(), PdfPCell.ALIGN_LEFT,false, new BaseColor(33, 47, 61)));
            mainInfoTable.addCell(createCell("MOZO:", PdfPCell.ALIGN_RIGHT,true));
            mainInfoTable.addCell(createCell(nickname, PdfPCell.ALIGN_LEFT,false, new BaseColor(33, 47, 61)));
            mainInfoTable.addCell(createCell("REF:", PdfPCell.ALIGN_RIGHT,true));
            mainInfoTable.addCell(createCell(table.getDenomination().toUpperCase(), PdfPCell.ALIGN_LEFT,false, new BaseColor(33, 47, 61)));
            document.add(mainInfoTable);

            // Separador
            addDashedSeparator(document, 0f);

            // **3. Detalle del pedido**
            PdfPTable detailTable = new PdfPTable(2);
            detailTable.setWidthPercentage(100);
            detailTable.setSpacingBefore(5f);
            detailTable.setWidths(new int[]{20, 80});

            detailTable.addCell(createCell("CANT", PdfPCell.ALIGN_CENTER, true));
            detailTable.addCell(createCell("DESCRIPCIÓN", PdfPCell.ALIGN_CENTER, true));

            List<OrderDetail> details = orderDetailRepository.findAllByOrderId(orderFinded.getId());
            details.forEach(detail -> {
                detailTable.addCell(createCell(String.valueOf(detail.getQuantity()), PdfPCell.ALIGN_CENTER, false,new BaseColor(33, 47, 61)));
                detailTable.addCell(createCell(ApiUtil.capitalizeEachWord(detail.getMenuItem().getDenomination()+" - "+detail.getMenuItem().getPresentation().getDenomination()), PdfPCell.ALIGN_LEFT,false, new BaseColor(33, 47, 61)));
                
            });

            document.add(detailTable);

            // Separador
            addDashedSeparator(document, 0f);

            document.close();
        }  catch (Exception e) {
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
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
        Font font = new Font(Font.FontFamily.HELVETICA, 10, bold ? Font.BOLD : Font.NORMAL);
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    private static PdfPCell createCell(String content, int alignment, boolean bold, BaseColor color) {
        Font font = new Font(Font.FontFamily.HELVETICA, 10, bold ? Font.BOLD : Font.NORMAL, color);
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    private static PdfPCell createCellH1(String content, int alignment, boolean bold) {
        Font font = new Font(Font.FontFamily.HELVETICA, 10, bold ? Font.BOLD : Font.NORMAL);
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
    private String getFullPath() {
        return host + apiBasePath;
    }
}
