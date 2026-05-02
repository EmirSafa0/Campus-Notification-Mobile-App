package com.safaemirr.kampusbildirimleri;

public class Emergency {

    private int id;
    private String title;
    private String description;
    private String category;
    private String status;   // ACTIVE / CLOSED
    private String date;

    // =================================================
    // ✅ YENİ KULLANIM (STATUS VAR)
    // =================================================
    public Emergency(int id,
                     String title,
                     String description,
                     String category,
                     String status,
                     String date) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.status = status;
        this.date = date;
    }

    // =================================================
    // 🔁 ESKİ KULLANIM (STATUS YOK → ACTIVE SAY)
    // =================================================
    public Emergency(int id,
                     String title,
                     String description,
                     String category,
                     String date) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.status = "ACTIVE";   // 🔥 varsayılan
        this.date = date;
    }

    // ================= GETTERS ================= //

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    // ================= HELPERS ================= //

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(status);
    }

    public boolean isClosed() {
        return "CLOSED".equalsIgnoreCase(status);
    }
}
