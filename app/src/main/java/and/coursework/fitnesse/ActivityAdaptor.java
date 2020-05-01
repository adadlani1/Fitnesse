package and.coursework.fitnesse;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.activity_layout, null);
        return new ProductViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ProductViewHolder holder, int position) {
        //getting the product of the specified position
        final Activity activity = activityList.get(position);

        if (activity.getActivity().equals("No Activity In This Month")) {

            holder.activityImage.setImageResource(R.drawable.cross);
            holder.textViewLocation.setText("Please click the Button above to add an activity");
            holder.textViewActivity.setText(activity.getActivity());
            holder.textViewDate.setVisibility(View.GONE);
            holder.textViewMinutes.setVisibility(View.GONE);
        } else {

            String date = activity.getDayAdded() + "/" + activity.getMonthAdded() + "/" + activity.getYearAdded();

            //binding the data with the viewholder views
            holder.textViewActivity.setText(activity.getActivity());
            holder.textViewLocation.setText(activity.getDescription());
            holder.textViewDate.setText("Date: " + date);
            holder.textViewMinutes.setText("Minutes Active: " + activity.getMinutes());

            setImageOfActivity(activity, holder);
            showEffortLevel(activity, holder);
        }
    }

    private void showEffortLevel(Activity activity, ProductViewHolder holder) {
        switch (activity.getEffortLevel()){
            case 0:
                holder.relativeLayout.setBackgroundColor(Color.parseColor("#FF9166"));
                break;
            case 1:
                holder.relativeLayout.setBackgroundColor(Color.parseColor("#EBC68A"));
                break;
            case 2:
                holder.relativeLayout.setBackgroundColor(Color.parseColor("#FFF08C"));
                break;
            case 3:
                holder.relativeLayout.setBackgroundColor(Color.parseColor("#D9EB67"));
                break;
            case 4:
                holder.relativeLayout.setBackgroundColor(Color.parseColor("#A9EB57"));
                break;
        }
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
        RelativeLayout relativeLayout;

        ProductViewHolder(View itemView) {
            super(itemView);

            textViewActivity = itemView.findViewById(R.id.activity);
            textViewLocation = itemView.findViewById(R.id.description);
            textViewDate = itemView.findViewById(R.id.date);
            textViewMinutes = itemView.findViewById(R.id.minutes);
            activityImage = itemView.findViewById(R.id.activityImage);
            cardView = itemView.findViewById(R.id.cardViewActivity);
            relativeLayout = itemView.findViewById(R.id.relativeLayoutCard);

        }
    }
}
