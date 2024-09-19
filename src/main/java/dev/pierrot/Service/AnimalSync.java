package dev.pierrot.Service;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;
import dev.pierrot.Main;
import dev.pierrot.Utils;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.Disposable;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class AnimalSync extends Utils {
    private final static Logger logger = getLogger("AnimalSync");
    private final static String HUB_URL = Main.config.getApp().websocket;
    private final HubConnection hubConnection;

    private final int maxReconnectAttempts = 10;
    private final int reconnectDelay = 4;
    private boolean reconnecting = false;
    private int reconnectAttempts = 0;
    private Disposable reconnectDisposable;

    public AnimalSync(String clientId) {
        var headers = new HashMap<String, String>();
        headers.put("Secret", "123");
        hubConnection = HubConnectionBuilder.create(HUB_URL + "?ClientId=" + clientId)
                .withHeaders(headers)
                .build();
        listenEvent();
        start();
    }

    public HubConnection getHubConnection() {
        return hubConnection;
    }

    private void listenEvent() {
        hubConnection.on("connection", logger::info, String.class);
        hubConnection.on("error", (msg) -> {
            logger.error(msg.toString());
        }, Object.class);
        hubConnection.on("disconnect", (msg) -> {
            logger.warn(msg);
            reconnect();
        }, String.class);
        hubConnection.onClosed((exception) -> {
            logger.warn("Connection closed. Attempting to reconnect...");
            reconnect();
        });
    }

    public void start() {
        if (hubConnection.getConnectionState() != HubConnectionState.CONNECTED) {
            hubConnection.start().subscribe(
                    () -> {
                        logger.info("Connected successfully");
                        reconnectAttempts = 0;
                        reconnecting = false;
                    },
                    error -> {
                        logger.error("Error while connecting: {}", error.getMessage());
                        reconnect();
                    }
            ).dispose();
        }
    }

    void reconnect() {
        if (reconnectAttempts < maxReconnectAttempts) {
            reconnecting = true;
            reconnectAttempts++;
            logger.info("Connection state: {}", hubConnection.getConnectionState().toString());

            if (reconnectDisposable != null && !reconnectDisposable.isDisposed()) {
                reconnectDisposable.dispose();
            }

            reconnectDisposable = Completable.timer(reconnectDelay, TimeUnit.SECONDS)
                    .subscribe(() -> {
                        logger.warn("Attempting to reconnect (attempt {} of {})", reconnectAttempts, maxReconnectAttempts);
                        start();
                    });
        } else {
            logger.error("Max reconnect attempts reached. Stopping reconnection attempts.");
            reconnecting = false;
            // Optionally, you might want to implement a more aggressive reconnection strategy here
            // For example, you could reset the reconnectAttempts and try again after a longer delay
            resetReconnectionAttempts();
        }
    }

    private void resetReconnectionAttempts() {
        reconnectAttempts = 0;
        reconnecting = false;
        // Wait for a longer time before trying to reconnect again
        Completable.timer(60, TimeUnit.SECONDS)
                .subscribe(this::start).dispose();
    }
}