package org.powernukkitx.anticheat.module.network;

import org.powernukkitx.event.EventHandler;
import org.powernukkitx.event.player.PlayerLoginEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import com.google.gson.Gson;
import org.powernukkitx.anticheat.AntiCheatPlugin;
import org.powernukkitx.anticheat.module.Module;
import org.powernukkitx.anticheat.module.ModuleType;


public class ProxyModuleA extends Module {

    private static final Gson GSON = new Gson();

    public ProxyBotModule(AntiCheatPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        final String ip = event.getPlayer().getAddress();

        try {
            final URL url = new URL("https://proxycheck.io/v2/" + ip);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("Content-Type", "application/json");

            final BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
            );
            final StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            final Map<?, ?> dataMap = GSON.fromJson(response.toString(), Map.class);
            final Object status = dataMap.get("status");
            final Object ipData = dataMap.get(ip);

            if (status != null && !"error".equals(status.toString()) && ipData instanceof Map) {
                final Map<?, ?> ipResult = (Map<?, ?>) ipData;
                final Object proxy = ipResult.get("proxy");

                if ("yes".equals(proxy)) {
                    event.setKickMessage("Error: Potential proxy, turn it off");
                    event.setCancelled();
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public String getName() {
        return "Proxy";
    }

    @Override
    public ModuleType getType() {
        return ModuleType.NETWORK;
    }
}
