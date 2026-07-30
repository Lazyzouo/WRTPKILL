package com.lazyz.wrtpkill;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MessageUtils {
    public static final String DEFAULT_PREFIX = "&#00D2FF&l[&#3A7BD5&lWRTP&#00D2FF&l] &8&l┃ &r";
    private static final Set<String> UNPREFIXED_MESSAGES = Set.of(
            "help_menu_player",
            "help_menu_admin",
            "help_menu_footer",
            "suicide_success",
            "death_respawned",
            "unlock_death_merged",
            "merged_offline_notice"
    );
    private static final Map<String, String> DEFAULT_MESSAGES = Map.ofEntries(
            Map.entry("only_player", "&c&l操作失败 &8► &7该指令仅限玩家使用！"),
            Map.entry("not_player", "&c&l操作失败 &8► &7该指令仅限玩家使用！"),
            Map.entry("wrong_usage", "&e&l参数错误 &8► &7未知指令，请使用 &6/wrtp help &7查看帮助。"),
            Map.entry("no_permission", "&c&l权限不足 &8► &7该指令仅限服务器 OP 使用！"),
            Map.entry("reload_success", "&a&l操作成功 &8► &7配置文件已重新加载！"),
            Map.entry("whitelist_add_usage", "&e&l参数错误 &8► &7用法：&6/wrtp whitelist add <玩家名称>"),
            Map.entry("whitelist_add_exists", "&e&l无需添加 &8► &7玩家 &f{player} &7已在白名单中。"),
            Map.entry("whitelist_add_success", "&a&l添加成功 &8► &7已将玩家 &f{player} &7加入传送白名单。"),
            Map.entry("tpa_disabled", "&c&l系统提示 &8► &7管理员已关闭玩家互传功能！"),
            Map.entry("tpa_usage", "&e&l参数错误 &8► &7用法：&6/tpa <玩家名称>"),
            Map.entry("tpa_player_offline", "&c&l请求失败 &8► &7该玩家不在线或不存在。"),
            Map.entry("tpa_self", "&e&l请求无效 &8► &7你不能向自己发送传送请求。"),
            Map.entry("tpa_sent", "&a&l请求已发送 &8► &7已向 &e{target} &7发送传送请求，30 秒内有效。"),
            Map.entry("tpa_received", "&e&l传送请求 &8► &7收到来自 &a{player} &7的传送请求。 {buttons}"),
            Map.entry("tpa_no_request", "&e&l暂无请求 &8► &7当前没有可处理的传送请求。"),
            Map.entry("tpa_sender_offline", "&c&l请求失效 &8► &7请求发起者已离线。"),
            Map.entry("tpa_target_locked", "&c&l传送受限 &8► &7对方当前处于传送锁定状态。"),
            Map.entry("tpa_sender_locked", "&c&l传送受限 &8► &7你当前处于传送锁定状态。"),
            Map.entry("tpa_accepted_receiver", "&a&l已接受 &8► &7你已接受传送请求。"),
            Map.entry("tpa_accepted_sender", "&a&l请求已接受 &8► &e{target} &7已接受请求，正在传送。"),
            Map.entry("tpa_success", "&a&l传送成功 &8► &7已传送到 &e{target} &7身边！"),
            Map.entry("tpa_fail", "&c&l传送失败 &8► &7服务器未能完成传送，请稍后重试。"),
            Map.entry("tpa_denied_receiver", "&a&l已拒绝 &8► &7你已拒绝本次传送请求。"),
            Map.entry("tpa_denied_sender", "&c&l请求被拒绝 &8► &7玩家 &f{target} &7拒绝了你的请求。"),
            Map.entry("tpa_cancelled_sender", "&a&l请求已取消 &8► &7已取消你发出的传送请求。"),
            Map.entry("tpa_cancelled_receiver", "&e&l请求已取消 &8► &7玩家 &f{sender} &7取消了传送请求。"),
            Map.entry("tpa_timeout_receiver", "&e&l请求超时 &8► &7来自 &f{sender} &7的传送请求已失效。"),
            Map.entry("tpa_timeout_sender", "&e&l请求超时 &8► &7玩家 &f{target} &7未及时回应你的请求。"),
            Map.entry("rtp_locked", "&c&l传送受限 &8► &7你当前处于传送锁定状态。"),
            Map.entry("suicide_success", "&b━━━━━━&3━━━━━━&9━━━━━━ &e✧ &9━━━━━━&3━━━━━━&b━━━━━━\n&c☠ &c你结束了自己的生命并完成复活 &c☠\n&b━━━━━━&3━━━━━━&9━━━━━━ &e✧ &9━━━━━━&3━━━━━━&b━━━━━━"),
            Map.entry("death_respawned", "&b━━━━━━&3━━━━━━&9━━━━━━ &e✧ &9━━━━━━&3━━━━━━&b━━━━━━\n&c☠ &c你已死亡并完成复活 &c☠\n&b━━━━━━&3━━━━━━&9━━━━━━ &e✧ &9━━━━━━&3━━━━━━&b━━━━━━"),
            Map.entry("unlock_death_merged", "&b━━━━━━&3━━━━━━&9━━━━━━ &e✧ &9━━━━━━&3━━━━━━&b━━━━━━\n&c☠ &c你已死亡并于主城复活 &c☠\n&8▪ &7随机传送(RTP)与玩家传送(TPA)限制已解除！\n&b━━━━━━&3━━━━━━&9━━━━━━ &e✧ &9━━━━━━&3━━━━━━&b━━━━━━"),
            Map.entry("merged_offline_notice", "&b━━━━━━&3━━━━━━&9━━━━━━ &e✧ &9━━━━━━&3━━━━━━&b━━━━━━\n&e❖ 离线清理与权限状态更新 ❖\n&8▪ &7背包与末影箱已清空，传送限制已解除。\n&b━━━━━━&3━━━━━━&9━━━━━━ &e✧ &9━━━━━━&3━━━━━━&b━━━━━━"),
            Map.entry("command_failed", "&c&l指令失败 &8► &7该指令暂时无法执行，请联系管理员。")
    );
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .character('&').hexColors().build();

    public static void send(CommandSender sender, WRTPKILL plugin, String path, String... replacements) {
        Object obj = plugin.getLanguageManager().get(path);
        if (obj == null) obj = DEFAULT_MESSAGES.getOrDefault(path, DEFAULT_MESSAGES.get("command_failed"));
        boolean unprefixed = UNPREFIXED_MESSAGES.contains(path);
        String prefix = unprefixed ? "" : getString(plugin, "prefix", DEFAULT_PREFIX);
        prefix = MessageLayout.leftAlign(prefix);
        if (!unprefixed && (prefix == null || prefix.isBlank())) prefix = DEFAULT_PREFIX;

        if (obj instanceof String) {
            String fullMsg = (String) obj;
            for (String line : fullMsg.split("\n")) {
                String renderedLine = MessageLayout.leftAlign(applyReplacements(line, replacements));
                sendSingleLine(sender, prefix + renderedLine);
            }
        } else if (obj instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> list = (List<String>) obj;
            for (String line : list) {
                sendSingleLine(sender, prefix + MessageLayout.leftAlign(applyReplacements(line, replacements)));
            }
        }
    }

    public static void sendRaw(CommandSender sender, String msg, String... replacements) {
        sendSingleLine(sender, applyReplacements(msg, replacements));
    }

    public static String getString(WRTPKILL plugin, String path, String fallback) {
        return plugin.getLanguageManager().getString(path, fallback);
    }

    private static void sendSingleLine(CommandSender sender, String msg) {
        String leftAligned = MessageLayout.leftAlign(msg);
        if (leftAligned == null || leftAligned.isEmpty()) return;
        sender.sendMessage(deserializeBold(leftAligned));
    }

    private static String applyReplacements(String message, String... replacements) {
        if (message == null) return "";
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace("{" + replacements[i] + "}", replacements[i + 1]);
            }
        }
        return message;
    }

    private static Component deserializeBold(String message) {
        return forceBold(SERIALIZER.deserialize(message));
    }

    private static Component forceBold(Component component) {
        List<Component> children = component.children().stream()
                .map(MessageUtils::forceBold)
                .toList();
        return component.children(children)
                .decoration(TextDecoration.BOLD, TextDecoration.State.TRUE);
    }

    public static void sendTpaRequest(CommandSender sender, WRTPKILL plugin, String senderName) {
        Object obj = plugin.getLanguageManager().get("tpa_received");
        if (!(obj instanceof String)) obj = DEFAULT_MESSAGES.get("tpa_received");
        String prefix = getString(plugin, "prefix", DEFAULT_PREFIX);
        if (prefix == null || prefix.isBlank()) prefix = DEFAULT_PREFIX;

        prefix = MessageLayout.leftAlign(prefix);
        Component acceptBtn = Component.text(MessageLayout.leftAlign(getString(plugin, "tpa_accept_button", "【✔接受】")))
                .color(NamedTextColor.GREEN)
                .decorate(TextDecoration.BOLD)
                .clickEvent(ClickEvent.runCommand("/tpaccept"))
                .hoverEvent(HoverEvent.showText(Component.text(
                        MessageLayout.leftAlign(getString(plugin, "tpa_accept_hover", "点击直接接受传送")))
                        .color(NamedTextColor.GREEN)
                        .decorate(TextDecoration.BOLD)));

        Component denyBtn = Component.text(MessageLayout.leftAlign(getString(plugin, "tpa_deny_button", "【✖拒绝】")))
                .color(NamedTextColor.RED)
                .decorate(TextDecoration.BOLD)
                .clickEvent(ClickEvent.runCommand("/tpdeny"))
                .hoverEvent(HoverEvent.showText(Component.text(
                        MessageLayout.leftAlign(getString(plugin, "tpa_deny_hover", "点击直接拒绝传送")))
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD)));

        if (obj instanceof String) {
            String fullMsg = (String) obj;
            for (String line : fullMsg.split("\n")) {
                line = prefix + MessageLayout.leftAlign(line.replace("{player}", senderName));

                if (line.contains("{buttons}")) {
                    String before = line.substring(0, line.indexOf("{buttons}"));
                    String after = line.substring(line.indexOf("{buttons}") + "{buttons}".length());

                    Component comp = deserializeBold(before)
                            .append(acceptBtn).append(Component.text("   ")).append(denyBtn)
                            .append(deserializeBold(after));
                    sender.sendMessage(forceBold(comp));
                } else {
                    sender.sendMessage(deserializeBold(line));
                }
            }
        }
    }
}
