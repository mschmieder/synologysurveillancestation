/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.thread;

import static org.openhab.binding.synologysurveillancestation.SynoBindingConstants.*;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.Thing;
import org.openhab.binding.synologysurveillancestation.handler.SynoCameraHandler;
import org.openhab.binding.synologysurveillancestation.internal.webapi.SynoEvent;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.EventResponse;

/**
 * Thread for getting camera events (motion, alarm)
 *
 * @author Pavion - Initial contribution
 */
@NonNullByDefault
public class SynoApiThreadEvent extends SynoApiThread<SynoCameraHandler> {
    // private final Logger logger = LoggerFactory.getLogger(SynoApiThreadEvent.class);

    private long lastEventTime;
    private Map<String, SynoEvent> events = new HashMap<>();

    public SynoApiThreadEvent(SynoCameraHandler handler, int refreshRate) {
        super(SynoApiThread.THREAD_EVENT, handler, refreshRate);
        lastEventTime = ZonedDateTime.now().minusSeconds(refreshRate * 2).toEpochSecond();
        events.put(CHANNEL_EVENT_MOTION, new SynoEvent(SynoEvent.EVENT_REASON_MOTION));
        events.put(CHANNEL_EVENT_ALARM, new SynoEvent(SynoEvent.EVENT_REASON_ALARM));
        events.put(CHANNEL_EVENT_MANUAL, new SynoEvent(SynoEvent.EVENT_REASON_MANUAL));
        events.put(CHANNEL_EVENT_EXTERNAL, new SynoEvent(SynoEvent.EVENT_REASON_EXTERNAL));
        events.put(CHANNEL_EVENT_ACTIONRULE, new SynoEvent(SynoEvent.EVENT_REASON_ACTIONRULE));
    }

    @Override
    public boolean isNeeded() {
        return (getSynoHandler().isLinked(CHANNEL_EVENT_MOTION) || getSynoHandler().isLinked(CHANNEL_EVENT_ALARM)
                || getSynoHandler().isLinked(CHANNEL_EVENT_MANUAL) || getSynoHandler().isLinked(CHANNEL_EVENT_EXTERNAL)
                || getSynoHandler().isLinked(CHANNEL_EVENT_ACTIONRULE));
    }

    @Override
    public boolean refresh() throws Exception {
        SynoCameraHandler cameraHandler = getSynoHandler();
        Thing thing = cameraHandler.getThing();

        EventResponse response = cameraHandler.getSynoWebApiHandler().getEventResponse(cameraHandler.getCameraId(),
                lastEventTime, events);
        if (response.isSuccess()) {
            for (String eventType : events.keySet()) {
                if (getSynoHandler().isLinked(eventType)) {
                    SynoEvent event = events.get(eventType);
                    Channel channel = cameraHandler.getThing().getChannel(eventType);
                    if (response.hasEvent(event.getReason())) {
                        SynoEvent responseEvent = response.getEvent(event.getReason());
                        if (responseEvent.getEventId() != event.getEventId()) {
                            event.setEventId(responseEvent.getEventId());
                            event.setEventCompleted(responseEvent.isEventCompleted());
                            cameraHandler.updateState(channel.getUID(), OnOffType.ON);
                            if (responseEvent.isEventCompleted()) {
                                cameraHandler.updateState(channel.getUID(), OnOffType.OFF);
                            }
                        } else if (responseEvent.getEventId() == event.getEventId() && responseEvent.isEventCompleted()
                                && !event.isEventCompleted()) {
                            event.setEventCompleted(true);
                            cameraHandler.updateState(channel.getUID(), OnOffType.OFF);
                        }
                    } else {
                        event.setEventCompleted(true);
                        cameraHandler.updateState(channel.getUID(), OnOffType.OFF);
                    }
                }
            }

            lastEventTime = response.getTimestamp();
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return the events
     */
    public Map<String, SynoEvent> getEvents() {
        return events;
    }

}
