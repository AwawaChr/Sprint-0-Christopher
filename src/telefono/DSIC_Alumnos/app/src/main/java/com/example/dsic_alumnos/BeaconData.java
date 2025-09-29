package com.example.dsic_alumnos;

public class BeaconData {

    private int major;
    private int minor;
    private int txPower;
    private long timestamp;
    private String mac;

    // -------------------- GETTERS --------------------
    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getTxPower() {
        return txPower;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMac() {
        return mac;
    }

    // -------------------- SETTERS --------------------
    public void setMajor(int major) {
        this.major = major;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public void setTxPower(int txPower) {
        this.txPower = txPower;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
