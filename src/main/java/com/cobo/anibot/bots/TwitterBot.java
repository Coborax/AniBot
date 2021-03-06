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
package com.cobo.anibot.bots;

import com.cobo.anibot.handlers.ImageHandler;
import com.cobo.anibot.handlers.RedditImageHandler;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 *
 * @author Mikkel L. Mouridsen
 */
public class TwitterBot extends Bot {
    
    private Twitter twitterClient;
    private ImageHandler imageHandler;
    
    private AccessToken accessToken;
    private RequestToken requestToken;
    
    private final long postDelay = 3600000;
    private long nextPostTime;
    
    public TwitterBot() {
        super();
        
        imageHandler = new RedditImageHandler("https://www.reddit.com/r/awwnime.json");
        nextPostTime = System.currentTimeMillis();
        twitterClient = TwitterFactory.getSingleton();
    }
    
    public TwitterBot(ImageHandler imageHandler) {
        super();
        
        this.imageHandler = imageHandler;
        nextPostTime = System.currentTimeMillis();
    }
    
    @Override
    public void handleLoop() {
        
        if (accessToken == null) {
            return;
        }
        
        try {
            twitterClient.verifyCredentials();
        } catch (TwitterException ex) {
            twitterClient.setOAuthAccessToken(accessToken);
        }
        
        long currentTime = System.currentTimeMillis();
        
        if (currentTime >= nextPostTime) {
            postTweet();
            nextPostTime = currentTime + postDelay;
        }
    }
    
    public void connectToTwitter(String pin) {
        
        try {
            accessToken = twitterClient.getOAuthAccessToken(requestToken, pin);
        } catch (TwitterException ex) {
            Logger.getLogger(TwitterBot.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        requestToken = null;
        
    }
    
    public String getAuthUrl() {
        
        try {
            requestToken = twitterClient.getOAuthRequestToken();
            return requestToken.getAuthorizationURL();
        } catch (TwitterException ex) {
            Logger.getLogger(TwitterBot.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "Could not get auth link...";
        
    }
    
    private void postTweet() {
        
        File img = imageHandler.fetchImage();
        
        StatusUpdate su = new StatusUpdate("");
        su.media(img);
        
        try {
            twitterClient.updateStatus(su);
        } catch (TwitterException ex) {
            Logger.getLogger(TwitterBot.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
}
