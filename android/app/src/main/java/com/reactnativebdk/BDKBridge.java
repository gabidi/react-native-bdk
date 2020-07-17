package com.reactnativebdk;

import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;

import org.bdk.jni.*;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Optional;
import java.nio.file.Path;

public class BDKBridge extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;
    private BdkApi bdkApi = new BdkApi();
    private Network network = Network.Testnet;
    //private val network = Network.Regtest
    private Thread bdkThread;

    public BDKBridge(ReactApplicationContext context) {
        super(context);
        this.reactContext = context;
        bdkApi.initLogger();
    }

    private Path getWorkDir() {
        return reactContext.getFilesDir().toPath();
    }

    private Optional<Config> getConfig() {
        return bdkApi.loadConfig(this.getWorkDir(), this.network);
    }

    @Override
    public String getName() {
        return "BDKBridge";
    }

    @ReactMethod
    public void initConfig(String passphrase, Promise promise) {
        Log.d("BDK:initConfig", "starting initConfig" + this.getWorkDir() + ", net:" + this.network + ", pass:" + passphrase);
        Optional<InitResult> initResult = this.bdkApi.initConfig(this.getWorkDir(), this.network, passphrase, "123");
        Log.d("BDK:initConfig", "initResult done...");
        String[] bitcoinPeers = {};
        this.bdkApi.updateConfig(this.getWorkDir(), this.network, bitcoinPeers, 10, true);
        Log.d("BDK:initConfig", "UpdateConfig done...");
        Log.d("BDK:initConfig", initResult.isPresent() ? "Init result present" : "Init result not there!");

        if (!initResult.isPresent()) {
            promise.reject("init result not present");
            return;
        }
        WritableMap reply = Arguments.createMap();
        reply.putString("mnemonicWords", initResult.get().getMnemonicWords().toString());
        reply.putString("depositAddress", initResult.get().getDepositAddress().getAddress());
        promise.resolve(reply);

    }

    @ReactMethod
    public void startBdk(Promise promise) {
        Log.d("BDK:startBdk", "starting startBdk");
        if (this.getConfig().isPresent() && (this.bdkThread == null || this.bdkThread.isAlive() != true)) {
            Log.d("BDK:startBdk", "starting bdk thread");
            Path workDir = this.getWorkDir();
            this.bdkThread = new Thread(new Runnable() {
                public void run() {
                    Log.d("BDK:Start:Thread", "bdk thread will start");
                    bdkApi.start(workDir, network, false);
                    Log.d("BDK:Start:Thread", "bdk thread start done");
                }
            });
            this.bdkThread.start();
            promise.resolve("ok");
        } else {
            promise.reject("config missing or thread already started");
        }
    }

    private boolean initCheck() {
        if (!this.getConfig().isPresent()) {
            Log.d("BDK:initCheck", "Config not present");
            return false;
        }
        if (this.bdkThread == null || this.bdkThread.isAlive() == false) {
            Log.d("BDK:initCheck", "thread null or not alive");
            return false;
        }
        return true;
    }

    @ReactMethod
    public void getDepositAddress(Promise promise) {
        if (!this.initCheck()) {
            promise.reject("Dead thread or no config");
            return;
        }
// TODO hashMap of getType, getNetwork etc..
        promise.resolve(this.bdkApi.depositAddress().getAddress());
    }

    @ReactMethod
    public void withdrawToAddress(
            String passphrase,
            String address,
            Long feePerVByte,
            Long amount,
            Promise promise
    ) {
        if (!this.initCheck()) {
            promise.reject("Dead thread or no config");
            return;
        }
        try {
            WithdrawTx withdrawTx = this.bdkApi.withdraw(passphrase, address, feePerVByte, amount);
            WritableMap reply = Arguments.createMap();
            reply.putString("txnId", withdrawTx.getTxid());
            reply.putString("fee", String.valueOf(withdrawTx.getFee()));
            promise.resolve(reply);
        } catch (Exception e) {
            Log.d("BDK:withdrawToAddress", "error: " + e.toString());
            promise.reject(e);
        }

    }

}

