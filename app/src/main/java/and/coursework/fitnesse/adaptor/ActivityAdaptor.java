package and.coursework.fitnesse.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import and.coursework.fitnesse.R;
import and.coursework.fitnesse.activity.ViewActivityMapsActivity;
import and.coursework.fitnesse.objects.Activity;
import and.coursework.fitnesse.utils.AppUtils;

import static and.coursework.fitnesse.utils.AppUtils.getDate;

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
        //inflating and returning the view holder
        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.activity_layout, null);
        return new ProductViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ProductViewHolder holder, int position) {
        //getting the product of the specified position
        final Activity activity = activityList.get(position);

        /*If there are no activities present*/
        if (activity.getActivity().equals("No Activity In This Month")) {

            String month = "0" + getDate("MM");
            String year = getDate("yyyy");

            holder.activityImage.setImageResource(R.drawable.cross);
            if (month.equals(activity.getMonthAdded()) && year.equals(activity.getYearAdded()))
                holder.textViewLocation.setText("Please click the Button above to add an activity");
            else
                holder.textViewLocation.setVisibility(View.GONE);
            holder.textViewActivity.setText(activity.getActivity());
            holder.textViewDate.setVisibility(View.GONE);
            holder.textViewMinutes.setVisibility(View.GONE);

        } else {

            String date = activity.getDayAdded() + "/" + activity.getMonthAdded() + "/" + activity.getYearAdded();

            //binding the data with the viewholder views
            holder.textViewActivity.setText(activity.getActivity());
            holder.textViewDate.setText("Date: " + date);
            holder.textViewMinutes.setText("Minutes Active: " + activity.getMinutes());

            setImageOfActivity(activity, holder);
            showEffortLevel(activity, holder);

            /*Starts map activity when card pressed*/
            holder.cardView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ViewActivityMapsActivity.class);
                addExtraInformation(intent, activity);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);

            });
        }
    }

    /*Adds extra information to intent*/
    private void addExtraInformation(Intent intent, Activity activity) {
        intent.putExtra("activity", activity.getActivity());
        intent.putExtra("dayAdded", activity.getDayAdded());
        intent.putExtra("monthAdded", activity.getMonthAdded());
        intent.putExtra("yearAdded", activity.getYearAdded());
        intent.putExtra("description", activity.getDescription());
        intent.putExtra("effortLevel", String.valueOf(activity.getEffortLevel()));
        intent.putExtra("latitude", activity.getLatitude());
        intent.putExtra("longitude", activity.getLongitude());
        intent.putExtra("minutes", activity.getMinutes());
    }

    /*Changes colour of card depending on effort level*/
    private void showEffortLevel(Activity activity, ProductViewHolder holder) {
        switch (activity.getEffortLevel()) {
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

    /*Sets image on card depending on activity*/
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
            default:
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
