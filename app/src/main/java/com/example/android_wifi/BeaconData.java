/**
 * Created by NOT on 2/15/18.
 */

package com.example.android_wifi;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;


//todo
public class BeaconData implements Serializable {

    private String id;
    private boolean isRescuer;
    private BloomFilter<String> mBloomfilter;


    BeaconData(boolean isRescuer, BloomFilter<String> bloomfilter){
        this.isRescuer = isRescuer;
        this.mBloomfilter = bloomfilter;
        id = UUID.randomUUID().toString();
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

    BloomFilter<String> getBloomfilter(){
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
