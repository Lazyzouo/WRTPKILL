package com.lazyz.wrtpkill;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageLayoutTest {
    @Test
    void centersColoredTextOnTheDividerStar() {
        String line = "&c☠ &c&l你已死亡并完成复活 &c☠";

        assertEquals(" ".repeat(13) + line, MessageLayout.centerOnDivider(line));
    }

    @Test
    void removesManualIndentBeforeCalculatingDynamicCentering() {
        String line = "           &#F8D34B&l✦ Online Positions ✦";

        String centered = MessageLayout.centerOnDivider(line);

        assertEquals((39 - 20) / 2, centered.indexOf('&'));
        assertEquals(20, MessageLayout.visibleLength(centered.trim()));
    }

    @Test
    void leavesDividerWidthTextWithoutIndent() {
        String divider = "&b━━━━━━━━━━━━━━━━━━ &e✧ &b━━━━━━━━━━━━━━━━━━";

        assertEquals(39, MessageLayout.visibleLength(divider));
        assertEquals(divider, MessageLayout.centerOnDivider(divider));
    }

    @Test
    @SuppressWarnings("unchecked")
    void officialCenteredPanelLinesFitTheDividerWidth() throws IOException {
        List<String> messageKeys = List.of(
                "suicide_success",
                "death_respawned",
                "merged_offline_notice",
                "unlock_death_merged",
                "pos_title",
                "pos_none",
                "pos_world_header",
                "pos_player_entry",
                "pos_total"
        );

        for (Path path : List.of(
                Path.of("src/main/resources/config.yml"),
                Path.of("src/main/resources/lang/en_US.yml"))) {
            try (InputStream input = Files.newInputStream(path)) {
                Map<String, Object> root = new Yaml().load(input);
                Map<String, Object> messages = (Map<String, Object>) root.get("messages");
                for (String key : messageKeys) {
                    String message = (String) messages.get(key);
                    for (String line : message.split("\\n")) {
                        assertTrue(MessageLayout.visibleLength(line) <= 39,
                                () -> path + " messages." + key + " exceeds the divider width: " + line);
                    }
                }
            }
        }
    }
}
