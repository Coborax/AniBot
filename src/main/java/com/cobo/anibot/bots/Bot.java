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

import com.cobo.anibot.eventlisteners.BotListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mikkel L. Mouridsen
 */
public abstract class Bot implements Runnable {
    
    private static Logger LOGGER;
    private boolean running = false;
    
    private ArrayList<BotListener> listeners = new ArrayList<BotListener>();
    
    public Bot() {
        LOGGER = Logger.getLogger(Bot.class.getName());
    }
    
    @Override
    public void run() {
        
        LOGGER.log(Level.INFO, "Bot starting...");
        running = true;
        
        listeners.forEach((BotListener bl) -> {
            bl.botStarted(this);
        });
        
        while(running) {
            
            handleLoop();
            
            listeners.forEach((BotListener bl) -> {
                bl.botUpdated(this);
            });
            
            LOGGER.log(Level.INFO, "Bot thread sleeping...");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        LOGGER.log(Level.INFO, "Bot stopping...");
        
        listeners.forEach((BotListener bl) -> {
            bl.botStoped(this);
        });
        
    }
    
    public void handleLoop() {
        
    }
    
    public void addListener(BotListener listener) {
        listeners.add(listener);
    }
    
    public boolean isRunning() { return running; }
    public void setRunning(boolean running) { this.running = running; }
    
}
