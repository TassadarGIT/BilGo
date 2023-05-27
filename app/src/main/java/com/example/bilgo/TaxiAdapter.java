package com.example.bilgo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilgo.model.TripModel;

import java.util.List;

public class TaxiAdapter extends RecyclerView.Adapter<TaxiAdapter.ViewHolder> {
    private List<TripModel> tripList;

    public TaxiAdapter(List<TripModel> tripList) {
        this.tripList = tripList;
    }

    public void updateData(List<TripModel> updatedTripList) {
        tripList = updatedTripList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TripModel trip = tripList.get(position);
        holder.bind(trip);
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tripTimeText;
        private TextView seatsAvailableText;
        private TextView deptText;
        private TextView destText;

        public ViewHolder(View itemView) {
            super(itemView);
            tripTimeText = itemView.findViewById(R.id.tripTime);
            seatsAvailableText = itemView.findViewById(R.id.seatsAvailable);
            deptText = itemView.findViewById(R.id.deptText);
            destText = itemView.findViewById(R.id.destText);
        }

        public void bind(TripModel trip) {
            tripTimeText.setText("At: " + trip.getDate().toString());
            seatsAvailableText.setText(trip.getSeatsAvailable() + " slots available");
            deptText.setText(trip.getDeparturePoint());
            destText.setText(trip.getDestinationPoint());
        }
    }
}
