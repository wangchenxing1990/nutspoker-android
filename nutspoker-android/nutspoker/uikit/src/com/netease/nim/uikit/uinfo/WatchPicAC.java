package com.netease.nim.uikit.uinfo;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.netease.nim.uikit.ImageLoaderKit;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.activity.TActionBarActivity;
import com.netease.nim.uikit.common.ui.dialog.CustomAlertDialog;
import com.netease.nim.uikit.common.ui.imageview.BaseZoomableImageView;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.ui.imageview.ImageGestureListener;
import com.netease.nim.uikit.common.ui.imageview.MultiTouchZoomableImageView;
import com.netease.nim.uikit.common.util.C;
import com.netease.nim.uikit.common.util.file.AttachmentStore;
import com.netease.nim.uikit.common.util.storage.StorageUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.nav.UIUrl;
import com.netease.nim.uikit.nav.UrlConstants;
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.NonViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by 周智慧 on 17/2/15.   参考WatchMessagePictureActivity.java的代码
 */
@UIUrl(urls = UrlConstants.WATCH_PIC_AC)
public class WatchPicAC extends TActionBarActivity {
    private BaseZoomableImageView image;
    private Bitmap mBitmap;
    private DisplayImageOptions options = HeadImageView.createImageOptions();
    protected CustomAlertDialog alertDialog;
    NimUserInfo userInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        actionBar = getSupportActionBar();
//        actionBar.hide();
        userInfo = (NimUserInfo) getIntent().getSerializableExtra(UrlConstants.WATCH_PIC_AC_USER_INFO);
        image = new MultiTouchZoomableImageView(this);
        if (userInfo.getGenderEnum() == GenderEnum.MALE) {
            image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.default_male_head));
        } else if (userInfo.getGenderEnum() == GenderEnum.FEMALE) {
            image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.default_female_head));
        } else {
            image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.default_male_head));
        }
        boolean needLoad = userInfo != null && ImageLoaderKit.isImageUriValid(userInfo.getAvatar());
        if (needLoad) {
            image.setTag(userInfo.getAccount());
        }
        String avater = userInfo != null ? userInfo.getAvatar() : null;
        int thumbSize = ScreenUtil.screenWidth;
        final String thumbUrl = HeadImageView.makeAvatarThumbNosUrl(avater, thumbSize);
        ImageLoader.getInstance().displayImage(thumbUrl, new NonViewAware(new ImageSize(thumbSize, thumbSize), ViewScaleType.CROP), options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (image.getTag() != null && image.getTag().equals(userInfo.getAccount())) {
                    mBitmap = loadedImage;
                    image.setImageBitmap(loadedImage);
                }
            }
        });

        image.setImageGestureListener(new ImageGestureListener() {
            @Override
            public void onImageGestureSingleTapConfirmed() {
                finish();
            }
            @Override
            public void onImageGestureLongPress() {
                showWatchPictureAction();
            }
            @Override
            public void onImageGestureFlingDown() {
            }
        });

        setContentView(image);
    }

    @Override
    protected boolean toggleOverridePendingTransition() {
        return true;
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.RIGHT;
    }

    // 图片长按
    protected  void showWatchPictureAction() {
        if (alertDialog == null) {
            alertDialog = new CustomAlertDialog(this);
        }
        if (alertDialog.isShowing()) {
            alertDialog.dismiss();
            return;
        }
        alertDialog.clearData();
        String path = "ddd";//((ImageAttachment) message.getAttachment()).getPath();//getThumbPath();by 周智慧：老的代码为啥要用getThumbPath改用getPath   // : 2017/2/10
        if (TextUtils.isEmpty(path)) {
            return;
        }
        String title;
        if (!TextUtils.isEmpty(/*((ImageAttachment) message.getAttachment()).getPath())*/"d")) {
            title = getString(R.string.save_to_device);
            alertDialog.addItem(title, new CustomAlertDialog.onSeparateItemClickListener() {
                @Override
                public void onClick() {
                    savePicture();
                }
            });
        }
        alertDialog.show();
    }

    // 保存图片
    public void savePicture() {
        if (mBitmap == null) {
            return;
        }
        String file_path = StorageUtil.getSystemImagePath();
        File dir = new File(file_path);
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dir, userInfo.getAccount() + "_" + mBitmap.toString() + ".jpg");
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
            Toast.makeText(WatchPicAC.this, getString(R.string.picture_save_to), Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(WatchPicAC.this, getString(R.string.picture_save_fail), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(WatchPicAC.this, getString(R.string.picture_save_fail), Toast.LENGTH_LONG).show();
        } finally {
            if (fOut != null) {
                try {
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
//        ImageAttachment attachment = (ImageAttachment) message.getAttachment();
//        String path = attachment.getPath();
//        if (TextUtils.isEmpty(path)) {
//            return;
//        }
//
//        String srcFilename = attachment.getFileName();
//        //默认jpg
//        String extension = TextUtils.isEmpty(attachment.getExtension()) ? "jpg" : attachment.getExtension();
//        srcFilename += ("." + extension);
//
//        String picPath = StorageUtil.getSystemImagePath();
//        String dstPath = picPath + srcFilename;
//        if (AttachmentStore.copy(path, dstPath) != -1) {
//            try {
//                ContentValues values = new ContentValues(2);
//                values.put(MediaStore.Images.Media.MIME_TYPE, C.MimeType.MIME_JPEG);
//                values.put(MediaStore.Images.Media.DATA, dstPath);
//                getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//                Toast.makeText(WatchPicAC.this, getString(R.string.picture_save_to), Toast.LENGTH_LONG).show();
//            } catch (Exception e) {
//                // may be java.lang.UnsupportedOperationException
//                Toast.makeText(WatchPicAC.this, getString(R.string.picture_save_fail), Toast.LENGTH_LONG).show();
//            }
//        } else {
//            Toast.makeText(WatchPicAC.this, getString(R.string.picture_save_fail), Toast.LENGTH_LONG).show();
//        }
    }
}
