package org.openhab.binding.synologysurveillancestation.internal.webapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.openhab.binding.synologysurveillancestation.internal.Config;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.SynoApiResponse;

/**
 * API request
 *
 * @author Nils
 *
 * @param <T>
 */
public abstract class SynoApiRequest<T extends SynoApiResponse> implements SynoApi {

    protected static final String API_TRUE = Boolean.TRUE.toString();
    protected static final String API_FALSE = Boolean.FALSE.toString();
    private SynoApiConfig apiConfig = null;

    final Class<T> typeParameterClass;

    private Config config = null;
    private String sessionId = null;

    /**
     * @param apiConfig
     * @param config
     * @param sessionId
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public SynoApiRequest(SynoApiConfig apiConfig, Config config, String sessionId) {

        super();

        this.typeParameterClass = ((Class) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0]);

        this.apiConfig = apiConfig;
        this.config = config;
        this.sessionId = sessionId;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.synologysurveillancestation.internal.webapi.SynoApi#getApiConfig()
     */
    @Override
    public SynoApiConfig getApiConfig() {
        return apiConfig;
    }

    /**
     * @return
     */
    protected Config getConfig() {
        return config;
    }

    /**
     * @return
     */
    protected String getSessionId() {
        return sessionId;
    }

    /**
     * Creates an URIBuilder to build the url.
     *
     * @return
     */
    protected URIBuilder getWebApiUrlBuilder() {
        URIBuilder b = new URIBuilder();

        b.setScheme(getConfig().getProtocol());
        b.setHost(getConfig().getHost());
        b.setPort(Integer.parseInt(getConfig().getPort()));
        return b;
    }

    /**
     * Calls the method.
     *
     * @param method
     * @return
     * @throws WebApiException
     */
    protected T callApi(String method) throws WebApiException {
        return callApi(method, null);
    }

    /**
     * Calls the method with the passed paramters.
     *
     * @param method
     * @param params
     * @return
     * @throws WebApiException
     */
    protected T callApi(String method, List<NameValuePair> params) throws WebApiException {

        URL url = getWebApiUrl(method, params);

        return callWebApi(url.toString());

    }

    /**
     * Builds the url for api.
     *
     * @param method
     * @param params
     * @return
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    protected URL getWebApiUrl(String method, List<NameValuePair> params) throws WebApiException {

        try {

            URIBuilder b = getWebApiUrlBuilder();

            // API script
            b.setPath(apiConfig.getScriptpath());

            // API data
            b.addParameter("api", apiConfig.getName());
            b.addParameter("version", apiConfig.getVersion());

            // API method
            b.addParameter("method", method);

            // API parameters
            if (params != null) {
                b.addParameters(params);
            }

            // API session
            b.addParameter("_sid", getSessionId());

            URL url = b.build().toURL();
            return url;

        } catch (URISyntaxException | IOException | UnsupportedOperationException e) {
            throw new WebApiException(e);
        }
    }

    /**
     * E
     *
     * @param apiurl
     * @return
     * @throws WebApiException
     * @throws URISyntaxException
     * @throws UnsupportedOperationException
     * @throws IOException
     */
    protected T callWebApi(String apiurl) throws WebApiException {

        try {

            URL url = new URL(apiurl);

            URI uri = url.toURI();
            HttpGet httpget = new HttpGet(uri);
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpClientContext context = HttpClientContext.create();
            CloseableHttpResponse response = httpclient.execute(httpget, context);

            HttpEntity entity = response.getEntity();

            if (entity != null) {

                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                String result = convertStreamToString(instream);
                // now you have the string representation of the HTML request
                System.out.println("RESPONSE: " + result);
                instream.close();
                if (response.getStatusLine().getStatusCode() == 200) {
                    // netState.setLogginDone(true);
                }

                Constructor<T> ctor = typeParameterClass.getConstructor(String.class);

                T vo = ctor.newInstance(new Object[] { result });

                return vo;

            } else {
                throw new WebApiException("Error calling Surveillance Station WebApi!");
            }

        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException | IOException | URISyntaxException e) {
            throw new WebApiException(e);
        }

    }

    /**
     * @param is
     * @return
     */
    private String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
