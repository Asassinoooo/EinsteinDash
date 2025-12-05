package com.EinsteinDash.frontend.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.EinsteinDash.frontend.utils.Constants;
import com.EinsteinDash.frontend.utils.Session;
import java.util.ArrayList;
import com.badlogic.gdx.utils.Json;
import com.EinsteinDash.frontend.model.LevelDto;

public class BackendFacade {

    // Interface sederhana untuk komunikasi balik ke UI
    public interface LoginCallback {
        void onSuccess();
        void onFailed(String errorMessage);
    }

    //interface callback untuk register
    public interface RegisterCallback {
        void onSuccess();
        void onFailed(String errorMessage);
    }

    //interface untuk komunikasi balik list level
    public interface LevelListCallback {
        void onSuccess(ArrayList<LevelDto> levels);
        void onFailed(String errorMessage);
    }

    public void login(String username, String password, final LoginCallback callback) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        String content = "{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}";

        Net.HttpRequest httpRequest = requestBuilder.newRequest()
            .method(Net.HttpMethods.POST)
            .url(Constants.BASE_URL + "/login")
            .header("Content-Type", "application/json")
            .content(content)
            .build();

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                String responseString = httpResponse.getResultAsString();

                if (statusCode == 200) {
                    // Parsing JSON
                    JsonValue root = new JsonReader().parse(responseString);

                    // SIMPAN KE SINGLETON SESSION
                    Session.getInstance().setUserData(
                        root.getInt("id"),
                        root.getString("username"),
                        root.getInt("totalStars")
                    );

                    // Beri tahu UI di thread utama (Wajib Gdx.app.postRunnable untuk UI)
                    Gdx.app.postRunnable(() -> callback.onSuccess());
                } else {
                    Gdx.app.postRunnable(() -> callback.onFailed("Login Gagal: " + statusCode));
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> callback.onFailed("Koneksi Error: " + t.getMessage()));
            }

            @Override
            public void cancelled() { }
        });
    }

    public void register(String username, String password, final RegisterCallback callback) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();

        // Buat JSON manual
        String content = "{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}";

        Net.HttpRequest httpRequest = requestBuilder.newRequest()
            .method(Net.HttpMethods.POST)
            .url(Constants.BASE_URL + "/register") // Endpoint register
            .header("Content-Type", "application/json")
            .content(content)
            .build();

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                String responseString = httpResponse.getResultAsString();

                // 200 OK berarti user berhasil dibuat
                if (statusCode == 200) {
                    Gdx.app.log("BACKEND", "Register Sukses: " + responseString);
                    Gdx.app.postRunnable(() -> callback.onSuccess());
                } else {
                    // 400 Bad Request biasanya jika username sudah ada
                    Gdx.app.error("BACKEND", "Register Gagal: " + statusCode);
                    Gdx.app.postRunnable(() -> callback.onFailed("Gagal: " + responseString));
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> callback.onFailed("Error: " + t.getMessage()));
            }

            @Override
            public void cancelled() { }
        });
    }

    public void fetchLevels(final LevelListCallback callback) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
            .method(Net.HttpMethods.GET)
            .url(Constants.BASE_URL + "/levels")
            .header("Content-Type", "application/json")
            .build();

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                String responseString = httpResponse.getResultAsString();

                if (statusCode == 200) {
                    try {
                        Json json = new Json();
                        // LibGDX Json cara parsing Array/List:
                        ArrayList<LevelDto> levels = json.fromJson(ArrayList.class, LevelDto.class, responseString);

                        Gdx.app.postRunnable(() -> callback.onSuccess(levels));
                    } catch (Exception e) {
                        Gdx.app.postRunnable(() -> callback.onFailed("Parse Error: " + e.getMessage()));
                    }
                } else {
                    Gdx.app.postRunnable(() -> callback.onFailed("Error " + statusCode));
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> callback.onFailed("Connection Error"));
            }

            @Override
            public void cancelled() { }
        });
    }
}
