package com.ourdreamit.blooddonationproject.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.ourdreamit.blooddonationproject.R;

/**
 * Created by anismizi on 4/18/17.
 */

public class AnimationsActivity {
    public View view1,view2;
    Context context;

    public AnimationsActivity(Context ctx){
        context = ctx;
    }

    public void playAnimation(final View view1, final View view2){
        this.view1 = view1;
        this.view2 = view2;


        Animation animation1 =
                AnimationUtils.loadAnimation(context,
                        R.anim.fade_out);
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view1.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        this.view1.startAnimation(animation1);

        Animation animation2 =
                AnimationUtils.loadAnimation(context,
                        R.anim.fade_in);
        animation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        this.view2.startAnimation(animation2);

    }

    public void buttonAnimation(View view){{
        final Animation buttonAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        BounceInterpolator interpolator = new BounceInterpolator(0.2, 20);
        buttonAnim.setInterpolator(interpolator);

        view.startAnimation(buttonAnim);
    }

    }
}
