package com.example.app_in_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.AlphabeticIndex;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.common.collect.ImmutableList;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BillingClient billingClient;
    PurchasesUpdatedListener purchasesUpdatedListener;
    List<ProductDetails> productDetailsList;
    ProductDetails productDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null){
                    for (Purchase purchase : list){
                        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()){
                            hanlePuchase(purchase);
                        }
                    }
                }
            }
        };

        billingClient = BillingClient.newBuilder(MainActivity.this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                connectGoogle();
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    getproductDetail();
                }
            }
        });

        getproductDetail();
    }


    private void getproductDetail() {
//        List<String> productIds = new ArrayList<>();
//        productIds.add("skill uper cut ");

        QueryProductDetailsParams queryProductDetailsParams = QueryProductDetailsParams.newBuilder().setProductList(
                ImmutableList.of(QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("free_image_animal_15_day")
                        .setProductType(BillingClient.ProductType.INAPP).build())
        ).build();

        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams, new ProductDetailsResponseListener() {
                    @Override
                    public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> list) {
                        Log.d("test", "adisubdau ");
                        if (!list.isEmpty()){
                            TextView tileTextView = findViewById(R.id.tvNane);
                            TextView priceTextView = findViewById(R.id.tvPrice);
                            Button btn_Price = findViewById(R.id.btnPrice);
                            productDetails = list.get(0);
                            tileTextView.setText(productDetails.getName());
                            priceTextView.setText(productDetails.getOneTimePurchaseOfferDetails().getFormattedPrice());

                            btn_Price.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParams =
                                            ImmutableList.of(
                                                    BillingFlowParams.ProductDetailsParams.newBuilder()
                                                            .setProductDetails(productDetails)
                                                            .build()
                                            );
                                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                            .setProductDetailsParamsList(productDetailsParams)
                                            .build();

                                    billingClient.launchBillingFlow(MainActivity.this, billingFlowParams);
                                }
                            });
                        }
                    }
                }
        );
    }

    private void connectGoogle(){}

    private void hanlePuchase(Purchase purchase) {
        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

        ConsumeResponseListener consumeResponseListener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){

                }
            }
        };

        billingClient.consumeAsync(consumeParams, consumeResponseListener);

    }

}