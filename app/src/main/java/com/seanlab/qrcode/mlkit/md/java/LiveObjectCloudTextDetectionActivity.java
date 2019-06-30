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

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.seanlab.qrcode.mlkit.R;
import com.seanlab.qrcode.mlkit.md.common.GraphicOverlayLabel;
import com.seanlab.qrcode.mlkit.md.common.VisionImageProcessor;
import com.seanlab.qrcode.mlkit.md.java.camera.CameraSource;
import com.seanlab.qrcode.mlkit.md.java.camera.CameraSourcePreview;
import com.seanlab.qrcode.mlkit.md.java.camera.GraphicOverlay;
import com.seanlab.qrcode.mlkit.md.java.camera.WorkflowModel;
import com.seanlab.qrcode.mlkit.md.java.objectdetection.MultiObjectProcessor;
import com.seanlab.qrcode.mlkit.md.java.objectdetection.ProminentObjectProcessor;
import com.seanlab.qrcode.mlkit.md.java.productsearch.BottomSheetScrimView;
import com.seanlab.qrcode.mlkit.md.java.productsearch.Product;
import com.seanlab.qrcode.mlkit.md.java.productsearch.ProductAdapter;
//import com.seanlab.qrcode.mlkit.md.java.productsearch.SearchEngineCloudImage;
import com.seanlab.qrcode.mlkit.md.java.productsearch.SearchEngine;
import com.seanlab.qrcode.mlkit.md.java.productsearch.SearchEngineCloudText;
import com.seanlab.qrcode.mlkit.md.java.productsearch.SearchedObject;
import com.seanlab.qrcode.mlkit.md.java.settings.AppStorage;
import com.seanlab.qrcode.mlkit.md.java.settings.PreferenceUtils;
import com.seanlab.qrcode.mlkit.md.java.settings.SettingsActivity;

import java.io.IOException;
import java.util.List;




//sean import com.google.android.material.floatingactionbutton.FloatingActionButton;
// error use FloatingActionButton

/** Demonstrates the object detection and visual search workflow using camera preview. */
public class LiveObjectCloudTextDetectionActivity extends AppCompatActivity implements OnClickListener {

  private static final String TAG = "LiveCloudActivity";

  private CameraSource cameraSource;
  private CameraSourcePreview preview;
  private GraphicOverlay graphicOverlay;
  private View settingsButton;
  private View flashButton;
  private Chip promptChip;
  private AnimatorSet promptChipAnimator;
  private ExtendedFloatingActionButton searchButton;
  // sean private FloatingActionButton searchButton;

  private AnimatorSet searchButtonAnimator;
  private ProgressBar searchProgressBar;
  //private WorkflowModel workflowModel;
  private WorkflowModel workflowModel;
  private WorkflowModel.WorkflowState currentWorkflowState;

  //private SearchEngine searchEngine;
  //sean
  private GraphicOverlayLabel graphicImageOverlay;
  private SearchEngineCloudText searchEngineCloud;
  private Bitmap searchbitmap;
  private VisionImageProcessor imageProcessor;

  private SearchEngine searchNullEngineCloud;





  private BottomSheetBehavior<View> bottomSheetBehavior;
  private BottomSheetScrimView bottomSheetScrimView;
  private RecyclerView productRecyclerView;
  private TextView bottomSheetTitleView;
  private Bitmap objectThumbnailForBottomSheet;
  private boolean slidingSheetUpFromHiddenState;

  // Payment

