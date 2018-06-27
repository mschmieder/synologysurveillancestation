/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.webapi.request;

import java.util.HashMap;
import java.util.Map;

import org.openhab.binding.synologysurveillancestation.internal.SynoConfig;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.SimpleResponse;

/**
 * SYNO.SurveillanceStation.SynoApiPTZ
 *
 * This API provides a set of methods to execute PTZ action, and to acquire PTZ related information such as
 * patrol list or patrol schedule of a camera.
 *
 * Method:
 * - Move
 * - Zoom
 * - ListPreset
 * - GoPreset
 * - ListPatrol
 * - RunPatrol
 * - Focus
 * - Iris
 * - AutoFocus
 * - AbsPtz
 * - Home
 * - AutoPan
 * - ObjTracking
 *
 * @author Nils
 *
 */
public class SynoApiPTZ extends SynoApiRequest<SimpleResponse> {

    // API configuration
    private static final String API_NAME = "SYNO.SurveillanceStation.PTZ";
    private static final SynoApiConfig apiConfig = new SynoApiConfig(API_NAME, API_VERSION_03, API_SCRIPT_ENTRY);

    /**
     * @param config
     */
    public SynoApiPTZ(SynoConfig config, String sessionID) {
        super(apiConfig, config, sessionID);
    }

    /**
     * calls api method 'zoom' with passed control
     *
     * @param cameraId
     * @param control
     * @return
     * @throws WebApiException
     */
    private SimpleResponse callZoom(String cameraId, String control) throws WebApiException {

        Map<String, String> params = new HashMap<>();

        // API parameters
        params.put("cameraId", cameraId);
        params.put("control", control);
        // params.put("moveType", "Start");

        return callApi(METHOD_ZOOM, params);
    }

    /**
     * calls api method 'move' with passed direction and speed
     *
     * @param cameraId
     * @param direction
     * @param speed
     * @return
     * @throws WebApiException
     */
    private SimpleResponse callMove(String cameraId, String direction, int speed) throws WebApiException {

        Map<String, String> params = new HashMap<>();

        // API Parameters
        params.put("cameraId", cameraId);
        params.put("direction", direction);
        params.put("speed", String.valueOf(speed));
        // params.put("moveType", "Start");

        return callApi(METHOD_MOVE, params);
    }

    /**
     * Control the PTZ camera to zoom out.
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse zoomOut(String cameraId) throws WebApiException {

        return callZoom(cameraId, "out");
    }

    /**
     * Control the PTZ camera to zoom in.
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse zoomIn(String cameraId) throws WebApiException {

        return callZoom(cameraId, "in");
    }

    /**
     * Control the PTZ camera to move its lens up.
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse moveUp(String cameraId) throws WebApiException {

        return callMove(cameraId, "up", 1);
    }

    /**
     * Control the PTZ camera to move its lens down.
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse moveDown(String cameraId) throws WebApiException {

        return callMove(cameraId, "down", 1);
    }

    /**
     * Control the PTZ camera to move its lens left.
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse moveLeft(String cameraId) throws WebApiException {

        return callMove(cameraId, "left", 1);
    }

    /**
     * Control the PTZ camera to move its lens right.
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse moveRight(String cameraId) throws WebApiException {

        return callMove(cameraId, "right", 1);
    }

    /**
     * Control the PTZ camera to move to preset HOME.
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse moveHome(String cameraId) throws WebApiException {

        return callMove(cameraId, "home", 1);
    }

    /**
     * calls api method 'ListPreset' and list all presets for moving.
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse listPresets(String cameraId) throws WebApiException {

        Map<String, String> params = new HashMap<>();

        // API Parameters
        params.put("cameraId", cameraId);

        SimpleResponse response = callApi(METHOD_LISTPRESET, params);

        return response;
    }

    /**
     * calls api method 'GoPreset' and move the camera lens to a pre-defined preset position.
     *
     * @param cameraId
     * @param presetId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse goPreset(String cameraId, String presetId) throws WebApiException {

        Map<String, String> params = new HashMap<>();

        // API Parameters
        params.put("cameraId", cameraId);
        params.put("presetId", presetId);
//        params.put("position", ???);
//        params.put("speed", ???);
//        params.put("type", ???);
//        params.put("isPatrol", ???);

        SimpleResponse response = callApi(METHOD_GOPRESET, params);

        return response;
    }

    /**
     * calls api method 'ListPatrol' and list all patrols.
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse listPatrol(String cameraId) throws WebApiException {

        Map<String, String> params = new HashMap<>();

        // API Parameters
        params.put("cameraId", cameraId);

        SimpleResponse response = callApi(METHOD_LISTPATROL, params);

        return response;
    }

    /**
     * calls api method 'RunPatrol' and execute the given patrol.
     *
     * @param cameraId
     * @param patrolId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse runPatrol(String cameraId, String patrolId) throws WebApiException {

        Map<String, String> params = new HashMap<>();

        // API Parameters
        params.put("cameraId", cameraId);
        params.put("patrolId", patrolId);

        SimpleResponse response = callApi(METHOD_RUNPATROL, params);

        return response;
    }

}
