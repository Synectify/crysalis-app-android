package com.tangem.domain.wallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.tangem.Constant;
import com.tangem.tangemcard.data.Blockchain;
import com.tangem.tangemcard.data.TangemCard;


public class TangemContext {
//    public static final String EXTRA_BLOCKCHAIN_DATA = "BLOCKCHAIN_DATA";
    private Context context;
    private TangemCard card;
    private CoinData coinData;
    private String error;
    private String message;


    public TangemContext() {

    }

    public TangemContext(TangemCard card) {
        this.card = card;
    }

    public Blockchain getBlockchain() {
        if (card == null) return Blockchain.Unknown;
        return card.getBlockchain();
    }

    public void setBlockchain(Blockchain blockchain) {
        if (card == null) return;
        card.setBlockchain(blockchain);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public TangemCard getCard() {
        return card;
    }

    public void setCard(TangemCard card) {
        this.card = card;
    }

    public CoinData getCoinData() {
        return coinData;
    }

    public void setCoinData(CoinData coinData) {
        this.coinData = coinData;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setMessage(String value) {
        this.message = value;
    }

    public void setMessage(int valueId) {
        this.message = getString(valueId);
    }

    public String getMessage() {
        return message;
    }


    public static TangemContext loadFromBundle(Context context, Bundle bundle) {
        TangemContext tangemContext = new TangemContext();
        tangemContext.setContext(context);

        if (bundle.containsKey(TangemCard.EXTRA_UID)) {
            tangemContext.card = new TangemCard(bundle.getString(TangemCard.EXTRA_UID));
            tangemContext.card.loadFromBundle(bundle.getBundle(TangemCard.EXTRA_CARD));
        }

        if (tangemContext.getBlockchain() != null) {
            if (bundle.containsKey(Constant.EXTRA_BLOCKCHAIN_DATA)) {
                tangemContext.coinData = CoinData.fromBundle(tangemContext.getBlockchain(), bundle.getBundle(Constant.EXTRA_BLOCKCHAIN_DATA));
            } else {
                tangemContext.coinData = CoinEngineFactory.INSTANCE.create(tangemContext).createCoinData();
            }
        }
        tangemContext.error = bundle.getString("Error");
        tangemContext.message = bundle.getString("Message");

        return tangemContext;
    }

    public void saveToIntent(Intent intent) {

        if (card != null) {
            intent.putExtra(TangemCard.EXTRA_UID, card.getUID());
            intent.putExtra(TangemCard.EXTRA_CARD, card.getAsBundle());
        }

        if (coinData != null) {
            intent.putExtra(Constant.EXTRA_BLOCKCHAIN_DATA, coinData.asBundle());
        }

        intent.putExtra("Error", error);
        intent.putExtra("Message", message);

    }

    public String getString(int stringId) {
        if (context != null) return getContext().getResources().getString(stringId);
        return "context.resources.string[" + stringId + "]";
    }

    public void setDenomination(byte[] denomination) {
        try {
            CoinEngine engine= CoinEngineFactory.INSTANCE.create(getBlockchain());
            CoinEngine.InternalAmount internalAmount=engine.convertToInternalAmount(denomination);
            CoinEngine.Amount amount=engine.convertToAmount(internalAmount);
            card.setDenomination(denomination,amount.toString());
        } catch (Exception e) {
            e.printStackTrace();
            card.setDenomination(denomination,"N/A");
        }
    }

}