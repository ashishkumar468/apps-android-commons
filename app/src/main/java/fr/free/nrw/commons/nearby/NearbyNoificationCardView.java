package fr.free.nrw.commons.nearby;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.free.nrw.commons.R;
import fr.free.nrw.commons.contributions.ContributionsActivity;
import fr.free.nrw.commons.contributions.ContributionsFragment;
import fr.free.nrw.commons.notification.Notification;

/**
 * Custom card view for nearby notification card view on main screen, above contributions list
 */
public class NearbyNoificationCardView  extends CardView{

    private Context context;

    private Button permissionRequestButton;
    private RelativeLayout contentLayout;
    private TextView notificationTextSwitcher;
    private TextView notificationTimeSwitcher;
    private ImageView notificationIcon;

    public NearbyNoificationCardView(@NonNull Context context) {
        super(context);
        this.context = context;
        init();
    }

    public NearbyNoificationCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public NearbyNoificationCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        View rootView = inflate(context, R.layout.nearby_card_view, this);

        permissionRequestButton = rootView.findViewById(R.id.permission_request_button);
        contentLayout = rootView.findViewById(R.id.content_layout);

        notificationTextSwitcher = rootView.findViewById(R.id.nearby_title);
        notificationTimeSwitcher = rootView.findViewById(R.id.nearby_distance);

        notificationIcon = rootView.findViewById(R.id.nearby_icon);

        setActionListeners();

        Log.d("deneme2",context.toString());
    }

    private void setActionListeners() {
        permissionRequestButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!((ContributionsActivity)context).isFinishing()) {
                    // TODO: why location manager is null
                    ((ContributionsActivity) context).contributionsFragment.locationManager.requestPermissions((ContributionsActivity) context);
                }
            }
        });
    }

    public void displayPermissionRequestButton(boolean isPermissionRequestButtonNeeded) {
        if (isPermissionRequestButtonNeeded) {
            contentLayout.setVisibility(GONE);
            permissionRequestButton.setVisibility(VISIBLE);
        } else {
            contentLayout.setVisibility(VISIBLE);
            permissionRequestButton.setVisibility(GONE);
        }
    }
}