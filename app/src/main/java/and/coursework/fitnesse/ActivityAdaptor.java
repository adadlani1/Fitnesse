package and.coursework.fitnesse;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ActivityAdaptor extends RecyclerView.Adapter<ActivityAdaptor.ProductViewHolder> {


    //this context we will use to inflate the layout
    private Context context;

    //we are storing all the products in a list
    private List<Activity> activityList;

    //getting the context and product list with constructor
    public ActivityAdaptor(Context context, List<Activity> activityList) {
        this.context = context;
        this.activityList = activityList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.list_layout, null);
        return new ProductViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ProductViewHolder holder, int position) {
        //getting the product of the specified position
        final Activity activity = activityList.get(position);

        //binding the data with the viewholder views
        holder.textViewActivity.setText(activity.getActivity());
        holder.textViewLocation.setText(activity.getDescription());
        holder.textViewDate.setText("Date: "+ activity.getDateAdded());
        holder.textViewMinutes.setText("Minutes Active: " + activity.getMinutes());

        setImageOfActivity(activity, holder);
    }

    private void setImageOfActivity(Activity activity, ProductViewHolder holder) {
        switch (activity.getActivity()) {
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
            case "Workout":
                holder.activityImage.setImageResource(R.drawable.workout);
                break;
        }

    }


    @Override
    public int getItemCount() {
        return activityList.size();
    }


    static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewActivity, textViewLocation, textViewDate, textViewMinutes;
        ImageView activityImage;
        CardView cardView;

        ProductViewHolder(View itemView) {
            super(itemView);

            textViewActivity = itemView.findViewById(R.id.activity);
            textViewLocation = itemView.findViewById(R.id.description);
            textViewDate = itemView.findViewById(R.id.date);
            textViewMinutes = itemView.findViewById(R.id.minutes);
            activityImage = itemView.findViewById(R.id.activityImage);
            cardView = itemView.findViewById(R.id.cardViewActivity);

        }
    }
}
