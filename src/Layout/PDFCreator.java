package Layout;

import DatabaseService.Player;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class PDFCreator {
    private List<Player> playersList = new ArrayList<>();
    private Document document;

    public PDFCreator(List<Player> playersList) {
        this.playersList = playersList;
    }

    public void generatePDF(String fileName) {
        String dest = "./pdfs/" + fileName + ".pdf";
        PdfWriter writer = null;
        try {
            writer = new PdfWriter(dest);
        } catch (FileNotFoundException e) // TODO - obsluga
        {
            e.printStackTrace();
        }
        if (writer != null) {
            PdfDocument pdf = new PdfDocument(writer);
            document = new Document(pdf, PageSize.A4.rotate());
            try {
                document.setMargins(20, 20, 20, 20);
                for (Player player : playersList)
                    createPlayerParagraph(player);
            } catch (IOException e) // TODO - obsluga
            {
                e.printStackTrace();
            }
            document.close();
        }
    }

    private void createPlayerParagraph(Player player) throws IOException {
        String dateText = "";
        PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN, "CP1250", true);
        PdfFont bold = PdfFontFactory.createFont(FontConstants.TIMES_BOLD, "CP1250", true);
        String firstName = player.getFirstName();
        String lastName = player.getLastName();
        Date date = player.getDate();
        if (date != null) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            dateText = df.format(date);
        }
        List<Player.PlayerRow> playerRows = player.getPlayerRows();
        Paragraph nameParagraph = new Paragraph(firstName + " " + lastName).setFont(bold).setFontSize(14);
        nameParagraph.setMarginTop(20);
        Paragraph dateParagraph = new Paragraph("Birthdate: " + dateText).setFont(font).setFontSize(12);
        document.add(nameParagraph);
        document.add(dateParagraph);
        createPlayerTable(playerRows, font, bold);
    }

    private void createPlayerTable(List<Player.PlayerRow> playerRows, PdfFont font, PdfFont bold) {
        Table table = new Table(new float[]{5, 5, 1, 3, 2, 1, 1, 1});
        table.setWidthPercent(100);
        table.addHeaderCell(new Cell().add(new Paragraph("Team").setFont(bold).setFontSize(12).setTextAlignment(TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addHeaderCell(new Cell().add(new Paragraph("League").setFont(bold).setFontSize(12).setTextAlignment(TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addHeaderCell(new Cell().add(new Paragraph("Apps").setFont(bold).setFontSize(12).setTextAlignment(TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addHeaderCell(new Cell().add(new Paragraph("First Squad").setFont(bold).setFontSize(12).setTextAlignment(TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addHeaderCell(new Cell().add(new Paragraph("Minutes").setFont(bold).setFontSize(12).setTextAlignment(TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addHeaderCell(new Cell().add(new Paragraph("Goals").setFont(bold).setFontSize(12).setTextAlignment(TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addHeaderCell(new Cell().add(new Paragraph("Yellow Cards").setFont(bold).setFontSize(12).setTextAlignment(TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addHeaderCell(new Cell().add(new Paragraph("Red Cards").setFont(bold).setFontSize(12).setTextAlignment(TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        for (Player.PlayerRow row : playerRows)
            createRow(row, table, font);
        document.add(table);
    }

    private void createRow(Player.PlayerRow row, Table table, PdfFont font) {
        String teamName = row.getTeamName();
        String leagueName = row.getLeagueName();
        String apps = Integer.toString(row.getApps());
        String firstSquad = Integer.toString(row.getFirstSquad());
        String minutes = Integer.toString(row.getMinutes());
        String goals = Integer.toString(row.getGoals());
        String yellowCards = Integer.toString(row.getYellowCards());
        String redCards = Integer.toString(row.getRedCards());
        table.addCell(new Cell().add(new Paragraph(teamName).setFont(font).setFontSize(12).setTextAlignment(TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().add(new Paragraph(leagueName).setFont(font).setFontSize(12).setTextAlignment(TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().add(new Paragraph(apps).setFont(font).setFontSize(12).setTextAlignment(TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().add(new Paragraph(firstSquad).setFont(font).setFontSize(12).setTextAlignment(TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().add(new Paragraph(minutes).setFont(font).setFontSize(12).setTextAlignment(TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().add(new Paragraph(goals).setFont(font).setFontSize(12).setTextAlignment(TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().add(new Paragraph(yellowCards).setFont(font).setFontSize(12).setTextAlignment(TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().add(new Paragraph(redCards).setFont(font).setFontSize(12).setTextAlignment(TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
    }

}
