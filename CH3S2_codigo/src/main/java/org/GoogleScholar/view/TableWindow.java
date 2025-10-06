package org.GoogleScholar.view;

import org.GoogleScholar.model.Article;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

public class TableWindow {

    /** Renderer multilinea para Abstract (usa JTextArea y ajusta alto). */
    static class TextAreaRenderer extends JTextArea implements TableCellRenderer {
        public TextAreaRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setText(value == null ? "" : value.toString());
            setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
            // Ajusta alto de fila a lo necesario (mínimo 60px)
            int h = Math.max(60, getPreferredSize().height);
            if (table.getRowHeight(row) != h) table.setRowHeight(row, h);
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }
            return this;
        }
    }

    /** Dialog modal con los artículos; bloquea hasta cerrar. */
    public static void showArticlesModal(List<Article> arts) {
        try {
            SwingUtilities.invokeAndWait(() -> {
                String[] cols = {"Author Query", "Article ID", "Title", "Authors", "Date", "Citations", "Link", "Keywords", "Abstract"};
                Object[][] data = new Object[arts.size()][cols.length];
                for (int i = 0; i < arts.size(); i++) {
                    Article a = arts.get(i);
                    data[i][0] = a.getAuthorQuery();
                    data[i][1] = a.getArticleId();
                    data[i][2] = a.getTitle();
                    data[i][3] = a.getAuthors();
                    data[i][4] = a.getPublicationDate();
                    data[i][5] = (a.getCitedBy() == null || a.getCitedBy() < 0) ? "No se encontró cites" : a.getCitedBy();
                    data[i][6] = a.getLink();
                    data[i][7] = a.getKeywords();
                    data[i][8] = a.getAbstractText();
                }

                JTable table = new JTable(new DefaultTableModel(data, cols) {
                    @Override public boolean isCellEditable(int r, int c) { return false; }
                });
                table.setAutoCreateRowSorter(true);
                table.setFillsViewportHeight(true);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

                // Anchos más cómodos
                table.getColumnModel().getColumn(0).setPreferredWidth(140);
                table.getColumnModel().getColumn(1).setPreferredWidth(120);
                table.getColumnModel().getColumn(2).setPreferredWidth(300);
                table.getColumnModel().getColumn(3).setPreferredWidth(240);
                table.getColumnModel().getColumn(4).setPreferredWidth(70);
                table.getColumnModel().getColumn(5).setPreferredWidth(85);
                table.getColumnModel().getColumn(6).setPreferredWidth(300);
                table.getColumnModel().getColumn(7).setPreferredWidth(160);
                table.getColumnModel().getColumn(8).setPreferredWidth(420); // Abstract más ancho

                // Renderer multilinea solo para Abstract (columna 8)
                table.getColumnModel().getColumn(8).setCellRenderer(new TextAreaRenderer());

                JScrollPane scroll = new JScrollPane(table);

                JButton closeBtn = new JButton("Close");
                closeBtn.addActionListener(e -> SwingUtilities.getWindowAncestor(closeBtn).dispose());
                JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                south.add(closeBtn);

                JDialog dialog = new JDialog((Frame) null, "Articles in DB", true);
                dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                dialog.getContentPane().setLayout(new BorderLayout());
                dialog.getContentPane().add(scroll, BorderLayout.CENTER);
                dialog.getContentPane().add(south, BorderLayout.SOUTH);
                dialog.setSize(1300, 650);
                dialog.setAlwaysOnTop(true);
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to open articles window", e);
        }
    }
}
