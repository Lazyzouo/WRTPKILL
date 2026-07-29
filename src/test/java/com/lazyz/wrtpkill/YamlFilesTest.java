package com.lazyz.wrtpkill;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class YamlFilesTest {
    @Test
    void trackedYamlFilesAreValid() throws IOException {
        List<Path> files = List.of(
                Path.of("src/main/resources/config.yml"),
                Path.of("src/main/resources/lang/en_US.yml"),
                Path.of("src/main/resources/plugin.yml"),
                Path.of("presets/config-en_US.yml"),
                Path.of(".github/workflows/build.yml"),
                Path.of(".github/workflows/release.yml"),
                Path.of(".github/workflows/codeql.yml"),
                Path.of(".github/dependabot.yml"),
                Path.of(".github/ISSUE_TEMPLATE/bug_report.yml"),
                Path.of(".github/ISSUE_TEMPLATE/feature_request.yml"),
                Path.of(".github/ISSUE_TEMPLATE/config.yml")
        );

        Yaml yaml = new Yaml();
        for (Path file : files) {
            try (InputStream input = Files.newInputStream(file)) {
                assertNotNull(yaml.load(input), () -> file + " parsed to null");
            }
        }
    }
}
