package com.tangem.domain.wallet;

import android.net.Uri;

import com.tangem.domain.cardReader.CardProtocol;
import com.tangem.domain.cardReader.TLV;
import com.tangem.util.Util;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.tangem.domain.wallet.FormatUtil.GetDecimalFormat;

/**
 * Created by Ilia on 15.02.2018.
 */

public class BtcEngine extends CoinEngine {
    public String GetNextNode(TangemCard mCard) {
        return getNextServiceHost(mCard);
    }

    public int GetNextNodePort(TangemCard mCard) {
        return getNextServicePort(mCard);
    }

    public String GetNode(TangemCard mCard) {
        return getServiceHost(mCard);
    }

    public int GetNodePort(TangemCard mCard) {
        return getServicePort(mCard);
    }

    public void SwitchNode(TangemCard mCard) {
        SelectNextBitconServiceIndex();
    }

    public static String[] GetBitcoinServiceHosts() {
        return new String[]{"vps.hsmiths.com", "tardis.bauerj.eu" /*"arihancckjge66iv.onion"*/, "electrumx.bot.nu", "electrumx.hopto.org"/* "btc.asis.io"*/, "e-x.not.fyi", "electrum.backplanedns.org", "helicarrier.bauerj.eu", "electrum.vom-stausee.de", "electrum0.snel.it", "kirsche.emzy.de"};
    }

    public static String[] GetBitcoinTestNetServiceHosts() {
        return new String[]{/*"testnetnode.arihanc.com"*/"testnet.hsmiths.com", "testnet.qtornado.com", "testnet1.bauerj.eu"};
    }

    public static Integer[] GetBitcoinServicePorts() {
        return new Integer[]{8080, 50001/* 8080*/, 50001, 50001, 50001, 50001, 50001, 50001, 50001, 50001};
    }

    public static Integer[] GetBitcoinTestNetServicePorts() {
        return new Integer[]{/*51001*/53011, 51001, 50001};
    }

    static int serviceIndex = GetNextBitconMainNetServiceIndex();

    static int dynamicIndex = GetNextBitconMainNetServiceIndex();

    static int serviceIndexTestNet = GetNextBitconTestNetServiceIndex();

    static int dynamicTestNetIndex = GetNextBitconTestNetServiceIndex();

    static long lastChangeServiceIndex = 0;

    public static int GetNextBitconMainNetServiceIndex() {
        Random r = new Random();
        return r.nextInt(GetBitcoinServiceHosts().length);
    }

    public static void SelectNextBitconMainNetServiceIndex() {
        //serviceIndex = GetNextBitconMainNetServiceIndex();
        serviceIndex++;
        if (serviceIndex > GetBitcoinServiceHosts().length - 1) serviceIndex = 0;
    }

    public static int GetNextBitconTestNetServiceIndex() {
        Random r = new Random();
        return r.nextInt(GetBitcoinTestNetServiceHosts().length);
    }

    public static void SelectNextBitconTestNetServiceIndex() {
        //serviceIndexTestNet = GetNextBitconTestNetServiceIndex();

        serviceIndexTestNet++;
        if (serviceIndexTestNet > GetBitcoinTestNetServiceHosts().length - 1)
            serviceIndexTestNet = 0;
    }

    public static void setNextDynamicIndex() {
        //dynamicIndex = GetNextBitconMainNetServiceIndex();
        dynamicIndex++;
        if (dynamicIndex > GetBitcoinServiceHosts().length - 1) dynamicIndex = 0;
    }

    public static void setNextDynamicTestNet() {
        //dynamicTestNetIndex = GetNextBitconTestNetServiceIndex();
        dynamicTestNetIndex++;
        if (dynamicTestNetIndex > GetBitcoinTestNetServiceHosts().length - 1)
            dynamicTestNetIndex = 0;

    }

    public static void SelectNextBitconServiceIndex() {
        long unixTime = System.currentTimeMillis() / 1000L;
        long nextStampOffset = 5;
        if (lastChangeServiceIndex == 0) {
            lastChangeServiceIndex = unixTime;
        } else if (lastChangeServiceIndex + nextStampOffset > unixTime) {
            return;
        }

        lastChangeServiceIndex = unixTime;

        SelectNextBitconMainNetServiceIndex();
        SelectNextBitconTestNetServiceIndex();
    }

    public static String getServiceHost(TangemCard mCard) {
        switch (mCard.getBlockchain()) {
            case Bitcoin:
                return GetBitcoinServiceHosts()[serviceIndex]; //"hsmiths.changeip.net";
            case BitcoinTestNet:
                return GetBitcoinTestNetServiceHosts()[serviceIndexTestNet]; //"testnetnode.arihanc.com";
        }
        return null;
    }

