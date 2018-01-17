package com.zachkirlew.applications.waxwanderer.similar_users;

import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.zachkirlew.applications.waxwanderer.R;

public class SwipeViewBuilderInstance {

    //private constructor to avoid client applications to use constructor
    public
    SwipeViewBuilderInstance(SwipePlaceHolderView SwipePlaceHolderView){

        SwipePlaceHolderView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setViewHeight(50)
                        .setPaddingTop(-20)
                        .setSwipeInMsgLayoutId(R.layout.swipe_in)
                        .setSwipeOutMsgLayoutId(R.layout.swipe_out));

    }

}

