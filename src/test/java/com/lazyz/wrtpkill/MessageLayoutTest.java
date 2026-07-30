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
    void keepsUnindentedColoredTextLeftAligned() {
        String line = "&c☠ &c&l你已死亡并完成复活 &c☠";

        assertEquals(line, MessageLayout.leftAlign(line));
    }

    @Test
    void removesManualIndentWithoutAddingAlignmentPadding() {
        String line = "           &#F8D34B&l✦ Online Positions ✦";

        String leftAligned = MessageLayout.leftAlign(line);

        assertEquals("&#F8D34B&l✦ Online Positions ✦", leftAligned);
        assertEquals(20, MessageLayout.visibleLength(leftAligned));
    }

    @Test
    void leavesDividerWidthTextWithoutIndent() {
        String divider = "&b━━━━━━━━━━━━━━━━━━ &e✧ &b━━━━━━━━━━━━━━━━━━";

        assertEquals(39, MessageLayout.visibleLength(divider));
        assertEquals(divider, MessageLayout.leftAlign(divider));
    }

    @Test
    @SuppressWarnings("unchecked")
    void officialPanelLinesAreLeftAlignedAndFitTheDividerWidth() throws IOException {
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
                        assertEquals(line.stripLeading(), line,
                                () -> path + " messages." + key + " is indented: " + line);
                        assertTrue(MessageLayout.visibleLength(line) <= 39,
                                () -> path + " messages." + key + " exceeds the divider width: " + line);
                    }
                }
            }
        }
    }
}
