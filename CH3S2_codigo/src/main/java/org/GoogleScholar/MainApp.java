// src/main/java/org/GoogleScholar/MainApp.java
package org.GoogleScholar;

import org.GoogleScholar.controller.ScholarController;
import org.GoogleScholar.controller.ScholarController.ArticleFirst;
import org.GoogleScholar.controller.ScholarController.AuthorRef;
import org.GoogleScholar.model.Author;
import org.GoogleScholar.model.AuthorRepository;
import org.GoogleScholar.view.ConsoleView;
import org.GoogleScholar.view.TableWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class MainApp {
    public static void main(String[] args) {
        // fuerza GUI por si el entorno entra headless
        System.setProperty("java.awt.headless", "false");

        ScholarController controller = new ScholarController();
        AuthorRepository repo = new AuthorRepository();
        ConsoleView view = new ConsoleView();
        Scanner sc = new Scanner(System.in);

        boolean usedArgsOnce = false;

        while (true) {
            // === 1) Búsqueda ===
            String query;
            if (!usedArgsOnce && args != null && args.length > 0) {
                query = String.join(" ", args);
                usedArgsOnce = true;
            } else {
                System.out.print("Please write your search (or 'exit'): ");
                query = sc.nextLine().trim();
                if (query.equalsIgnoreCase("exit") || query.equalsIgnoreCase("exit")) break;
                if (query.isEmpty()) {
                    System.out.println("→ Enter some text.\n");
                    continue;
                }
            }

            try {
                // === 2) Primer artículo con autores ===
                ArticleFirst art = controller.fetchFirstArticleAndAuthors(query);

                List<Author> toSave = new ArrayList<>();
                for (AuthorRef ref : art.authors) {
                    toSave.add(controller.enrichAuthor(ref, art.title));
                }
                repo.saveAll(toSave);
                view.savedToDbNotice(toSave.size());
                view.renderAuthors(toSave, art.title);

                // === 3) Preguntar si abrir ventana ===
                String ver;
                while (true) {
                    System.out.print("¿Do you wan to see the table in a window? (y/n): ");
                    ver = sc.nextLine().trim().toLowerCase(Locale.ROOT);
                    if (ver.equals("y") || ver.equals("yep") || ver.equals("yes") || ver.equals("n") || ver.equals("not")) break;
                    System.out.println("Respond 'y' o 'n', please.");
                }
                if (ver.startsWith("y")) {
                    System.out.println("→ open window…");       // DEBUG 1
                    var all = repo.findAll();
                    TableWindow.showModal(all);                       // MODAL; bloquea hasta cerrar
                    System.out.println("→ window closed.\n");       // DEBUG 2 (aparece al cerrar)
                }

            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println();
        }

        sc.close();
        System.out.println("👋 Program finished.");
    }
}
