package eu.sengy.blockjail.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import static eu.sengy.blockjail.JailSpigot.*;

public class Papi extends PlaceholderExpansion {


    @Override
    public @NotNull String getIdentifier() {
        return "jail";
    }

    @Override
    public @NotNull String getAuthor() {
        return "SengyEU";
    }

    @Override
    public @NotNull String getVersion() {
        return "1";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        String uuid = player.getUniqueId().toString().replaceAll("-","");
        if(params.equalsIgnoreCase("currCount")){
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT currCount FROM jail WHERE uuid = ?"
                );
                statement.setString(1, uuid);
                ResultSet result = statement.executeQuery();

                if (result.next()) {
                    return String.valueOf(result.getInt("currCount"));
                }
                else {
                    return "0";
                }

            } catch (SQLException er) {
                System.out.println("Failed read info from database: " + er.getMessage());
            }
        }
        else if(params.equalsIgnoreCase("neededCount")) {
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT neededCount FROM jail WHERE uuid = ?"
                );
                statement.setString(1, uuid);
                ResultSet result = statement.executeQuery();

                if (result.next()) {
                    return String.valueOf(result.getInt("neededCount"));
                }
                else {
                    return "0";
                }

            } catch (SQLException er) {
                System.out.println("Failed read info from database: " + er.getMessage());
            }
        }
        else if(params.equalsIgnoreCase("admin")) {
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT admin FROM jail WHERE uuid = ?"
                );
                statement.setString(1, uuid);
                ResultSet result = statement.executeQuery();

                if (result.next()) {
                    return result.getString("admin");
                }
                else {
                    return "Null";
                }

            } catch (SQLException er) {
                System.out.println("Failed read info from database: " + er.getMessage());
            }
        }
        else if(params.equalsIgnoreCase("datum")) {
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT timestamp FROM jail WHERE uuid = ?"
                );
                statement.setString(1, uuid);
                ResultSet result = statement.executeQuery();

                if (result.next()) {
                    return new SimpleDateFormat("dd. MM. yyyy").format(result.getTimestamp("timestamp"));
                }
                else {
                    return "Null";
                }

            } catch (SQLException er) {
                System.out.println("Failed read info from database: " + er.getMessage());
            }
        }
        else if(params.equalsIgnoreCase("id")) {
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT id FROM jail WHERE uuid = ?"
                );
                statement.setString(1, uuid);
                ResultSet result = statement.executeQuery();

                if (result.next()) {
                    return String.valueOf(result.getInt("id"));
                }
                else {
                    return "0";
                }

            } catch (SQLException er) {
                System.out.println("Failed read info from database: " + er.getMessage());
            }
        }
        else if(params.equalsIgnoreCase("neededCountHalf")) {
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT neededCount FROM jail WHERE uuid = ?"
                );
                statement.setString(1, uuid);
                ResultSet result = statement.executeQuery();

                if (result.next()) {
                    return String.valueOf(result.getInt("neededCount")/2);
                }
                else {
                    return "0";
                }

            } catch (SQLException er) {
                System.out.println("Failed read info from database: " + er.getMessage());
            }
        }
        else if(params.equalsIgnoreCase("currCount2")) {
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT neededCount,currCount FROM jail WHERE uuid = ?"
                );
                statement.setString(1, uuid);
                ResultSet result = statement.executeQuery();

                if (result.next()) {
                    if(result.getInt("currCount")<=result.getInt("neededCount")/2){
                        return "0";
                    }
                    return String.valueOf(result.getInt("currCount")-(result.getInt("neededCount")/2));
                }
                else {
                    return "0";
                }

            } catch (SQLException er) {
                System.out.println("Failed read info from database: " + er.getMessage());
            }
        }
        else if(params.equalsIgnoreCase("reason")) {
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT reason FROM jail WHERE uuid = ?"
                );
                statement.setString(1, uuid);
                ResultSet result = statement.executeQuery();

                if (result.next()) {
                    return result.getString("reason");
                }
                else {
                    return "Null";
                }

            } catch (SQLException er) {
                System.out.println("Failed read info from database: " + er.getMessage());
            }
        }

        return null; // Placeholder is unknown by the Expansion
    }
}
