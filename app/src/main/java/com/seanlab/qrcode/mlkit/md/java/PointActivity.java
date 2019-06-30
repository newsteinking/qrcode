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

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
import com.seanlab.qrcode.mlkit.R;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.seanlab.qrcode.mlkit.md.java.settings.AppStorage;


/** Entry activity to select the detection mode. */
public class PointActivity extends AppCompatActivity {


  private  TextView titleView;
  private ImageView pointimage;
  private  TextView point;
  private ImageButton PointPlusButton;
  private  TextView pointplus;
  private ImageButton PointMinusButton;
  private  TextView pointminus;

  private Integer PointValue;

  DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
  DatabaseReference conditionRef = mRootRef.child("qrcodeactivate");

  AppStorage storage;

  @Override
  protected void onCreate(@Nullable Bundle bundle) {
    super.onCreate(bundle);



    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    setContentView(R.layout.activity_main_point);

    storage=new AppStorage(this);


    addListenerOnButton();

  }

  @Override
  protected void onResume() {
    super.onResume();
    if (!Utils.allPermissionsGranted(this)) {
      Utils.requestRuntimePermissions(this);
    }
    //close database
    if (FirebaseDatabase.getInstance() != null)
    {
      FirebaseDatabase.getInstance().goOnline();
    }
  }

  public void addListenerOnButton() {

    //
    titleView =(TextView)findViewById(R.id.title1);
    pointimage=(ImageView)findViewById(R.id.imageView1);
    point =(TextView)findViewById(R.id.point);


    PointPlusButton = (ImageButton) findViewById(R.id.btnPointplus);
    pointplus =(TextView)findViewById(R.id.point);
    PointMinusButton = (ImageButton) findViewById(R.id.btnPointminus);
    pointminus =(TextView)findViewById(R.id.point);

    pointimage.setImageResource(R.drawable.mypoint);
    point.setText("100");


    conditionRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        String text = dataSnapshot.getValue(String.class);
        if (text==null)
        {
          PointValue =100;
        } else
        {
          PointValue = Integer.parseInt(text);
          if (PointValue==1200){
            storage.setPurchasedRemoveAds(true);
            storage.setsusribedRemoveAds(true);
          }else
          {
            storage.setPurchasedRemoveAds(false);
            storage.setsusribedRemoveAds(false);
          }
        }
        point.setText(text);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });



    PointPlusButton.setOnClickListener(new Button.OnClickListener() {
      @Override
      public void onClick(View view) {
        // TODO : click event
        PointValue=PointValue+100;
        String text=Integer.toString(PointValue);
        //point.setText("200");
        int add=Integer.parseInt(text);

        conditionRef.setValue(text);



      }
    });
    PointMinusButton.setOnClickListener(new Button.OnClickListener() {
      @Override
      public void onClick(View view) {
        // TODO : click event
        PointValue=PointValue-100;
        String text=Integer.toString(PointValue);
        //String text="-100";
        //point.setText("200");
        conditionRef.setValue(text);

      }
    });
  }


  @Override
  public void onPause() {

    super.onPause();

    if(FirebaseDatabase.getInstance()!=null)
    {
      FirebaseDatabase.getInstance().goOffline();
    }
  }


  @Override
  public void onBackPressed(){


    super.onBackPressed();
    if(FirebaseDatabase.getInstance()!=null)
    {
      FirebaseDatabase.getInstance().goOffline();
    }
  }


}
