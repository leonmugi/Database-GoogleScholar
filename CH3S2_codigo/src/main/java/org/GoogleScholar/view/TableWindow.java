
// src/main/java/org/GoogleScholar/view/TableWindow.java
package org.GoogleScholar.view;

import org.GoogleScholar.model.Author;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TableWindow {

    /** Abre una ventana MODAL con la tabla; bloquea hasta presionar "Cerrar". */
    public static void showModal(List<Author> authors) {
        // Construimos TODO en el hilo de eventos y bloqueamos hasta cerrar
        try {
            SwingUtilities.invokeAndWait(() -> {
                String[] cols = {"Autor", "Author ID", "Citations", "Artículo", "Perfil"};
                Object[][] data = new Object[authors.size()][cols.length];
                for (int i = 0; i < authors.size(); i++) {
                    Author a = authors.get(i);
                    data[i][0] = a.getName();
                    data[i][1] = a.getAuthorId();
                    data[i][2] = a.getCitations();
                    data[i][3] = a.getArticleTitle();
                    data[i][4] = a.getProfileUrl();
                }

                JTable table = new JTable(new DefaultTableModel(data, cols) {
                    @Override public boolean isCellEditable(int r, int c) { return false; }
                });
                table.setAutoCreateRowSorter(true);
                table.setFillsViewportHeight(true);

                JScrollPane scroll = new JScrollPane(table);

                JButton closeBtn = new JButton("Cerrar");
                closeBtn.addActionListener(e -> SwingUtilities.getWindowAncestor(closeBtn).dispose());
                JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                south.add(closeBtn);

                // DIÁLOGO MODAL, SIEMPRE ENCIMA Y CENTRADO
                JDialog dialog = new JDialog((Frame) null, "Autores en BD", true);
                dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                dialog.getContentPane().setLayout(new BorderLayout());
                dialog.getContentPane().add(scroll, BorderLayout.CENTER);
                dialog.getContentPane().add(south, BorderLayout.SOUTH);
                dialog.setSize(1000, 480);
                dialog.setAlwaysOnTop(true);                 // <- que no quede detrás
                dialog.setLocationRelativeTo(null);          // <- centrado pantalla
                Toolkit.getDefaultToolkit().beep();          // <- pequeña señal audible
                dialog.setVisible(true);                     // bloquea hasta cerrar
            });
        } catch (Exception e) {
            throw new RuntimeException("No se pudo abrir la ventana", e);
        }
    }
}
