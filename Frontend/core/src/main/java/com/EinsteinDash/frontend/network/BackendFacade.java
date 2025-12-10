package com.EinsteinDash.frontend.network;

import java.util.ArrayList;

import com.EinsteinDash.frontend.model.LevelDto;
import com.EinsteinDash.frontend.model.ProgressDto;
import com.EinsteinDash.frontend.utils.Constants;
import com.EinsteinDash.frontend.utils.Session;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

/**
 * BackendFacade - Pattern Facade untuk komunikasi dengan REST API backend.
 * Menyederhanakan HTTP requests untuk Login, Register, Levels, Progress, dan Leaderboard.
 */
public class BackendFacade {

    // ==================== CALLBACK INTERFACES ====================

    public interface LoginCallback {
        void onSuccess();
        void onFailed(String errorMessage);
    }

    public interface RegisterCallback {
        void onSuccess();
        void onFailed(String errorMessage);
    }

    public interface LeaderboardCallback {
        void onSuccess(JsonValue rootData);
        void onFailed(String errorMessage);
    }

    public interface LevelListCallback {
        void onSuccess(ArrayList<LevelDto> levels);
        void onFailed(String errorMessage);
    }

    public interface SyncCallback {
        void onSuccess(int serverCoins, boolean serverCompleted);
        void onFailed(String error);
    }

    public interface ProgressListCallback {
        void onSuccess(ArrayList<ProgressDto> progressList);
        void onFailed(String error);
    }

    // ==================== AUTHENTICATION ====================

    /** Login user dan simpan data ke Session */
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

    public void getLeaderboard(final LeaderboardCallback callback) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
            .method(Net.HttpMethods.GET)
            .url(Constants.BASE_URL + "/leaderboard") // Endpoint backend
            .build();

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                String responseString = httpResponse.getResultAsString();

                if (statusCode == 200) {
                    // Parse JSON Array
                    // Format: [{"username": "...", "totalStars": 10}, ...]
                    JsonValue root = new JsonReader().parse(responseString);

                    Gdx.app.postRunnable(() -> callback.onSuccess(root));
                } else {
                    Gdx.app.postRunnable(() -> callback.onFailed("Error: " + statusCode));
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

   public void fetchLevels(final LevelListCallback callback) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
            .method(Net.HttpMethods.GET)
            .url(Constants.BASE_URL + "/levels")
            .header("Content-Type", "application/json")
            .timeout(30000)
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

    @SuppressWarnings("unchecked")  // biar bersih ajah
    public void syncProgress(int userId, int levelId, int percentage, int attemptsToAdd, int coinsCollected, final SyncCallback callback) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();

        // Setup Object Json
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);


        // Buat Data & Konversi ke String
        SyncData data = new SyncData(userId, levelId, percentage, attemptsToAdd, coinsCollected);
        String content = json.toJson(data);

        Net.HttpRequest httpRequest = requestBuilder.newRequest()
            .method(Net.HttpMethods.POST)
            .url(Constants.BASE_URL + "/sync")
            .header("Content-Type", "application/json")
            .content(content)
            .timeout(30000)
            .build();

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                if (httpResponse.getStatus().getStatusCode() == 200) {
                    Gdx.app.log("BACKEND", "Progress Synced!");
                    String responseString = httpResponse.getResultAsString();
                    JsonValue root = new JsonReader().parse(responseString);
                    int serverCoins = root.getInt("coinsCollected", 0);
                    boolean serverCompleted = root.getBoolean("completed", false);

                    // simpan ke memory local
                    Session.getInstance().saveLocalProgress(levelId, serverCoins);
                    Gdx.app.postRunnable(() -> callback.onSuccess(serverCoins, serverCompleted));
                } else {
                    Gdx.app.error("BACKEND", "Sync Failed: " + httpResponse.getStatus().getStatusCode());
                    Gdx.app.postRunnable(() -> callback.onFailed("Sync Error"));
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> callback.onFailed(t.getMessage()));
            }

            @Override
            public void cancelled() { }
        });
    }

    public void fetchUserProgress(int userId, final ProgressListCallback callback) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
            .method(Net.HttpMethods.GET)
            .url(Constants.BASE_URL + "/progress/" + userId)
            .timeout(30000)
            .build();

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                if (httpResponse.getStatus().getStatusCode() == 200) {
                    String jsonString = httpResponse.getResultAsString();

                    Json json = new Json();
                    // Ignor unknown fields biar gak error kalau ada field aneh
                    json.setIgnoreUnknownFields(true);

                    ArrayList<ProgressDto> progressList = json.fromJson(ArrayList.class, ProgressDto.class, jsonString);

                    Gdx.app.postRunnable(() -> callback.onSuccess(progressList));
                } else {
                    Gdx.app.postRunnable(() -> callback.onFailed("Error: " + httpResponse.getStatus().getStatusCode()));
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> callback.onFailed(t.getMessage()));
            }

            @Override
            public void cancelled() { }
        });
    }

    // Helper Class
    private static class SyncData {
        public int userId;
        public int levelId;
        public int percentage;
        public int attemptsToAdd;
        public int coinsCollected;

        public SyncData(int userId, int levelId, int percentage, int attemptsToAdd, int coinsCollected) {
            this.userId = userId;
            this.levelId = levelId;
            this.percentage = percentage;
            this.attemptsToAdd = attemptsToAdd;
            this.coinsCollected = coinsCollected;
        }
    }
}
