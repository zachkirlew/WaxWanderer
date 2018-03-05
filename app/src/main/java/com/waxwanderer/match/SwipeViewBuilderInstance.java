package com.waxwanderer.match;

import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.waxwanderer.R;

public class SwipeViewBuilderInstance {

    //private constructor to avoid client applications to use constructor
    public
    SwipeViewBuilderInstance(SwipePlaceHolderView SwipePlaceHolderView){

        SwipePlaceHolderView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(-20)
                        .setSwipeInMsgLayoutId(R.layout.swipe_in)
                        .setSwipeOutMsgLayoutId(R.layout.swipe_out));
    }

}

