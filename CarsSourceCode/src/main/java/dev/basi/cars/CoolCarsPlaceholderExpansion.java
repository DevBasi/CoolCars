package dev.basi.cars;

import java.util.Locale;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;

/**
 * @author basi
 */
public final class CoolCarsPlaceholderExpansion extends PlaceholderExpansion {

    private final MainCars plugin;

    public CoolCarsPlaceholderExpansion(MainCars plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "coolcars";
    }

    @Override
    public String getAuthor() {
        return "basi";
    }

    @Override
    public String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params == null) {
            return "";
        }

        String key = params.toLowerCase(Locale.ROOT);
        if (key.equals("total_cars")) {
            return Integer.toString(plugin.getActiveCarCount());
        }

        if (player == null || player.getUniqueId() == null) {
            return fallbackForNoCar(key);
        }

        CarEntity car = plugin.findCarForPlaceholder(player);
        if (car == null) {
            return fallbackForNoCar(key);
        }

        return switch (key) {
            case "has_car" -> "1";
            case "car_uuid" -> car.getVehicleId().toString();
            case "car_model_key" -> plugin.getCarModelKey(car);
            case "car_model_name" -> plugin.getCarModelName(car);
            case "owner_uuid" -> {
                java.util.UUID owner = plugin.getCarOwnerId(car);
                yield owner == null
                    ? "-"
                    : owner.toString();
            }
            case "owner_name" -> {
                String ownerName = plugin.getCarOwnerName(car);
                yield ownerName == null || ownerName.isBlank()
                    ? "-"
                    : ownerName;
            }
            case "speed_kmh" -> format1(car.getSpeedMetersPerSecond() * 3.6D);
            case "speed_mps" -> format2(car.getSpeedMetersPerSecond());
            case "fuel" -> format1(car.getFuelLiters());
            case "fuel_max" -> format1(car.getFuelTankCapacity());
            case "fuel_percent" -> format1(car.getFuelPercent() * 100.0D);
            case "health" -> format1(car.getHealth());
            case "health_max" -> format1(car.getMaxHealth());
            case "health_percent" -> format1(car.getHealthPercent() * 100.0D);
            case "engine" -> car.isEngineRunning()
                ? "1"
                : "0";
            case "lights" -> car.isHeadlightsOn()
                ? "1"
                : "0";
            case "advanced_damage" -> car.isAdvancedDamageEnabled()
                ? "1"
                : "0";
            case "front_percent" -> format1(
                car.getFrontHealthPercent() * 100.0D
            );
            case "rear_percent" -> format1(car.getRearHealthPercent() * 100.0D);
            case "wheel_fl_percent" -> format1(
                car.getPartHealthPercent(CarEntity.DamagePart.WHEEL_FL) * 100.0D
            );
            case "wheel_fr_percent" -> format1(
                car.getPartHealthPercent(CarEntity.DamagePart.WHEEL_FR) * 100.0D
            );
            case "wheel_rl_percent" -> format1(
                car.getPartHealthPercent(CarEntity.DamagePart.WHEEL_RL) * 100.0D
            );
            case "wheel_rr_percent" -> format1(
                car.getPartHealthPercent(CarEntity.DamagePart.WHEEL_RR) * 100.0D
            );
            case "passengers" -> Integer.toString(car.getOccupiedSeatCount());
            case "passengers_max" -> Integer.toString(car.getSeatCount());
            case "driver_name" -> {
                Entity driver = car.getDriver();
                yield driver == null
                    ? "-"
                    : driver.getName();
            }
            case "world" -> {
                Location location = car.getSafeLocation();
                yield location == null || location.getWorld() == null
                    ? "-"
                    : location.getWorld().getName();
            }
            case "x" -> {
                Location location = car.getSafeLocation();
                yield location == null
                    ? "0.0"
                    : format1(location.getX());
            }
            case "y" -> {
                Location location = car.getSafeLocation();
                yield location == null
                    ? "0.0"
                    : format1(location.getY());
            }
            case "z" -> {
                Location location = car.getSafeLocation();
                yield location == null
                    ? "0.0"
                    : format1(location.getZ());
            }
            case "yaw" -> {
                Location location = car.getSafeLocation();
                yield location == null
                    ? "0.0"
                    : format1(location.getYaw());
            }
            default -> "";
        };
    }

    private static String fallbackForNoCar(String key) {
        if (key.equals("has_car")) {
            return "0";
        }
        if (
            key.equals("car_uuid") ||
            key.equals("car_model_key") ||
            key.equals("car_model_name") ||
            key.equals("owner_uuid") ||
            key.equals("owner_name") ||
            key.equals("driver_name") ||
            key.equals("world")
        ) {
            return "-";
        }
        if (
            key.equals("engine") ||
            key.equals("lights") ||
            key.equals("advanced_damage") ||
            key.equals("passengers") ||
            key.equals("passengers_max") ||
            key.equals("total_cars")
        ) {
            return "0";
        }
        return "0.0";
    }

    private static String format1(double value) {
        return String.format(
            Locale.US,
            "%.1f",
            value
        );
    }

    private static String format2(double value) {
        return String.format(
            Locale.US,
            "%.2f",
            value
        );
    }
}
