/*
 * The MIT License
 *
 * Copyright 2020 Mikkel L. Mouridsen.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.cobo.anibot.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;

/**
 *
 * @author Mikkel L. Mouridsen
 */
public class RedditImageHandler extends ImageHandler {
    
    private final String url;
    private ArrayList<String> imageUrls = new ArrayList<String>();
    
    public RedditImageHandler(String url) {
        this.url = url;
    }
    
    @Override
    public File fetchImage() {
        
        if (imageUrls.isEmpty()) {
            getImages();
        }
        
        String imgUrl = imageUrls.get(0);
        
        String[] urlSplitted = imgUrl.split("/");
        String filename = urlSplitted[urlSplitted.length - 1];
        
        File resImg = Unirest.get(imgUrl)
                .asFile(filename)
                .getBody();
        
        imageUrls.remove(0);
        
        return resImg;
    }
    
    private void getImages() {
        JSONArray results = Unirest.get(url)
                .asJson()
                .getBody()
                .getObject()
                .getJSONObject("data")
                .getJSONArray("children");
        
        for (int i = 0; i < results.length(); i++) {
            imageUrls.add(results.getJSONObject(i).getJSONObject("data").getString("url"));
        }
    }
    
}
