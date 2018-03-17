/**
 * Created by NOT on 2/15/18.
 */

package com.example.android_wifi;

import com.google.common.hash.BloomFilter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.UUID;

public class BeaconData implements Serializable {

    private String id;
    private boolean isRescuer;
    private BloomFilter<String> mBloomfilter;


    public BeaconData(BloomFilter<String> bloomFilter){
        id = UUID.randomUUID().toString();
        isRescuer = false;
        mBloomfilter = bloomFilter;
    }

    public String getId() {
        return id;
    }

    public boolean isRescuer(){
        return isRescuer;
    }

    public byte[] getByte(){
        return id.getBytes();
    }

    public BloomFilter<String> getBoomfilter(){
        return mBloomfilter;
    }

    public void setBloomFiler(BloomFilter<String> filter){
       this.mBloomfilter = filter;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
}
