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

package com.seanlab.qrcode.mlkit.md.java.productsearch;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;
import com.seanlab.qrcode.mlkit.md.java.objectdetection.DetectedObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//import com.google.firebase.ml.md.java.objectdetection.DetectedObject;


/** A fake search engine to help simulate the complete work flow. */
public class SearchEngineCloudText {

  private static final String TAG = "SearchEngineCloudland";


    private Bitmap searchbitmap;
    private final FirebaseVisionTextRecognizer detector;


    public interface SearchResultListener {
    void onSearchCompleted(DetectedObject object, List<Product> productList);
  }

  private final RequestQueue searchRequestQueue;
  private final ExecutorService requestCreationExecutor;

  public SearchEngineCloudText(Context context) {
    searchRequestQueue = Volley.newRequestQueue(context);
    requestCreationExecutor = Executors.newSingleThreadExecutor();

    //initial .setLanguageHints(Arrays.asList("en", "hi"))

      FirebaseVisionCloudTextRecognizerOptions options = new FirebaseVisionCloudTextRecognizerOptions.Builder()
              .setLanguageHints(Arrays.asList("en", "hi"))
              .build();

      detector = FirebaseVision.getInstance().getCloudTextRecognizer(options);

  }

  public void search(DetectedObject object, SearchResultListener listener) {
    // Crops the object image out of the full image is expensive, so do it off the UI thread.

       Tasks.call(requestCreationExecutor, () -> createRequest(object))
               .addOnSuccessListener(new OnSuccessListener<JsonObjectRequest>() {
                   @Override
                   public void onSuccess(JsonObjectRequest jsonObjectRequest) {
                       //sean

                       searchbitmap = object.getBitmap();
                       FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(searchbitmap);
                       detector.processImage(image)
                         //-----------------------------------------------------------------------
                       .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                           @Override
                           public void onSuccess(FirebaseVisionText firebaseVisionText) {
                               Log.d(TAG, "block.size: " + firebaseVisionText.getTextBlocks().size());

                               List<Product> productList = new ArrayList<>();
                               for(FirebaseVisionText.TextBlock block: firebaseVisionText.getTextBlocks()){
                                   Log.d(TAG, "block.text: " + block.getText());

                                   String blockText = block.getText();
                                   Float blockConfidence = block.getConfidence();
                                   String confidence="";
                                   if (blockConfidence==null){
                                       confidence="0.99";
                                   }
                                   Log.d(TAG, "blockConfidence: " + confidence);

                                   // block text -------------------------------------------
                                   /*
                                   if (blockConfidence==null){
                                       productList.add(
                                               new Product("", block.getText() , confidence +" %" ));
                                   }else{
                                       productList.add(
                                               new Product("", block.getText() , blockConfidence.toString() +" %" ));
                                   }
                                   */
                                   // --------------------------------------------------------

                                   List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
                                   Point[] blockCornerPoints = block.getCornerPoints();
                                   Rect blockFrame = block.getBoundingBox();

                                   // line
                                   for (FirebaseVisionText.Line line: block.getLines()) {
                                       String lineText = line.getText();
                                       Log.d(TAG, "lineText: " + lineText);
                                       Float lineConfidence = line.getConfidence();
                                       String confidenceline="";
                                       if (lineConfidence==null){
                                           confidenceline="0.99";
                                       }
                                       Log.d(TAG, "lineConfidence: " + confidenceline);
                                       // line text -------------------------------------------
                                       if (lineConfidence==null){
                                           productList.add(
                                                   new Product("", lineText , confidenceline +" %" ));
                                       }else{
                                           productList.add(
                                                   new Product("", lineText , lineConfidence.toString() +" %" ));
                                       }
                                       // -----------------------------------------------------

                                       List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                                       Point[] lineCornerPoints = line.getCornerPoints();
                                       Rect lineFrame = line.getBoundingBox();
                                       //element
                                       for (FirebaseVisionText.Element element: line.getElements()) {
                                           String elementText = element.getText();
                                           //Float elementConfidence = Float(0.99);
                                           String confidence3="0.99";
                                           Log.d(TAG, "elementText: " + elementText);
                                           //Log.d(TAG, "elementConfidence: " + elementConfidence);
                                           //productList.add(
                                           //        new Product("", elementText , confidence3 +" %" ));
                                           List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
                                           Point[] elementCornerPoints = element.getCornerPoints();
                                           Rect elementFrame = element.getBoundingBox();
                                       }
                                   }

                               }
                               listener.onSearchCompleted(object, productList);
                           }
                       })

                       .addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               List<Product> productList = new ArrayList<>();
                               for (int i = 0; i < 5; i++) {
                                   productList.add(
                                           new Product( "", "MLkit Fail " + i, "Product subtitle " + i));
                               }
                               listener.onSearchCompleted(object, productList);
                           }
                       })
                       //-------------------------------------------------------------
                        ;

                   }
               })

        .addOnFailureListener(
            e -> {
              Log.e(TAG, "Failed to create product search request!", e);
              // Remove the below dummy code after your own product search backed hooked up.
              List<Product> productList = new ArrayList<>();
              for (int i = 0; i < 3; i++) {
                productList.add(
                    new Product( "", "Search Fail " + i, "Product subtitle " + i));
              }
              listener.onSearchCompleted(object, productList);
            });

  }



  private  JsonObjectRequest createRequest(DetectedObject searchingObject)  {
    byte[] objectImageData = searchingObject.getImageData();

    if (objectImageData == null) {
      //throw new Exception("Failed to get object image data!");
    }

    // Hooks up with your own product search backend here.
    Log.e(TAG, "SEAN:image lenght" +objectImageData.length);
    //inputBitmap=searchingObject.getBitmap();
      List<Product> productList = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
          productList.add(
                  new Product( "", "Product title " + i, "Product subtitle " + i));
      }

      return null;
    //throw new Exception("Hooks up with your own product search backend.");
  }

  public void shutdown() {
    searchRequestQueue.cancelAll(TAG);
    requestCreationExecutor.shutdown();
  }






}
