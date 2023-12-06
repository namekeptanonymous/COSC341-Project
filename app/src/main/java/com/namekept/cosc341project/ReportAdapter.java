package com.namekept.cosc341project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private final List<Report> reportList;
    private final OnReportListener onReportListener;

    // Constructor
    public ReportAdapter(List<Report> reportList, OnReportListener onReportListener) {
        this.reportList = reportList;
        this.onReportListener = onReportListener;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_item, parent, false);
        return new ReportViewHolder(itemView, onReportListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reportList.get(position);
        holder.bind(report);
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    // ViewHolder class
    public static class ReportViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textViewTitle;
        private final TextView textViewDescription;
        private final OnReportListener onReportListener;

        public ReportViewHolder(View itemView, OnReportListener onReportListener) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_title);
            textViewDescription = itemView.findViewById(R.id.text_description);

            this.onReportListener = onReportListener;
            itemView.setOnClickListener(this);
        }

        void bind(Report report) {
            textViewTitle.setText(report.getTitle());
            textViewDescription.setText(report.getDescription());
            // Bind other views if necessary
        }

        @Override
        public void onClick(View view) {
            if (onReportListener != null) {
                onReportListener.onReportClicked(getAdapterPosition());
            }
        }
    }

    // Interface for click events
    public interface OnReportListener {
        void onReportClicked(int position);

        void onMoreOptionsClicked(int position);
    }
}
