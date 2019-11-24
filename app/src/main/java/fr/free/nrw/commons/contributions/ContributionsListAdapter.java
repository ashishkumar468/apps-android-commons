package fr.free.nrw.commons.contributions;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import fr.free.nrw.commons.R;
import fr.free.nrw.commons.contributions.model.DisplayableContribution;
import java.util.ArrayList;
import java.util.List;

public class ContributionsListAdapter extends RecyclerView.Adapter<ContributionViewHolder> {

    private Callback callback;
    List<Contribution> contributions;

    public ContributionsListAdapter(Callback callback) {
        this.callback = callback;
        contributions=new ArrayList<>();
    }

    /**
     * Add contributions to existing list
     * @param contributions
     */
    void addContributions(List<Contribution> contributions) {
        this.contributions.addAll(contributions);
        notifyDataSetChanged();
        //TODO add DiffUtils
    }

    @NonNull
    @Override
    public ContributionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ContributionViewHolder viewHolder = new ContributionViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_contribution, parent, false), callback);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContributionViewHolder holder, int position) {
        final Contribution contribution = contributions.get(position);
        DisplayableContribution displayableContribution = new DisplayableContribution(contribution,
                position);
        holder.init(position, displayableContribution);
    }

    @Override
    public int getItemCount() {
        return contributions.size();
    }

    public interface Callback {

        void retryUpload(Contribution contribution);

        void deleteUpload(Contribution contribution);

        void openMediaDetail(int contribution);
    }
}