  // PRODUCT & SUBSCRIPTION IDS
  private static final String PRODUCT_ID = "seanlabml1_1";
  private static final String SUBSCRIPTION_ID = "seanlabml1_1_subscribe";
  private static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgFAv9iG2K2T+JbGGGQnUpiwzu9Y93Vc0G71CBdkjKzypMawNPjJRRoLTg2SJR+QciEfpQy7ffkKYsYOkMViI/NOGeqgIes6tav8+WUMHklOWgfRy76DJwNkgC/MJrpP1Sb5dCQX5Imd3ojm7nwZ0jJIAnDcnND/neSCGOqrfGkiSaQ20B5JdMfF74unF9bWIrmY8gUW18xE+2mL+Hi5DyY14sBMGt2Dq1DvK5nOhDdH+eP5ECR1i5cURJ0pUfmGSDtWEqDnEwP15ZZ/XvuBVBG1hlBzRe79wIxY8mXmE/RQf1PiNIqtW29Kg/h7ksG4ekhwWUZkn78SMWkyKfm+3KwIDAQAB";
  ; // PUT YOUR MERCHANT KEY HERE;
  // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
  // if filled library will provide protection against Freedom alike Play Market simulators
  private static final String MERCHANT_ID="14802266721074175569";

  private BillingProcessor bp;
  private boolean readyToPurchase = false;

    // Real Database
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference conditionRef = mRootRef.child("firebaseactivate");

  private boolean ISPurchase = false;
  private boolean ISSubscribe = false;



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    //searchEngine = new SearchEngine(getApplicationContext());
    //searchEngineCloud = new SearchEngineCloudText(getApplicationContext());

    setContentView(R.layout.activity_live_object_cloud);
    preview = findViewById(R.id.camera_preview);
    graphicOverlay = findViewById(R.id.camera_preview_graphic_overlay);
    graphicOverlay.setOnClickListener(this);
    cameraSource = new CameraSource(graphicOverlay);

    promptChip = findViewById(R.id.bottom_prompt_chip);
    promptChipAnimator =
        (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.bottom_prompt_chip_enter);
    promptChipAnimator.setTarget(promptChip);

    searchButton = findViewById(R.id.product_search_button);
    searchButton.setOnClickListener(this);
    searchButtonAnimator =
        (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.search_button_enter);
    searchButtonAnimator.setTarget(searchButton);

    searchProgressBar = findViewById(R.id.search_progress_bar);

    //sean
    graphicImageOverlay = (GraphicOverlayLabel) findViewById(R.id.previewOverlay);
    if (graphicImageOverlay == null) {
      Log.d(TAG, "graphicOverlay is null");
    }


    setUpBottomSheet();

