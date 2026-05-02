package com.safaemirr.kampusbildirimleri;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/*
 * Bu adapter Report nesnelerini RecyclerView üzerinde
 * listelemek için kullanılır. Arama, filtreleme ve liste
 * güncelleme işlemlerini yönetir.
 */
public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    // Adapter’ın çalıştığı context
    private final Context ctx;

    // Tüm raporların tutulduğu ana liste
    private ArrayList<Report> originalList;

    // Filtrelenmiş ve ekranda gösterilen liste
    private ArrayList<Report> displayList;

    // Adapter constructor’ı
    public ReportAdapter(Context ctx, ArrayList<Report> reports) {
        this.ctx = ctx;

        if (reports == null) reports = new ArrayList<>();

        this.originalList = new ArrayList<>(reports);
        this.displayList = new ArrayList<>(reports);
    }

    // ViewHolder oluşturulur
    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx)
                .inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    // Liste elemanları ekrana bağlanır
    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {

        // Liste sınır kontrolü
        if (position < 0 || position >= displayList.size()) return;

        Report r = displayList.get(position);
        if (r == null) return;

        String title = safe(r.getTitle());
        String description = safe(r.getDescription());
        String date = safe(r.getDate());
        String status = safe(r.getStatus());

        holder.txtReportTitle.setText(title);

        // Açıklama yoksa kullanıcıya bilgi verilir
        holder.txtReportDescription.setText(
                description.isEmpty() ? "Açıklama yok" : description
        );

        holder.txtReportDate.setText(date);
        holder.txtReportStatus.setText(status);

        // Liste elemanına tıklanınca detay ekranı açılır
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(ctx, ReportDetailActivity.class);
            i.putExtra("report_id", r.getId());
            ctx.startActivity(i);
        });
    }

    // RecyclerView’da gösterilecek eleman sayısı
    @Override
    public int getItemCount() {
        return displayList == null ? 0 : displayList.size();
    }

    /*
     * RecyclerView için ViewHolder sınıfı.
     * Tek bir rapor kartındaki view’ları tutar.
     */
    static class ReportViewHolder extends RecyclerView.ViewHolder {

        TextView txtReportTitle, txtReportDescription, txtReportDate, txtReportStatus;
        ImageView imgReportIcon;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);

            txtReportTitle       = itemView.findViewById(R.id.txtReportTitle);
            txtReportDescription = itemView.findViewById(R.id.txtReportDescription);
            txtReportDate        = itemView.findViewById(R.id.txtReportDate);
            txtReportStatus      = itemView.findViewById(R.id.txtReportStatus);
            imgReportIcon        = itemView.findViewById(R.id.imgReportIcon);
        }
    }

    // Null değerleri güvenli şekilde boş string olarak döndürür
    private String safe(String value) {
        return value == null ? "" : value;
    }

    /*
     * Metin bazlı arama filtresi.
     * Başlık, açıklama ve durum alanlarında arama yapar.
     */
    public void filter(String text) {

        text = safe(text).toLowerCase().trim();
        ArrayList<Report> temp = new ArrayList<>();

        if (text.isEmpty()) {
            temp.addAll(originalList);
        } else {
            for (Report r : originalList) {

                String title = safe(r.getTitle()).toLowerCase();
                String desc = safe(r.getDescription()).toLowerCase();
                String status = safe(r.getStatus()).toLowerCase();

                if (title.contains(text) || desc.contains(text) || status.contains(text)) {
                    temp.add(r);
                }
            }
        }

        displayList = temp;
        notifyDataSetChanged();
    }

    /*
     * Duruma göre filtreleme yapar.
     * Örneğin Açık, İnceleniyor veya Çözüldü.
     */
    public void filterStatus(String status) {

        status = safe(status);
        ArrayList<Report> temp = new ArrayList<>();

        if (status.equals("Tümü")) {
            temp.addAll(originalList);
        } else {
            for (Report r : originalList) {
                if (safe(r.getStatus()).equalsIgnoreCase(status)) {
                    temp.add(r);
                }
            }
        }

        displayList = temp;
        notifyDataSetChanged();
    }

    /*
     * Liste tamamen güncellendiğinde kullanılır.
     * Genellikle veritabanından yeni veri çekildiğinde çağrılır.
     */
    public void updateData(ArrayList<Report> newList) {

        if (newList == null) newList = new ArrayList<>();

        originalList = new ArrayList<>(newList);
        displayList = new ArrayList<>(newList);

        notifyDataSetChanged();
    }

    /*
     * Admin paneli gibi ekranlarda
     * sadece gösterilecek listeyi güncellemek için kullanılır.
     */
    public void filterList(ArrayList<Report> newList) {

        if (newList == null) newList = new ArrayList<>();

        displayList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }
}
