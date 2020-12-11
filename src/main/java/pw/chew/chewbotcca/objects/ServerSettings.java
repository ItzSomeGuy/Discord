/*
 * Copyright (C) 2020 Chewbotcca
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package pw.chew.chewbotcca.objects;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import pw.chew.chewbotcca.util.PropertiesManager;
import pw.chew.chewbotcca.util.RestClient;

import java.util.HashMap;

// Server settings
public class ServerSettings {
    static final HashMap<String, ServerSettings> cache = new HashMap<>();
    final JSONObject data;
    public ServerSettings(JSONObject input) {
        data = input;
    }

    /**
     * Get a server from the cache, or retrieve it if not set.
     * Can't use getOrDefault() because it always retrieves, even if it's in the cache.
     * @param id the server id
     * @return a Server
     */
    public static ServerSettings getServer(String id) {
        if(cache.containsKey(id)) {
            return cache.get(id);
        } else {
            return retrieveServer(id);
        }
    }

    /**
     * Get a server from the cache, and only the cache.
     * Don't attempt retrieving it.
     * @param id the server ID
     * @return a possibly null server
     */
    public static ServerSettings getServerIfCached(String id) {
        return cache.get(id);
    }

    /**
     * Retrieve server info
     * @param id the server id
     * @return a server settings
     */
    public static ServerSettings retrieveServer(String id) {
        JSONObject response = new JSONObject(RestClient.get(
                "https://chew.pw/chewbotcca/discord/api/server/" + id,
                PropertiesManager.getChewKey()
        ));
        cache.put(id, new ServerSettings(response));
        LoggerFactory.getLogger(Profile.class).debug("Saving " + id + " to Server cache");
        return new ServerSettings(response);
    }

    public void saveData(String key, String value) {
        HashMap<String, Object> inputMap = new HashMap<>();
        inputMap.put(key, value);
        JSONObject response = new JSONObject(
            RestClient.post(
                "https://chew.pw/chewbotcca/discord/api/server/" + getId(),
                inputMap,
                PropertiesManager.getChewKey()
            )
        );
        cache.put(getId(), new ServerSettings(response));
        LoggerFactory.getLogger(Profile.class).debug("Saving " + getId() + " to Server cache");
    }

    /**
     * @return the server's ID
     */
    public String getId() {
        return String.valueOf(data.getLong("serverid"));
    }

    /**
     * @return the server's prefix, if there is one
     */
    public String getPrefix() {
        return data.isNull("prefix") ? null : data.getString("prefix");
    }
}
