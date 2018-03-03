/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.webapi.request;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.openhab.binding.synologysurveillancestation.internal.SynoConfig;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.CameraResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SYNO.SurveillanceStation.Camera
 *
 * This API provides a set of methods to acquire camera-related information and to enable/disable cameras.
 *
 * Method:
 * - Save
 * - List
 * - GetInfo
 * - ListGroup
 * - GetSnapshot
 * - Enable
 * - Disable
 * - GetCapabilityByCamId
 * - MigrationEnum
 * - Migrate
 * - CountByCategory
 * - RecountEventSize
 * - SaveOptimizeParam
 * - GetOccupiedSize
 * - CheckCamValid
 * - MigrationCancel
 * - Delete
 * - GetLiveViewPath
 *
 * @author Nils
 *
 */
public class SynoApiCamera extends SynoApiRequest<CameraResponse> {
    private final Logger logger = LoggerFactory.getLogger(SynoApiCamera.class);

    // API configuration
    private static final String API_NAME = "SYNO.SurveillanceStation.Camera";
    private static final SynoApiConfig apiConfig = new SynoApiConfig(API_NAME, API_VERSION_08, API_SCRIPT_ENTRY);

    /**
     * @param config
     */
    public SynoApiCamera(SynoConfig config, String sessionID) {
        super(apiConfig, config, sessionID);
    }

    /**
     * Calls the passed method for all cameras.
     *
     * @param method
     * @return
     * @throws WebApiException
     */
    private CameraResponse call(String method) throws WebApiException {

        return call(method, null);
    }

    /**
     * Calls the passed method.
     *
     * @param method
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    private CameraResponse call(String method, String cameraId) throws WebApiException {

        Map<String, String> params = new HashMap<>();

        // API parameters
        params.put("blFromCamList", API_TRUE);
        params.put("privCamType", API_TRUE);
        params.put("blIncludeDeletedCam", API_FALSE);
        params.put("basic", API_TRUE);
        params.put("streamInfo", API_TRUE);
        params.put("blPrivilege", API_FALSE);
        // params.put("camStm", "1");

        if (cameraId != null) {
            params.put("cameraIds", cameraId);
        }

        return callApi(method, params);
    }

    /**
     * Get the up-to-date snapshot of the selected camera in JPEG format.
     *
     * @throws WebApiException
     * @throws IOException
     * @throws UnsupportedOperationException
     * @throws URISyntaxException
     *
     */
    public byte[] getSnapshot(String cameraId, int timeout, int streamId)
            throws IOException, URISyntaxException, WebApiException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {

            Map<String, String> params = new HashMap<>();

            // API parameters
            params.put("cameraId", cameraId);
            params.put("camStm", String.valueOf(streamId));

            Request request = getWebApiUrl(METHOD_GETSNAPSHOT, params);

            long responseTime = System.currentTimeMillis();

            ContentResponse response = request.timeout(timeout, TimeUnit.SECONDS).send();

            responseTime = System.currentTimeMillis() - responseTime;
            if (response.getStatus() == 200) {
                InputStream is = new ByteArrayInputStream(response.getContent());
                IOUtils.copy(is, baos);
            }
            logger.debug("Device: {}, API response time: {} ms, stream id: {}", cameraId, responseTime, streamId);
            return baos.toByteArray();
        } catch (IllegalArgumentException | SecurityException | ExecutionException | TimeoutException
                | InterruptedException e) {
            throw new WebApiException(e);
        }

    }

    /**
     * Get snapshot URI of the selected camera
     *
     * @throws WebApiException
     * @throws IOException
     * @throws UnsupportedOperationException
     * @throws URISyntaxException
     *
     */
    public String getSnapshotUri(String cameraId, int streamId) throws WebApiException {
        try {
            Map<String, String> params = new HashMap<>();

            // API parameters
            params.put("cameraId", cameraId);
            params.put("camStm", String.valueOf(streamId));

            Request request = getWebApiUrl(METHOD_GETSNAPSHOT, params);
            return request.getURI().toString();

        } catch (Exception e) {
            throw new WebApiException(e);
        }
    }

    /**
     * Get the list of all cameras.
     *
     * @return
     * @throws WebApiException
     */
    public CameraResponse list() throws WebApiException {

        return call(METHOD_LIST);
    }

    /**
     * Get specific camera settings.
     *
     * @return
     * @throws WebApiException
     */
    public CameraResponse getInfo(String cameraId) throws WebApiException {

        return call(METHOD_GETINFO, cameraId);
    }

    /**
     * Enable camera.
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public CameraResponse enable(String cameraId) throws WebApiException {

        Map<String, String> params = new HashMap<>();
        params.put("cameraIds", cameraId);

        return callApi(METHOD_ENABLE, params);
    }

    /**
     * Disable camera.
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public CameraResponse disable(String cameraId) throws WebApiException {

        Map<String, String> params = new HashMap<>();
        params.put("cameraIds", cameraId);

        return callApi(METHOD_DISABLE, params);
    }

}