    public static String getNextServiceHost(TangemCard mCard) {
        switch (mCard.getBlockchain()) {
            case Bitcoin: {
                setNextDynamicIndex();
                return GetBitcoinServiceHosts()[dynamicIndex];
            }
            case BitcoinTestNet: {
                setNextDynamicTestNet();
                return GetBitcoinTestNetServiceHosts()[dynamicTestNetIndex]; //"testnetnode.arihanc.com";
            }
            //case BitcoinCash:
            //{
            //  return GetBitcoinCashServiceHosts()[0];
            //}
            //case BitcoinCashTestNet: {
            //  return GetBitcoinCashTestNetServiceHosts()[0];
            //}
        }
        return null;
    }

    public static int getNextServicePort(TangemCard mCard) {
        switch (mCard.getBlockchain()) {
            case Bitcoin: {
                setNextDynamicIndex();
                return GetBitcoinServicePorts()[dynamicIndex];//8080;
            }
            case BitcoinTestNet: {
                setNextDynamicTestNet();
                return GetBitcoinTestNetServicePorts()[dynamicTestNetIndex];//51001;
            }
        }
        return 8080;
    }

    public static int getServicePort(TangemCard mCard) {
        switch (mCard.getBlockchain()) {
            case Bitcoin:
                return GetBitcoinServicePorts()[serviceIndex];//8080;
            case BitcoinTestNet:
                return GetBitcoinTestNetServicePorts()[serviceIndexTestNet];//51001;
        }
        return 8080;
    }


    public boolean InOutPutVisible() {
        return true;
    }

    public boolean AwaitingConfirmation(TangemCard card) {
        return card.getBalanceUnconfirmed() != 0;
    }

    public String GetBalanceWithAlter(TangemCard mCard) {
        return GetBalance(mCard);
    }

    public boolean IsBalanceAlterNotZero(TangemCard card) {
        return true;
    }

    public Long GetBalanceLong(TangemCard mCard) {
        return mCard.getBalance();
    }

    public boolean IsBalanceNotZero(TangemCard card) {
        return card.getBalance() > 0;
    }

    public boolean CheckAmount(TangemCard card, String amount) throws Exception {
        DecimalFormat decimalFormat = GetDecimalFormat();
        BigDecimal amountValue = (BigDecimal) decimalFormat.parse(amount);

        // Convert Balance to BigDecimal
        BigDecimal maxValue = new BigDecimal(GetBalanceValue(card));
        maxValue = maxValue.divide(new BigDecimal(1000));

        //if (use_mCurrency) {
        amountValue = amountValue.divide(new BigDecimal(1000));
        //}

        if (amountValue.compareTo(maxValue) > 0) {
            return false;
        }

        return true;
    }

    public boolean HasBalanceInfo(TangemCard card) {
        return card.hasBalanceInfo();
    }

    public String GetBalanceCurrency(TangemCard card) {
        return "mBTC";
    }

    public boolean CheckUnspentTransaction(TangemCard mCard) {
        return mCard.getUnspentTransactions().size() != 0;
    }

    public String GetFeeCurrency() {
        return "mBTC";
    }

    public boolean ValdateAddress(String address, TangemCard card) {
        if (address == null || address.isEmpty()) {
            return false;
        }

        if (address.length() < 25) {
            return false;
        }

        if (address.length() > 35) {
            return false;
        }

        if (!address.startsWith("1") && !address.startsWith("2") && !address.startsWith("3") && !address.startsWith("n") && !address.startsWith("m")) {
            return false;
        }

        byte[] decAddress = Base58.decodeBase58(address);

        if (decAddress == null || decAddress.length == 0) {
            return false;
        }

        byte[] rip = new byte[21];
        for (int i = 0; i < 21; ++i) {
            rip[i] = decAddress[i];
        }

        byte[] kcv = CryptoUtil.doubleSha256(rip);

        for (int i = 0; i < 4; ++i) {
            if (kcv[i] != decAddress[21 + i])
                return false;
        }

        if (card.getBlockchain() != Blockchain.BitcoinTestNet && card.getBlockchain() != Blockchain.Bitcoin) {
            return false;
        }

        if (card.getBlockchain() == Blockchain.BitcoinTestNet && (address.startsWith("1") || address.startsWith("3"))) {
            return false;
        }

        return true;
    }


    public int GetTokenDecimals(TangemCard card) {
        return 0;
    }

