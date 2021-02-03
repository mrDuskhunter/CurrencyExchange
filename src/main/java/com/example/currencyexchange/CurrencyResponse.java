package com.example.currencyexchange;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class CurrencyResponse extends JFrame {
    private String[][] rates = getRates();
    JFrame frame;

    public CurrencyResponse() throws IOException, SAXException, ParserConfigurationException {
        frame = new JFrame();
        setTitle("Курс Валют");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String[] columnNames = {"Код валюты", "Цена"};
        JTable table = new JTable(rates, columnNames);

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Arial",Font.BOLD,20));

        table.setFont(new Font("Serif",Font.PLAIN,18));
        table.setRowHeight(table.getRowHeight()+10);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(cellRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(cellRenderer);

        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private static String[][] getRates() throws ParserConfigurationException, SAXException, IOException {
        String[][] rates;
        HashMap<String, NodeList> result = new HashMap<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date();
        String url = "http://www.cbr.ru/scripts/XML_daily.asp?date_reg=" + dateFormat.format(date);
        Document document = loadDocument(url);

        NodeList nl1 = document.getElementsByTagName("Valute");

        for(int i = 0;i< nl1.getLength();i++){
            Node node = nl1.item(i);
            NodeList nlChild = node.getChildNodes();
            for (int j = 0; j< nlChild.getLength(); j++) {
                if(nlChild.item(j).getNodeName().equals("CharCode")) {
                    result.put(nlChild.item(j).getTextContent(), nlChild);
                }
            }
        }

        System.out.println(document.getXmlVersion());

        int k = 0;
        rates = new String[result.size()][2];

        for (Map.Entry<String , NodeList> entry:result.entrySet()) {
            NodeList temp = entry.getValue();
            double value=0;
            int nominal=0;

            for (int i = 0;i< temp.getLength(); i++) {
                if(temp.item(i).getNodeName().equals("Value")) {
                    value = Double.parseDouble(temp.item(i).getTextContent().replace(",", "."));
                }
                if(temp.item(i).getNodeName().equals("Nominal")) {
                    nominal = Integer.parseInt(temp.item(i).getTextContent());
                }
            }

            String amount = new DecimalFormat("#0.00").format(value / nominal);
            rates[k][0] = entry.getKey();
            rates[k][1] = amount + " рублей";
            k++;
        }
        return rates;
    }

    private static Document loadDocument(String url) throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        return factory.newDocumentBuilder().parse(new URL(url).openStream());
    }

}
