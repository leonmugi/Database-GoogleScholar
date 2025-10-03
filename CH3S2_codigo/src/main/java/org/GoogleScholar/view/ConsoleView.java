package org.GoogleScholar.view;

import org.GoogleScholar.model.Author;
import java.util.List;

public class ConsoleView {
    public void renderAuthors(List<Author> authors, String articleTitle) {
        System.out.println("\n=== Autores del primer artículo ===");
        System.out.println("Artículo: " + articleTitle + "\n");
        int i = 1;
        for (Author a : authors) {
            System.out.printf("%2d) %s%n", i++, a.getName());
            System.out.printf("    author_id: %s%n", a.getAuthorId() == null ? "N/D" : a.getAuthorId());
            System.out.printf("    citations: %s%n", a.getCitations() == null ? "N/D" : a.getCitations());
            if (a.getProfileUrl() != null) {
                System.out.printf("    perfil   : %s%n", a.getProfileUrl());
            }
            System.out.println();
        }
        System.out.println("Total autores: " + authors.size());
    }

    public void savedToDbNotice(int count) {
        System.out.println("✔ Guardados en MySQL: " + count + " registro(s).");
    }
}
