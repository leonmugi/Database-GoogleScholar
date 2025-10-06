package org.GoogleScholar.view;

import org.GoogleScholar.model.Article;
import org.GoogleScholar.model.Issue;

import java.util.List;

public class ConsoleView {

    public void savedToDbNotice(int count) {
        System.out.println("✔ Saved to MySQL: " + count + " article(s).");
    }

    public void renderArticles(List<Article> arts) {
        System.out.println("\n=== Articles collected ===\n");
        int i = 1;
        for (Article a : arts) {
            System.out.printf("%2d) [%s] %s%n", i++, a.getAuthorQuery(), a.getTitle());
            System.out.printf("    id      : %s%n", a.getArticleId());
            System.out.printf("    authors : %s%n", a.getAuthors());
            System.out.printf("    date    : %s%n", a.getPublicationDate());
            System.out.printf("    cites   : %s%n", (a.getCitedBy() == null || a.getCitedBy() < 0) ? "No se encontró cites" : a.getCitedBy());
            System.out.printf("    link    : %s%n", a.getLink() == null ? "N/D" : a.getLink());
            System.out.printf("    keywords: %s%n", a.getKeywords());
            String shortAbs = a.getAbstractText();
            if (shortAbs != null && shortAbs.length() > 220) shortAbs = shortAbs.substring(0,220) + "…";
            System.out.printf("    abstract: %s%n", shortAbs);
            System.out.println();
        }
        System.out.println("Total articles: " + arts.size());
    }

    public void renderIssuesSummary(List<Issue> issues) {
        if (issues == null || issues.isEmpty()) {
            System.out.println("✓ No issues logged.");
            return;
        }
        System.out.println("\n⚠ Issues logged (" + issues.size() + "):");
        for (Issue is : issues) {
            System.out.printf(" - [%s] %s :: %s%n",
                    is.getAuthorQuery(),
                    is.getArticleTitle() == null ? "(HTTP)" : is.getArticleTitle(),
                    is.getIssue() + (is.getHttpStatus() == null ? "" : (" [HTTP " + is.getHttpStatus() + "]"))
            );
        }
    }
}
