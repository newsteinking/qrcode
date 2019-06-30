/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seanlab.qrcode.mlkit.md.java;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.seanlab.qrcode.mlkit.R;

import com.seanlab.qrcode.mlkit.md.java.settings.AppStorage;

/** Entry activity to select the detection mode. */
public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";

  private enum DetectionMode {
    ODT_LIVE_BARCODE(R.drawable.barcode_scanning2,R.string.mode_odt_live_barcode_title, R.string.mode_odt_live_barcode_subtitle),
    ODT_LIVE_MYPOINT(R.drawable.mypoint,R.string.mode_odt_live_mypoint_title, R.string.mode_odt_live_mypoint_subtitle),;
    //ODT_LIVE_IMAGE_LABEL(R.drawable.image_labelling2,R.string.mode_odt_live_image_label_title, R.string.mode_odt_live_image_label_subtitle),
    //ODT_LIVE_TEXT(R.drawable.text_recognition2,R.string.mode_odt_live_text_title, R.string.mode_odt_live_text_subtitle),
    //ODT_LIVE_MYPOINT(R.drawable.mypoint,R.string.mode_odt_live_mypoint_title, R.string.mode_odt_live_mypoint_subtitle),;

   // ODT_LIVE_IMAGE(R.drawable.image_labelling,R.string.mode_odt_live_image_title, R.string.mode_odt_live_image_subtitle),
   // ODT_LIVE_TEXT(R.drawable.text_recognition,R.string.mode_odt_live_text_title, R.string.mode_odt_live_text_subtitle),
   // ODT_LIVE_LANDMARK(R.drawable.landmark_identification,R.string.mode_odt_live_landmark_title, R.string.mode_odt_live_landmark_subtitle);
    //ODT_LIVE_FACE(R.drawable.face_detection,R.string.mode_odt_live_face_title, R.string.mode_odt_live_face_subtitle);
    /*
    ODT_LIVE(R.string.mode_odt_live_image_title, R.string.mode_odt_live_image_subtitle),
    ODT_STATIC(R.string.mode_odt_static_title, R.string.mode_odt_static_subtitle),
    BARCODE_LIVE(R.string.mode_barcode_live_title, R.string.mode_barcode_live_subtitle);
    */
    //sean
    private final int imageResId;

    private final int titleResId;
    private final int subtitleResId;

    DetectionMode(int imageResId,int titleResId, int subtitleResId) {
      this.imageResId=imageResId;
      this.titleResId = titleResId;
      this.subtitleResId = subtitleResId;
    }
  }

  // Payment
  // PRODUCT & SUBSCRIPTION IDS
  private static final String PRODUCT_ID = "seanlabqrcode";
  private static final String SUBSCRIPTION_ID = "seanlabqrcode_subscribe";
  private static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgFAv9iG2K2T+JbGGGQnUpiwzu9Y93Vc0G71CBdkjKzypMawNPjJRRoLTg2SJR+QciEfpQy7ffkKYsYOkMViI/NOGeqgIes6tav8+WUMHklOWgfRy76DJwNkgC/MJrpP1Sb5dCQX5Imd3ojm7nwZ0jJIAnDcnND/neSCGOqrfGkiSaQ20B5JdMfF74unF9bWIrmY8gUW18xE+2mL+Hi5DyY14sBMGt2Dq1DvK5nOhDdH+eP5ECR1i5cURJ0pUfmGSDtWEqDnEwP15ZZ/XvuBVBG1hlBzRe79wIxY8mXmE/RQf1PiNIqtW29Kg/h7ksG4ekhwWUZkn78SMWkyKfm+3KwIDAQAB";
  ; // PUT YOUR MERCHANT KEY HERE;
  // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
  // if filled library will provide protection against Freedom alike Play Market simulators
  private static final String MERCHANT_ID="14802266721074175569";

  private BillingProcessor bp;
  private boolean readyToPurchase = false;

  // Real Database
  DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
  DatabaseReference conditionRef = mRootRef.child("qrcodeactivate");
  private boolean ISPurchase = false;
  private boolean ISSubscribe = false;






  @Override
  protected void onCreate(@Nullable Bundle bundle) {
    super.onCreate(bundle);

    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    setContentView(R.layout.activity_main_google);

    RecyclerView modeRecyclerView = findViewById(R.id.mode_recycler_view);
    modeRecyclerView.setHasFixedSize(true);
    modeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    modeRecyclerView.setAdapter(new ModeItemAdapter(DetectionMode.values()));

    AppStorage storage = new AppStorage(this);
    ISPurchase=storage.purchasedRemoveAds();
    ISSubscribe=storage.subsribedRemoveAds();
    Log.d(TAG, "ISPurchase : " + ISPurchase);
    Log.d(TAG, "ISSubscribe : " + ISSubscribe);

    //storage.setPurchasedRemoveAds(ISPurchase);
    //storage.setsusribedRemoveAds(ISSubscribe);

    //Payment
    if(!BillingProcessor.isIabServiceAvailable(this)) {
      showToast("In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
    }
    bp = new BillingProcessor(this, LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
      @Override
      public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        //showToast("onProductPurchased: " + productId);
        Log.d(TAG, "onProductPurchased : " + productId);
        ISPurchase=true;
        storage.setPurchasedRemoveAds(ISPurchase);

        //updateTextViews();
      }
      @Override
      public void onBillingError(int errorCode, @Nullable Throwable error) {
        //showToast("onBillingError: " + Integer.toString(errorCode));
        Log.d(TAG, "onBillingError : " + Integer.toString(errorCode));
      }
      @Override
      public void onBillingInitialized() {
        //showToast("onBillingInitialized");
        Log.d(TAG, "onBillingInitialized : " );
        readyToPurchase = true;
        //updateTextViews();
      }
      @Override
      public void onPurchaseHistoryRestored() {
        showToast("onPurchaseHistoryRestored");
        Log.d(TAG, "onPurchaseHistoryRestored : " );
        for(String sku : bp.listOwnedProducts())
          Log.d(TAG, "Owned Managed Product: " + sku);
        for(String sku : bp.listOwnedSubscriptions()) {
          Log.d(TAG, "Owned Subscription: " + sku);
          Log.d(TAG, "Owned Subscription: " + sku);
          if (sku == SUBSCRIPTION_ID) {
            Log.d(TAG, "Owned Subscription: " + SUBSCRIPTION_ID);
            ISSubscribe = true;
            storage.setsusribedRemoveAds(ISSubscribe);
          }
        }
        //updateTextViews();
      }
    });


    //checkDBValue();

  }

  public void checkDBValue()
  {
    conditionRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        String text = dataSnapshot.getValue(String.class);
        if (text==null)
        {
          ISPurchase=false;
          ISSubscribe =false;
          Log.d(TAG, "Text Null: " + ISSubscribe);
        } else if (Integer.parseInt(text)==1200)
        {
          ISPurchase=true;
          ISSubscribe = true;
          Log.d(TAG, "Text 1200: " + ISSubscribe);
        }

      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Log.d(TAG, "databaseError: " );
      }
    });

  }




  @Override
  protected void onResume() {
    super.onResume();
    if (!Utils.allPermissionsGranted(this)) {
      Utils.requestRuntimePermissions(this);
    }
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (requestCode == Utils.REQUEST_CODE_PHOTO_LIBRARY
        && resultCode == Activity.RESULT_OK
        && data != null) {
      Intent intent = new Intent(this, StaticObjectDetectionActivity.class);
      intent.setData(data.getData());
      startActivity(intent);
    } else {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

  private class ModeItemAdapter extends RecyclerView.Adapter<ModeItemAdapter.ModeItemViewHolder> {

    private final DetectionMode[] detectionModes;

    ModeItemAdapter(DetectionMode[] detectionModes) {
      this.detectionModes = detectionModes;
    }

    @NonNull
    @Override
    public ModeItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      return new ModeItemViewHolder(
          LayoutInflater.from(parent.getContext())
              .inflate(R.layout.detection_mode_item_ml, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ModeItemViewHolder modeItemViewHolder, int position) {
      modeItemViewHolder.bindDetectionMode(detectionModes[position]);
    }

    @Override
    public int getItemCount() {
      return detectionModes.length;
    }

    private class ModeItemViewHolder extends RecyclerView.ViewHolder {

      private final ImageView imgView;
      private final TextView titleView;
      private final TextView subtitleView;

      ModeItemViewHolder(@NonNull View view) {
        super(view);
        imgView=view.findViewById(R.id.iViewApi);
        titleView = view.findViewById(R.id.mode_title);
        subtitleView = view.findViewById(R.id.mode_subtitle);
      }

      void bindDetectionMode(DetectionMode detectionMode) {

        imgView.setImageResource(detectionMode.imageResId);
        titleView.setText(detectionMode.titleResId);
        subtitleView.setText(detectionMode.subtitleResId);
        itemView.setOnClickListener(
            view -> {
              Activity activity = MainActivity.this;
              switch (detectionMode) {
                case ODT_LIVE_BARCODE:
                  activity.startActivity(new Intent(activity, LiveBarcodeScanningActivity.class));
                  break;

                case ODT_LIVE_MYPOINT:
                  activity.startActivity(new Intent(activity, com.seanlab.qrcode.mlkit.md.java.PointActivity.class));
                  break;

                  /*
                case ODT_LIVE_IMAGE_LABEL:
                  //activity.startActivity(new Intent(activity, LiveObjectCloudDetectionActivity.class));
                  activity.startActivity(new Intent(activity, LiveObjectCloudDetectionPayActivity.class));
                  break;
                case ODT_LIVE_TEXT:
                  //Utils.openImagePicker(activity);
                  activity.startActivity(new Intent(activity, com.seanlab.qrcode.mlkit.md.java.LiveObjectCloudTextDetectionActivity.class));
                  break;

                case ODT_LIVE_MYPOINT:
                  activity.startActivity(new Intent(activity, com.seanlab.qrcode.mlkit.md.java.PointActivity.class));
                  break;
                  */
                  /*
                case ODT_LIVE_HOME:
                  activity.startActivity(new Intent(activity, WebHomeActivity.class));
                  break;
                case ODT_LIVE_MYIMG:
                  activity.startActivity(new Intent(activity, LoginActivity.class));
                  break;
                case ODT_LIVE_MYPOINT:
                  activity.startActivity(new Intent(activity, PointActivity.class));
                  break;
                  */
                  /*
                //case ODT_LIVE_IMAGE:
                //  activity.startActivity(new Intent(activity, com.seanlab.qrcode.mlkit.md.java.LiveObjectCloudImageDetectionActivity.class));
                //  break;
                case ODT_LIVE_TEXT:
                  //Utils.openImagePicker(activity);
                  activity.startActivity(new Intent(activity, com.seanlab.qrcode.mlkit.md.java.LiveObjectCloudTextDetectionActivity.class));
                  break;

                case ODT_LIVE_LANDMARK:
                  activity.startActivity(new Intent(activity, com.seanlab.qrcode.mlkit.md.java.LiveObjectCloudLandDetectionActivity.class));
                  break;
                //case ODT_LIVE_FACE:
                //  activity.startActivity(new Intent(activity, com.seanlab.qrcode.mlkit.md.java.LiveObjectDetectionActivity.class));
                 // break;
                 */
                /*
                case ODT_LIVE:
                  activity.startActivity(new Intent(activity, com.seanlab.qrcode.mlkit.md.java.LiveObjectDetectionActivity.class));
                  break;
                case ODT_STATIC:
                  Utils.openImagePicker(activity);
                  break;
                case BARCODE_LIVE:
                  activity.startActivity(new Intent(activity, com.seanlab.qrcode.mlkit.md.java.LiveBarcodeScanningActivity.class));
                  break;
                  */
              }
            });
      }
    }
  }

  //Payment
  private void showToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
  }

}