    public String GetContractAddress(TangemCard card) {
        return "";
    }

    public boolean IsNeedCheckNode() {
        return true;
    }

    public Uri getShareWalletURIExplorer(TangemCard mCard) {
        return Uri.parse((mCard.getBlockchain() == Blockchain.Bitcoin ? "https://blockchain.info/address/" : "https://testnet.blockchain.info/address/") + mCard.getWallet());
    }

    public Uri getShareWalletURI(TangemCard mCard) {
        return Uri.parse("bitcoin:" + mCard.getWallet());
    }

    public boolean CheckAmountValie(TangemCard mCard, String amountValue, String feeValue, Long minFeeInInternalUnits) {
        Long fee = null;
        Long amount = null;
        try {
            amount = mCard.InternalUnitsFromString(amountValue);
            fee = mCard.InternalUnitsFromString(feeValue);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (fee == null || amount == null)
            return false;

        if (fee == 0 || amount == 0)
            return false;

        if (fee > amount)
            return false;

        if (fee < minFeeInInternalUnits)
            return false;

        return true;
    }

    public String EvaluteFeeEquivalent(TangemCard mCard, String fee) {
        return GetAmountEqualentDescriptor(mCard, fee);
    }

    @Override
    public String GetBalanceEquivalent(TangemCard mCard) {
        Double balance = Double.NaN;
        try {
            Long val = mCard.getBalance();
            balance = mCard.AmountFromInternalUnits(val);
        } catch (Exception ex) {
            mCard.setRate(0);
        }

        return mCard.getAmountEquivalentDescription(balance);
    }

    public String GetBalance(TangemCard mCard) {
        if (mCard.hasBalanceInfo()) {
            Double balance = mCard.AmountFromInternalUnits(mCard.getBalance());
            return mCard.getAmountDescription(balance);
        } else {
            return "-- -- -- " + mCard.getBlockchain().getCurrency();
        }
    }

    public String GetBalanceValue(TangemCard mCard) {
        if (mCard.hasBalanceInfo()) {
            Double balance = mCard.getBalance() / (mCard.getBlockchain().getMultiplier() / 1000.0);

            String output = FormatUtil.DoubleToString(balance);
            //String pattern = "#0.000"; // If you like 4 zeros
            //DecimalFormat myFormatter = new DecimalFormat(pattern);
            //String output = myFormatter.format(balance);
            return output;

            //return Double.toString(balance);
        } else {
            return "0";
        }
    }

    public String calculateAddress(TangemCard mCard, byte[] pkUncompressed) throws NoSuchProviderException, NoSuchAlgorithmException {

        byte netSelectionByte;
        switch (mCard.getBlockchain()) {
            case Bitcoin:
                netSelectionByte = (byte) 0x00; //0 - MainNet 0x6f - TestNet
                break;
            default:
                netSelectionByte = (byte) 0x6f; //0 - MainNet 0x6f - TestNet
                break;
        }

        byte hash1[] = Util.calculateSHA256(pkUncompressed);
        byte hash2[] = Util.calculateRIPEMD160(hash1);

        ByteBuffer BB = ByteBuffer.allocate(hash2.length + 1);

        BB.put(netSelectionByte);
        BB.put(hash2);

        byte hash3[] = Util.calculateSHA256(BB.array());
        byte hash4[] = Util.calculateSHA256(hash3);

        BB = ByteBuffer.allocate(hash2.length + 5);
        BB.put(netSelectionByte); //BB.put((byte) 0x6f);
        BB.put(hash2);
        BB.put(hash4[0]);
        BB.put(hash4[1]);
        BB.put(hash4[2]);
        BB.put(hash4[3]);

        return org.bitcoinj.core.Base58.encode(BB.array());

    }

    @Override
    public String ConvertByteArrayToAmount(TangemCard mCard, byte[] bytes) throws Exception {
        byte[] reversed = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) reversed[i] = bytes[bytes.length - i - 1];
        return FormatUtil.DoubleToString(1000.0 * mCard.AmountFromInternalUnits(Util.byteArrayToLong(reversed)));
    }

