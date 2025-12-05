package com.einsteindash.frontend.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.einsteindash.frontend.utils.Constants;
import com.einsteindash.frontend.utils.Session;

public class BackendFacade {

    // Interface sederhana untuk komunikasi balik ke UI
    public interface LoginCallback {
        void onSuccess();
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
}