    findViewById(R.id.close_button).setOnClickListener(this);
    flashButton = findViewById(R.id.flash_button);
    flashButton.setOnClickListener(this);
    settingsButton = findViewById(R.id.settings_button);
    settingsButton.setOnClickListener(this);


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
          }
        }
        //updateTextViews();
      }
    });

    AppStorage storage = new AppStorage(this);
    ISPurchase=storage.purchasedRemoveAds();
    ISSubscribe=storage.subsribedRemoveAds();

      // checkDB
    Log.d(TAG, "purchasedRemoveAds : " + ISPurchase);
    //checkDBValue();
    Log.d(TAG, "subsribedRemoveAds : " + ISSubscribe);


    //searchEngine = new SearchEngine(getApplicationContext());
    if(ISSubscribe) {
      searchEngineCloud = new SearchEngineCloudText(getApplicationContext());
    } else
    {
      searchNullEngineCloud = new SearchEngine(getApplicationContext());
    }

    setUpWorkflowModel();


  }

    public void checkDBValue()
    {
        conditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String text = dataSnapshot.getValue(String.class);
                if (text==null)
                {
                    ISSubscribe =false;
                  Log.d(TAG, "Text Null: " + ISSubscribe);
                } else if (Integer.parseInt(text)==1200)
                {
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

    workflowModel.markCameraFrozen();
    settingsButton.setEnabled(true);
    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    currentWorkflowState = WorkflowModel.WorkflowState.NOT_STARTED;
    cameraSource.setFrameProcessor(
        PreferenceUtils.isMultipleObjectsMode(this)
            ? new MultiObjectProcessor(graphicOverlay, workflowModel)
            : new ProminentObjectProcessor(graphicOverlay, workflowModel));
    workflowModel.setWorkflowState(WorkflowModel.WorkflowState.DETECTING);



  }

  @Override
  protected void onPause() {
    super.onPause();
    currentWorkflowState = WorkflowModel.WorkflowState.NOT_STARTED;
    stopCameraPreview();


  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (cameraSource != null) {
      cameraSource.release();
      cameraSource = null;
    }
    //searchEngine.shutdown();
    //searchEngineCloud.shutdown();
    if(ISSubscribe) {
      searchEngineCloud.shutdown();
    } else
    {
      searchNullEngineCloud.shutdown();
    }




  }

  @Override
  public void onBackPressed() {
    if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
      bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    } else {
      super.onBackPressed();
    }


  }

  @Override
  public void onClick(View view) {
    int id = view.getId();
    if (id == R.id.product_search_button) {
      searchButton.setEnabled(false);
      workflowModel.onSearchButtonClicked();

    } else if (id == R.id.bottom_sheet_scrim_view) {
      bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

    } else if (id == R.id.close_button) {
      onBackPressed();

    } else if (id == R.id.flash_button) {
      if (flashButton.isSelected()) {
        flashButton.setSelected(false);
        cameraSource.updateFlashMode(Camera.Parameters.FLASH_MODE_OFF);
      } else {
        flashButton.setSelected(true);
        cameraSource.updateFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
      }

    } else if (id == R.id.settings_button) {
      // Sets as disabled to prevent the user from clicking on it too fast.
      settingsButton.setEnabled(false);
      startActivity(new Intent(this, SettingsActivity.class));

    }
  }

  private void startCameraPreview() {
    if (!workflowModel.isCameraLive() && cameraSource != null) {
      try {
        workflowModel.markCameraLive();
        preview.start(cameraSource);
      } catch (IOException e) {
        Log.e(TAG, "Failed to start camera preview!", e);
        cameraSource.release();
        cameraSource = null;
      }
    }
  }

  private void stopCameraPreview() {
    if (workflowModel.isCameraLive()) {
      workflowModel.markCameraFrozen();
      flashButton.setSelected(false);
      preview.stop();
    }
  }

  private void setUpBottomSheet() {
    bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
    bottomSheetBehavior.setBottomSheetCallback(
        new BottomSheetBehavior.BottomSheetCallback() {
          @Override
          public void onStateChanged(@NonNull View bottomSheet, int newState) {
            Log.d(TAG, "Bottom sheet new state: " + newState);
            bottomSheetScrimView.setVisibility(
                newState == BottomSheetBehavior.STATE_HIDDEN ? View.GONE : View.VISIBLE);
            graphicOverlay.clear();

            switch (newState) {
              case BottomSheetBehavior.STATE_HIDDEN:
                workflowModel.setWorkflowState(WorkflowModel.WorkflowState.DETECTING);
                break;
              case BottomSheetBehavior.STATE_COLLAPSED:
              case BottomSheetBehavior.STATE_EXPANDED:
              case BottomSheetBehavior.STATE_HALF_EXPANDED:
                slidingSheetUpFromHiddenState = false;
                break;
              case BottomSheetBehavior.STATE_DRAGGING:
              case BottomSheetBehavior.STATE_SETTLING:
              default:
                break;
            }
          }

          @Override
          public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            SearchedObject searchedObject = workflowModel.searchedObject.getValue();
            if (searchedObject == null || Float.isNaN(slideOffset)) {
              return;
            }

            int collapsedStateHeight =
                Math.min(bottomSheetBehavior.getPeekHeight(), bottomSheet.getHeight());
            if (slidingSheetUpFromHiddenState) {
              RectF thumbnailSrcRect =
                  graphicOverlay.translateRect(searchedObject.getBoundingBox());
              bottomSheetScrimView.updateWithThumbnailTranslateAndScale(
                  objectThumbnailForBottomSheet,
                  collapsedStateHeight,
                  slideOffset,
                  thumbnailSrcRect);

            } else {
              bottomSheetScrimView.updateWithThumbnailTranslate(
                  objectThumbnailForBottomSheet, collapsedStateHeight, slideOffset, bottomSheet);
            }
          }
        });

    bottomSheetScrimView = findViewById(R.id.bottom_sheet_scrim_view);
    bottomSheetScrimView.setOnClickListener(this);

    bottomSheetTitleView = findViewById(R.id.bottom_sheet_title);
    productRecyclerView = findViewById(R.id.product_recycler_view);
    productRecyclerView.setHasFixedSize(true);
    productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    productRecyclerView.setAdapter(new ProductAdapter(ImmutableList.of()));
    //sean


  }

  private void setUpWorkflowModel() {
    workflowModel = ViewModelProviders.of(this).get(WorkflowModel.class);

    // Observes the workflow state changes, if happens, update the overlay view indicators and
    // camera preview state.


    workflowModel.workflowState.observe(
        this,
        workflowState -> {
          if (workflowState == null || Objects.equal(currentWorkflowState, workflowState)) {
            return;
          }

          currentWorkflowState = workflowState;
          Log.d(TAG, "Current workflow state: " + currentWorkflowState.name());

          if (PreferenceUtils.isAutoSearchEnabled(this)) {
            stateChangeInAutoSearchMode(workflowState);
          } else {
            stateChangeInManualSearchMode(workflowState);
          }
        });

    // Observes changes on the object to search, if happens, fire product search request.
    ///---------------------------------------------------------------------
    //Payment
    if (ISSubscribe)
    {
      Log.d(TAG, "SEAN:FirebaseVisionImage===>onSuccess");

      workflowModel.objectToSearch.observe(
              this, object -> searchEngineCloud.search(object, workflowModel));


      // Observes changes on the object that has search completed, if happens, show the bottom sheet
      // to present search result.
      workflowModel.searchedObject.observe(
              this,
              searchedObject -> {
                if (searchedObject != null) {
                  List<Product> productList = searchedObject.getProductList();

                  objectThumbnailForBottomSheet = searchedObject.getObjectThumbnail();

                  //sean
                  //getFromCloud();

                  bottomSheetTitleView.setText(
                          getResources()
                                  .getQuantityString(
                                          R.plurals.bottom_sheet_title, productList.size(), productList.size()));
                  productRecyclerView.setAdapter(new ProductAdapter(productList));

                  Log.d(TAG, "searched : " + productList.size());


                  slidingSheetUpFromHiddenState = true;
                  bottomSheetBehavior.setPeekHeight(preview.getHeight() / 2);
                  bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
              });
    }  //if subscribe
    else
    {
      Log.d(TAG, "SEAN:FirebaseVisionImage===>Not Subscribe");
      workflowModel.objectToSearch.observe(
              this, object -> searchNullEngineCloud.search(object, workflowModel));


      // Observes changes on the object that has search completed, if happens, show the bottom sheet
      // to present search result.
      workflowModel.searchedObject.observe(
              this,
              searchedObject -> {
                if (searchedObject != null) {
                  List<Product> productList = searchedObject.getProductList();

                  objectThumbnailForBottomSheet = searchedObject.getObjectThumbnail();

                  //sean
                  //getFromCloud();

                  bottomSheetTitleView.setText(
                          getResources()
                                  .getQuantityString(
                                          R.plurals.bottom_sheet_title, productList.size(), productList.size()));
                  productRecyclerView.setAdapter(new ProductAdapter(productList));

                  Log.d(TAG, "searched : " + productList.size());


                  slidingSheetUpFromHiddenState = true;
                  bottomSheetBehavior.setPeekHeight(preview.getHeight() / 2);
                  bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                  //Payment popup
                  bp.subscribe(this,SUBSCRIPTION_ID);

                }
              });
    }
    /*
    workflowModel.objectToSearch.observe(
            this, object -> searchEngineCloud.search(object, workflowModel));



    // Observes changes on the object that has search completed, if happens, show the bottom sheet
    // to present search result.
    workflowModel.searchedObject.observe(
            this,
            searchedObject -> {
              if (searchedObject != null) {
                List<Product> productList = searchedObject.getProductList();

                objectThumbnailForBottomSheet = searchedObject.getObjectThumbnail();


                bottomSheetTitleView.setText(
                        getResources()
                                .getQuantityString(
                                        R.plurals.bottom_sheet_title, productList.size(), productList.size()));
                productRecyclerView.setAdapter(new ProductAdapter(productList));

                Log.d(TAG, "searched : " + productList.size());

                slidingSheetUpFromHiddenState = true;
                bottomSheetBehavior.setPeekHeight(preview.getHeight() / 2);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
              }
            });
       */
      ///----------------------------------------------------------
  }


  private void stateChangeInAutoSearchMode(WorkflowModel.WorkflowState workflowState) {
    boolean wasPromptChipGone = (promptChip.getVisibility() == View.GONE);

    //sean searchButton.setVisibility(View.GONE);
    searchButton.setVisibility(View.GONE);
    searchProgressBar.setVisibility(View.GONE);
    switch (workflowState) {
      case DETECTING:
      case DETECTED:
      case CONFIRMING:
        promptChip.setVisibility(View.VISIBLE);
        promptChip.setText(
            workflowState == WorkflowModel.WorkflowState.CONFIRMING
                ? R.string.prompt_hold_camera_steady
                : R.string.prompt_point_at_an_object);
        startCameraPreview();
        break;
      case CONFIRMED:
        promptChip.setVisibility(View.VISIBLE);
        promptChip.setText(R.string.prompt_searching);



        stopCameraPreview();
        break;
      case SEARCHING:
        searchProgressBar.setVisibility(View.VISIBLE);
        promptChip.setVisibility(View.VISIBLE);
        promptChip.setText(R.string.prompt_searching);
        stopCameraPreview();
        break;
      case SEARCHED:
        promptChip.setVisibility(View.GONE);

        stopCameraPreview();
        break;
      default:
        promptChip.setVisibility(View.GONE);
        break;
    }

    boolean shouldPlayPromptChipEnteringAnimation =
        wasPromptChipGone && (promptChip.getVisibility() == View.VISIBLE);
    if (shouldPlayPromptChipEnteringAnimation && !promptChipAnimator.isRunning()) {
      promptChipAnimator.start();
    }
  }

  private void stateChangeInManualSearchMode(WorkflowModel.WorkflowState workflowState) {
    boolean wasPromptChipGone = (promptChip.getVisibility() == View.GONE);
    boolean wasSearchButtonGone = (searchButton.getVisibility() == View.GONE);

    searchProgressBar.setVisibility(View.GONE);
    switch (workflowState) {
      case DETECTING:
      case DETECTED:
      case CONFIRMING:
        promptChip.setVisibility(View.VISIBLE);
        promptChip.setText(R.string.prompt_point_at_an_object);
        searchButton.setVisibility(View.GONE);
        startCameraPreview();
        break;
      case CONFIRMED:
        promptChip.setVisibility(View.GONE);
        searchButton.setVisibility(View.VISIBLE);
        searchButton.setEnabled(true);
        searchButton.setBackgroundColor(Color.WHITE);
        startCameraPreview();
        break;
      case SEARCHING:
        promptChip.setVisibility(View.GONE);
        searchButton.setVisibility(View.VISIBLE);
        searchButton.setEnabled(false);
        searchButton.setBackgroundColor(Color.GRAY);
        searchProgressBar.setVisibility(View.VISIBLE);
        stopCameraPreview();
        break;
      case SEARCHED:
        promptChip.setVisibility(View.GONE);
        searchButton.setVisibility(View.GONE);
        stopCameraPreview();
        break;
      default:
        promptChip.setVisibility(View.GONE);
        searchButton.setVisibility(View.GONE);
        break;
    }

    boolean shouldPlayPromptChipEnteringAnimation =
        wasPromptChipGone && (promptChip.getVisibility() == View.VISIBLE);
    if (shouldPlayPromptChipEnteringAnimation && !promptChipAnimator.isRunning()) {
      promptChipAnimator.start();
    }

    boolean shouldPlaySearchButtonEnteringAnimation =
        wasSearchButtonGone && (searchButton.getVisibility() == View.VISIBLE);
    if (shouldPlaySearchButtonEnteringAnimation && !searchButtonAnimator.isRunning()) {
      searchButtonAnimator.start();
    }
  }

  //Payment
  private void showToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
  }

}