    @Override
    public byte[] ConvertAmountToByteArray(TangemCard mCard, String amount) throws Exception {
        byte[] bytes = Util.longToByteArray(mCard.InternalUnitsFromString(amount));
        byte[] reversed = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) reversed[i] = bytes[bytes.length - i - 1];
        return reversed;
    }

    @Override
    public String GetAmountDescription(TangemCard mCard, String amount) throws Exception {
        return mCard.getAmountDescription(Double.parseDouble(amount) / 1000.0);
    }

    public static String getAmountEquivalentDescriptionBTC(Double amount, float rate) {
        if (rate > 0) {
            return String.format("≈ USD %.2f", amount * rate);
        } else {
            return "≈ USD  ---";
        }
    }

    public String GetAmountEqualentDescriptor(TangemCard mCard, String value) {
        return getAmountEquivalentDescriptionBTC(Double.parseDouble(value) / 1000.0, mCard.getRate());
    }

    public byte[] Sign(String feeValue, String amountValue, String toValue, TangemCard mCard, CardProtocol protocol) throws Exception {

        String myAddress = mCard.getWallet();
        byte[] pbKey = mCard.getWalletPublicKey();
        String outputAddress = toValue;
        String changeAddress = myAddress;

        // Build script for our address
        List<TangemCard.UnspentTransaction> rawTxList = mCard.getUnspentTransactions();
        byte[] outputScriptWeAreAbleToSpend = Transaction.Script.buildOutput(myAddress).bytes;

        // Collect unspent
        ArrayList<UnspentOutputInfo> unspentOutputs = BTCUtils.getOutputs(rawTxList, outputScriptWeAreAbleToSpend);

        long fullAmount = 0;
        for (int i = 0; i < unspentOutputs.size(); ++i) {
            fullAmount += unspentOutputs.get(i).value;
        }


        long fees = FormatUtil.ConvertStringToLong(feeValue);
        long amount = FormatUtil.ConvertStringToLong(amountValue);
        amount = amount - fees;

        long change = fullAmount - fees - amount;

        if (amount + fees > fullAmount) {
            throw new Exception(String.format("Balance (%d) < amount (%d) + (%d)", fullAmount, change, amount));
        }

        byte[][] dataForSign = new byte[unspentOutputs.size()][];

        for (int i = 0; i < unspentOutputs.size(); ++i) {
            byte[] newTX = BTCUtils.buildTXForSign(myAddress, outputAddress, changeAddress, unspentOutputs, i, amount, change);

            byte[] hashData = Util.calculateSHA256(newTX);
            byte[] doubleHashData = Util.calculateSHA256(hashData);

            unspentOutputs.get(i).bodyDoubleHash = doubleHashData;
            unspentOutputs.get(i).bodyHash = hashData;

            if (mCard.getSigningMethod() == TangemCard.SigningMethod.Sign_Raw || mCard.getSigningMethod() == TangemCard.SigningMethod.Sign_Raw_Validated_By_Issuer) {
                dataForSign[i] = newTX;
            } else {
                dataForSign[i] = doubleHashData;
            }

        }

        byte[] signFromCard = null;
        if (mCard.getSigningMethod() == TangemCard.SigningMethod.Sign_Raw || mCard.getSigningMethod() == TangemCard.SigningMethod.Sign_Raw_Validated_By_Issuer) {
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            if (dataForSign.length > 10) throw new Exception("To much hashes in one transaction!");
            for (int i = 0; i < dataForSign.length; i++) {
                if (i != 0 && dataForSign[0].length != dataForSign[i].length)
                    throw new Exception("Hashes length must be identical!");
                bs.write(dataForSign[i]);
            }
            signFromCard = protocol.run_SignRaw(PINStorage.getPIN2(), bs.toByteArray()).getTLV(TLV.Tag.TAG_Signature).Value;
        } else {
            signFromCard = protocol.run_SignHashes(PINStorage.getPIN2(), dataForSign, mCard.getSigningMethod() == TangemCard.SigningMethod.Sign_Hash_Validated_By_Issuer, null, mCard.getIssuer()).getTLV(TLV.Tag.TAG_Signature).Value;
            // TODO slice signFromCard to hashes.length parts
        }

        LastSignStorage.setLastSignDate(mCard.getWallet(), new Date());


        for (int i = 0; i < unspentOutputs.size(); ++i) {
            BigInteger r = new BigInteger(1, Arrays.copyOfRange(signFromCard, 0 + i * 64, 32 + i * 64));
            BigInteger s = new BigInteger(1, Arrays.copyOfRange(signFromCard, 32 + i * 64, 64 + i * 64));
            s = CryptoUtil.toCanonicalised(s);
            byte[] encodingSign = DerEncodingUtil.packSignDer(r, s, pbKey);

            unspentOutputs.get(i).scriptForBuild = encodingSign;
        }

        byte[] realTX = BTCUtils.buildTXForSend(outputAddress, changeAddress, unspentOutputs, amount, change);
        return realTX;
    }
}