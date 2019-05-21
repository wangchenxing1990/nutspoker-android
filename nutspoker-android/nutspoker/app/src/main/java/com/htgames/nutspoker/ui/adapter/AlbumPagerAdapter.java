package com.htgames.nutspoker.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.view.AlbumView;

import java.util.ArrayList;

/**
 * Created by 20150726 on 2015/10/20.
 */
public class AlbumPagerAdapter extends PagerAdapter {
    Context context;
    ArrayList<String> imgList;
    private int defualtPhotoId = R.mipmap.user_photo_defualt;

    public AlbumPagerAdapter(Context context , ArrayList<String> imgList , int albumType) {
        this.context = context;
        this.imgList = imgList;
        if(albumType == AlbumView.ALBUM_TYPE_USER){
            defualtPhotoId = R.mipmap.user_photo_defualt;
        } else{
//            defualtPhotoId = R.mipmap.club_photo_defualt;
            defualtPhotoId = R.mipmap.user_photo_defualt;
        }
    }

    @Override
    public int getCount() {
        return imgList == null ? 0 : imgList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView iv = new ImageView(context);
        iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT));
        iv.setImageResource(defualtPhotoId);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        container.addView(iv);
        return iv;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        container.removeView((View)object);
    }
}
