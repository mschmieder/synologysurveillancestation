/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.webapi.response;

import com.google.gson.JsonElement;

/**
 * {@link InfoResponse} provides information about current camera setup
 *
 * @author Nils
 */
public class InfoResponse extends SimpleResponse {

    /**
     * @param jsonResponse
     */
    public InfoResponse(String jsonResponse) {
        super(jsonResponse);
    }

    public JsonElement getCameras() {
        return getData().get("cameras");
    }

}
