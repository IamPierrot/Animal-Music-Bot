package dev.pierrot;

import dev.arbjerg.lavalink.client.Helpers;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.event.WebSocketClosedEvent;
import dev.arbjerg.lavalink.client.loadbalancing.builtin.VoiceRegionPenaltyProvider;
import dev.arbjerg.lavalink.libraries.jda.JDAVoiceUpdateListener;
import dev.pierrot.Listener.JDAListener;
import dev.pierrot.Service.AnimalSync;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.Objects;

import static dev.pierrot.Listener.LavaLinkListener.lavaLinkRegisterEvents;

public final class App extends Utils {
    public static LavalinkClient client;
    public static JDA jda;
    public static AnimalSync animalSync;

    private static final int SESSION_INVALID = 4006;
    final String TOKEN = Main.config.getApp().TOKEN;

    private App() throws InterruptedException {
        client = new LavalinkClient(Helpers.getUserIdFromToken(TOKEN));
        animalSync = new AnimalSync(String.valueOf(client.getUserId()));
        jda = JDABuilder.createDefault(TOKEN)
                .setVoiceDispatchInterceptor(new JDAVoiceUpdateListener(client))
                .enableIntents(GatewayIntent.GUILD_VOICE_STATES)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .enableCache(CacheFlag.VOICE_STATE)
                .addEventListeners(new JDAListener())
                .build()
                .awaitReady();

        animalSync.start();
    }

    public static void appInitialize() throws InterruptedException {
        new App();

        lavaLinkRegisterEvents(client);
        loadLavaLinkEvent();

        getLogger(App.class).info("App init!");
    }

    private static void loadLavaLinkEvent() {
        client.getLoadBalancer().addPenaltyProvider(new VoiceRegionPenaltyProvider());

        client.on(WebSocketClosedEvent.class).subscribe((event) -> {
            if (event.getCode() == SESSION_INVALID) {
                final var guildId = event.getGuildId();
                final var guild = jda.getGuildById(guildId);

                if (guild == null) {
                    return;
                }

                final var connectedChannel = Objects.requireNonNull(guild.getSelfMember().getVoiceState()).getChannel();
                if (connectedChannel == null) {
                    return;
                }

                jda.getDirectAudioController().reconnect(connectedChannel);
            }
        });
    }
}
