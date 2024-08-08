package dev.pierrot.Component.Button;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.pierrot.Component.ButtonComponent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class Pause extends ButtonComponent {

    static {
        registerComponent(new Pause());
    }

    Pause() {
        super("pause");

    }

    @Override
    public void callback(LavalinkClient client, @NotNull ButtonInteractionEvent event) {
        var guild = event.getGuild();
        assert guild != null;
        client.getOrCreateLink(guild.getIdLong())
                .getPlayer()
                .flatMap((player) -> player.setPaused(!player.getPaused()))
                .subscribe((player) -> event.getInteraction().reply("Player has been " + (player.getPaused() ? "paused" : "resumed") + "!").queue());
    }
}
