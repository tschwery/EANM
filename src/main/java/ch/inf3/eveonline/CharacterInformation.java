package ch.inf3.eveonline;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class CharacterInformation {

    private static final Map<String, CharacterInformation> CACHE = new HashMap<>();

    private final long id;
    private final String name;
    private final String gender;

    private CharacterInformation(long id, CharacterESIResponse esiResponse) {
        this.id = id;
        this.name = esiResponse.name;
        this.gender = esiResponse.gender;
    }

    private CharacterInformation(long id, String name, String gender) {
        this.id = id;
        this.name = name;
        this.gender = gender;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static final class CharacterESIResponse {

        @SerializedName("alliance_id")
        private Integer allianceId;

        @SerializedName("ancestry_id")
        private Integer ancestryId;

        @SerializedName("birthday")
        private Date birthday;

        @SerializedName("bloodline_id")
        private Integer bloodlineId;

        @SerializedName("corporation_id")
        private Integer corporationId;

        @SerializedName("description")
        private String description;

        @SerializedName("faction_id")
        private Integer factionId;

        @SerializedName("gender")
        private String gender;

        @SerializedName("name")
        private String name;

        @SerializedName("race_id")
        private Integer raceId;

        @SerializedName("security_status")
        private BigDecimal securityStatus;
    }

    public static void saveCache(Properties props) {
        props.put("chars.available", CACHE.keySet().stream().collect(Collectors.joining(", ")));

        for (Map.Entry<String, CharacterInformation> cacheEntry : CACHE.entrySet()) {
            String cacheKey = cacheEntry.getKey();
            CharacterInformation ci = cacheEntry.getValue();
            props.put("chars." + cacheKey + ".id", Long.toString(ci.id));
            props.put("chars." + cacheKey + ".name", ci.name);
            props.put("chars." + cacheKey + ".gender", ci.gender);
        }
    }

    public static void loadCache(Properties props) {
        String cacheKeys = (String) props.get("chars.available");

        if (cacheKeys == null || cacheKeys.isEmpty()) {
            return;
        }

        for (String cacheKey : cacheKeys.split(", ")) {
            String id = (String) props.get("chars." + cacheKey + ".id");
            String name = (String) props.get("chars." + cacheKey + ".name");
            String gender = (String) props.get("chars." + cacheKey + ".gender");

            CACHE.put(cacheKey, new CharacterInformation(Long.parseLong(id, 10), name, gender));
        }

    }

    public static CharacterInformation getInfos(long charId, String datasource) {
        String cacheId = charId + "-" + datasource;
        if (CACHE.containsKey(cacheId)) {
            return CACHE.get(cacheId);
        }

        URL url;
        try {
            url = new URL("https://esi.evetech.net/latest/characters/" + charId + "/?datasource=" + datasource);
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
        try (InputStream is = url.openStream()) {
            CharacterESIResponse esiResponse = new GsonBuilder().create().fromJson(new InputStreamReader(is), CharacterESIResponse.class);
            CharacterInformation ci = new CharacterInformation(charId, esiResponse);
            CACHE.put(cacheId, ci);
            return ci;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }
}
