/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.thread;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.openhab.binding.synologysurveillancestation.handler.SynoBridgeHandler;
import org.openhab.binding.synologysurveillancestation.handler.SynoCameraHandler;
import org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApiHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for thread management (events, snapshot)
 *
 * @author Pav
 *
 */
@NonNullByDefault
public class SynoApiThread {
    private final Logger logger = LoggerFactory.getLogger(SynoApiThread.class);

    private final AtomicBoolean refreshInProgress = new AtomicBoolean(false);
    private @Nullable ScheduledFuture<?> future;
    private int refreshRate = 0;
    private final SynoCameraHandler handler;
    private final String name;

    /**
     * Defines a runnable for a refresh job
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (refreshInProgress.compareAndSet(false, true)) {
                    if (getApiHandler() == null) {
                        logger.error("Thread {}: Handler not (yet) initialized", name);
                    } else if (isNeeded()) {
                        boolean success = refresh();
                        updateStatus(success);
                    }
                    refreshInProgress.set(false);
                }
            } catch (IllegalStateException e) {
                logger.debug("Thread {}: Refreshing Thing failed, handler might be OFFLINE", name);
            } catch (Exception e) {
                logger.error("Thread {}: Unknown error", name, e);
            }
        }
    };

    /**
     * Main constructor
     *
     * @param threadId ID of this thread for logging purposes
     * @param refreshRate refresh rate of this thread in milliseconds
     * @param handler camera handler
     */
    public SynoApiThread(String name, SynoCameraHandler handler, int refreshRate) {
        this.name = name;
        this.handler = handler;
        this.refreshRate = refreshRate;
    }

    /**
     * Starts the refresh job
     */
    public void start() {
        if (refreshRate > 0) {
            future = handler.getScheduler().scheduleAtFixedRate(runnable, 0, refreshRate, TimeUnit.SECONDS);
        }
    }

    /**
     * Stops the refresh job
     */
    public void stop() {
        if (future != null) {
            future.cancel(true);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
        }
    }

    /**
     * Dummy for a refresh function
     */
    public boolean refresh() {
        return true;
    }

    /**
     * Update handler status on runnable feedback
     *
     * @param success
     */
    private void updateStatus(boolean success) {
        if (success && !handler.getThing().getStatus().equals(ThingStatus.ONLINE)) {
            handler.updateStatus(ThingStatus.ONLINE);
        } else if (!success && handler.getThing().getStatus().equals(ThingStatus.ONLINE)) {
            handler.updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "Thread " + name);
        }

    }

    /**
     * @return the refreshRate
     */
    public int getRefreshRate() {
        return refreshRate;
    }

    /**
     * @param refreshRate the refreshRate to set
     */
    public void setRefreshRate(int refreshRate) {
        if (this.refreshRate != refreshRate) {
            this.refreshRate = refreshRate;
            stop();
            start();
        }
    }

    /**
     * @return the handler
     */
    public SynoCameraHandler getHandler() {
        return handler;
    }

    /**
     * @return the API handler
     */
    public @Nullable SynoWebApiHandler getApiHandler() {
        Bridge bridge = handler.getBridge();
        if (bridge != null) {
            SynoBridgeHandler bridgeHandler = ((SynoBridgeHandler) bridge.getHandler());
            if (bridgeHandler != null) {
                return bridgeHandler.getSynoWebApiHandler();
            }
        }
        return null;
    }

    /**
     *
     * @return if thread has to be run
     */
    public boolean isNeeded() {
        return false;
    }

}
