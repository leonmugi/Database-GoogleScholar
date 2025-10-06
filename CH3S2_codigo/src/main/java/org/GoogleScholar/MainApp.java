package org.GoogleScholar;

import org.GoogleScholar.controller.ScholarController;
import org.GoogleScholar.model.*;
import org.GoogleScholar.view.ConsoleView;
import org.GoogleScholar.view.TableWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class MainApp {
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");

        ScholarController controller = new ScholarController();
        ArticleRepository repo = new ArticleRepository();
        IssueRepository issueRepo = new IssueRepository();
        ConsoleView view = new ConsoleView();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("Enter Author 1 (or 'exit'): ");
            String a1 = sc.nextLine().trim();
            if (a1.equalsIgnoreCase("exit") || a1.equalsIgnoreCase("salir")) break;
            if (a1.isEmpty()) { System.out.println("→ Please type a name.\n"); continue; }

            System.out.print("Enter Author 2 (or 'exit'): ");
            String a2 = sc.nextLine().trim();
            if (a2.equalsIgnoreCase("exit") || a2.equalsIgnoreCase("salir")) break;
            if (a2.isEmpty()) { System.out.println("→ Please type a name.\n"); continue; }

            try {
                List<Article> toSave = new ArrayList<>();
                toSave.addAll(controller.fetchTopArticlesByAuthorName(a1, 3));
                toSave.addAll(controller.fetchTopArticlesByAuthorName(a2, 3));

                repo.saveAll(toSave);
                view.savedToDbNotice(toSave.size());
                view.renderArticles(toSave);

                // Guardar y mostrar incidencias
                List<Issue> issues = controller.drainIssues();
                issueRepo.saveAll(issues);
                view.renderIssuesSummary(issues);

                String ans;
                while (true) {
                    System.out.print("Do you want to see the table in a window? (y/n): ");
                    ans = sc.nextLine().trim().toLowerCase(Locale.ROOT);
                    if (ans.equals("y") || ans.equals("n")) break;
                }
                if (ans.equals("y")) {
                    TableWindow.showArticlesModal(repo.findAll());
                }
                System.out.println();

            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        sc.close();
        System.out.println("👋 Program finished.");
    }
}
