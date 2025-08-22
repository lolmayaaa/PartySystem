package lol.mayaaa.partysystem.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class MessageUtils {

    public static Component getErrorMessage(String message) {
        return Component.text(message).color(NamedTextColor.RED);
    }

    public static Component getSuccessMessage(String message) {
        return Component.text(message).color(NamedTextColor.GREEN);
    }

    public static Component getInfoMessage(String message) {
        return Component.text(message).color(NamedTextColor.YELLOW);
    }
}