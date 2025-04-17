package deso1.ngoxuannghiem.btl_adr_nc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RevenueReportAdapter extends RecyclerView.Adapter<RevenueReportAdapter.ReportViewHolder> {

    private List<RevenueItem> reportList;

    public RevenueReportAdapter(List<RevenueItem> reportList) {
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_revenue_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        RevenueItem item = reportList.get(position);
        holder.tvStt.setText(String.valueOf(item.getIndex()));
        holder.tvMovieName.setText(item.getMovieName());
        holder.tvTicketCount.setText(String.valueOf(item.getTicketCount()));
        holder.tvRevenue.setText(item.getRevenue() + " VND");
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView tvStt, tvMovieName, tvTicketCount, tvRevenue;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStt = itemView.findViewById(R.id.tvStt);
            tvMovieName = itemView.findViewById(R.id.tvMovieName);
            tvTicketCount = itemView.findViewById(R.id.tvTicketCount);
            tvRevenue = itemView.findViewById(R.id.tvRevenue);
        }
    }
}
