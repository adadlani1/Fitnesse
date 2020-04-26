package and.coursework.fitnesse;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //getting the product of the specified position
        Activity activity = activityList.get(position);

        //binding the data with the viewholder views
        holder.textViewActivity.setText(activity.getActivity());
        holder.textViewLocation.setText(activity.getDescription());
        holder.textViewDate.setText("Date: "+ activity.getDateAdded());
        holder.textViewMinutes.setText("Minutes Active: " + activity.getMinutes());

    }


    @Override
    public int getItemCount() {
        return activityList.size();
    }


    static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewActivity, textViewLocation, textViewDate, textViewMinutes;
        ImageView activityImage;

        ProductViewHolder(View itemView) {
            super(itemView);

            textViewActivity = itemView.findViewById(R.id.activity);
            textViewLocation = itemView.findViewById(R.id.location);
            textViewDate = itemView.findViewById(R.id.date);
            textViewMinutes = itemView.findViewById(R.id.minutes);
            activityImage = itemView.findViewById(R.id.activityImage);

        }
    }
}
