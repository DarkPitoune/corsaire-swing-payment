package com.sumup.corsaireduswing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.sumup.merchant.reader.models.TransactionInfo;
import com.sumup.merchant.reader.api.SumUpAPI;
import com.sumup.merchant.reader.api.SumUpLogin;
import com.sumup.merchant.reader.api.SumUpPayment;

import java.math.BigDecimal;
import java.util.UUID;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE_LOGIN = 1;
    private static final int REQUEST_CODE_PAYMENT = 2;
    private static final int REQUEST_CODE_CARD_READER_PAGE = 4;

    private TextView mResultCode;
    private TextView mResultMessage;
    private TextView mTxCode;
    private TextView mReceiptSent;
    private TextView mTxInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        login();

        Button login = (Button) findViewById(R.id.button_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Please go to https://me.sumup.com/developers to get your Affiliate Key by entering the application ID of your app. (e.g. com.sumup.sdksampleapp)
                login();
            }
        });

        Button logMerchant = (Button) findViewById(R.id.button_log_merchant);
        logMerchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!SumUpAPI.isLoggedIn()) {
                    mResultCode.setText("Result code: " + SumUpAPI.Response.ResultCode.ERROR_NOT_LOGGED_IN);
                    mResultMessage.setText("Message: Not logged in");
                } else {
                    mResultCode.setText("Result code: " + SumUpAPI.Response.ResultCode.SUCCESSFUL);
                    mResultMessage.setText(
                            String.format("Currency: %s, Merchant Code: %s", SumUpAPI.getCurrentMerchant().getCurrency().getIsoCode(),
                                    SumUpAPI.getCurrentMerchant().getMerchantCode()));
                }
            }
        });

        Button btnCharge2 = (Button) findViewById(R.id.button_charge_2);
        btnCharge2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payAmount(2);
            }
        });


        Button btnCharge5 = (Button) findViewById(R.id.button_charge_5);
        btnCharge5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payAmount(5);
            }
        });
        Button btnCharge10 = (Button) findViewById(R.id.button_charge_10);
        btnCharge10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payAmount(10);
            }
        });

        Button btnCharge20 = (Button) findViewById(R.id.button_charge_20);
        btnCharge20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payAmount(20);
            }
        });

        Button btnCharge50 = (Button) findViewById(R.id.button_charge_50);
        btnCharge50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payAmount(50);
            }
        });

        Button openDebug = (Button) findViewById(R.id.open_debug);
        openDebug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout debugPanel = (LinearLayout) findViewById(R.id.debug);
                debugPanel.setVisibility(View.VISIBLE);
            }});

        Button cardReaderPage = (Button) findViewById(R.id.button_card_reader_page);
        cardReaderPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SumUpAPI.openCardReaderPage(MainActivity.this, REQUEST_CODE_CARD_READER_PAGE);
            }
        });

        Button prepareCardTerminal = (Button) findViewById(R.id.button_prepare_card_terminal);
        prepareCardTerminal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SumUpAPI.prepareForCheckout();
            }
        });

        Button btnLogout = (Button) findViewById(R.id.button_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SumUpAPI.logout();
            }
        });
    }

    private void login() {
        SumUpLogin sumupLogin = SumUpLogin.builder("7ca84f17-84a5-4140-8df6-6ebeed8540fc").build();
        SumUpAPI.openLoginActivity(MainActivity.this, sumupLogin, REQUEST_CODE_LOGIN);
    }

    private void payAmount(int value) {
        SumUpPayment payment = SumUpPayment.builder()
                // mandatory parameters
                .total(new BigDecimal(value)) // minimum 1.00
                .currency(SumUpPayment.Currency.EUR)
                // optional: add details
                .title("Les Corsaires du Swing")
                .skipSuccessScreen()
                // optional: foreign transaction ID, must be unique!
                .foreignTransactionId(UUID.randomUUID().toString()) // can not exceed 128 chars
                .build();

        SumUpAPI.checkout(MainActivity.this, payment, REQUEST_CODE_PAYMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        resetViews();

        switch (requestCode) {
            case REQUEST_CODE_LOGIN:
            case REQUEST_CODE_CARD_READER_PAGE:
                setResultCodeAndMessage(data);
                break;

            case REQUEST_CODE_PAYMENT:
                if (data != null) {
                    Bundle extra = data.getExtras();

                    mResultCode.setText("Result code: " + extra.getInt(SumUpAPI.Response.RESULT_CODE));
                    mResultMessage.setText("Message: " + extra.getString(SumUpAPI.Response.MESSAGE));

                    String txCode = extra.getString(SumUpAPI.Response.TX_CODE);
                    mTxCode.setText(txCode == null ? "" : "Transaction Code: " + txCode);

                    boolean receiptSent = extra.getBoolean(SumUpAPI.Response.RECEIPT_SENT);
                    mReceiptSent.setText("Receipt sent: " + receiptSent);

                    TransactionInfo transactionInfo = extra.getParcelable(SumUpAPI.Response.TX_INFO);
                    mTxInfo.setText(transactionInfo == null ? "" : "Transaction Info : " + transactionInfo);
                }
                break;

            default:
                break;
        }
    }

    private void setResultCodeAndMessage(Intent data) {
        if (data != null) {
            Bundle extra = data.getExtras();
            mResultCode.setText("Result code: " + extra.getInt(SumUpAPI.Response.RESULT_CODE));
            mResultMessage.setText("Message: " + extra.getString(SumUpAPI.Response.MESSAGE));
        }
    }

    private void resetViews() {
        mResultCode.setText("");
        mResultMessage.setText("");
        mTxCode.setText("");
        mReceiptSent.setText("");
        mTxInfo.setText("");
    }

    private void findViews() {
        mResultCode = (TextView) findViewById(R.id.result);
        mResultMessage = (TextView) findViewById(R.id.result_msg);
        mTxCode = (TextView) findViewById(R.id.tx_code);
        mReceiptSent = (TextView) findViewById(R.id.receipt_sent);
        mTxInfo = (TextView) findViewById(R.id.tx_info);
    }
}
