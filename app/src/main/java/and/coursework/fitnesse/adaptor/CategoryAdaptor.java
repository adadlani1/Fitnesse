package and.coursework.fitnesse.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import and.coursework.fitnesse.R;
import and.coursework.fitnesse.objects.ActivityCategory;

public class CategoryAdaptor extends RecyclerView.Adapter<CategoryAdaptor.ProductViewHolder> {
    private Context context;
    private List<ActivityCategory> activityCategoriesList;

    //getting the context and product list with constructor
    public CategoryAdaptor(Context context, List<ActivityCategory> activityCategoriesList) {
        this.context = context;
        this.activityCategoriesList = activityCategoriesList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating and returning the view holder
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_categories_layout, parent, false);
        return new ProductViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ProductViewHolder holder, int position) {
        Collections.sort(activityCategoriesList, ActivityCategory.numberOfActivitiesComparator);
        //getting the product of the specified position
        final ActivityCategory activityCategory = activityCategoriesList.get(position);

        holder.textViewActivity.setText(activityCategory.getName());
        holder.textViewAverageEffortLevel.setText("Average Effort Level: " + activityCategory.getAverageEffortLevel());
        holder.textViewAverageMinutes.setText("Average Minutes: " + activityCategory.getAverageMinutes());
        holder.textViewFrequency.setText("No. of Activities: " + activityCategory.getFrequency());
        setImageOfActivity(activityCategory, holder);
    }

    /*Sets Image depending on activity*/
    private void setImageOfActivity(ActivityCategory activity, CategoryAdaptor.ProductViewHolder holder) {
        switch (activity.getName()) {
            case "Boxercise":
                holder.activityImage.setImageResource(R.drawable.boxing);
                break;
            case "Badminton":
                holder.activityImage.setImageResource(R.drawable.badminton);
                break;
            case "Basketball":
                holder.activityImage.setImageResource(R.drawable.basketball);
                break;
            case "Cricket":
                holder.activityImage.setImageResource(R.drawable.cricket);
                break;
            case "Crossfit":
                holder.activityImage.setImageResource(R.drawable.crossfit);
                break;
            case "Football":
                holder.activityImage.setImageResource(R.drawable.football);
                break;
            case "High Intensity Interval Training (HIIT)":
                holder.activityImage.setImageResource(R.drawable.hiit);
                break;
            case "Other":
                holder.activityImage.setImageResource(R.drawable.other);
                break;
            case "Running":
                holder.activityImage.setImageResource(R.drawable.running);
                break;
            case "Strength Training":
                holder.activityImage.setImageResource(R.drawable.strength_training);
                break;
            case "Walking":
                holder.activityImage.setImageResource(R.drawable.walking);
                break;
            case "Weightlifting":
                holder.activityImage.setImageResource(R.drawable.weightlifting);
                break;
            default:
                holder.activityImage.setImageResource(R.drawable.workout);
                break;
        }

    }


    @Override
    public int getItemCount() {
        return activityCategoriesList.size();
    }


    static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewActivity, textViewAverageEffortLevel, textViewAverageMinutes, textViewFrequency;
        ImageView activityImage;
        CardView cardView;
        RelativeLayout relativeLayout;

        ProductViewHolder(View itemView) {
            super(itemView);

            textViewActivity = itemView.findViewById(R.id.activityCategory);
            textViewAverageEffortLevel = itemView.findViewById(R.id.averageEffortLevel);
            textViewAverageMinutes = itemView.findViewById(R.id.averageMinutes);
            textViewFrequency = itemView.findViewById(R.id.frequency);
            activityImage = itemView.findViewById(R.id.activityImageCategories);
            cardView = itemView.findViewById(R.id.cardViewActivityCategories);
            relativeLayout = itemView.findViewById(R.id.relativeLayoutCardCategories);

        }
    }
}
